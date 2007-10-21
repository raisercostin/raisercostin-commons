/**
 * @author cgrigore
 * @date Jul 2, 2002
 *
 * Copyright (c) 2002 Softwin SRL, Romania, Bucharest, All Rights Reserved.
 */
package raiser.io;

import java.io.IOException;
import java.io.Writer;

/**
 * @author cgrigore
 */
public class TextOutputStream {
	public TextOutputStream(final Writer writer) {
		this.writer = writer;
		separator = " ";
		lineSeparator = computeLineSeparator();
	}

	public TextOutputStream(final Writer w, final String comment) {
		this(w);
		setComment(comment);
	}

	public void close() throws IOException {
		writer.close();
	}

	public void writeLine(final String line) throws IOException {
		write(line);
		newLine();
	}

	public void writeComment(final String line) throws IOException {
		if (withComments == true) {
			write(comment);
			write(line);
			newLine();
		}
	}

	public void writeWord(final String word) throws IOException {
		write(word);
		write(separator);
	}

	public void writeInteger(final int value) throws IOException {
		write(Integer.toString(value));
		write(separator);
	}

	public void writeDouble(final double value) throws IOException {
		write(Double.toString(value));
		write(separator);
	}

	public void writeBoolean(final boolean value) throws IOException {
		if (value) {
			write("true");
		} else {
			write("false");
		}
		write(separator);
	}

	public void setComment(final String comment) {
		this.comment = comment;
		withComments = true;
	}

	/**
	 * Method suppresComments.
	 */
	public void suppresComments() {
		withComments = false;
	}

	public void newLine() throws IOException {
		writer.write(lineSeparator);
	}

	private void write(final String line) throws IOException {
		writer.write(line);
	}

	private String computeLineSeparator() {
		String result = System.getProperty("line.separator");
		if (result == null) {
			result = "\n";
		}
		return result;
	}

	private final String separator;

	private final String lineSeparator;

	private String comment;

	private boolean withComments = false;

	private Writer writer = null;
}