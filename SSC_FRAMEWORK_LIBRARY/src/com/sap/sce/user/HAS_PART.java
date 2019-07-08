package com.sap.sce.user;

import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.ddb.ddbc_p_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb_class_query;
import com.sap.sce.kbrt.kb_class_query_seq;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_decomp_item;
import com.sap.sce.kbrt.oo_class;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.tms.imp.tmse_jtype;
import com.sap.sxe.util.bdt_type;
import com.sap.sxe.util.cstic_type_descriptor;
import com.sap.sxe.util.imp.float_value_imp;
import com.sap.sxe.util.imp.symbol_value_imp;


public class HAS_PART implements sce_user_fn {
	
	static final long serialVersionUID = 0;
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
	
	@Override
	public boolean execute(fn_args args, Object obj) {
		try {
			fn_args_deluxe args_deluxe = (fn_args_deluxe) args;
			
			tmsc_trigger trigger = args_deluxe.get_trigger();
			int jtype = tmse_jtype.C_SPECIAL;
			cfg_imp cfgImp = (cfg_imp)obj;		
            explc_owner userOwner = (explc_owner)cfgImp.tms_get_user_owner();
            
			ddbc_p_inst bomParentInstance = (ddbc_p_inst)args.find(parentInstanceCsticName).kb_get_binding();
			if (bomParentInstance == null) return true;
			
			String bomPositionNumber = args.get_value(positionNumberCsticName);
			if (bomPositionNumber == null) return true;			
			
			kb_decomp_item bomDecompItem = bomParentInstance.ddb_get_decomp().kb_find_decomp_item(bomPositionNumber);
			kb_class_query_seq csticValuePairs = getCsticValuePairs(args, bomDecompItem);
			ddbc_inst bomChildInstance = (ddbc_inst)bomParentInstance.ddb_find_or_create_part_inst(bomDecompItem, csticValuePairs, userOwner, trigger, jtype);
			
			kb_class_query referencingInstanceArg = args.find(referencingInstanceCsticName);
			if (referencingInstanceArg == null) return true;
			
			ddbc_inst referencingInstance = (ddbc_inst)referencingInstanceArg.kb_get_binding();
			if (referencingInstance == null) return true;
			
			String referencingCsticString = args.get_value(referencingCsticName);
			if (referencingCsticString == null) return true;	
			
			kb_cstic referencingCstic = referencingInstance.ddb_get_inst_cstic(referencingCsticString);
			if (referencingCstic == null) return true;
			
			referencingInstance.ddb_set_val(referencingCstic, bomChildInstance, userOwner, trigger, jtype);
		}
	    catch (Exception e) {return true;}
	    return true;	    
	}

	private kb_class_query_seq getCsticValuePairs (fn_args args, kb_decomp_item bomDecompItem) {
		kb_class_query_seq csticValuePairs  = kb_class_query_seq.C_EMPTY; kb_class_query csticValuePair = null;
		
		String iCsticName = null; String iCsticValStringSymbol = null; String iCsticValStringFloat = null;
		kb_cstic iCstic = null; symbol_value_imp iValueSymbol = null; float_value_imp iValueFloat = null; ddbc_inst iValueDDBCInst = null;
		cstic_type_descriptor typeDescriptor = null; int typeCode = 0;
		
		for  (int index = 0; index < 5; index++){ 
			iCsticName = args.get_value(identifyingCsticName[index]);
			if (iCsticName == null) continue;  // no such csticName: don't bother trying to get its cstic or its cstic's corresponding Value
			
			iCstic = bomDecompItem.kb_get_type().kb_has_cstic_p(iCsticName);
			if (iCstic == null) continue; // no such cstic: don't bother trying to get its 	corresponding Value
			
			typeDescriptor = iCstic.kb_get_type_descriptor();
			
			if (typeDescriptor instanceof oo_class){typeCode = C_CLASS_TYPE_DESCRIPTOR;}
			if (typeDescriptor instanceof bdt_type){typeCode = ((bdt_type)typeDescriptor).get_type_code();}
			
			if (typeCode == C_CLASS_TYPE_DESCRIPTOR){
				iValueDDBCInst = (ddbc_inst)args.find(identifyingCsticValADT[index]).kb_get_binding();
				csticValuePair = new kb_class_query(iCstic, iValueDDBCInst);	
			}		
			if (typeCode == bdt_type.C_FLOAT_TYPE_DESCRIPTOR){
				iCsticValStringFloat = args.get_value(identifyingCsticValFloat[index]);
				if (iCsticValStringFloat == null) continue; // no value: don't bother trying to set it
				iValueFloat = float_value_imp.get_float_value(iCsticValStringFloat);
				csticValuePair = new kb_class_query(iCstic, iValueFloat);				
			}
			if (typeCode == bdt_type.C_STRING_TYPE_DESCRIPTOR){
				iCsticValStringSymbol = args.get_value(identifyingCsticValSymbol[index]);
				if (iCsticValStringSymbol == null) continue; // no value: don't bother trying to set it
				iValueSymbol = symbol_value_imp.get_symbol_value(iCsticValStringSymbol);
				csticValuePair = new kb_class_query(iCstic, iValueSymbol);
			}			
	
			csticValuePairs = new kb_class_query_seq (csticValuePair, csticValuePairs); //Append the class query to the existing seq
		}
		return csticValuePairs;
	}
}
