/*
 ******************************************************************************
 *    $Logfile: /factory/src/javacommon/raiser/util/SortedList.java $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.*;

/**
 * @author: Costin Emilian GRIGORE
 */
public class SortedList implements List
{
    private List elements;
    private Comparator comparator;
    public SortedList()
    {
        this.elements = new ArrayList();
    }
    public SortedList(List elements)
    {
        this.elements = elements;
        resort();
    }
    public SortedList(Comparator comparator)
    {
        this();
        this.comparator = comparator;
    }
    public Iterator iterator()
    {
        return this.elements.iterator();
    }
    public int size()
    {
        return this.elements.size();
    }
    private void resort()
    {
        if (this.comparator == null)
        {
            Collections.sort(this.elements);
        }
        else
        {
            Collections.sort(this.elements, this.comparator);
        }
    }
    public boolean add(Object object)
    {
        boolean answer = this.elements.add(object);
        this.resort();
        return answer;
    }
    public boolean addAll(Collection collection)
    {
        boolean answer = this.elements.addAll(collection);
        this.resort();
        return answer;
    }
    public Object get(int index)
    {
        return elements.get(index);
    }
    public void add(int index, Object element)
    {
        elements.add(index, element);
        this.resort();
    }

    public boolean addAll(int index, Collection c)
    {
        final boolean result = elements.addAll(index, c);
        this.resort();
        return result;
    }

    public void clear()
    {
        elements.clear();
        this.resort();
    }

    public Object remove(int index)
    {
        Object result = elements.remove(index);
        this.resort();
        return result;
    }

    public boolean remove(Object o)
    {
        boolean result = elements.remove(o);
        this.resort();
        return result;
    }

    public boolean removeAll(Collection c)
    {
        boolean result = elements.removeAll(c);
        this.resort();
        return result;
    }

    public boolean retainAll(Collection c)
    {
        boolean result = elements.retainAll(c);
        this.resort();
        return result;
    }

    public Object set(int index, Object element)
    {
        Object result = elements.set(index, element);
        this.resort();
        return result;
    }

    /**
     * @param o
     * @return
     */
    public boolean contains(Object o)
    {
        return elements.contains(o);
    }

    /**
     * @param c
     * @return
     */
    public boolean containsAll(Collection c)
    {
        return elements.containsAll(c);
    }

    public boolean equals(Object obj)
    {
        return elements.equals(obj);
    }

    public int hashCode()
    {
        return elements.hashCode();
    }

    /**
     * @param o
     * @return
     */
    public int indexOf(Object o)
    {
        return elements.indexOf(o);
    }

    /**
     * @return
     */
    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    /**
     * @param o
     * @return
     */
    public int lastIndexOf(Object o)
    {
        return elements.lastIndexOf(o);
    }

    /**
     * @return
     */
    public ListIterator listIterator()
    {
        return elements.listIterator();
    }

    /**
     * @param index
     * @return
     */
    public ListIterator listIterator(int index)
    {
        return elements.listIterator(index);
    }

    /**
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public List subList(int fromIndex, int toIndex)
    {
        return elements.subList(fromIndex, toIndex);
    }

    /**
     * @return
     */
    public Object[] toArray()
    {
        return elements.toArray();
    }

    /**
     * @param a
     * @return
     */
    public Object[] toArray(Object[] a)
    {
        return elements.toArray(a);
    }

    public String toString()
    {
        return elements.toString();
    }

}
