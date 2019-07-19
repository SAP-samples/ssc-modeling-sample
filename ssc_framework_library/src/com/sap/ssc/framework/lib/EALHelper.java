package com.sap.ssc.framework.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import com.sap.sce.front.base.InstanceType;

public final class EALHelper {

	EALHelper() {}

	public static boolean setIsNonPartType (InstanceType instanceType)
	{
		boolean isNonPartInstance = !instanceType.getKbType().kb_has_decomp_no_load_p();
		
		try {EALHelper.invokeSetField(instanceType, "_isNonPartType", "BOOLEAN", isNonPartInstance);} 
		catch (Exception ex) {ex.printStackTrace(); return false;}
		catch (Throwable t) {t.printStackTrace(); return false;}
		
		return true;
	}
	
	public static boolean setDecompType (InstanceType instanceType){
		
		try {EALHelper.invokeGetMethod(instanceType,"getDecompType", new String[]{});} 
		catch (Exception ex) {ex.printStackTrace(); return false;}
		catch (Throwable t) {t.printStackTrace(); return false;}

		return true;
	}

	/*
	 * Retrieves the value of an attribute invoking the fieldname of the object thru reflection.
	 */
	
	public static Object invokeGetField(Object obj, String fieldName, String fieldType) throws Throwable {
		Object rtn = null; Field field = null; 
		
		try {
			try {field = obj.getClass().getDeclaredField(fieldName);} 
			catch (Exception ex) {}
			
			if (field != null) {
				if (!field.isAccessible()){field.setAccessible(true);}
				rtn = field.get(obj);
				
			} else throw new Exception("Field " + fieldName + " cannot be found");
		} 
		catch (IllegalAccessException ie) {throw new RuntimeException("IllegalAccessException: " + ie.getMessage());}
		catch (InvocationTargetException e) {throw e.getTargetException();}
		
		return(rtn);
	}
	
	public static boolean invokeSetField(Object obj, String fieldName, String fieldType, Object value) throws Throwable {
		Field field = null; 
		
		try {
			try {field = obj.getClass().getDeclaredField(fieldName);} 
			catch (Exception ex) {}
			
			if (field != null) {
				if (!field.isAccessible()){field.setAccessible(true);}
				if (fieldType.equals("OBJECT")){field.set(obj, value);}
				if (fieldType.equals("BOOLEAN")){field.setBoolean(obj, (Boolean)value);}
				
			} else throw new Exception("Field " + fieldName + " cannot be found");
		} 
		catch (IllegalAccessException ie) {throw new RuntimeException("IllegalAccessException: " + ie.getMessage());}
		catch (InvocationTargetException e) {throw e.getTargetException();}
		
		return true;
	}	
	
	/*
	 * Retrieves the value of an attribute invoking the methodName of the object thru reflection.
	 */
	
	public static Object invokeGetMethod(Object obj, String methodName, Object[] params) throws Throwable {
		Object rtn = null; Method method = null; Class<?>[] paramTypes = new Class[params.length];
		
		for (int i = 0; i<params.length; i++)
			paramTypes[i] = params[i].getClass();
		try {
			try {method = obj.getClass().getDeclaredMethod(methodName, paramTypes);} 
			catch (Exception ex) {}
			if ( method == null && obj.getClass().getSuperclass() != null) {
				try {method = obj.getClass().getSuperclass().getDeclaredMethod(methodName, paramTypes);}
				catch (Exception ex) {}
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
