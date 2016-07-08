package org.raisercostin.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;

public class StringUtils {
	private static int MAX_LENGTH = 200;
	private static final String ENCODING_UTF8 = "UTF8";
	
	private static final boolean OS_WINDOWS = System.getProperty("os.name").contains("Windows");

	public static String toString(byte[] content) {
		return toString(content, content.length);
	}

	public static String toString(byte[] content, int length) {
		int newLength = Math.min(length, content.length);
		try {
			return new String(content, 0, newLength, ENCODING_UTF8)
					+ (content.length > length ? "...(" + content.length + " bytes)" : "");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] fromString(String text) {
		try {
			return text.getBytes(ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toConsoleString(byte[] content2) {
		if (content2 == null) {
			return "sample(size=0 bytes):null";
		}
		return "sample(size=" + content2.length + " bytes):" + escape(toString(content2, MAX_LENGTH))
				+ (content2.length > MAX_LENGTH ? "..." : "");
	}

	private static String escape(String string) {
		return string.replaceAll("[^\\x20-\\x7E]", "?");
	}
	
	public static boolean equals(String firstString, String secondString) {
		return org.apache.commons.lang.StringUtils.equals(firstString, secondString);
	}
	
	public static boolean isEmpty(String theString) {
		return org.apache.commons.lang.StringUtils.isEmpty(theString);
	}
	
	public static String normalize(String toNormalize) {
		return toNormalize.replaceAll(IOUtils.LINE_SEPARATOR_WINDOWS, IOUtils.LINE_SEPARATOR_UNIX);
	}
}
