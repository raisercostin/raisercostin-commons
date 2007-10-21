/*
 ******************************************************************************
 *    $Logfile: /factory/src/javacommon/raiser/util/SortedList.java $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author: Costin Emilian GRIGORE
 */
public class SortedList<T extends Comparable<? super T>> implements List<T> {
	private List<T> elements;

	private Comparator<T> comparator;

	public SortedList() {
		elements = new ArrayList<T>();
	}

	public SortedList(final List<T> elements) {
		this.elements = elements;
		resort();
	}

	public SortedList(final Comparator<T> comparator) {
		this();
		this.comparator = comparator;
	}

	public Iterator<T> iterator() {
		return elements.iterator();
	}

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

	public boolean add(final T object) {
		final boolean answer = elements.add(object);
		resort();
		return answer;
	}

	public boolean addAll(final Collection<? extends T> collection) {
		final boolean answer = elements.addAll(collection);
		resort();
		return answer;
	}

	public T get(final int index) {
		return elements.get(index);
	}

	public void add(final int index, final T element) {
		elements.add(index, element);
		resort();
	}

	public boolean addAll(final int index, final Collection<? extends T> c) {
		final boolean result = elements.addAll(index, c);
		resort();
		return result;
	}

	public void clear() {
		elements.clear();
		resort();
	}

	public T remove(final int index) {
		final T result = elements.remove(index);
		resort();
		return result;
	}

	public boolean remove(final Object o) {
		final boolean result = elements.remove(o);
		resort();
		return result;
	}

	public boolean removeAll(final Collection<?> c) {
		final boolean result = elements.removeAll(c);
		resort();
		return result;
	}

	public boolean retainAll(final Collection<?> c) {
		final boolean result = elements.retainAll(c);
		resort();
		return result;
	}

	public T set(final int index, final T element) {
		final T result = elements.set(index, element);
		resort();
		return result;
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean contains(final Object o) {
		return elements.contains(o);
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean containsAll(final Collection<?> c) {
		return elements.containsAll(c);
	}

	@Override
	public boolean equals(final Object obj) {
		return elements.equals(obj);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	/**
	 * @param o
	 * @return
	 */
	public int indexOf(final Object o) {
		return elements.indexOf(o);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * @param o
	 * @return
	 */
	public int lastIndexOf(final Object o) {
		return elements.lastIndexOf(o);
	}

	/**
	 * @return
	 */
	public ListIterator<T> listIterator() {
		return elements.listIterator();
	}

	/**
	 * @param index
	 * @return
	 */
	public ListIterator<T> listIterator(final int index) {
		return elements.listIterator(index);
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public List<T> subList(final int fromIndex, final int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return elements.toArray();
	}

	/**
	 * @param a
	 * @return
	 */
	public <E> E[] toArray(final E[] a) {
		return elements.toArray(a);
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}
