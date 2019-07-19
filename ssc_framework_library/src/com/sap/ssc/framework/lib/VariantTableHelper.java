package com.sap.ssc.framework.lib;

import java.util.Vector;

import com.sap.custdev.projects.fbs.slc.util.ExceptionUtil;
import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_class_query;
import com.sap.sce.kbrt.kb_class_query_seq;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_query_table_result;
import com.sap.sce.kbrt.kb_table;
import com.sap.sce.kbrt.imp.kb_cstic_seq_imp;
import com.sap.sce.kbrt.imp.kb_table_access_imp;
import com.sap.sce.kbrt.imp.kb_table_imp;
import com.sap.sce.user.fn_args;
import com.sap.sce.user.sce_user_fn;
import com.sap.sce.user.sce_user_fn_logging;
import com.sap.sxe.util.bdt_type;
import com.sap.sxe.util.binding;
import com.sap.sxe.util.cstic_type_descriptor;
import com.sap.sxe.util.cstic_value;
import com.sap.sxe.util.float_value;
import com.sap.sxe.util.symbol_value;
import com.sap.sxe.util.imp.float_value_imp;
import com.sap.sxe.util.imp.symbol_value_imp;

public final class VariantTableHelper {
	private static String tableNameCsticName = "TABLE_NAME";
	private static String tableInputCsticNameBase = "TABLE_INPUT_CSTIC_NAME";
	private static String tableInputCsticValBase = "TABLE_INPUT_CSTIC_VAL";
	private static String tableOutputCsticName = "TABLE_OUTPUT_CSTIC_NAME";
	private static String lookupTypeCsticName = "TABLE_LOOKUP_TYPE";
	private static String multiValueLookupType = "MULTIPLE_VALUE";
	private static String outputInstCsticName = "OUTPUT_INSTANCE";
	private static String outputCsticCsticName = "OUTPUT_INSTANCE_CSTIC_NAME";
	private static String tableOutputSortCsticName = "TABLE_OUTPUT_SORT_CSTIC_NAME";	
	private static int noCsticTypeFound           = -999999;
	
	VariantTableHelper () {}
	
