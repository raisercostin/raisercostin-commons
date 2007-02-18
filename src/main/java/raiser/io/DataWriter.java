/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.2 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.IOException;

/**
 * @author: Costin Emilian GRIGORE
 */
public interface DataWriter {
	public abstract void close() throws IOException;

	public abstract DataWriter writeInteger(Integer value) throws IOException;

	public abstract DataWriter writeInt(int value) throws IOException;

	public abstract DataWriter writeLong(long value) throws IOException;

	public abstract DataWriter writeDouble(double value) throws IOException;

	public abstract DataWriter writeString(String value) throws IOException;

	public abstract DataWriter writeCharacter(Character value)
			throws IOException;

	public abstract DataWriter writeChar(char value) throws IOException;

	public abstract DataWriter writeNewLine() throws IOException;

	public abstract void flush() throws IOException;
}