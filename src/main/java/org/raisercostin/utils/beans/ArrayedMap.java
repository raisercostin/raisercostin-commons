package org.raisercostin.utils.beans;

import java.util.*;

public class ArrayedMap<Key, Value> extends HashMap<Key, Value> implements OrderedIndexedMap<Key, Value> {
	private static final long serialVersionUID = 8544588595673314378L;

	private final List<Key> allKeys;


	public ArrayedMap() {
		super();
		this.allKeys = new ArrayList<Key>();
	}

	public ArrayedMap(int i, float f) {
		super(i, f);
		this.allKeys = new ArrayList<Key>();
	}

	public ArrayedMap(int i) {
		super(i);
		this.allKeys = new ArrayList<Key>();
	}

	public ArrayedMap(Map<? extends Key, ? extends Value> arg0) {
		super(arg0);
		this.allKeys = new ArrayList<Key>();
	}

	private ArrayedMap(ArrayedMap<Key, Value> arrayedMap, boolean allowSetIterator) {
		this(arrayedMap);
	}

	@Override
	public Key getKeyAt(int index) {
		return this.allKeys.get(index);
	}

	@Override
	public Value getValueAt(int index) {
		return get(this.allKeys.get(index));
	}

	@Override
	public Value put(Key key, Value value) {
		if (!this.allKeys.contains(key)) {
			this.allKeys.add(key);
		}

		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends Key, ? extends Value> m) {
		throw new IllegalStateException("Not implemented yet.");
	}

	@Override
	public Value removeKey(Key key) {
		this.allKeys.remove(key);
		return super.remove(key);
	}

	@Override
	public Value remove(Object obj) {
		throw new IllegalStateException("Not implemented yet.");
	}

	@Override
	public List<Key> keyList() {
		return this.allKeys;
	}

	@Override
	@Deprecated
	public Set<Key> keySet() {
		return super.keySet();
//		if (allowSetIterator) {
//			return super.keySet();
//		}
//		throw new IllegalStateException("Use keyList instead.");
	}

	@Override
	public void replaceValue(Key key, Value newValue) {
		super.remove(key);
		super.put(key, newValue);
	}

	@Override
	public Map<Key, Value> lightMapClone() {
		return new ArrayedMap<Key, Value>(this, true);
	}

	@Override
	public void replaceKey(Key key, Key newKey) {
		put(newKey, get(key));
		removeKey(key);
	}
}
