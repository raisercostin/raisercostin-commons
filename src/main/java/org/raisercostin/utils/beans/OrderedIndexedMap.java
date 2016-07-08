package org.raisercostin.utils.beans;

import java.util.List;
import java.util.Map;

public interface OrderedIndexedMap<Key, Value> extends Map<Key, Value> {
    public Value getValueAt(int index);

    public Key getKeyAt(int index);

    public Value removeKey(Key key);
    
    public List<Key> keyList();

	public void replaceValue(Key key, Value newValue);

	public Map<Key, Value> lightMapClone();

	public void replaceKey(Key key, Key newKey);
}
