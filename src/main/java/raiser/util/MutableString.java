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
	public MutableString(String string) {
		setString(string);
	}

	String string;

	/**
	 * @param data
	 * @return
	 */
	public static String copyValueOf(char[] data) {
		return String.copyValueOf(data);
	}

	/**
	 * @param data
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String copyValueOf(char[] data, int offset, int count) {
		return String.copyValueOf(data, offset, count);
	}

	/**
	 * @param b
	 * @return
	 */
	public static String valueOf(boolean b) {
		return String.valueOf(b);
	}

	/**
	 * @param c
	 * @return
	 */
	public static String valueOf(char c) {
		return String.valueOf(c);
	}

	/**
	 * @param data
	 * @return
	 */
	public static String valueOf(char[] data) {
		return String.valueOf(data);
	}

	/**
	 * @param data
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String valueOf(char[] data, int offset, int count) {
		return String.valueOf(data, offset, count);
	}

	/**
	 * @param d
	 * @return
	 */
	public static String valueOf(double d) {
		return String.valueOf(d);
	}

	/**
	 * @param f
	 * @return
	 */
	public static String valueOf(float f) {
		return String.valueOf(f);
	}

	/**
	 * @param i
	 * @return
	 */
	public static String valueOf(int i) {
		return String.valueOf(i);
	}

	/**
	 * @param obj
	 * @return
	 */
	public static String valueOf(Object obj) {
		return String.valueOf(obj);
	}

	/**
	 * @param l
	 * @return
	 */
	public static String valueOf(long l) {
		return String.valueOf(l);
	}

	/**
	 * @param index
	 * @return
	 */
	public char charAt(int index) {
		return string.charAt(index);
	}

	/**
	 * @param o
	 * @return
	 */
	public int compareTo(Object o) {
		return string.compareTo(o.toString());
	}

	/**
	 * @param anotherString
	 * @return
	 */
	public int compareTo(String anotherString) {
		return string.compareTo(anotherString);
	}

	/**
	 * @param str
	 * @return
	 */
	public int compareToIgnoreCase(String str) {
		return string.compareToIgnoreCase(str);
	}

	/**
	 * @param str
	 * @return
	 */
	public String concat(String str) {
		return string.concat(str);
	}

	/**
	 * @param sb
	 * @return
	 */
	public boolean contentEquals(StringBuffer sb) {
		return string.contentEquals(sb);
	}

	/**
	 * @param suffix
	 * @return
	 */
	public boolean endsWith(String suffix) {
		return string.endsWith(suffix);
	}

	@Override
	public boolean equals(Object obj) {
		return string.equals(obj);
	}

	/**
	 * @param anotherString
	 * @return
	 */
	public boolean equalsIgnoreCase(String anotherString) {
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
	public byte[] getBytes(String charsetName)
			throws UnsupportedEncodingException {
		return string.getBytes(charsetName);
	}

	/**
	 * @param srcBegin
	 * @param srcEnd
	 * @param dst
	 * @param dstBegin
	 */
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
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
	public int indexOf(int ch) {
		return string.indexOf(ch);
	}

	/**
	 * @param ch
	 * @param fromIndex
	 * @return
	 */
	public int indexOf(int ch, int fromIndex) {
		return string.indexOf(ch, fromIndex);
	}

	/**
	 * @param str
	 * @return
	 */
	public int indexOf(String str) {
		return string.indexOf(str);
	}

	/**
	 * @param str
	 * @param fromIndex
	 * @return
	 */
	public int indexOf(String str, int fromIndex) {
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
	public int lastIndexOf(int ch) {
		return string.lastIndexOf(ch);
	}

	/**
	 * @param ch
	 * @param fromIndex
	 * @return
	 */
	public int lastIndexOf(int ch, int fromIndex) {
		return string.lastIndexOf(ch, fromIndex);
	}

	/**
	 * @param str
	 * @return
	 */
	public int lastIndexOf(String str) {
		return string.lastIndexOf(str);
	}

	/**
	 * @param str
	 * @param fromIndex
	 * @return
	 */
	public int lastIndexOf(String str, int fromIndex) {
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
	public boolean matches(String regex) {
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
	public boolean regionMatches(boolean ignoreCase, int toffset, String other,
			int ooffset, int len) {
		return string.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}

	/**
	 * @param toffset
	 * @param other
	 * @param ooffset
	 * @param len
	 * @return
	 */
	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return string.regionMatches(toffset, other, ooffset, len);
	}

	/**
	 * @param oldChar
	 * @param newChar
	 * @return
	 */
	public String replace(char oldChar, char newChar) {
		return string.replace(oldChar, newChar);
	}

	/**
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public String replaceAll(String regex, String replacement) {
		return string.replaceAll(regex, replacement);
	}

	/**
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public String replaceFirst(String regex, String replacement) {
		return string.replaceFirst(regex, replacement);
	}

	/**
	 * @param regex
	 * @return
	 */
	public String[] split(String regex) {
		return string.split(regex);
	}

	/**
	 * @param regex
	 * @param limit
	 * @return
	 */
	public String[] split(String regex, int limit) {
		return string.split(regex, limit);
	}

	/**
	 * @param prefix
	 * @return
	 */
	public boolean startsWith(String prefix) {
		return string.startsWith(prefix);
	}

	/**
	 * @param prefix
	 * @param toffset
	 * @return
	 */
	public boolean startsWith(String prefix, int toffset) {
		return string.startsWith(prefix, toffset);
	}

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	public CharSequence subSequence(int start, int end) {
		return string.subSequence(start, end);
	}

	/**
	 * @param beginIndex
	 * @return
	 */
	public String substring(int beginIndex) {
		return string.substring(beginIndex);
	}

	/**
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public String substring(int beginIndex, int endIndex) {
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
	public String toLowerCase(Locale locale) {
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
	public String toUpperCase(Locale locale) {
		return string.toUpperCase(locale);
	}

	/**
	 * @return
	 */
	public String trim() {
		return string.trim();
	}

	public void setString(String string) {
		this.string = string;
	}
}
