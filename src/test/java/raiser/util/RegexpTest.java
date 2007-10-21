/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import junit.framework.TestCase;

/**
 * @author: Costin Emilian GRIGORE
 */
public class RegexpTest extends TestCase {

	/**
	 * Constructor for RegexpTest.
	 * 
	 * @param arg0
	 */
	public RegexpTest(final String arg0) {
		super(arg0);
	}

	public void testEscape() {
		assertEquals("\\$\\{variable\\}", Regexp.escape("${variable}"));
	}

	public void testEscapeAll() {
	}

}
