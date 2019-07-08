package com.sap.sce.user;

import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.tms.imp.tmse_jtype;
import com.sap.sxe.util.imp.symbol_value_imp;

public class GET_KB_NAME_AND_VERSION implements sce_user_fn {
	
	static final long serialVersionUID = 0;
	
	private static String updateInstanceCsticName = "UPDATE_INSTANCE";
	private static String kbNameCsticName = "KB_NAME_CSTIC";
	private static String kbVersionCsticName = "KB_VERSION_CSTIC";
	
	@Override
	public boolean execute(fn_args args, Object obj) {
	
		fn_args_deluxe args_deluxe = (fn_args_deluxe) args;
		tmsc_trigger trigger = args_deluxe.get_trigger();
		int jtype = tmse_jtype.C_SPECIAL;
		cfg_imp cfgImp = (cfg_imp)obj;		
        explc_owner userOwner = (explc_owner)cfgImp.tms_get_user_owner();
        kb cfgKB = cfgImp.cfg_get_kb();
		
		try {
			ddbc_inst updateInstance = (ddbc_inst)args.find(updateInstanceCsticName).kb_get_binding();
			if (updateInstance == null) return true;
			
			String kbNameCsticNameString = args.get_value(kbNameCsticName);
			if (kbNameCsticNameString == null) return true;
			
			kb_cstic kbNameCstic = updateInstance.ddb_get_inst_cstic(kbNameCsticNameString);
			if (kbNameCstic == null) return true; 			
			
			String kbVersionCsticNameString = args.get_value(kbVersionCsticName);
			if (kbVersionCsticNameString == null) return true;			
			
			kb_cstic kbVersionCstic = updateInstance.ddb_get_inst_cstic(kbVersionCsticNameString);
			if (kbVersionCstic == null) return true;
			
			updateInstance.ddb_set_val(kbNameCstic, symbol_value_imp.get_symbol_value(cfgKB.kb_name()), userOwner, trigger, jtype);
			updateInstance.ddb_set_val(kbVersionCstic, symbol_value_imp.get_symbol_value(cfgKB.kb_version()), userOwner, trigger, jtype);				
		}
	    catch (Exception e)
	    {
	        return true;
	    }
	    return true;	    
	}
}
