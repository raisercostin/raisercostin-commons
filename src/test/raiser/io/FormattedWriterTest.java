/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.*;
import java.io.BufferedWriter;

import junit.framework.TestCase;

/**
 * @author: Costin Emilian GRIGORE
 */
public class FormattedWriterTest extends TestCase
{

    /**
     * Constructor for FormattedWriterTest.
     * @param arg0
     */
    public FormattedWriterTest(String arg0)
    {
        super(arg0);
    }

    public void testFormattedWriter() throws IOException
    {
        new File("temp").mkdirs();
        FormattedWriter fw =
            new FormattedWriter(
                new BufferedWriter(new FileWriter("temp/test.txt")));
        fw.writeString("hi");
        fw.close();
    }

}
