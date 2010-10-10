package org.raisercostin.utils.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.springframework.util.StringUtils;

public class AmountValidator implements org.hibernate.validator.Validator<Amount> {
	private BigDecimal max;
	private String getter;
	private final static String DIGITS = "999999999999999999999999999999999999999999999";

	public boolean isValid(Object value2) {
		if (value2 == null) {
			return true;
		}
		Object value;
		try {
			value = value2.getClass().getMethod(getter).invoke(value2);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		if (!(value instanceof BigDecimal)) {
			throw new IllegalArgumentException("The monetary validator should be applied to a BigDecimal.");
		}
		BigDecimal theValue = (BigDecimal) value;
		return theValue.compareTo(max) <= 0;
	}

	public void initialize(Amount parameters) {
		max = new BigDecimal(DIGITS.substring(0, parameters.precision() - parameters.scale()) + "." + DIGITS.substring(0, parameters.scale()));
		getter = "get" + StringUtils.capitalize(parameters.field());
	}
}
