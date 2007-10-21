package raiser.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/*
 * Created on Jan 17, 2004
 */
/**
 * A mutable string.
 */
public class MutableString {
	/**
	 * @param name
	 */
	public MutableString(final String string) {
		setString(string);
	}

	String string;

	/**
	 * @param data
	 * @return
	 */
	public static String copyValueOf(final char[] data) {
		return String.copyValueOf(data);
	}

	/**
	 * @param data
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String copyValueOf(final char[] data, final int offset,
			final int count) {
		return String.copyValueOf(data, offset, count);
	}

	/**
	 * @param b
	 * @return
	 */
	public static String valueOf(final boolean b) {
		return String.valueOf(b);
	}

	/**
	 * @param c
	 * @return
	 */
	public static String valueOf(final char c) {
		return String.valueOf(c);
	}

	/**
	 * @param data
	 * @return
	 */
	public static String valueOf(final char[] data) {
		return String.valueOf(data);
	}

	/**
	 * @param data
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String valueOf(final char[] data, final int offset,
			final int count) {
		return String.valueOf(data, offset, count);
	}

	/**
	 * @param d
	 * @return
	 */
	public static String valueOf(final double d) {
		return String.valueOf(d);
	}

	/**
	 * @param f
	 * @return
	 */
	public static String valueOf(final float f) {
		return String.valueOf(f);
	}

	/**
	 * @param i
	 * @return
	 */
	public static String valueOf(final int i) {
		return String.valueOf(i);
	}

	/**
	 * @param obj
	 * @return
	 */
	public static String valueOf(final Object obj) {
		return String.valueOf(obj);
	}

	/**
	 * @param l
	 * @return
	 */
	public static String valueOf(final long l) {
		return String.valueOf(l);
	}

	/**
	 * @param index
	 * @return
	 */
	public char charAt(final int index) {
		return string.charAt(index);
	}

	/**
	 * @param o
	 * @return
	 */
	public int compareTo(final Object o) {
		return string.compareTo(o.toString());
	}

	/**
	 * @param anotherString
	 * @return
	 */
	public int compareTo(final String anotherString) {
		return string.compareTo(anotherString);
	}

	/**
	 * @param str
	 * @return
	 */
	public int compareToIgnoreCase(final String str) {
		return string.compareToIgnoreCase(str);
	}

	/**
	 * @param str
	 * @return
	 */
	public String concat(final String str) {
		return string.concat(str);
	}

	/**
	 * @param sb
	 * @return
	 */
	public boolean contentEquals(final StringBuffer sb) {
		return string.contentEquals(sb);
	}

	/**
	 * @param suffix
	 * @return
	 */
	public boolean endsWith(final String suffix) {
		return string.endsWith(suffix);
	}

	@Override
	public boolean equals(final Object obj) {
		return string.equals(obj);
	}

	/**
	 * @param anotherString
	 * @return
	 */
	public boolean equalsIgnoreCase(final String anotherString) {
		return string.equalsIgnoreCase(anotherString);
	}

	/**
	 * @return
	 */
	public byte[] getBytes() {
		return string.getBytes();
	}

