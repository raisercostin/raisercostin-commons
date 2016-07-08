package org.raisercostin.util;

import java.util.*;
import java.util.Map.Entry;

public class TreeMapOfMaps<K1, K2, V> implements MapOfMap<K1, K2, V> {
	public static <K1, K2, V> MapOfMap<K1, K2, V> create() {
		return new TreeMapOfMaps<K1, K2, V>();
	}

	private final Map<K1, Map<K2, V>> map = new TreeMap<K1, Map<K2, V>>();

	public TreeMapOfMaps() {
	}

	@Override
	public V get(K1 key1, K2 key2) {
		return getStuff(key1).get(key2);
	}

	@Override
	public void put(K1 key1, K2 key2, V value) {
		getStuff(key1).put(key2, value);
	}

	private Map<K2, V> getStuff(K1 key1) {
		Map<K2, V> result = map.get(key1);
		if (result == null) {
			result = new TreeMap<K2, V>();
			map.put(key1, result);
		}
		return result;
	}

	@Override
	public void remove(K1 key1, K2 key2) {
		getStuff(key1).remove(key2);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Set<K1> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Map<K2, V>> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K1, Map<K2, V>>> entrySet() {
		return map.entrySet();
	}
}
