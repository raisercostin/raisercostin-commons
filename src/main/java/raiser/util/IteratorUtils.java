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
	public static <T> Iterator<T> createIterator(final Enumeration<T> enumeration) {
		return new Iterator<T>() {
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			public T next() {
				return enumeration.nextElement();
			}

			public void remove() {
				throw new RuntimeException("Not impelemented.");
			}

		};
	}
}