	/**
	 * @param charsetName
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	public byte[] getBytes(final String charsetName)
			throws UnsupportedEncodingException {
		return string.getBytes(charsetName);
	}

	/**
	 * @param srcBegin
	 * @param srcEnd
	 * @param dst
	 * @param dstBegin
	 */
	public void getChars(final int srcBegin, final int srcEnd,
			final char[] dst, final int dstBegin) {
		string.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	/**
	 * @param ch
	 * @return
	 */
	public int indexOf(final int ch) {
		return string.indexOf(ch);
	}

	/**
	 * @param ch
	 * @param fromIndex
	 * @return
	 */
	public int indexOf(final int ch, final int fromIndex) {
		return string.indexOf(ch, fromIndex);
	}

	/**
	 * @param str
	 * @return
	 */
	public int indexOf(final String str) {
		return string.indexOf(str);
	}

	/**
	 * @param str
	 * @param fromIndex
	 * @return
	 */
	public int indexOf(final String str, final int fromIndex) {
		return string.indexOf(str, fromIndex);
	}

	/**
	 * @return
	 */
	public String intern() {
		return string.intern();
	}

	/**
	 * @param ch
	 * @return
	 */
	public int lastIndexOf(final int ch) {
		return string.lastIndexOf(ch);
	}

	/**
	 * @param ch
	 * @param fromIndex
	 * @return
	 */
	public int lastIndexOf(final int ch, final int fromIndex) {
		return string.lastIndexOf(ch, fromIndex);
	}

	/**
	 * @param str
	 * @return
	 */
	public int lastIndexOf(final String str) {
		return string.lastIndexOf(str);
	}

	/**
	 * @param str
	 * @param fromIndex
	 * @return
	 */
	public int lastIndexOf(final String str, final int fromIndex) {
		return string.lastIndexOf(str, fromIndex);
	}

	/**
	 * @return
	 */
	public int length() {
		return string.length();
	}

	/**
	 * @param regex
	 * @return
	 */
	public boolean matches(final String regex) {
		return string.matches(regex);
	}

	/**
	 * @param ignoreCase
	 * @param toffset
	 * @param other
	 * @param ooffset
	 * @param len
	 * @return
	 */
	public boolean regionMatches(final boolean ignoreCase, final int toffset,
			final String other, final int ooffset, final int len) {
		return string.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}

	/**
	 * @param toffset
	 * @param other
	 * @param ooffset
	 * @param len
	 * @return
	 */
	public boolean regionMatches(final int toffset, final String other,
			final int ooffset, final int len) {
		return string.regionMatches(toffset, other, ooffset, len);
	}

	/**
	 * @param oldChar
	 * @param newChar
	 * @return
	 */
	public String replace(final char oldChar, final char newChar) {
		return string.replace(oldChar, newChar);
	}

	/**
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public String replaceAll(final String regex, final String replacement) {
		return string.replaceAll(regex, replacement);
	}

	/**
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public String replaceFirst(final String regex, final String replacement) {
		return string.replaceFirst(regex, replacement);
	}

	/**
	 * @param regex
	 * @return
	 */
	public String[] split(final String regex) {
		return string.split(regex);
	}

	/**
	 * @param regex
	 * @param limit
	 * @return
	 */
	public String[] split(final String regex, final int limit) {
		return string.split(regex, limit);
	}

	/**
	 * @param prefix
	 * @return
	 */
	public boolean startsWith(final String prefix) {
		return string.startsWith(prefix);
	}

	/**
	 * @param prefix
	 * @param toffset
	 * @return
	 */
	public boolean startsWith(final String prefix, final int toffset) {
		return string.startsWith(prefix, toffset);
	}

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	public CharSequence subSequence(final int start, final int end) {
		return string.subSequence(start, end);
	}

	/**
	 * @param beginIndex
	 * @return
	 */
	public String substring(final int beginIndex) {
		return string.substring(beginIndex);
	}

	/**
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public String substring(final int beginIndex, final int endIndex) {
		return string.substring(beginIndex, endIndex);
	}

	/**
	 * @return
	 */
	public char[] toCharArray() {
		return string.toCharArray();
	}

	/**
	 * @return
	 */
	public String toLowerCase() {
		return string.toLowerCase();
	}

	/**
	 * @param locale
	 * @return
	 */
	public String toLowerCase(final Locale locale) {
		return string.toLowerCase(locale);
	}

	@Override
	public String toString() {
		return string.toString();
	}

	/**
	 * @return
	 */
	public String toUpperCase() {
		return string.toUpperCase();
	}

	/**
	 * @param locale
	 * @return
	 */
	public String toUpperCase(final Locale locale) {
		return string.toUpperCase(locale);
	}

	/**
	 * @return
	 */
	public String trim() {
		return string.trim();
	}

	public void setString(final String string) {
		this.string = string;
	}
}
