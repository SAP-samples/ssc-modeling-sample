
package com.sap.ssc.framework.userfunctions;

import java.util.Vector;
import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_class_query;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.oo_class;
import com.sap.sce.user.fn_args;
import com.sap.sce.user.fn_args_deluxe;
import com.sap.sce.user.sce_user_fn;
import com.sap.sxe.db.column;
import com.sap.sxe.db.columns;
import com.sap.sxe.db.conn;
import com.sap.sxe.db.db;
import com.sap.sxe.db.res;
import com.sap.sxe.db.schema;
import com.sap.sxe.db.sys_query_pair;
import com.sap.sxe.db.table;
import com.sap.sxe.db.tables;
import com.sap.sxe.db.type_info;
import com.sap.sxe.sys.exc.exc_database_error;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.tms.imp.tmse_jtype;
import com.sap.sxe.util.bdt_type;
import com.sap.sxe.util.bdt_value;
import com.sap.sxe.util.binding;
import com.sap.sxe.util.float_value;
import com.sap.sxe.util.symbol_value;
import com.sap.sxe.util.imp.float_value_imp;
import com.sap.sxe.util.imp.symbol_value_imp;

public class DATABASE_TABLE_LOOKUP implements sce_user_fn{
	private static final long serialVersionUID = 1;
	private static String tableNameCsticName      = "TABLE_NAME";
	private static String tableInputCsticNameBase = "TABLE_INPUT_CSTIC_NAME";
	private static String tableInputCsticValBase  = "TABLE_INPUT_CSTIC_VAL";
	private static String tableOutputCsticName    = "TABLE_OUTPUT_CSTIC_NAME";
	private static String outputInstCsticName     = "OUTPUT_INSTANCE";
	private static String outputCsticCsticName    = "OUTPUT_INSTANCE_CSTIC_NAME";

	// This method assumes the = operator, because that is the only thing accepted by the variant table APIs.
	public boolean execute (fn_args args_raw, Object obj) {
		fn_args_deluxe args = (fn_args_deluxe)args_raw;
		tmsc_trigger trigger = args.get_trigger();
		int jtype = tmse_jtype.C_SPECIAL;
		cfg_imp cfg = (cfg_imp)obj;
		explc_owner userOwner = (explc_owner)cfg.tms_get_user_owner();
		kb kb = cfg.cfg_get_kb();

		ddbc_inst outputInst = getInstFromArgs(args,outputInstCsticName);
		if (outputInst == null)  return true;

		res result =  queryNormalTable(args, kb, tableNameCsticName);
		if (result == null)  return true;

		setOutputValues(args,result,outputInst,userOwner,trigger,jtype);
		return true;
	}
	///////////// query the table
	res queryNormalTable(fn_args args, kb kb, String tableNameCsticName) {
		String tableName = getStringFromArgs(args, tableNameCsticName);
		if (tableName == null)  return null;
		
		db dataBase = db.getDb();
		table normalTable = getNormalTable(dataBase, tableName);

		Object[] keySpec = getKeySpec(args, normalTable);
		sys_query_pair[] queryPairs  = getQueryPairs(keySpec);
		String[] projection = getProjection(args);
		return dataBase.db_read_table(tableName, queryPairs, projection);	
	}
	
	table getNormalTable (db dataBase, String tableName) {
		conn connection = dataBase.db_get_connection();
		schema currentSchema = connection.db_get_current_schema();
		tables currentTables = currentSchema.get_tables();
		return currentTables.get_table(tableName);
	}
	Object[] getKeySpec(fn_args args, table table) {
		Vector<Object> columnSpec = new Vector<Object>(4);
		getSelectionColumns(args,columnSpec, table);
		return columnSpec.toArray();
	}

	//this method can be called for either String or float (= numeric) arguments
	//only add if we get a csticName and also a csticVal. Break as soon as either fails.
	void getSelectionColumns (fn_args args, Vector<Object> columnSpec, table table) {
		for (int index = 1; index < 6;index++) {
			String csticName = getStringFromArgs(args, formNameCsticName(index));
			if (csticName == null)   break;  // no such csticName
			
			binding csticVal = getBindingFromArgs(args, formValCsticName("S", index));
			if (csticVal == null) csticVal = getBindingFromArgs(args, formValCsticName("F", index));
			if (csticVal == null)   break;  // no such binding. Exit	
			
			csticVal = coerceKeyValue(csticName,csticVal,table);
			columnSpec.add(csticName);
			columnSpec.add(csticVal); // csticVal is a String or a float_value
		}	
	}
	
	binding coerceKeyValue(String columnName, binding csticVal, table table) {
		int columnType = getColumnType(table, columnName);
		if (csticVal instanceof symbol_value) {
			switch (columnType) { // if it is already a string, no need to convert
			case type_info.FLOAT:
			case type_info.INTEGER:
			case type_info.DOUBLE:
			case type_info.NUMERIC:
				try { return float_value_imp.get_float_value(Double.parseDouble(((symbol_value)csticVal).toString())); }
				catch (Exception exc) { return csticVal; }
			}
		}
		if (csticVal instanceof float_value) {		
			switch (columnType) { // no need to convert unless we have a string
			case type_info.NCHAR:
			case type_info.NVARCHAR:
			case type_info.VARCHAR:
				return symbol_value_imp.get_symbol_value(((float_value)csticVal).toString());
			}
		}
		return csticVal;  // no coercion necessary or possible. 
	}
		
