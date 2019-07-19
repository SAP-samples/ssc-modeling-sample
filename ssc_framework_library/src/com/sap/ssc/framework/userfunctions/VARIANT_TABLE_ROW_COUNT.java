
package com.sap.ssc.framework.userfunctions;

import java.util.Vector;
import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_class_query;
import com.sap.sce.kbrt.kb_class_query_seq;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_query_table_result;
import com.sap.sce.kbrt.kb_table;
import com.sap.sce.kbrt.oo_class;
import com.sap.sce.kbrt.imp.kb_cstic_seq_imp;
//import com.sap.sce.kbrt.imp.kb_imp;
import com.sap.sce.kbrt.imp.kb_table_access_imp;
import com.sap.sce.kbrt.imp.kb_table_imp;
import com.sap.sce.user.fn_args;
import com.sap.sce.user.fn_args_deluxe;
import com.sap.sce.user.sce_user_fn;
import com.sap.sxe.db.res;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.tms.imp.tmse_jtype;
import com.sap.sxe.util.bdt_type;
import com.sap.sxe.util.binding;
import com.sap.sxe.util.cstic_type_descriptor;
import com.sap.sxe.util.cstic_value;
import com.sap.sxe.util.float_value;
import com.sap.sxe.util.symbol_value;
import com.sap.sxe.util.imp.float_value_imp;
import com.sap.sxe.util.imp.symbol_value_imp;

public class VARIANT_TABLE_ROW_COUNT implements sce_user_fn{
	private static final long serialVersionUID = 1;
	private static String tableNameCsticName      = "TABLE_NAME";
	private static String tableInputCsticNameBase = "TABLE_INPUT_CSTIC_NAME";
	private static String tableInputCsticValBase  = "TABLE_INPUT_CSTIC_VAL";
	private static String tableOutputCsticName    = "TABLE_OUTPUT_CSTIC_NAME";
	private static String outputInstCsticName     = "OUTPUT_INSTANCE";
	private static String outputCsticCsticName    = "OUTPUT_INSTANCE_CSTIC_NAME";
	private static int noCsticTypeFound           = -999999;

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
		
		kb_table table = getTable(args, kb, tableNameCsticName); //Get reference to the table
		if (table == null)   return true;	

		kb_query_table_result result = getVtableResultSet(args, kb, table);
		if (result == null)  return true;

