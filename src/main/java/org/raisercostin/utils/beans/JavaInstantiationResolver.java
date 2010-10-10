package org.raisercostin.utils.beans;

import java.text.ParseException;


public class JavaInstantiationResolver implements InstantiationResolver {
	public Object newInstance(Class<?> type, Object value, OrderedIndexedMap<String, String> parameters, String path) {
		try {
			return BeanUtils.createFromString(type, (String) value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Can't instantiate an object of type [" + type + "] using parameters=["
					+ value + "]", e);
		}
	}
}
