package com.sap.ssc.framework.lib;

import java.util.Enumeration;

import com.sap.custdev.projects.fbs.slc.util.ExceptionUtil;
import com.sap.sce.engine.ddb_inst;
import com.sap.sce.engine.expl_owner;
import com.sap.sce.engine.ddb.ddb_fact;
import com.sap.sce.engine.ddb.ddbc;
import com.sap.sce.engine.ddb.ddbc_facet;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_class_query;
import com.sap.sce.kbrt.kb_class_query_seq;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_cstic_kind;
import com.sap.sce.kbrt.object_type;
import com.sap.sce.kbrt.oo_class;
import com.sap.sce.kbrt.oo_class_key;
import com.sap.sce.user.fn_args;
import com.sap.sce.user.sce_user_fn;
import com.sap.sce.user.sce_user_fn_logging;
import com.sap.sce.user.scelib;
import com.sap.sxe.sys.seq.vector;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.util.bdt_type;
import com.sap.sxe.util.bdt_value;
import com.sap.sxe.util.binding;
import com.sap.sxe.util.cstic_type_descriptor;
import com.sap.sxe.util.cstic_value;
import com.sap.sxe.util.domain;
import com.sap.sxe.util.imp.float_value_imp;
import com.sap.sxe.util.imp.symbol_value_imp;

public final class GenericUserHelper {
	
	private static int C_CLASS_TYPE_DESCRIPTOR = 0;
	
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
	
	GenericUserHelper () {}
	
