/*
 * Created on Nov 19, 2003
 */
package org.raisercostin.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author org.raisercostin
 */
public class IteratorUtils {
	public static <T> Iterator<T> createIterator(
			final Enumeration<T> enumeration) {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public T next() {
				return enumeration.nextElement();
			}

			@Override
			public void remove() {
				throw new RuntimeException("Not impelemented.");
			}

		};
	}
}