		setRowCount (args,result,outputInst,userOwner,trigger,jtype);
		return true;
	}
	/////////////   finding the table
	kb_table getTable (fn_args args, kb kb, String tableNameCsticName) {
		String tableName = getStringFromArgs(args, tableNameCsticName);
		if (tableName == null)  return null;

		return kb.kb_get_table(tableName); //Get reference to the table
	}

	//////////// performing the lookup
	kb_query_table_result getVtableResultSet(fn_args args, kb kb, kb_table table) {
		Object[] keySpec = getKeySpec(args, table);
		kb_class_query_seq query_seq  = getQuerySeq(keySpec,table);
		kb_cstic_seq_imp projection = getProjection(args, table);
		kb_table_access_imp kbtableimp = kb_table_access_imp.kb_get_table_access((kb_table_imp)table);
		return kbtableimp.kb_query(query_seq, projection);
	}

	Object[] getKeySpec(fn_args args, kb_table table) {
		Vector<Object> columnSpec = new Vector<Object>(4);
		getSelectionColumns(args,columnSpec, table);
		return columnSpec.toArray();
	}
	
	//this method can be called for either String or float (= numeric) arguments
	//we expect to find and add pairs of cstic name + cstic val.
	// Break as soon as either fails.
	void getSelectionColumns (fn_args args, Vector<Object> columnSpec, kb_table table) {
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

	String formNameCsticName (int index) {  return String.format("%s_%d", tableInputCsticNameBase, index); }
	String formValCsticName (String type, int index) {   return String.format("%s_%s_%d",  tableInputCsticValBase, type, index); }

	kb_class_query_seq getQuerySeq (Object[]keySpec, kb_table tableObj) {
		kb_class_query_seq querySeq  = kb_class_query_seq.C_EMPTY;
		for  (int keyIndex = 0; keyIndex < keySpec.length;) { //the array contains pairs of key,value
			String keyName = (String)keySpec[keyIndex++];
			binding keyVal   = (binding)keySpec[keyIndex++];
			kb_cstic keyCstic = tableObj.kb_has_cstic_p(keyName); //the key column must have a valid reference
			if (keyCstic == null)   continue; // go on to the next

			kb_class_query query = getClassQuery(keyCstic, keyVal); // Add a new the query of the form: KeyColumn, keyValue
			querySeq = new kb_class_query_seq (query, querySeq); //Append the class query to the existing seq
		}
		return querySeq;
	}

	kb_cstic_seq_imp getProjection(fn_args args, kb_table tableObj) {
		String projectionFieldName = getProjectionFieldName(args);
		if (projectionFieldName == null)
			return new kb_cstic_seq_imp(0);
		kb_cstic_seq_imp projection = new kb_cstic_seq_imp(1);
		kb_cstic projectionCstic = tableObj.kb_has_cstic_p(projectionFieldName);
		projection.push(projectionFieldName,projectionCstic);
		return projection;
	}

	String getProjectionFieldName (fn_args args) {
		return getStringFromArgs(args, tableOutputCsticName);
	}
	//////////////  For getting the row count
	void setRowCount (
			fn_args args,Object result,ddbc_inst outputInst,
			explc_owner userOwner,tmsc_trigger trigger,int jtype) {
		kb_cstic rowCountCstic = getOutputCstic(args, outputInst);
		if (rowCountCstic == null)     return;

		cstic_value rowCountVal = getCsticValueFromValAndType(getRowCount(result), rowCountCstic);
		outputInst.ddb_set_val(rowCountCstic,rowCountVal,userOwner,trigger,jtype);
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

	int getRowCount(Object result) {
		if (result instanceof kb_query_table_result)
			return ((kb_query_table_result)result).kb_get_row_count();
		if (result instanceof res) {
			res resResult = (res)result;
			if (resResult.db_eof_p())
				return 0;
			int rowCount = 0;
			do {  rowCount++; }  while (resResult.db_next_row_p());
			return rowCount;
		}
		return 0;
	}

	/////////////////////// value type coercion and conversion
	binding coerceKeyValue(String columnName, binding csticVal, kb_table table) {
		int keyType = getColumnType(table, columnName);
		if (keyType == noCsticTypeFound)   return csticVal;
		if (csticVal instanceof symbol_value && keyType == bdt_type.C_FLOAT_TYPE_DESCRIPTOR)
				try { return float_value_imp.get_float_value(Double.parseDouble(((symbol_value)csticVal).toString())); }
				catch (Exception exc) { return csticVal; }
		if (csticVal instanceof float_value && keyType == bdt_type.C_STRING_TYPE_DESCRIPTOR)	
				return symbol_value_imp.get_symbol_value(((float_value)csticVal).toString());
		return csticVal;  // no coercion
	}
	int getColumnType (kb_table currentTable, String columnName) {
		kb_cstic columnCstic =  currentTable.kb_has_cstic_p(columnName);
		if (columnCstic == null)
			return noCsticTypeFound;
		cstic_type_descriptor typeDescriptor = columnCstic.kb_get_type_descriptor();
		if (typeDescriptor instanceof bdt_type)
			return ((bdt_type)typeDescriptor).get_type_code();
		return noCsticTypeFound;
	}
	// We have been passed an int or a float value.
	// We will return the appropriate cstic_value for that input
	cstic_value getCsticValueFromValAndType(double value, kb_cstic cstic) {
		try {
			switch (((bdt_type)cstic.kb_get_type_descriptor()).get_type_code()) {
				case bdt_type.C_FLOAT_TYPE_DESCRIPTOR:
				case bdt_type.C_INTEGER_TYPE_DESCRIPTOR:
					return  float_value_imp.get_float_value(value);
				case bdt_type.C_STRING_TYPE_DESCRIPTOR:
					return symbol_value_imp.get_symbol_value(Double.toString(value));
				default: return null;
			}
		}
		catch (ClassCastException ccExc){ return null; }
	}
	//////////////  Utils for accessing the args
	//returns a symbol_value or a float_value. We don't care which.
	binding getBindingFromArgs (fn_args args, String csticName) {
		kb_class_query cq = args.find(csticName);
		if (cq == null)
			return null;
		return  cq.kb_get_binding();
	}

	kb_class_query getClassQuery (kb_cstic keyCstic, Object keyValue) {
		if (keyValue instanceof symbol_value)
			return new kb_class_query(keyCstic, (symbol_value)keyValue);
		if (keyValue instanceof float_value)
			return new kb_class_query(keyCstic, (float_value)keyValue);
		return null;
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