	public static ddbc_inst getInstanceFromArgs (sce_user_fn pfunction, fn_args args, String instanceIDCsticName) throws RuntimeException {
		kb_class_query instanceIDArg = args.find(instanceIDCsticName);
		if (instanceIDArg == null){
			new sce_user_fn_logging().writeLogError(pfunction, instanceIDCsticName + " not specified");
			throw new RuntimeException(instanceIDCsticName + " not specified");
		}
		ddbc_inst instanceFromArgs = null;
		try {instanceFromArgs = (ddbc_inst)instanceIDArg.kb_get_binding();}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(instanceIDCsticName + " not found or incorrect", e);			
		}
		return instanceFromArgs;		
	}
	
	public static kb_cstic getCsticFromArgs(sce_user_fn pfunction, fn_args args, String instanceCsticCsticName, ddbc_inst owningInstance) throws RuntimeException {
		String instanceCsticName = args.get_value(instanceCsticCsticName);
		if (instanceCsticName == null){
			new sce_user_fn_logging().writeLogError(pfunction, instanceCsticCsticName + " not specified");
			throw new RuntimeException(instanceCsticCsticName + " not specified");
		}			
		kb_cstic instanceCstic = owningInstance.ddb_get_inst_type().kb_has_cstic_p(instanceCsticName);
		if (instanceCstic == null){
			new sce_user_fn_logging().writeLogError(pfunction, "Instance [" + owningInstance + "] has no Cstic [" + instanceCsticName + "]");
			throw new RuntimeException("Instance [" + owningInstance + "] has no Cstic [" + instanceCsticName + "]");			
		}
		return instanceCstic;
	}
	
	public static float_value_imp getFloatValueBindingFromArgs(sce_user_fn pfunction, fn_args args, String bindingCsticName) throws RuntimeException {
		kb_class_query classQuery = args.find(bindingCsticName);
		binding csticValueNumeric = classQuery.kb_get_binding();
		float_value_imp floatValue = null;
		try {floatValue = (float_value_imp)csticValueNumeric;}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(bindingCsticName + " has incorrect type (not numeric)", e);	
		}
		return floatValue;
	}
	
	public static symbol_value_imp getSymbolValueBindingFromArgs(sce_user_fn pfunction, fn_args args, String bindingCsticName) throws RuntimeException {
		kb_class_query classQuery = args.find(bindingCsticName);
		binding csticValueSymbol = classQuery.kb_get_binding();
		symbol_value_imp symbolValue = null;
		try {symbolValue = (symbol_value_imp)csticValueSymbol;}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(bindingCsticName + " has incorrect type (not symbol)", e);	
		}
		return symbolValue;		
	}
	
	public static ddbc_inst getInstanceValueBindingFromArgs(sce_user_fn pfunction, fn_args args, String bindingCsticName) throws RuntimeException {
		kb_class_query instanceValueBindingArg = args.find(bindingCsticName);
		if (instanceValueBindingArg == null){
			new sce_user_fn_logging().writeLogError(pfunction, bindingCsticName + " not specified");
			throw new RuntimeException(bindingCsticName + " not specified");
		}
		ddbc_inst instanceValueBinding = null;
		try {instanceValueBinding = (ddbc_inst)instanceValueBindingArg.kb_get_binding();}
		catch (Exception e){
	    	new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException(bindingCsticName + " not found or incorrect", e);			
		}
		return instanceValueBinding;		
	}
	
	public static bdt_value getFloatValueFromArgs(sce_user_fn pfunction, fn_args args, String csticName) throws RuntimeException {
		String csticvalueFloat = args.get_value(csticName);
		if (csticvalueFloat == null){throwException(pfunction, "Characteristic argument [" + csticName + "] not specified");}
		return float_value_imp.get_float_value(csticvalueFloat);
	}
	
	public static bdt_value getSymbolValueFromArgs(sce_user_fn pfunction, fn_args args, String csticName) throws RuntimeException {
		String csticValueSymbol = args.get_value(csticName);
		if (csticValueSymbol == null){throwException(pfunction, "Characteristic argument [" + csticName + "] not specified");}
		return symbol_value_imp.get_symbol_value(csticValueSymbol);
	}
	
	public static String getStringValueFromArgsOptional(sce_user_fn pfunction, fn_args args, String csticName) throws RuntimeException {
		kb_class_query classQuery = args.find(csticName);
		if (classQuery == null) return null;
		binding stringValueBinding = classQuery.kb_get_binding();
		return stringValueBinding.toString();
	}
	
	public static String getStringValueFromArgs(sce_user_fn pfunction, fn_args args, String csticName) throws RuntimeException {
		String csticvalueString = args.get_value(csticName);
		if (csticvalueString == null){throwException(pfunction, "Characteristic argument [" + csticName + "] not specified");}
		return csticvalueString;
	}
	
	public static void setDefault(sce_user_fn pfunction, ddbc_inst instance, kb_cstic cstic, cstic_value csticValue, tmsc_trigger trigger, int jtype) throws RuntimeException {
		try {instance.ddb_set_default(cstic, csticValue, trigger, jtype);}
		catch (Exception e){
			String rootCauseMessage = getExceptionRootCause(e);
			new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to set Default Value [" + csticValue + "] on Cstic [" + cstic + "] on Instance [" + instance + "]" + rootCauseMessage, e);				
		}
	}
	
	public static void setValue(sce_user_fn pfunction, ddbc_inst instance, kb_cstic cstic, cstic_value csticValue, explc_owner owner, tmsc_trigger trigger, int jtype) throws RuntimeException {
		try {instance.ddb_set_val(cstic, csticValue, owner, trigger, jtype);}
		catch (Exception e){
			String rootCauseMessage = getExceptionRootCause(e);
			new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to set Value [" + csticValue + "] on Cstic [" + cstic + "] on Instance [" + instance + "]" + rootCauseMessage, e);				
		}
	}
	
	public static void setOrReplaceValue(sce_user_fn pfunction, ddbc_inst instance, kb_cstic cstic, cstic_value csticValue, explc_owner owner, tmsc_trigger trigger, int jtype) throws RuntimeException {
		try {instance.ddb_set_or_replace_val(cstic, csticValue, owner, trigger, jtype);}
		catch (Exception e){
			String rootCauseMessage = getExceptionRootCause(e);
			new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to set Value [" + csticValue + "] on Cstic [" + cstic + "] on Instance [" + instance + "]" + rootCauseMessage, e);				
		}
	}
	
	public static void deleteValue(sce_user_fn pfunction, ddbc_inst instance, kb_cstic cstic, cstic_value csticValue, explc_owner owner) throws RuntimeException {
		try {instance.ddb_del_val(cstic, csticValue, owner);}
		catch (Exception e){
			String rootCauseMessage = getExceptionRootCause(e);
			new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to delete Value [" + csticValue + "] on Cstic [" + cstic + "] on Instance [" + instance + "]" + rootCauseMessage, e);							
		}
	}		
	
	public static String[][] getDomainRestrictionsForOwner(sce_user_fn pfunction, ddb_inst instance, kb_cstic cstic, expl_owner owner) {
			if(!kb_cstic_kind.kb_restrictable_p(cstic.kb_get_kind())) {throwException (pfunction, "Cstic not restrictable: " + cstic);}

			vector domains = new vector();
			Enumeration<?> dynamicDomainFacts = ((ddbc_inst)instance).ddb_find_facts(cstic, ddbc_facet.C_DOM);
			domains = selectDomainRestrictionsForOwner(domains, dynamicDomainFacts, owner);
			Enumeration<?> userDomainFacts = ((ddbc_inst)instance).ddb_find_facts(cstic, ddbc_facet.C_USER_DOM);
			domains = selectDomainRestrictionsForOwner(domains, userDomainFacts, owner);			
			String[][] result = new String[domains.length()][];
			domains.copy_into(result);
			return result;
	}
	
	private static vector selectDomainRestrictionsForOwner(vector domainRestrictions, Enumeration<?> domainFacts, expl_owner owner){
		for(; domainFacts.hasMoreElements(); ) {
			ddb_fact fact = (ddb_fact)domainFacts.nextElement();
			if(owner == null || ddbc.ddb_has_justification_of_owner(fact, owner, false)) {
				domain dom = (domain)fact.ddb_get_binding();
				domainRestrictions.pushz(scelib.bdt_dom_to_strings(dom));
			}
		}	
		return domainRestrictions;
	}
	
	
	public static void assertDomainRestriction(sce_user_fn pfunction, ddbc_inst instance, kb_cstic cstic, domain domainRestriction, explc_owner owner, tmsc_trigger trigger, int jtype) throws RuntimeException {
		try {instance.ddb_restrict_dom(cstic, domainRestriction, owner, trigger, jtype);}
		catch  (Exception e){
			throwCaughtException(pfunction, e, "Failed to assert Domain Restriction [" + domainRestriction + "] on Cstic [" + cstic + "] on Instance [" + instance + "]");
		}
	}
	
	public static void deleteDomainRestriction(sce_user_fn pfunction, ddbc_inst instance, kb_cstic cstic, domain domainRestriction, explc_owner owner)  throws RuntimeException {
		try {instance.ddb_del_dom(cstic, domainRestriction, owner);}
		catch (Exception e){
			String rootCauseMessage = getExceptionRootCause(e);
			new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
	    	throw new RuntimeException("Failed to delete Domain Restriction [" + domainRestriction + "] on Cstic [" + cstic + "] on Instance [" + instance + "]" + rootCauseMessage, e);				
		}
	}

	public static kb_class_query_seq getIdentifyingCsticValuePairs (sce_user_fn pfunction, fn_args args, ddbc_inst owningInstance)  throws RuntimeException {
		kb_class_query_seq csticValuePairs  = kb_class_query_seq.C_EMPTY; kb_class_query csticValuePair = null;
				
		for  (int index = 0; index < 5; index++){ 
			csticValuePair = getIdentifyingCsticValuePair(pfunction, args, owningInstance, index);
			if (csticValuePair == null) continue;			
			
			csticValuePairs = new kb_class_query_seq (csticValuePair, csticValuePairs); //Append the class query to the existing seq
		}
		return csticValuePairs;
	}	
	
	public static kb_class_query getIdentifyingCsticValuePair(sce_user_fn pfunction, fn_args args, ddbc_inst owningInstance, int index) throws RuntimeException {
		kb_class_query csticValuePair = null;
		
		String iCsticValStringSymbol = null; String iCsticValStringFloat = null;
		symbol_value_imp iValueSymbol = null; float_value_imp iValueFloat = null; ddbc_inst iValueDDBCInst = null;
		cstic_type_descriptor typeDescriptor = null; int typeCode = 0;
		
		String iCsticName = args.get_value(identifyingCsticName[index]);
		
		if (iCsticName == null) return csticValuePair;  // no such csticName: don't bother trying to get its cstic or its cstic's corresponding Value
		
		kb_cstic iCstic = owningInstance.ddb_get_inst_type().kb_has_cstic_p(iCsticName);			
		
		if (iCstic == null){ throwException(pfunction, owningInstance.ddb_get_inst_type() + " has no such chracteristic as " + iCsticName); }
		
		typeDescriptor = iCstic.kb_get_type_descriptor();
		
		if (typeDescriptor instanceof oo_class){typeCode = C_CLASS_TYPE_DESCRIPTOR;}
		if (typeDescriptor instanceof bdt_type){typeCode = ((bdt_type)typeDescriptor).get_type_code();}
		
		if (typeCode == C_CLASS_TYPE_DESCRIPTOR){
			iValueDDBCInst = getIdentifyingCsticValADTInstance(pfunction, args, index);
			csticValuePair = new kb_class_query(iCstic, iValueDDBCInst);	
		}		
		
		if (typeCode == bdt_type.C_FLOAT_TYPE_DESCRIPTOR){
			iCsticValStringFloat = args.get_value(identifyingCsticValFloat[index]);
			
			if (iCsticValStringFloat == null){ throwException(pfunction, identifyingCsticValFloat[index] + " not specified"); }
			
			iValueFloat = float_value_imp.get_float_value(iCsticValStringFloat);
			csticValuePair = new kb_class_query(iCstic, iValueFloat);				
		}
		
		if (typeCode == bdt_type.C_STRING_TYPE_DESCRIPTOR){
			iCsticValStringSymbol = args.get_value(identifyingCsticValSymbol[index]);
			
			if (iCsticValStringSymbol == null){ throwException(pfunction, identifyingCsticValSymbol[index] + " not specified"); }
			
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
	
	public static oo_class getOOClassByKey (String ooClassName, String classType, object_type objectType, kb cfgKB){
		oo_class_key ooClassKey = cfgKB.kb_make_class_key(objectType, classType, ooClassName);
		return ooClassKey.kb_find_oo_class();		
	}
	
	public static void throwException(sce_user_fn pfunction, String exceptionMessage) throws RuntimeException {
		new sce_user_fn_logging().writeLogError(pfunction, exceptionMessage);
		throw new RuntimeException(exceptionMessage);
	}
	
	public static void throwCaughtException(sce_user_fn pfunction, Exception e, String exceptionMessage) throws RuntimeException {
		String rootCauseMessage = getExceptionRootCause(e);
		new sce_user_fn_logging().writeLogError(pfunction, e.getMessage(), e);
		if (rootCauseMessage != null){exceptionMessage = exceptionMessage + " [" + rootCauseMessage + "]";}
    	throw new RuntimeException(exceptionMessage, e);						
	}
	
	private static String getExceptionRootCause (Exception e) {
		Throwable rootCause = ExceptionUtil.getRootCause(e); if (rootCause == null){return null;}
		String rootMessage = rootCause.getMessage(); if (rootMessage == null) return " due to Unknown Exception";
		return " due to underlying " + rootMessage;		
	}
}

