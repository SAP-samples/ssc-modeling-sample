package com.sap.sce.user;

import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.object_type;
import com.sap.sce.kbrt.oo_class;
import com.sap.sce.kbrt.oo_class_key;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.tms.imp.tmse_jtype;
import com.sap.sxe.util.imp.symbol_value_imp;

public class SPECIALIZE_BY_VALUE implements sce_user_fn {
	
	static final long serialVersionUID = 0;
	
	private static String specializationInstanceCsticName = "SPECIALIZATION_INSTANCE";
	private static String specializationTypeCsticName = "SPECIALIZATION_TYPE";
	private static String specializationValueCsticName = "SPECIALIZATION_VALUE";
	private static String specializedToMaterialCsticName = "SPECIALIZED_TO_MATERIAL_CSTIC";	
	private static String specializedToMaterialValueName = "SPECIALIZED_TO_MATERIAL_VALUE";	
	private static String material = "MARA";
	private static String sapClass = "KLAH";
	private static String configurableType = "300";
	
	@Override
	public boolean execute(fn_args args, Object obj) {
	
		fn_args_deluxe args_deluxe = (fn_args_deluxe) args;
		
		tmsc_trigger trigger = args_deluxe.get_trigger();
		int jtype = tmse_jtype.C_SPECIAL;
		cfg_imp cfgImp = (cfg_imp)obj;		
        explc_owner userOwner = (explc_owner)cfgImp.tms_get_user_owner();
        kb cfgKB = cfgImp.cfg_get_kb();
		
		try {
			ddbc_inst specializationInstance = (ddbc_inst)args.find(specializationInstanceCsticName).kb_get_binding();
			if (specializationInstance == null) return true;
			
			String specializationTypeValue = args.get_value(specializationTypeCsticName);
			if (!material.equals(specializationTypeValue) && !sapClass.equals(specializationTypeValue)){
				return true;
			}
			
			String specializationValue = args.get_value(specializationValueCsticName);
			if (specializationValue == null) return true;			
			
			object_type objectType = null; String classType = configurableType; oo_class_key specializationKey = null;
			
			if (material.equals(specializationTypeValue)){objectType = object_type.C_MATERIAL;}
			if (sapClass.equals(specializationTypeValue)){objectType = object_type.C_CLASS;}
	
			specializationKey = cfgKB.kb_make_class_key(objectType, classType, specializationValue);
			
			oo_class specializationClass = specializationKey.kb_find_oo_class();
			specializationInstance.ddb_specialize_inst(specializationClass, userOwner, trigger, jtype);
			
			if (!material.equals(specializationTypeValue)) return true;
			
			String specializedToMaterialCsticString = args.get_value(specializedToMaterialCsticName);
			if (specializedToMaterialCsticString == null) return true;
			
			kb_cstic specializedToMaterialCstic = specializationInstance.ddb_get_inst_cstic(specializedToMaterialCsticString);
			if (specializedToMaterialCstic == null) return true; 
			
			String specializedToMaterialValueString = args.get_value(specializedToMaterialValueName);
			if (specializedToMaterialValueString == null) return true;
			
			symbol_value_imp specializedToMaterialValue = symbol_value_imp.get_symbol_value(specializedToMaterialValueString);
			
			specializationInstance.ddb_set_val(specializedToMaterialCstic, specializedToMaterialValue, userOwner, trigger, jtype);		
		} catch (Exception e){
	        return true;
	    }
	    return true;	    
	}
}
