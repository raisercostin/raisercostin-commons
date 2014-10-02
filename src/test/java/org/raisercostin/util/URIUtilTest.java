/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.2 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.raisercostin.io.FileUtils;

/**
 * @author: Costin Emilian GRIGORE
 */
public class URIUtilTest extends TestCase {

	/**
	 * Constructor for URIUtilTest.
	 * 
	 * @param arg0
	 */
	public URIUtilTest(final String arg0) {
		super(arg0);
	}

	public void testGetAbsolutePath() throws IOException, URISyntaxException {
		final char disk = new File(".").getAbsolutePath().charAt(0);
		String path = new File("").getAbsolutePath();
		if (path.charAt(1) == ':') {
			path = path.substring(2);
		}
		final String sep = FileUtils.getFileSeparator();

		// absoluta
		// file:///c: is current directory on drive c:
		// assertEquals("c:"+sep ,URIUtil.uriToAbsoluteFile("file:///c:"));
		assertEquals("c:" + sep, URIUtil.uriToAbsoluteFile("file:///c:/"));
		assertEquals("c:" + sep + "ceva", URIUtil
				.uriToAbsoluteFile("file:///c:/ceva"));

		// absoluta cu diskul curent
		assertEquals(disk + ":" + sep + "c", URIUtil
				.uriToAbsoluteFile("file:///c"));

		// relativa la calea curenta
		assertEquals(disk + ":" + path, URIUtil.uriToAbsoluteFile("file:///:/"));
		assertEquals(disk + ":" + path + sep + "ceva", URIUtil
				.uriToAbsoluteFile("file:///:/ceva"));
	}

}
