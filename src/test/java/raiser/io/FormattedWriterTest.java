/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author: Costin Emilian GRIGORE
 */
public class FormattedWriterTest extends TestCase {

	/**
	 * Constructor for FormattedWriterTest.
	 * 
	 * @param arg0
	 */
	public FormattedWriterTest(final String arg0) {
		super(arg0);
	}

	public void testFormattedWriter() throws IOException {
		new File("target\\temp").mkdirs();
		final FormattedWriter fw = new FormattedWriter(new BufferedWriter(
				new FileWriter("target/temp/test.txt")));
		fw.writeString("hi");
		fw.close();
	}

}
