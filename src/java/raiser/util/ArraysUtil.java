/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author: Costin Emilian GRIGORE
 */
public class ArraysUtil
{
    public static byte[] toByteArray(Object[] array)
    {
        byte[] result = new byte[array.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = ((Number) array[i]).byteValue();
        }
        return result;
    }

    public static int[] toIntArray(Object[] array)
    {
        int[] result = new int[array.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = ((Number) array[i]).intValue();
        }
        return result;
    }

    public static String toString(byte[] objects)
    {
        return toString(objects,objects.length,0, ", ", false, false,false,false);
    }

    public static Object toCharString(byte[] objects)
    {
        StringBuffer result = new StringBuffer();
        result.append("[");
        for (int i = 0; i < objects.length; i++)
        {
            if (i > 0)
            {
                result.append(", ");
            }
            result.append(Character.toString((char) objects[i]));
        }
        result.append("]");
        return result;
    }

    public static String toString(byte[] objects, int len, int offsetLineNumber,String separator,
            boolean writeCharTo, boolean writeBinary, boolean writeHex, boolean writeLineNumber)
    {
        StringBuffer result = new StringBuffer();
        result.append("[");
        len = Math.min(len,objects.length);
        for (int i = 0; i < len; i++)
        {
            if (i > 0)
            {
                result.append(separator);
            }
            if(writeLineNumber)
            {
                result.append(Formatter.toString(offsetLineNumber+i,5));
                result.append('.');
            }
            if (writeHex)
            {
                result.append(" 0x");
                result.append(Formatter.toHexString(objects[i],2));
            }
            result.append(" ");
            result.append(Formatter.toString(objects[i],3));
            if (writeCharTo)
            {
                result.append(" '");
                if (Character.isISOControl((char) objects[i]))
                {
                    result.append(" ");
                }
                else
                {
                    result.append((char) objects[i]);
                }
                result.append("'");
            }
            if (writeCharTo)
            {
                result.append(" 0b");
                result.append(Formatter.toBinString(objects[i],8));
            }
        }
        result.append("]");
        return result.toString();
    }

    public static String toString(Map map)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        Iterator i = map.entrySet().iterator();
        boolean hasNext = i.hasNext();
        while (hasNext)
        {
            Map.Entry e = (Map.Entry) (i.next());
            Object key = e.getKey();
            Object value = e.getValue();
            if (value instanceof byte[])
            {
                value = toString((byte[]) value);
            }
            buf.append((key == map ? "(this Map)" : key) + "="
                    + (value == map ? "(this Map)" : value));
            hasNext = i.hasNext();
            if (hasNext)
                buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }
    public static String toString(List list)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (Object object : list) {
            buf.append(object).append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    public static boolean equalsMaps(Map map1, Map map2)
    {
        if (map2 == map1)
            return true;
        if (map2.size() != map1.size())
            return false;
        try
        {
            Iterator i = map1.entrySet().iterator();
            while (i.hasNext())
            {
                Entry e = (Entry) i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                if (value == null)
                {
                    if (!(map2.get(key) == null && map2.containsKey(key)))
                        return false;
                }
                else
                {
                    if (value instanceof byte[])
                    {
                        return Arrays.equals((byte[]) value, (byte[]) map2
                                .get(key));
                    }
                    if (!value.equals(map2.get(key)))
                        return false;
                }
            }
        }
        catch (ClassCastException unused)
        {
            return false;
        }
        catch (NullPointerException unused)
        {
            return false;
        }
        return true;
    }

    public static boolean equalsLists(List map1, List map2)
    {
        if (map2 == map1)
            return true;
        if (map2.size() != map1.size())
            return false;
        try
        {
            Iterator i1 = map1.iterator();
            Iterator i2 = map2.iterator();
            while(i1.hasNext() && i2.hasNext())
            {
                Object value1 = i1.next();
                Object value2 = i2.next();
                if(value1 instanceof byte[])
                {
                    if(!Arrays.equals((byte[])value1,(byte[])value2))
                    {
                        return false;
                    }
                }
                else
                {
                    if(!value1.equals(value2))
                    {
                        return false;
                    }
                }
            }
            if(i1.hasNext() || i2.hasNext())
            {
                return false;
            }
        }
        catch (ClassCastException unused)
        {
            return false;
        }
        catch (NullPointerException unused)
        {
            return false;
        }
        return true;
    }
}