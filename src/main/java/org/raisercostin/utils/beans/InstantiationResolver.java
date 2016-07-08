package org.raisercostin.utils.beans;


/**
 * Helper for instantiating objects that don't have a default constructor. A registry of such resolvers is kept and use
 * based on types needed.
 * 
 * @author raisercostin
 * 
 */
public interface InstantiationResolver {
	Object newInstance(Class<?> type, Object value, OrderedIndexedMap<String, String> parameters, String path);
}
