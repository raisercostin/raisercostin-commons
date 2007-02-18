/*
 * Created on Nov 19, 2003
 */
package raiser.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author raiser
 */
public class IteratorUtils {
	public static Iterator createIterator(final Enumeration enumeration) {
		return new Iterator() {
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			public Object next() {
				return enumeration.nextElement();
			}

			public void remove() {
				throw new RuntimeException("Not impelemented.");
			}

		};
	}
}