	public static ddbc_inst getOutputInstance (sce_user_fn pfunction, fn_args args) throws RuntimeException {
		kb_class_query outputInstanceArg = args.find(outputInstCsticName);
		if (outputInstanceArg == null){
			new sce_user_fn_logging().writeLogError(pfunction, outputInstCsticName + " not specified");
			throw new RuntimeException(outputInstCsticName + " not specified");
		}
		ddbc_inst outputInstance = null;
		try {outputInstance = (ddbc_inst)outputInstanceArg.kb_get_binding();}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(outputInstCsticName + " not found or incorrect", e);			
		}
		return outputInstance;		
	}
	
	public static kb_cstic getOutputCstic(sce_user_fn pfunction, fn_args args, ddbc_inst outputInstance) throws RuntimeException {
		String outputCsticName = args.get_value(outputCsticCsticName);
		if (outputCsticName == null){
			new sce_user_fn_logging().writeLogError(pfunction, outputCsticCsticName + " not specified");
			throw new RuntimeException(outputCsticCsticName + " not specified");
		}			
		kb_cstic outputCstic = outputInstance.ddb_get_inst_type().kb_has_cstic_p(outputCsticName);
		if (outputCstic == null){
			new sce_user_fn_logging().writeLogError(pfunction, "Output Instance [" + outputInstance + "] has no Cstic [" + outputCsticName + "]");
			throw new RuntimeException("Output Instance [" + outputInstance + "] has no Cstic [" + outputCsticName + "]");			
		}
		return outputCstic;
	}
	
	public static String getLookupType(sce_user_fn pfunction, fn_args args) throws RuntimeException {
		String inputLookupType = args.get_value(lookupTypeCsticName);
		if (inputLookupType == null) return multiValueLookupType;
		if (!"MULTIPLE_VALUE".equals(inputLookupType) && !"DOMAIN_RESTRICTION".equals(inputLookupType)){
			new sce_user_fn_logging().writeLogError(pfunction, lookupTypeCsticName + " incorrect");
			throw new RuntimeException(lookupTypeCsticName + " incorrect");				
		}		
		return inputLookupType;
	}
	
	public static kb_table getVariantTable (sce_user_fn pfunction, fn_args args, kb kb) throws RuntimeException {
		String variantTableName = args.get_value(tableNameCsticName);
		if (variantTableName == null){
			new sce_user_fn_logging().writeLogError(pfunction, tableNameCsticName + " not specified");
			throw new RuntimeException(tableNameCsticName + " not specified");
		}		
		kb_table variantTable = null;
		try {variantTable = kb.kb_get_table(variantTableName);}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(variantTableName + " not found or incorrect", e);				
		}
		if (variantTable == null){
			new sce_user_fn_logging().writeLogError(pfunction, tableNameCsticName + " not found");
			throw new RuntimeException(variantTableName + " not found");	
		}
		return variantTable;
	}
	
	public static kb_query_table_result getVtableResultSet(sce_user_fn pfunction, fn_args args, cfg_imp cfg, kb_table table) {
		Object[] keySpec = getKeySpecification(pfunction, args, table);
		kb_class_query_seq query_seq  = getQuerySequence(pfunction, keySpec,table);
		kb_cstic_seq_imp projection = getProjection(pfunction, args, table);
		kb_table_access_imp kbtableimp = kb_table_access_imp.kb_get_table_access(cfg, (kb_table_imp)table);
		
		kb_query_table_result result = null;
		
		try {result = kbtableimp.kb_query(query_seq, projection);}
		catch (Exception e) {
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to retrieve result for projection [" + projection + "] and query [" + query_seq + "] on variantTable [" + table + "]", e);				
		}		
		return result;
	}

	public static Object[] getKeySpecification(sce_user_fn pfunction, fn_args args, kb_table table) throws RuntimeException {
		Vector<Object> columnSpec = new Vector<Object>(9);

		for (int index = 1; index < 11;index++) {
			String csticName = getStringFromArgs(args, formNameCsticName(index));
			if (csticName == null)   continue;  // no such csticName
			
			binding csticVal = getBindingFromArgs(args, formValCsticName("S", index));
			if (csticVal == null) csticVal = getBindingFromArgs(args, formValCsticName("F", index));
			if (csticVal == null){
				new sce_user_fn_logging().writeLogError(pfunction, formNameCsticName(index) + " has no value");
				throw new RuntimeException(formNameCsticName(index) + " has no value");
			}			
			csticVal = coerceKeyValue(csticName,csticVal,table);
			columnSpec.add(csticName);
			columnSpec.add(csticVal); // csticVal is a String or a float_value
		}				
		return columnSpec.toArray();
	}

	static String formNameCsticName (int index) {  return String.format("%s_%d", tableInputCsticNameBase, index); }
	static String formValCsticName (String type, int index) {   return String.format("%s_%s_%d",  tableInputCsticValBase, type, index); }

	public static kb_class_query_seq getQuerySequence (sce_user_fn pfunction, Object[]keySpec, kb_table tableObj) throws RuntimeException {
		kb_class_query_seq querySeq  = kb_class_query_seq.C_EMPTY;
		for  (int keyIndex = 0; keyIndex < keySpec.length;) { //the array contains pairs of key,value
			String keyName = (String)keySpec[keyIndex++];
			binding keyVal   = (binding)keySpec[keyIndex++];
			kb_cstic keyCstic = tableObj.kb_has_cstic_p(keyName); //the key column must have a valid reference
			if (keyCstic == null){
				new sce_user_fn_logging().writeLogError(pfunction, "Variant Table [" + tableObj + "] has no column [" + keyName + "]");
				throw new RuntimeException("Variant Table [" + tableObj + "] has no column [" + keyName + "]");
			}
			kb_class_query query = getClassQuery(keyCstic, keyVal); // Add a new the query of the form: KeyColumn, keyValue
			querySeq = new kb_class_query_seq (query, querySeq); //Append the class query to the existing seq
		}
		return querySeq;
	}
	
	public static kb_cstic_seq_imp getProjection(sce_user_fn pfunction, fn_args args, kb_table tableObj){
		kb_cstic_seq_imp projection = null;
		if (pfunction.getClass().getName().equals("com.sap.sce.user.VARIANT_TABLE_ROW_COUNT") ||
			pfunction.getClass().getName().equals("com.sap.sce.user.VARIANT_TABLE_LOOKUP")){
			projection = getProjectionUnsorted(pfunction, args, tableObj);
		}
		if (pfunction.getClass().getName().equals("com.sap.sce.user.HAS_PART_VARIANT_TABLE")){
			projection = getProjectionSorted(pfunction, args, tableObj);			
		}
		return projection;
	}

	public static kb_cstic_seq_imp getProjectionUnsorted(sce_user_fn pfunction, fn_args args, kb_table tableObj) throws RuntimeException {
		String projectionFieldName = args.get_value(tableOutputCsticName);
		if (projectionFieldName == null){
			if (pfunction.getClass().getName().equals("com.sap.sce.user.VARIANT_TABLE_ROW_COUNT")){
				return new kb_cstic_seq_imp(0);
			}
			if (pfunction.getClass().getName().equals("com.sap.sce.user.VARIANT_TABLE_LOOKUP")){
				new sce_user_fn_logging().writeLogError(pfunction, tableOutputCsticName = " not specified");
				throw new RuntimeException(tableOutputCsticName = " not specified");
			}
		}
		kb_cstic_seq_imp projection = new kb_cstic_seq_imp(1);
		kb_cstic projectionCstic = tableObj.kb_has_cstic_p(projectionFieldName);
		if (projectionCstic == null){
			new sce_user_fn_logging().writeLogError(pfunction, "Variant Table [" + tableObj + "] has no column [" + projectionCstic + "]");
			throw new RuntimeException("Variant Table [" + tableObj + "] has no column [" + projectionCstic + "]");
		}
		projection.push(projectionFieldName,projectionCstic);
		return projection;
	}
	
	public static kb_cstic_seq_imp getProjectionSorted(sce_user_fn pfunction, fn_args args, kb_table tableObj) {
		kb_cstic_seq_imp projection = null;
		String projectionFieldName = args.get_value(tableOutputCsticName);
		if (projectionFieldName == null){
			new sce_user_fn_logging().writeLogError(pfunction, tableOutputCsticName = " not specified");
			throw new RuntimeException(tableOutputCsticName = " not specified");			
		}
		String projectionSortFieldName = args.get_value(tableOutputSortCsticName);
		if (projectionSortFieldName != null) {
			projection = new kb_cstic_seq_imp(2);
			kb_cstic projectionSortCstic = tableObj.kb_has_cstic_p(projectionSortFieldName);
			if (projectionSortCstic == null){
				new sce_user_fn_logging().writeLogError(pfunction, "Variant Table [" + tableObj + "] has no column [" + projectionSortCstic + "]");
				throw new RuntimeException("Variant Table [" + tableObj + "] has no column [" + projectionSortCstic + "]");				
			}
			projection.push(projectionSortFieldName, projectionSortCstic);
		}
		if (projectionSortFieldName == null) {projection = new kb_cstic_seq_imp(1);}
		kb_cstic projectionCstic = tableObj.kb_has_cstic_p(projectionFieldName);
		if (projectionCstic == null){
			new sce_user_fn_logging().writeLogError(pfunction, "Variant Table [" + tableObj + "] has no column [" + projectionCstic + "]");
			throw new RuntimeException("Variant Table [" + tableObj + "] has no column [" + projectionCstic + "]");
		}		
		projection.push(projectionFieldName,projectionCstic);
		return projection;
	}
	
	static binding getBindingFromArgs (fn_args args, String csticName) {
		kb_class_query cq = args.find(csticName);
		if (cq == null)
			return null;
		return  cq.kb_get_binding();
	}

	static kb_class_query getClassQuery (kb_cstic keyCstic, Object keyValue) {
		if (keyValue instanceof symbol_value)
			return new kb_class_query(keyCstic, (symbol_value)keyValue);
		if (keyValue instanceof float_value)
			return new kb_class_query(keyCstic, (float_value)keyValue);
		return null;
	}

	static String getStringFromArgs (fn_args args, String csticName) {
		kb_class_query str_cq = args.find(csticName);
		if (str_cq == null) return null;

		binding str_bind = str_cq.kb_get_binding();
		if (str_bind == null) return null;

		return ((symbol_value)str_bind).toString();
	}
	
	static binding coerceKeyValue(String columnName, binding csticVal, kb_table table) {
		int keyType = getColumnType(table, columnName);
		if (keyType == noCsticTypeFound)   return csticVal;
		if (csticVal instanceof symbol_value && keyType == bdt_type.C_FLOAT_TYPE_DESCRIPTOR)
				try { return float_value_imp.get_float_value(Double.parseDouble(((symbol_value)csticVal).toString())); }
				catch (Exception exc) { return csticVal; }
		if (csticVal instanceof float_value && keyType == bdt_type.C_STRING_TYPE_DESCRIPTOR)	
				return symbol_value_imp.get_symbol_value(((float_value)csticVal).toString());
		return csticVal;  // no coercion
	}
	
	static int getColumnType (kb_table currentTable, String columnName) {
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
	static cstic_value getCsticValueFromValAndType(double value, kb_cstic cstic) {
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
		catch (Exception ccExc){
			getExceptionRootCause(ccExc);
			return null; }
	}
	private static String getExceptionRootCause (Exception e) {
		Throwable rootCause = ExceptionUtil.getRootCause(e); if (rootCause == null){return null;}
		String rootMessage = rootCause.getMessage(); if (rootMessage == null) return " due to Unknown Exception";
		return " due to underlying " + rootMessage;		
	}
}
