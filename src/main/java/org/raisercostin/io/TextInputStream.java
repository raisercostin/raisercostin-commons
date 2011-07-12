/**
 * @author cgrigore
 * @date Jul 2, 2002
 *
 * Copyright (c) 2002 Softwin SRL, Romania, Bucharest, All Rights Reserved.
 */
package org.raisercostin.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cgrigore
 */
public class TextInputStream {
	final String wordRegexp = "(\\S+)";

	final String integerRegexp = "([+-]?\\d+)";

	final String doubleRegexp = "([+-]?\\ *(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?)";

	final String booleanRegexp = "(true|false)";

	/**
	 * Constructor for TextInputStream.
	 * 
	 * @param r
	 */
	public TextInputStream(final Reader r) throws ParseException {
		reader = new BufferedReader(r);
	}

	public void close() throws IOException {
		reader.close();
	}

	/**
	 * Constructor TextInputStream.
	 * 
	 * @param r
	 * @param string
	 */
	public TextInputStream(final Reader r, final String commentChar)
			throws ParseException {
		this(r);
		setComment(commentChar);
	}

	/**
	 * Method readLine.
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String readLine() throws ParseException {
		final String result = read("(.*)", 1, -1);
		return result;
	}

	/**
	 * Method readWord.
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String readWord() throws ParseException {
		final String result = read("\\s*" + wordRegexp + "\\s*(.*\\S*)\\s*", 1,
				2);
		return result;
	}

	/**
	 * Method readSpecificWord.
	 * 
	 * @param string
	 * @return String
	 */
	public String readSpecificWord(final String string) throws ParseException {
		final String result = read(string + "\\s*(.*\\S*)\\s*", 1, 2);
		return result;
	}

	/**
	 * Method readInteger.
	 * 
	 * @return Integer
	 * @throws IOException
	 */
	public Integer readInteger() throws ParseException {
		final String result = read("\\s*" + integerRegexp + "\\s*(.*\\S*)\\s*",
				1, 2);
		if (result == null) {
			return null;
		}
		return new Integer(result);
	}

	/**
	 * Method readDouble.
	 * 
	 * @return Double
	 * @throws IOException
	 */
	public Double readDouble() throws ParseException {
		final String result = read("\\s*" + doubleRegexp + "\\s*(.*\\S*)\\s*",
				1, 2);
		if (result == null) {
			return null;
		}
		return new Double(result);
	}

	/**
	 * Method readBoolean.
	 */
	public Boolean readBoolean() throws ParseException {
		final String result = read("\\s*" + booleanRegexp + "\\s*(.*\\S*)\\s*",
				1, 2);
		if (result == null) {
			return null;
		}
		return new Boolean(result);
	}

	/**
	 * Method setCommentChar.
	 * 
	 * @param c
	 */
	public void setComment(final String commentChar) {
		this.commentChar = commentChar.charAt(0);
		withComments = true;
	}

	/**
	 * Method withoutComments.
	 */
	public void withoutComments() {
		withComments = false;
	}

	/**
	 * Method getLineNo.
	 * 
	 * @return int
	 */
	public int lineno() {
		if (currentLine == 0) {
			return 1;
		}
		return currentLine;
	}

	public boolean eof() throws IOException {
		nextLine();
		return (lastLine == null);
	}

	private String read(final String patternText, final int matchGroup,
			final int restGroup) throws ParseException {
		try {
			if (currentLine == 0) {
				nextLine();
				if (lastLine == null) {
					return null;
				}
			}
			final Pattern pattern = Pattern.compile(patternText,
					Pattern.COMMENTS | Pattern.MULTILINE);
			final Matcher matcher = pattern.matcher(lastLine);
			matcher.matches();
			if (restGroup == -1) {
				lastLine = null;
			} else {
				if (matcher.groupCount() < restGroup) {
					lastLine = matcher.group(restGroup);
				} else {
					lastLine = matcher.group(matcher.groupCount());
				}
			}
			nextLine();
			return matcher.group(matchGroup);
		} catch (final Exception e) {
			throw new ParseException("error at line: " + lineno(), e);
		}
	}

	private void nextLine() throws IOException {
		if ((lastLine == null) || (lastLine.length() == 0)) {
			do {
				lastLine = reader.readLine();
				currentLine++;
			} while (withComments && (lastLine != null)
					&& (lastLine.length() > 0)
					&& (lastLine.charAt(0) == commentChar));
		}
	}

	private String lastLine;

	private char commentChar;

	private boolean withComments = false;

	private int currentLine = 0;

	private BufferedReader reader = null;
}