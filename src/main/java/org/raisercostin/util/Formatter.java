/*
 * Created on Nov 13, 2003
 */
package org.raisercostin.util;

import java.text.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author org.raisercostin
 */
public class Formatter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Formatter.class);

	public static String toHexString(int number, final int digits) {
		boolean possitive = true;
		if (number < 0) {
			number = -number;
			possitive = false;
		}
		final StringBuffer result = new StringBuffer(Integer
				.toHexString(number));
		if (result.length() < digits) {
			for (int i = digits - result.length(); i > 0; i--) {
				result.insert(0, '0');
			}
		}
		if (!possitive) {
			result.insert(0, '-');
		}
		return result.toString();
	}

	public static String formatDouble(final double value, final int width,
			final int decimals) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < width; i++) {
			sb.append('0');
		}
		if (decimals > 0) {
			sb.append('.');
			for (int i = 0; i < decimals; i++) {
				sb.append('0');
			}
		}
		formatter.applyPattern(sb.toString());
		return formatter.format(value);
	}

	public static String formatInteger(final int value, final int width) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < width; i++) {
			sb.append("0");
		}
		formatter.applyPattern(sb.toString());
		return formatter.format(value);
	}

	static DecimalFormat formatter = new DecimalFormat();

	/**
	 * Method formatDouble.
	 * 
	 * @param double
	 * @param i
	 * @param i1
	 * @return int
	 */
	public static String formatDouble(final Double value, final int width,
			final int decimals) {
		return formatDouble(value.doubleValue(), width, decimals);
	}

	/**
	 * Method formatDouble.
	 * 
	 * @param double
	 * @param pattern
	 * @return Object
	 */
	public static Object formatDouble(final Double value, final String pattern) {
		formatter.applyPattern(pattern);
		return formatter.format(value);
	}

	/**
	 * Method formatDouble.
	 * 
	 * @param d
	 * @param string
	 * @return Object
	 */
	public static Object formatDouble(final double value, final String pattern) {
		formatter.applyPattern(pattern);
		return formatter.format(value);
	}

	public static String paddLeft(String string, final int length,
			final char paddChar) {
		if (string.length() < length) {
			final StringBuffer buffer = new StringBuffer();
			for (int i = length - string.length(); i > 0; i--) {
				buffer.append(paddChar);
			}
			buffer.append(string);
			string = buffer.toString();
		}
		return string;
	}

	public static String toString(final byte value, final int length) {
		return Formatter.paddLeft(Byte.toString(value), length, '0');
	}

	public static String toString(final int value, final int length) {
		return Formatter.paddLeft(Integer.toString(value), length, '0');
	}

	public static String toHexString(final byte value) {
		return Integer.toHexString(value & 0xff);
	}

	public static String toHexString(final byte value, final int length) {
		return Formatter.paddLeft(Integer.toHexString(value & 0xff), length,
				'0');
	}

	public static String toBinString(final byte value) {
		return Integer.toBinaryString(value & 0xff);
	}

	public static String toBinString(final int value) {
		return Integer.toBinaryString(value & 0xffffffff);
	}

	public static String toBinString(final byte value, final int length) {
		return Formatter.paddLeft(toBinString(value), length, '0');
	}

	public static String toBinString(final int value, final int length) {
		return Formatter.paddLeft(toBinString(value), length, '0');
	}

	/**
	 * @param ch
	 * @return
	 */
	public static String decode(final int value) {
		return value + "(" + (value >= 32 ? (char) value : '*') + ")";
	}

	public static String toChar(final int value) {
		if ((value < 32) && (value != 13) && (value != 10)) {
			return "(" + value + ")";
		}
		return Character.toString((char) value);
	}

	public static String[][] DATE_FORMAT = {
			{ "1", "EEE MMM dd HH:mm:ss yyyy", },
			{ "11", "EEE MMM dd HH:mm:ss zzzz yyyy", },
			{ "2", "yyyy-mm-dd hh:mm:ss", }, { "date", "yyyy.MM.dd", },
			{ "time", "hh.mm.ss", },
			{ "splited timestamp", "yyyy.MM.dd.hh.mm.ss", },
			{ "timestamp", "yyyyMMddhhmmss", },
			{ "3", "yyyy.MM.dd G 'at' HH:mm:ss z", },
			{ "4", "EEE, MMM d, ''yy", }, { "5", "h:mm a", },
			{ "6", "hh 'o''clock' a, zzzz", }, { "7", "K:mm a, z", },
			{ "8", "yyyyy.MMMMM.dd GGG hh:mm aaa", },
			{ "9", "EEE, d MMM yyyy HH:mm:ss Z", }, { "10", "yyMMddHHmmssZ", },
			{ "11", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", }, };

	/**
	 * Recognised formats : "EEE MMM dd HH:mm:ss yyyy", "Wed Apr 02 12:15:33
	 * EEST 2003", "yyyy-mm-dd hh:mm:ss", "yyyy.MM.dd G 'at' HH:mm:ss z"
	 * 2001.07.04 AD at 12:08:56 PDT "EEE, MMM d, ''yy" Wed, Jul 4, '01 "h:mm a"
	 * 12:08 PM "hh 'o''clock' a, zzzz" 12 o'clock PM, Pacific Daylight Time
	 * "K:mm a, z" 0:08 PM, PDT "yyyyy.MMMMM.dd GGG hh:mm aaa" 02001.July.04 AD
	 * 12:08 PM "EEE, d MMM yyyy HH:mm:ss Z" Wed, 4 Jul 2001 12:08:56 -0700
	 * "yyMMddHHmmssZ" 010704120856-0700 "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
	 * 2001-07-04T12:08:56.235-0700
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar parseDate(final String date) {
		for (final Map.Entry<String, Formatter.FormatterEntry> entry : parsers
				.entrySet()) {
			try {
				final Date d = entry.getValue().getDateFormatter().parse(date);
				if (d != null) {
					final Calendar result = Calendar.getInstance();
					result.setTimeInMillis(d.getTime());
					return result;
				}
			} catch (final ParseException e) {
				logger.info("Can't parse with " + entry.getValue().getName()
						+ "[" + entry.getValue().getValue() + "] the date ["
						+ date + "].");
			}
		}
		return null;
	}

	private static class FormatterEntry {
		String name;

		String value;

		DateFormat dateFormatter;

		public FormatterEntry(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public DateFormat getDateFormatter() {
			if (dateFormatter == null) {
				dateFormatter = new SimpleDateFormat(value);
			}
			return dateFormatter;
		}
	}

	private static Map<String, FormatterEntry> parsers;
	static {
		parsers = new TreeMap<String, FormatterEntry>();
		for (final String[] element : DATE_FORMAT) {
			addParser(element[0], element[1]);
		}
	}

	public static void addParser(final String name, final String formatter) {
		parsers.put(name, new FormatterEntry(name, formatter));
	}

	public static DateFormat getParser(final String parserName) {
		return parsers.get(parserName).getDateFormatter();
	}
}
