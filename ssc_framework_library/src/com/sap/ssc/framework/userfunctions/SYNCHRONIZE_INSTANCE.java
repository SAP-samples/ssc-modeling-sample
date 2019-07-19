package com.sap.ssc.framework.userfunctions;

import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.sce.engine.cfg.cfg_imp;
import com.sap.sce.engine.ddb.ddbc_inst;
import com.sap.sce.kbrt.kb_class_query_seq;
import com.sap.sce.user.fn_args;
import com.sap.sce.user.sce_user_fn;
import com.sap.sce.user.scelib;
import com.sap.ssc.framework.lib.GenericUserHelper;

public class SYNCHRONIZE_INSTANCE implements sce_user_fn {

	static final long serialVersionUID = 0;

	private static String synchronizedInstanceCsticName = "SYNCHRONIZED_INSTANCE";

	@Override
	public boolean execute(fn_args args, Object obj) {

		cfg_imp cfgImp = (cfg_imp) obj;

		ddbc_inst synchronizedInstance = GenericUserHelper.getInstanceFromArgs(this, args,
				synchronizedInstanceCsticName);

		kb_class_query_seq csticValuePairs = GenericUserHelper.getIdentifyingCsticValuePairs(this, args,
				synchronizedInstance);

		try {
			@SuppressWarnings("unused")
			OrchestratedInstance orchestratedInstance = scelib.synchronize_instance(cfgImp, synchronizedInstance,csticValuePairs);
		} catch (Exception e) {
			GenericUserHelper.throwCaughtException(this, e, "Failed Synchronize Instances");
		}
		return true;
	}
}
