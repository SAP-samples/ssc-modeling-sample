package com.sap.sce.engine.ddb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.kbrt.kb_cstic;
import com.sap.sce.kbrt.kb_decomp_item;
import com.sap.sxe.tms.imp.tmsc_trigger;
import com.sap.sxe.util.binding;

public final class ddbc_helper {

	ddbc_helper() {}

	public static boolean ddb_justify_part_inst (
			ddbc_p_inst parent_inst, ddbc_inst part_inst, kb_decomp_item decomp_item, 
			explc_owner owner, tmsc_trigger trigger, int jtype)
	{
		if (parent_inst == null) throw new NullPointerException("parent_inst not specified");
		if (decomp_item == null) throw new NullPointerException("decomp_item not specified");		
		if (owner == null)       throw new NullPointerException("owner not specified");	
		if (trigger == null)     throw new NullPointerException("trigger not specified");

		parent_inst.ddb_get_ddb().pms_interrupt(ddbc.C_PMS_DDB_SET_PROPERTY);	
//		ddbc_variable p_var = parent_inst.ddb_get_p_variable(decomp_item);
		Object [] params = { decomp_item };
		ddbc_variable p_var = null;
		try {
			p_var = (ddbc_variable) ddbc_helper.invokeGetMethod(parent_inst,"ddb_get_p_variable", params);
		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (p_var != null) 
			p_var.ddb_add_property(ddbc_facet.C_VAL, part_inst, owner, trigger, jtype);	

		parent_inst.ddb_get_ddb().pms_continue(ddbc.C_PMS_DDB_SET_PROPERTY);

		return true;
	}

	public static boolean ddb_justify_cstic_value (
			ddbc_inst instance, kb_cstic cstic, binding binding,
			explc_owner owner, tmsc_trigger trigger, int jtype) {
		
		if (instance == null) throw new NullPointerException("instance not specified");	
		if (cstic == null) 	  throw new NullPointerException("cstic not specified");
		if (binding == null)  throw new NullPointerException("binding not specified");		
		if (owner == null)    throw new NullPointerException("owner not specified");	
		if (trigger == null)  throw new NullPointerException("trigger not specified");
		
		instance.ddb_get_ddb().pms_interrupt(ddbc.C_PMS_DDB_SET_PROPERTY);
		Object [] params = { cstic };
		ddbc_variable c_variable = null;
		try {
			c_variable =  (ddbc_variable) ddbc_helper.invokeGetMethod((ddbc_inst)instance,"ddb_get_c_variable", params);
		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (c_variable != null)
			c_variable.ddb_add_property(ddbc_facet.C_VAL, binding, owner, trigger, jtype);
		instance.ddb_get_ddb().pms_continue(ddbc.C_PMS_DDB_SET_PROPERTY);

		return true;
	}
	
	/*
	 * Retrieves the value of an attribute invoking the methodName of the object thru reflection.
	 */
	
	public static Object invokeGetMethod(Object obj, String methodName, Object[] params) throws Throwable {
		Object rtn = null;
		Method method = null;
		Class<?>[] paramTypes = new Class[params.length];
		for (int i = 0; i<params.length; i++)
			paramTypes[i] = params[i].getClass();
		try {
			try {
				method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
			} catch (Exception ex) {}
			if ( method == null && obj.getClass().getSuperclass() != null) {
				try {
					method = obj.getClass().getSuperclass().getDeclaredMethod(methodName, paramTypes);
				} catch (Exception ex) {}
			}
			if (method == null) {
				int cnt = 0;

				do {
					Method [] mds = null;
					
					if (cnt == 0)
						mds = obj.getClass().getDeclaredMethods();		
					else if (obj.getClass().getSuperclass() != null)
						mds = obj.getClass().getSuperclass().getDeclaredMethods();	
					
					if (mds != null) {
						for (Method m : mds) {
							if (m.getName().equals(methodName)) {
								method = m;
								break;
							}	
						}
					}
				} while (++cnt < 2 && method == null);
			}
			
			if (method != null) {
				if (!method.isAccessible())
					method.setAccessible(true);
				rtn = method.invoke(obj, (Object[])params);
			} else throw new Exception("Method " + methodName + " cannot be found");
		} catch (IllegalAccessException ie) {
			throw new RuntimeException("IllegalAccessException: " +
					ie.getMessage());
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		return(rtn);
	}
	
	
}
