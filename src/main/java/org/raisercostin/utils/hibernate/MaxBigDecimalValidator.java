package org.raisercostin.utils.hibernate;

import java.math.BigDecimal;

public class MaxBigDecimalValidator implements org.hibernate.validator.Validator<MaxBigDecimal> {
	private BigDecimal max;
	private final static String DIGITS = "999999999999999999999999999999999999999999999";

	public boolean isValid(Object value) {
		if (value == null) {
			return true;
		}
		if (!(value instanceof BigDecimal)) {
			throw new IllegalArgumentException("The monetary validator should be applied to a BigDecimal.");
		}
		BigDecimal theValue = (BigDecimal) value;
		return theValue.compareTo(max) <= 0;
	}

	public void initialize(MaxBigDecimal parameters) {
		max = new BigDecimal(DIGITS.substring(0, parameters.precision() - parameters.scale()) + "." + DIGITS.substring(0, parameters.scale()));
	}
}
