package org.raisercostin.util;

import java.util.*;
import java.util.Map.Entry;

public interface MapOfMap<K1, K2, V> {
	V get(K1 key1, K2 key2);
	void put(K1 key1, K2 key2, V value);
	void remove(K1 key1, K2 key2);
	void clear();
	public Set<K1> keySet();
	public Collection<Map<K2, V>> values();
	public Set<Entry<K1, Map<K2, V>>> entrySet();
}
