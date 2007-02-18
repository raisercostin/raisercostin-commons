/*
 ******************************************************************************
 *    $Logfile: /factory/src/javacommon/raiser/util/SortedCollection.java $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SortedCollection<T extends Comparable<? super T>> extends
		AbstractCollection<T> implements Collection<T> {
	private List<T> elements;

	private Comparator<? super T> comparator;

	public SortedCollection() {
		elements = new LinkedList<T>();
	}

	public SortedCollection(Comparator<T> comparator) {
		this();
		this.comparator = comparator;
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

	@Override
	public int size() {
		return elements.size();
	}

	private void resort() {
		if (comparator == null) {
			Collections.sort(elements);
		} else {
			Collections.sort(elements, comparator);
		}
	}

	@Override
	public boolean add(T object) {
		boolean answer = elements.add(object);
		resort();
		return answer;
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		boolean answer = elements.addAll(collection);
		resort();
		return answer;
	}
}
