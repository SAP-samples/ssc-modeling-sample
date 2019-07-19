package com.sap.sce.user;

import com.sap.sce.engine.ddb_inst;
import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_type;
import com.sap.sxe.sys.read_only_sequence;
import com.sap.sxe.util.imp.float_value_imp;

public class SME_NO_ITEMS_PF implements sce_user_fn
{
	private static final long serialVersionUID = 1L;

	public boolean execute(fn_args args, Object configObj)
	{
		ddb_inst classInstance = (ddb_inst) args.find("IS_ITEM_OF_SV").kb_get_binding();
		try
		{
			kb_type classInstType = classInstance.ddb_get_inst_type();
			kb_cstic csticToCnt = classInstType.kb_has_cstic_p("HAS_ITEM_SV");
			read_only_sequence rs = classInstance.ddb_get_values(csticToCnt);

			classInstance.ddb_set_or_replace_val(
					classInstType.kb_has_cstic_p("SME_NO_ITEMS_NF"),
					float_value_imp.get_float_value(rs.length()),
					((cfg_imp) configObj).tms_get_generic_default_owner());
		}
		catch (Exception e)
		{}

		return true;
	}
}