	int getColumnType (table currentTable, String columnName) {
		columns currentColumns = currentTable.get_columns();
		column currentColumn = currentColumns.get_column(columnName);
		type_info currentTypeInfo = currentColumn.get_data_type();
		return currentTypeInfo.get_data_type();
	}

	sys_query_pair[] getQueryPairs (Object[]keySpec) {
		sys_query_pair[] queryPairs = new sys_query_pair[keySpec.length/2];
		int specIndex = 0;
		for  (int keyIndex    = 0; keyIndex < keySpec.length;) { //the array contains pairs of key,value
			String keyName    = (String)keySpec[keyIndex++];
			binding keyValue  = (binding)keySpec[keyIndex++];
			if (keyValue instanceof symbol_value)
				queryPairs[specIndex++] =  new sys_query_pair(keyName, ((symbol_value)keyValue).to_ext_string());
			else if (keyValue instanceof float_value)
				queryPairs[specIndex++] =  new sys_query_pair(keyName, ((float_value)keyValue).util_get_value_as_float());
		}
		return queryPairs;
	}

	String[] getProjection(fn_args args) {
		String projectionFieldName = getProjectionFieldName(args);
		if (projectionFieldName == null)
			return null;
		return new String[]{projectionFieldName};
	}

	String getProjectionFieldName (fn_args args) {
		return getStringFromArgs(args, tableOutputCsticName);
	}

	String formNameCsticName (int index) {  return String.format("%s_%d", tableInputCsticNameBase, index); }
	String formValCsticName (String type, int index) {   return String.format("%s_%s_%d",  tableInputCsticValBase, type, index); }
	///////////   output the results to the output cstic
	void setOutputValues (
			fn_args args,res result,ddbc_inst outputInst,
			explc_owner userOwner,tmsc_trigger trigger,int jtype) {
		if (result.db_eof_p())    return;
		kb_cstic outputCstic = getOutputCstic(args, outputInst);
		if (outputCstic == null)    return;
		bdt_type descriptor = null;
		try { // not handling non-bdt cstics. Should we try to interpret numbers or strings as ADT IDs?
			descriptor = (bdt_type)outputCstic.kb_get_type_descriptor();
			try { // if we get an exception, jump out and give up.
				do {
					bdt_value rowVal = getOutputValue(result,descriptor);
					// How should we respond to null: continue, break, or throw exception?
					if (rowVal == null)   continue; // decided on continue
					outputInst.ddb_set_val(outputCstic,rowVal,userOwner,trigger,jtype);
				} while (result.db_next_row_p());
			}
			catch (IllegalArgumentException iaExc) {}
			catch (exc_database_error excDB) {}
		}
		catch (ClassCastException ccExc) { } 
	}

	kb_cstic getOutputCstic(fn_args args, ddbc_inst outputInst) {
		oo_class instType = (oo_class)outputInst.ddb_get_inst_type();
		if (instType == null) return null;

		String outputCsticName = getOutputCsticName(args);
		if (outputCsticName == null)   return null;
		return instType.kb_has_cstic_p(outputCsticName);
	}
	
	String getOutputCsticName (fn_args args) {
		return getStringFromArgs(args, outputCsticCsticName);
	}

	// assumption: we are projecting exactly one column, so the column number is always 0.
	bdt_value getOutputValue(res result,bdt_type descriptor) {
		int colType = result.db_get_col_type(0);
		int descriptorTypeCode = descriptor.get_type_code();
		switch (descriptorTypeCode) {
		case bdt_type.C_FLOAT_TYPE_DESCRIPTOR:
		case bdt_type.C_INTEGER_TYPE_DESCRIPTOR:
			switch (colType) {
			case res.C_FLOAT_TYPE:
			case res.C_INTEGER_TYPE:
				return  descriptor.bdt_value_from_db(result, 0);
			case res.C_NCHAR_TYPE:
			case res.C_VARCHAR_TYPE:
			case res.C_NVARCHAR_TYPE:
				return float_value_imp.get_float_value(result.db_get_row_element_string(0));
			}
		case bdt_type.C_STRING_TYPE_DESCRIPTOR:
			return symbol_value_imp.get_symbol_value(result.db_get_row_element_as_string(0));
		}
		return  descriptor.bdt_value_from_db(result, 0);
	}
	
	//////////////  utils for getting bindings
	float_value getFloatValue (int intVal) {
		return float_value_imp.get_float_value(new Double(intVal));
	}
	float_value getFloatValueDouble (Double doubleVal) {
		return float_value_imp.get_float_value(doubleVal);
	}

	//////////////  Utils for accessing the args
	//returns a symbol_value or a float_value. We don't care which.
	binding getBindingFromArgs (fn_args args, String csticName) {
		kb_class_query cq = args.find(csticName);
		if (cq == null)
			return null;
		return  cq.kb_get_binding();
	}

	ddbc_inst getInstFromArgs (fn_args args, String adtCsticName) {
		kb_class_query self_cq = args.find(adtCsticName);
		if (self_cq == null) return null;

		return (ddbc_inst) self_cq.kb_get_binding();
	}

	String getStringFromArgs (fn_args args, String csticName) {
		kb_class_query str_cq = args.find(csticName);
		if (str_cq == null)  return null;

		binding str_bind = str_cq.kb_get_binding();
		if (str_bind == null)        return null;

		return ((symbol_value)str_bind).toString();
	}
}
