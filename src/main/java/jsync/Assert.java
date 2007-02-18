// -< Assert.java >---------------------------------------------------*--------*
// JSYNC Version 1.04 (c) 1998 GARRET * ? *
// (Java synchronization classes) * /\| *
//                                                                   * / \ *
//                          Created: 20-Jun-98 K.A. Knizhnik * / [] \ *
//                          Last update: 5-Jul-98 K.A. Knizhnik * GARRET *
//-------------------------------------------------------------------*--------*
// Runtime checking of current program state
//-------------------------------------------------------------------*--------*

package jsync;

/**
 * Class for checking program invariants. Analog of C <code>assert()</code>
 * macro. As far as Java compiler doesn't provide information about compiled
 * file and line number, place of assertion failure can be located only by
 * analyzing stack trace of thrown AssertionFailed exception.
 * 
 * @see jsync.AssertionFailed
 */
public final class Assert {
	private Assert() {
	};

	/**
	 * Check specified condition and raise <code>AssertionFailed</code>
	 * exception if it is not true.
	 * 
	 * @param cond
	 *            result of checked condition
	 */
	public static void that(final boolean cond) {
		if (!cond) {
			throw new AssertionFailed();
		}
	}

	/**
	 * Check specified condition and raise <code>AssertionFailed</code>
	 * exception if it is not true.
	 * 
	 * @param description
	 *            string describing checked condition
	 * @param cond
	 *            result of checked condition
	 */
	public static void that(final String description, final boolean cond) {
		if (!cond) {
			throw new AssertionFailed(description);
		}
	}
}
