package org.raisercostin.util;

public class Security {
	public static String sqlEscape(String stringValue) {
		stringValue = stringValue.replaceAll("\'", "");
		return stringValue;
	}
}
