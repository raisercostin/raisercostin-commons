/*
 ******************************************************************************
 *    $Logfile: /factory/src/javacommon/org.raisercostin/util/SortedList.java $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.util;

import java.util.*;

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
	public boolean add(final T object) {
		final boolean answer = elements.add(object);
		resort();
		return answer;
	}

	@Override
	public boolean addAll(final Collection<? extends T> collection) {
		final boolean answer = elements.addAll(collection);
		resort();
		return answer;
	}

	@Override
	public T get(final int index) {
		return elements.get(index);
	}

	@Override
	public void add(final int index, final T element) {
		elements.add(index, element);
		resort();
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends T> c) {
		final boolean result = elements.addAll(index, c);
		resort();
		return result;
	}

	@Override
	public void clear() {
		elements.clear();
		resort();
	}

	@Override
	public T remove(final int index) {
		final T result = elements.remove(index);
		resort();
		return result;
	}

	@Override
	public boolean remove(final Object o) {
		final boolean result = elements.remove(o);
		resort();
		return result;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		final boolean result = elements.removeAll(c);
		resort();
		return result;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		final boolean result = elements.retainAll(c);
		resort();
		return result;
	}

	@Override
	public T set(final int index, final T element) {
		final T result = elements.set(index, element);
		resort();
		return result;
	}

	/**
	 * @param o
	 * @return
	 */
	@Override
	public boolean contains(final Object o) {
		return elements.contains(o);
	}

	/**
	 * @param c
	 * @return
	 */
	@Override
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
	@Override
	public int indexOf(final Object o) {
		return elements.indexOf(o);
	}

	/**
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * @param o
	 * @return
	 */
	@Override
	public int lastIndexOf(final Object o) {
		return elements.lastIndexOf(o);
	}

	/**
	 * @return
	 */
	@Override
	public ListIterator<T> listIterator() {
		return elements.listIterator();
	}

	/**
	 * @param index
	 * @return
	 */
	@Override
	public ListIterator<T> listIterator(final int index) {
		return elements.listIterator(index);
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	@Override
	public List<T> subList(final int fromIndex, final int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	/**
	 * @return
	 */
	@Override
	public Object[] toArray() {
		return elements.toArray();
	}

	/**
	 * @param a
	 * @return
	 */
	@Override
	public <E> E[] toArray(final E[] a) {
		return elements.toArray(a);
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}
