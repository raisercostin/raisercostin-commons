package org.raisercostin.utils.beans;

import java.security.InvalidParameterException;
import java.util.List;


public class CompositeInstantiationResolver implements InstantiationResolver {
	private List<InstantiationResolver> instantiationResolvers;

	public void setInstantiationResolvers(List<InstantiationResolver> instantiationResolvers) {
		this.instantiationResolvers = instantiationResolvers;
	}

	public Object newInstance(Class<?> type, Object value, OrderedIndexedMap<String, String> parameters, String path) {
		for (InstantiationResolver instantiationResolver : instantiationResolvers) {
			Object result = instantiationResolver.newInstance(type, value, parameters, path);
			if (result != null) {
				return result;
			}
		}
		throw new InvalidParameterException("Couldn't find any InstantiationResolver for class [" + type + "].");
	}
}
