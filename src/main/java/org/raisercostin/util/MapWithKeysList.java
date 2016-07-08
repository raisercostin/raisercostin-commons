package org.raisercostin.util;

import java.util.*;

//TODO could be improved for efficiency
//TODO Should be threadsafe
/** Should be threadsafe. */
public class MapWithKeysList<K, V> implements Map<K, V> {
	private final Map<K, V> map = Collections.synchronizedMap(new TreeMap<K, V>());

	public List<K> keyList() {
		synchronized (map) {
			return new ArrayList<K>(keySet());
		}
	}

	public Set<K> keySetSerializable() {
		synchronized (map) {
			return new HashSet<K>(keySet());
		}
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}
}
