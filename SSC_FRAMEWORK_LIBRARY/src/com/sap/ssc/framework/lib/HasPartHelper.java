package com.sap.ssc.framework.lib;

import com.sap.custdev.projects.fbs.slc.util.ExceptionUtil;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.ddb.ddbc_p_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb_class_query;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_decomp;
import com.sap.sce.kbrt.kb_decomp_item;
import com.sap.sce.kbrt.oo_class;
import com.sap.sce.user.fn_args;
import com.sap.sce.user.sce_user_fn;
import com.sap.sce.user.sce_user_fn_logging;
import com.sap.sxe.sys.read_only_sequence;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.util.bdt_type;
import com.sap.sxe.util.cstic_type_descriptor;
import com.sap.sxe.util.imp.float_value_imp;
import com.sap.sxe.util.imp.symbol_value_imp;

public class HasPartHelper {
	private static int C_CLASS_TYPE_DESCRIPTOR = 0;
	
	private static String parentInstanceCsticName = "BOM_PARENT_INSTANCE";
	private static String positionNumberCsticName = "BOM_POSITION_NUMBER";
	
	private static String[] identifyingCsticName = new String[]{
		"IDENTIFYING_CSTIC_NAME_1", "IDENTIFYING_CSTIC_NAME_2", "IDENTIFYING_CSTIC_NAME_3", "IDENTIFYING_CSTIC_NAME_4", "IDENTIFYING_CSTIC_NAME_5"
	};
	
	private static String[] identifyingCsticValSymbol = new String[]{
		"IDENTIFYING_CSTIC_VAL_S_1" , "IDENTIFYING_CSTIC_VAL_S_2", "IDENTIFYING_CSTIC_VAL_S_3", "IDENTIFYING_CSTIC_VAL_S_4", "IDENTIFYING_CSTIC_VAL_S_5" 
	};
	
	private static String[] identifyingCsticValFloat = new String[]{
		"IDENTIFYING_CSTIC_VAL_F_1", "IDENTIFYING_CSTIC_VAL_F_2", "IDENTIFYING_CSTIC_VAL_F_3", "IDENTIFYING_CSTIC_VAL_F_4", "IDENTIFYING_CSTIC_VAL_F_5" 		
	};
	
	private static String[] identifyingCsticValADT = new String []{
		"IDENTIFYING_CSTIC_VAL_A_1", "IDENTIFYING_CSTIC_VAL_A_2", "IDENTIFYING_CSTIC_VAL_A_3", "IDENTIFYING_CSTIC_VAL_A_4", "IDENTIFYING_CSTIC_VAL_A_5" 				
	};
	
	private static String referencingInstanceCsticName = "REFERENCING_INSTANCE";
	private static String referencingCsticName = "REFERENCING_CSTIC_NAME";	
	
	HasPartHelper () {}
	
