/**
 * @author cgrigore
 * @date Jul 2, 2002
 *
 * Copyright (c) 2002 Softwin SRL, Romania, Bucharest, All Rights Reserved.
 */
package raiser.io;
import java.io.*;

/**
 * @author cgrigore
 */
public class TextOutputStream
{
    public TextOutputStream(Writer writer)
    {
        this.writer = writer;
        separator = " ";
        lineSeparator = computeLineSeparator();
    }
    public TextOutputStream(Writer w, String comment)
    {
        this(w);
        setComment(comment);
    }
    public void close() throws IOException
    {
        writer.close();
    }
    public void writeLine(String line) throws IOException
    {
        write(line);
        newLine();
    }
    public void writeComment(String line) throws IOException
    {
        if( withComments == true )
        {
            write(comment);
            write(line);
            newLine();
        }
    }
    public void writeWord(String word) throws IOException
    {
        write(word);
        write(separator);
    }
    public void writeInteger(int value) throws IOException
    {
        write(Integer.toString(value));
        write(separator);
    }
    public void writeDouble(double value) throws IOException
    {
        write(Double.toString(value));
        write(separator);
    }
    public void writeBoolean(boolean value) throws IOException
    {
        if(value)
        {
            write("true");
        }
        else
        {
            write("false");
        }
        write(separator);
    }
    public void setComment(String comment)
    {
        this.comment = comment;
        this.withComments = true;
    }
    /**
     * Method suppresComments.
     */
    public void suppresComments()
    {
        this.withComments = false;
    }

    public void newLine() throws IOException
    {
        writer.write(lineSeparator);
    }
    private void write(String line) throws IOException
    {
        writer.write(line);
    }
    private String computeLineSeparator()
    {
        String result = System.getProperty("line.separator");
        if( result == null )
        {
            result = "\n";
        }
        return result;
    }
    private String separator;
    private String lineSeparator;
    private String comment;
    private boolean withComments = false;
    private Writer writer = null;
}