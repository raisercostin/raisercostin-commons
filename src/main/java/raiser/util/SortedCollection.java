/*
 ******************************************************************************
 *    $Logfile: /factory/src/javacommon/raiser/util/SortedCollection.java $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.*;

public class SortedCollection extends AbstractCollection implements Collection
{
    private List elements;
    private Comparator comparator;
    public SortedCollection()
    {
        this.elements = new LinkedList();
    }
    public SortedCollection(Comparator comparator)
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
}
