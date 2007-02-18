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

	public SortedList(List<T> elements) {
		this.elements = elements;
		resort();
	}

	public SortedList(Comparator<T> comparator) {
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

	public boolean add(T object) {
		boolean answer = elements.add(object);
		resort();
		return answer;
	}

	public boolean addAll(Collection<? extends T> collection) {
		boolean answer = elements.addAll(collection);
		resort();
		return answer;
	}

	public T get(int index) {
		return elements.get(index);
	}

	public void add(int index, T element) {
		elements.add(index, element);
		resort();
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		final boolean result = elements.addAll(index, c);
		resort();
		return result;
	}

	public void clear() {
		elements.clear();
		resort();
	}

	public T remove(int index) {
		T result = elements.remove(index);
		resort();
		return result;
	}

	public boolean remove(Object o) {
		boolean result = elements.remove(o);
		resort();
		return result;
	}

	public boolean removeAll(Collection<?> c) {
		boolean result = elements.removeAll(c);
		resort();
		return result;
	}

	public boolean retainAll(Collection<?> c) {
		boolean result = elements.retainAll(c);
		resort();
		return result;
	}

	public T set(int index, T element) {
		T result = elements.set(index, element);
		resort();
		return result;
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean contains(Object o) {
		return elements.contains(o);
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean containsAll(Collection c) {
		return elements.containsAll(c);
	}

	@Override
	public boolean equals(Object obj) {
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
	public int indexOf(Object o) {
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
	public int lastIndexOf(Object o) {
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
	public ListIterator<T> listIterator(int index) {
		return elements.listIterator(index);
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public List<T> subList(int fromIndex, int toIndex) {
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
	public <E> E[] toArray(E[] a) {
		return elements.toArray(a);
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}
