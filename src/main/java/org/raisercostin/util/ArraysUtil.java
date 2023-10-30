/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.util;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author: Costin Emilian GRIGORE
 */
public class ArraysUtil {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ArraysUtil.class);

  public static byte[] toByteArray(final Object[] array) {
    final byte[] result = new byte[array.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = ((Number) array[i]).byteValue();
    }
    return result;
  }

  public static int[] toIntArray(final Object[] array) {
    final int[] result = new int[array.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = ((Number) array[i]).intValue();
    }
    return result;
  }

  public static String toString(final byte[] objects) {
    return toString(objects, objects.length, 0, ", ", false, false, false,
      false);
  }

  public static Object toCharString(final byte[] objects) {
    final StringBuffer result = new StringBuffer();
    result.append("[");
    for (int i = 0; i < objects.length; i++) {
      if (i > 0) {
        result.append(", ");
      }
      result.append(Character.toString((char) objects[i]));
    }
    result.append("]");
    return result;
  }

  public static String toString(final byte[] objects, int len,
      final int offsetLineNumber, final String separator,
      final boolean writeCharTo, final boolean writeBinary,
      final boolean writeHex, final boolean writeLineNumber) {
    final StringBuffer result = new StringBuffer();
    result.append("[");
    len = Math.min(len, objects.length);
    for (int i = 0; i < len; i++) {
      if (i > 0) {
        result.append(separator);
      }
      if (writeLineNumber) {
        result.append(Formatter.toString(offsetLineNumber + i, 5));
        result.append('.');
      }
      if (writeHex) {
        result.append(" 0x");
        result.append(Formatter.toHexString(objects[i], 2));
      }
      result.append(" ");
      result.append(Formatter.toString(objects[i], 3));
      if (writeCharTo) {
        result.append(" '");
        if (Character.isISOControl((char) objects[i])) {
          result.append(" ");
        } else {
          result.append((char) objects[i]);
        }
        result.append("'");
      }
      if (writeCharTo) {
        result.append(" 0b");
        result.append(Formatter.toBinString(objects[i], 8));
      }
    }
    result.append("]");
    return result.toString();
  }

  public static String toString(final Map<Object, Object> map) {
    final StringBuffer buf = new StringBuffer();
    buf.append("{");
    final Iterator<Map.Entry<Object, Object>> i = map.entrySet().iterator();
    boolean hasNext = i.hasNext();
    while (hasNext) {
      final Map.Entry<Object, Object> e = i.next();
      final Object key = e.getKey();
      Object value = e.getValue();
      if (value instanceof byte[]) {
        value = toString((byte[]) value);
      }
      buf.append((key == map ? "(this Map)" : key) + "="
          + (value == map ? "(this Map)" : value));
      hasNext = i.hasNext();
      if (hasNext) {
        buf.append(", ");
      }
    }
    buf.append("}");
    return buf.toString();
  }

  public static <T> String toString(final List<T> list) {
    final StringBuffer buf = new StringBuffer();
    buf.append("{");
    for (final T object : list) {
      buf.append(object).append(", ");
    }
    buf.append("}");
    return buf.toString();
  }

  public static boolean equalsMaps(final Map<Object, Object> map1,
      final Map<Object, Object> map2) {
    if (map2 == map1) {
      return true;
    }
    if (map2.size() != map1.size()) {
      return false;
    }
    try {
      final Iterator<Entry<Object, Object>> i = map1.entrySet()
        .iterator();
      while (i.hasNext()) {
        final Entry<Object, Object> e = i.next();
        final Object key = e.getKey();
        final Object value = e.getValue();
        if (value == null) {
          if (!((map2.get(key) == null) && map2.containsKey(key))) {
            return false;
          }
        } else {
          if (value instanceof byte[]) {
            return Arrays.equals((byte[]) value, (byte[]) map2
              .get(key));
          }
          if (!value.equals(map2.get(key))) {
            return false;
          }
        }
      }
    } catch (final ClassCastException ex) {
      log.info("ignored", ex);
      return false;
    } catch (final NullPointerException ex) {
      log.info("ignored", ex);
      return false;
    }
    return true;
  }

  public static <T1, T2> boolean equalsLists(final List<T1> map1,
      final List<T2> map2) {
    if (map2 == map1) {
      return true;
    }
    if (map2.size() != map1.size()) {
      return false;
    }
    try {
      final Iterator<T1> i1 = map1.iterator();
      final Iterator<T2> i2 = map2.iterator();
      while (i1.hasNext() && i2.hasNext()) {
        final Object value1 = i1.next();
        final Object value2 = i2.next();
        if (value1 instanceof byte[]) {
          if (!Arrays.equals((byte[]) value1, (byte[]) value2)) {
            return false;
          }
        } else {
          if (!value1.equals(value2)) {
            return false;
          }
        }
      }
      if (i1.hasNext() || i2.hasNext()) {
        return false;
      }
    } catch (final ClassCastException ex) {
      log.info("ignored", ex);
      return false;
    } catch (final NullPointerException ex) {
      log.info("ignored", ex);
      return false;
    }
    return true;
  }
}