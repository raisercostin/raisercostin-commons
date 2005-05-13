/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.2 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.*;

/**
 * @author: Costin Emilian GRIGORE
 */
public class FormattedWriter implements DataWriter
{
    private BufferedWriter writer;
    public FormattedWriter(BufferedWriter writer)
    {
        this.writer = writer;
    }
    public void close() throws IOException
    {
        writer.close();
    }
    public DataWriter writeInteger(Integer value) throws IOException
    {
        writer.write(value.toString());
        writer.write(' ');
        return this;
    }
    public DataWriter writeInt(int value) throws IOException
    {
        writer.write(Integer.toString(value));
        writer.write(' ');
        return this;
    }
    public DataWriter writeString(String value) throws IOException
    {
        String[] values = value.split("\\n", -1);
        for (int i = 0; i < values.length; i++)
        {
            writer.write(values[i]);
            if (i + 1 < values.length)
            {
                writeNewLine();
            }
        }
        if (values[values.length - 1].length() > 0)
        {
            writer.write(' ');
        }
        return this;
    }
    public DataWriter writeCharacter(Character value) throws IOException
    {
        writer.write(value.charValue());
        writer.write(' ');
        return this;
    }
    public DataWriter writeChar(char value) throws IOException
    {
        writer.write(Character.toString(value));
        writer.write(' ');
        return this;
    }
    public DataWriter writeNewLine() throws IOException
    {
        writer.newLine();
        return this;
    }
    public void flush() throws IOException
    {
        writer.flush();
    }
    public DataWriter writeDouble(double value) throws IOException
    {
        writer.write(Double.toString(value));
        writer.write(' ');
        return this;
    }
    public DataWriter writeLong(long value) throws IOException
    {
        writer.write(Long.toString(value));
        writer.write(' ');
        return this;
}
}
