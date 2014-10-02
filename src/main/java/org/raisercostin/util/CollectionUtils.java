package org.raisercostin.util;

import java.util.*;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public class CollectionUtils {
	@SafeVarargs
	public static <T1 extends Comparable<T1>, T2> Map<T1, T2> map(Pair<T1, T2>... pairs) {
		Map<T1, T2> result = Maps.newTreeMap();
		for (Pair<T1, T2> pair : pairs) {
			result.put(pair._1, pair._2);
		}
		return result;
	}

	public static <T1, T2> Pair<T1, T2> pair(T1 key, T2 value) {
		return new Pair<>(key, value);
	}

	public static <T1, T2> Pair<T1, T2> pairEmpty() {
		return new Pair<>(null, null);
	}

	@SafeVarargs
	public static <T1, T2> List<Pair<T1, T2>> listEmpty(Pair<T1, T2>... pairEmpty) {
		return new ArrayList<Pair<T1, T2>>();
	}

	@SafeVarargs
	public static <T> List<T> list(T... values) {
		return new ArrayList<T>(Arrays.asList(values));
	}

	public static <T> List<T> list() {
		return Arrays.asList();
	}

	public static <T> List<T> sublist(List<T> result, long startIndex, long size) {
		long endIndex = result.size();
		if (size < endIndex - startIndex) {
			endIndex = startIndex + size;
		}
		if (startIndex > Integer.MAX_VALUE) {
			throw new RuntimeException("StartIndex " + startIndex
					+ " is bigger than maxInt. You shouldn't use a InMemoryRawMessagingDao.");
		}
		if (endIndex > Integer.MAX_VALUE) {
			throw new RuntimeException("EndIndex " + endIndex
					+ " is bigger than maxInt. You shouldn't use a InMemoryRawMessagingDao.");
		}
		return result.subList(Ints.checkedCast(startIndex), Ints.checkedCast(endIndex));
	}

	// HACK needs performance improvement
	public static <T> List<T> sublist(Collection<T> values, long startIndex, long size) {
		return sublist(new ArrayList<T>(values), startIndex, size);
	}

	public static <K, V> V getNotNull(Map<K, V> map, K key) {
		return getNotNull("", map, key);
	}
	public static <K, V> V getNotNull(String message, Map<K, V> map, K key) {
		V result = map.get(key);
		if (result == null) {
			throw new IllegalStateException(message + "Couldn't find key [" + key + "] in map. Existing keys are "
					+ map.keySet() + ".");
		}
		return result;
	}
}
