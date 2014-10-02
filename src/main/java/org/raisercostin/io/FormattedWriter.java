/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.2 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.io;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author: Costin Emilian GRIGORE
 */
public class FormattedWriter implements DataWriter {
	private final BufferedWriter writer;

	public FormattedWriter(final BufferedWriter writer) {
		this.writer = writer;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public DataWriter writeInteger(final Integer value) throws IOException {
		writer.write(value.toString());
		writer.write(' ');
		return this;
	}

	@Override
	public DataWriter writeInt(final int value) throws IOException {
		writer.write(Integer.toString(value));
		writer.write(' ');
		return this;
	}

	@Override
	public DataWriter writeString(final String value) throws IOException {
		final String[] values = value.split("\\n", -1);
		for (int i = 0; i < values.length; i++) {
			writer.write(values[i]);
			if (i + 1 < values.length) {
				writeNewLine();
			}
		}
		if (values[values.length - 1].length() > 0) {
			writer.write(' ');
		}
		return this;
	}

	@Override
	public DataWriter writeCharacter(final Character value) throws IOException {
		writer.write(value.charValue());
		writer.write(' ');
		return this;
	}

	@Override
	public DataWriter writeChar(final char value) throws IOException {
		writer.write(Character.toString(value));
		writer.write(' ');
		return this;
	}

	@Override
	public DataWriter writeNewLine() throws IOException {
		writer.newLine();
		return this;
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public DataWriter writeDouble(final double value) throws IOException {
		writer.write(Double.toString(value));
		writer.write(' ');
		return this;
	}

	@Override
	public DataWriter writeLong(final long value) throws IOException {
		writer.write(Long.toString(value));
		writer.write(' ');
		return this;
	}
}
