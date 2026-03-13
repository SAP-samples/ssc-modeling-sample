package com.sap.sce.user;

import com.sap.sce.engine.expl.explc_owner;
import com.sap.sce.user.helpers.GenericUserHelper;
import com.sap.sxe.util.imp.float_value_imp;

public class GET_ALPHA_VALUE_FOR_NUMBER implements sce_user_fn {
	static final long serialVersionUID = 0;

	private static final String INPUT_NUMERIC_VALUE_ARGUMENT = "INPUT_NUMERIC_VALUE";
	private static final String DROP_INPUT_DECIMAL_ZERO_ARGUMENT = "DROP_INPUT_DECIMAL_ZERO";
	private static final String OUTPUT_ALPHANUMERIC_CHARACTER_ARGUMENT = "OUTPUT_ALPHANUMERIC_VALUE";

	public boolean execute(fn_args args, Object obj) {

		fn_args_deluxe args_deluxe = (fn_args_deluxe) args;
		explc_owner ruleOwner = (explc_owner)args_deluxe.get_owner();
		
		float_value_imp inputNumericFloatValue = GenericUserHelper.getFloatValueBindingFromArgs(this, ruleOwner, args_deluxe, INPUT_NUMERIC_VALUE_ARGUMENT);
		
		String dropInputDecimalZero = GenericUserHelper.getStringValueFromArgsOptional(this, args_deluxe, DROP_INPUT_DECIMAL_ZERO_ARGUMENT);
		
		if (dropInputDecimalZero == null){ dropInputDecimalZero = "NO"; }
		if (!dropInputDecimalZero.equals("NO") && !dropInputDecimalZero.equals("YES")) {GenericUserHelper.throwException(this, ruleOwner, "Characteristic [" + DROP_INPUT_DECIMAL_ZERO_ARGUMENT + "] specified with incorrect value"); }
		
		double inputNumericDouble = inputNumericFloatValue.get_value();		
		int inputNumericInteger = (int)inputNumericDouble; 
		
		String outputAlphanumericValue = null;
		
		if (dropInputDecimalZero.equals("YES") && inputNumericDouble == Math.floor(inputNumericDouble)) { 
			outputAlphanumericValue = Integer.toString(inputNumericInteger); 
		}
		if (!dropInputDecimalZero.equals("YES") || inputNumericDouble != Math.floor(inputNumericDouble)) { 
			outputAlphanumericValue = Double.toString(inputNumericDouble);
		}
				
		args.set_value(OUTPUT_ALPHANUMERIC_CHARACTER_ARGUMENT, outputAlphanumericValue);

		return true;
	}
}