	public static ddbc_p_inst getBomParentInstance(sce_user_fn pfunction, fn_args args) throws RuntimeException {
		kb_class_query bomParentInstanceArg = args.find(parentInstanceCsticName);
		if (bomParentInstanceArg == null){
			new sce_user_fn_logging().writeLogError(pfunction, parentInstanceCsticName + " not specified");
			throw new RuntimeException(parentInstanceCsticName + " not specified");
		}
		ddbc_p_inst bomParentInstance = null;
		try {bomParentInstance = (ddbc_p_inst)bomParentInstanceArg.kb_get_binding();}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(parentInstanceCsticName + " not found or incorrect", e);			
		}
		return bomParentInstance;
	}
	
	public static String getBomPositionNumber(sce_user_fn pfunction, fn_args args) throws RuntimeException {
		String bomPositionNumber = args.get_value(positionNumberCsticName);
		if (bomPositionNumber == null){
			new sce_user_fn_logging().writeLogError(pfunction, positionNumberCsticName + " not specified");
			throw new RuntimeException(positionNumberCsticName + " not specified");			
		}
		return bomPositionNumber;
	}
	
	public static kb_decomp_item getDecompItem(sce_user_fn pfunction, ddbc_p_inst parentInstance, String positionNumber) throws RuntimeException {
		kb_decomp bomDecomp = parentInstance.ddb_get_decomp();
		if (bomDecomp == null){
			new sce_user_fn_logging().writeLogError(pfunction, parentInstance + " has no BOM");
			throw new RuntimeException("Instance [" + parentInstance + "] has no BOM");			
		}
		kb_decomp_item bomDecompItem = bomDecomp.kb_find_decomp_item(positionNumber);
		if (bomDecompItem == null){
			new sce_user_fn_logging().writeLogError(pfunction, "Instance [" + parentInstance + "] has no position at [" + positionNumber + "]");
			throw new RuntimeException("Instance [" + parentInstance + "] has no position at [" + positionNumber + "]");				
		}
		return bomDecompItem;
	}
	
	public static  kb_cstic getBaseIdentifyingCstic(sce_user_fn pfunction, kb_decomp_item bomDecompItem, fn_args args) throws RuntimeException {
		String identifyingCsticNameString = args.get_value(identifyingCsticName[0]);
		if (identifyingCsticNameString == null){
			new sce_user_fn_logging().writeLogError(pfunction, identifyingCsticName[0] + " not specified");
			throw new RuntimeException(identifyingCsticName[0] + " not specified");			
		}
		kb_cstic identifyingCstic = bomDecompItem.kb_get_type().kb_has_cstic_p(identifyingCsticNameString);
		if (identifyingCstic == null) {
			new sce_user_fn_logging().writeLogError(pfunction, "Cstic [" + identifyingCsticNameString + "] not on DecompItem [" + bomDecompItem + "]");
			throw new RuntimeException("Cstic [" + identifyingCsticNameString + "] not on DecompItem [" + bomDecompItem + "]");			
		}
		return identifyingCstic;
	}
	
	public static ddbc_inst findOrCreatePartInstance(sce_user_fn pfunction, ddbc_p_inst bomParentInstance, kb_decomp_item decompItem, read_only_sequence csticValuePairs, explc_owner ruleOwner, tmsc_trigger trigger, int jtype) throws RuntimeException {
		ddbc_inst bomChildInstance = null;
		try {bomChildInstance = (ddbc_inst)bomParentInstance.ddb_find_or_create_part_inst(decompItem, csticValuePairs, ruleOwner, trigger, jtype);}
		catch (Exception e){
			String rootCauseMessage = getExceptionRootCause(e);
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed find or create on Instance [" + bomParentInstance + "] at position [" + decompItem + "]" + rootCauseMessage, e);				
		}
		return bomChildInstance;
	}
	
	public static ddbc_inst getReferencingInstance(sce_user_fn pfunction, fn_args args) throws RuntimeException {
		kb_class_query referencingInstanceArg = args.find(referencingInstanceCsticName);
		if (referencingInstanceArg == null) return null;
		ddbc_inst referencingInstance = null;
		try {referencingInstance = (ddbc_inst)referencingInstanceArg.kb_get_binding();}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(referencingInstanceCsticName + " not found or incorrect", e);
		}
		return referencingInstance;
	}
	
	public static kb_cstic getReferencingCstic(sce_user_fn pfunction, fn_args args, ddbc_inst referencingInstance) throws RuntimeException {
		String referencingCsticString = args.get_value(referencingCsticName);
		if (referencingCsticString == null) {
			new sce_user_fn_logging().writeLogError(pfunction, referencingCsticName + " not specified");
			throw new RuntimeException(referencingCsticName + " not specified");
		}		
		kb_cstic referencingCstic = referencingInstance.ddb_get_inst_cstic(referencingCsticString);
		if (referencingCstic == null) {
			new sce_user_fn_logging().writeLogError(pfunction, "Cstic [" + referencingCstic + "] not on Instance [" + referencingInstance + "]");
			throw new RuntimeException("Cstic [" + referencingCstic + "] not on Instance [" + referencingInstance + "]");
		}
		return referencingCstic;
	}
	
	public static void setReferencingCsticValue(sce_user_fn pfunction, ddbc_inst referencingInstance, kb_cstic referencingCstic, ddbc_inst bomChildInstance, explc_owner ruleOwner, tmsc_trigger trigger, int jtype) throws RuntimeException {
		try {referencingInstance.ddb_set_val(referencingCstic, bomChildInstance, ruleOwner, trigger, jtype);}
		catch (Exception e) {
			String rootCauseMessage = getExceptionRootCause(e);
			new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to set value [" + bomChildInstance + "] for cstic [" + referencingCstic + "] on Instance [" + referencingInstance + "]" + rootCauseMessage, e);				
		}
	}
	
	public static kb_class_query getCsticValuePair(sce_user_fn pfunction, fn_args args, kb_decomp_item bomDecompItem, int index) throws RuntimeException {
		kb_class_query csticValuePair = null;
		
		String iCsticValStringSymbol = null; String iCsticValStringFloat = null;
		symbol_value_imp iValueSymbol = null; float_value_imp iValueFloat = null; ddbc_inst iValueDDBCInst = null;
		cstic_type_descriptor typeDescriptor = null; int typeCode = 0;
		
		String iCsticName = args.get_value(identifyingCsticName[index]);
		if (iCsticName == null) return csticValuePair;  // no such csticName: don't bother trying to get its cstic or its cstic's corresponding Value
		
		kb_cstic iCstic = bomDecompItem.kb_get_type().kb_has_cstic_p(iCsticName);			
		if (iCstic == null){
			new sce_user_fn_logging().writeLogError(pfunction, bomDecompItem.kb_get_type() + " has no such chracteristic as " + iCsticName);
			throw new RuntimeException(bomDecompItem.kb_get_type() + " has no such chracteristic as " + iCsticName);					
		}
		
		typeDescriptor = iCstic.kb_get_type_descriptor();
		
		if (typeDescriptor instanceof oo_class){typeCode = C_CLASS_TYPE_DESCRIPTOR;}
		if (typeDescriptor instanceof bdt_type){typeCode = ((bdt_type)typeDescriptor).get_type_code();}
		
		if (typeCode == C_CLASS_TYPE_DESCRIPTOR){
			iValueDDBCInst = getIdentifyingCsticValADTInstance(pfunction, args, index);
			csticValuePair = new kb_class_query(iCstic, iValueDDBCInst);	
		}		
		if (typeCode == bdt_type.C_FLOAT_TYPE_DESCRIPTOR){
			iCsticValStringFloat = args.get_value(identifyingCsticValFloat[index]);
			if (iCsticValStringFloat == null){
				new sce_user_fn_logging().writeLogError(pfunction, identifyingCsticValFloat[index] + " not specified");
				throw new RuntimeException(identifyingCsticValFloat[index] + " not specified");						
			}
			iValueFloat = float_value_imp.get_float_value(iCsticValStringFloat);
			csticValuePair = new kb_class_query(iCstic, iValueFloat);				
		}
		if (typeCode == bdt_type.C_STRING_TYPE_DESCRIPTOR){
			iCsticValStringSymbol = args.get_value(identifyingCsticValSymbol[index]);
			if (iCsticValStringSymbol == null){
				new sce_user_fn_logging().writeLogError(pfunction, identifyingCsticValSymbol[index] + " not specified");
				throw new RuntimeException(identifyingCsticValSymbol[index] + " not specified");						
			}
			iValueSymbol = symbol_value_imp.get_symbol_value(iCsticValStringSymbol);
			csticValuePair = new kb_class_query(iCstic, iValueSymbol);
		}
		return csticValuePair;
	}
	
	public static ddbc_inst getIdentifyingCsticValADTInstance(sce_user_fn pfunction, fn_args args, int index) throws RuntimeException {
		kb_class_query identifyingCsticValADTArg = args.find(identifyingCsticValADT[index]);
		if (identifyingCsticValADTArg == null){
			new sce_user_fn_logging().writeLogError(pfunction, identifyingCsticValADT[index] + " not specified");
			throw new RuntimeException(identifyingCsticValADT[index] + " not specified");
		}
		ddbc_inst identifyingCsticValADTInstance = null;
		try {identifyingCsticValADTInstance = (ddbc_inst)identifyingCsticValADTArg.kb_get_binding();}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(identifyingCsticValADT[index] + " not found or incorrect", e);			
		}
		return identifyingCsticValADTInstance;		
	}
	
	private static String getExceptionRootCause (Exception e) {
		Throwable rootCause = ExceptionUtil.getRootCause(e); if (rootCause == null){return null;}
		String rootMessage = rootCause.getMessage(); if (rootMessage == null) return " due to Unknown Exception";
		return " due to underlying " + rootMessage;		
	}
}
