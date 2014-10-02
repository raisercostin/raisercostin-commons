/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author: Costin Emilian GRIGORE
 */
public class FormattedReader implements DataReader {
	private final BufferedReader reader;

	StringTokenizer st;

	String line;

	public FormattedReader(final BufferedReader reader) {
		this.reader = reader;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public Integer readInteger() throws IOException {
		if ((line == null) || (line.length() == 0) || (!st.hasMoreTokens())) {
			do {
				line = reader.readLine(); // slurp in a line
				if (line == null) {
					return null;
				}
				st = new StringTokenizer(line);
			} while (!st.hasMoreTokens());
		}
		return new Integer(st.nextToken());
	}

	@Override
	public String readString() throws IOException {
		if ((line == null) || (line.length() == 0) || (!st.hasMoreTokens())) {
			do {
				line = reader.readLine(); // slurp in a line
				if (line == null) {
					return null;
				}
				st = new StringTokenizer(line);
			} while (!st.hasMoreTokens());
		}
		return st.nextToken();
	}

	@Override
	public char readCharacter(final String accepted) throws IOException {
		final char result = readCharacter();
		if (accepted.indexOf(result) == -1) {
			throw new IOException("Expected a char from '" + accepted
					+ "', got an '" + result + "' wich is an invalid char.");
		}
		return result;
	}

	private char readCharacter() throws IOException {
		final String result = readString();
		if (result.length() > 1) {
			throw new IOException("Expected a char, got an entire string '"
					+ result + "'.");
		}
		return result.charAt(0);
	}

	/**
	 * 
	 */
	public Double readDouble() throws IOException {
		if ((line == null) || (line.length() == 0) || (!st.hasMoreTokens())) {
			do {
				line = reader.readLine(); // slurp in a line
				if (line == null) {
					return null;
				}
				st = new StringTokenizer(line);
			} while (!st.hasMoreTokens());
		}
		return new Double(st.nextToken());
	}
}
