package org.raisercostin.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.UnhandledException;

/**
 * <p>
 * Assists in implementing {@link Object#clone()} methods for simple model classes.
 * </p>
 * 
 * <p>
 * This class aims to simplify the process of creating by including either all relevant fields from the object or the
 * fields specified using the {@link CloneBuilder#append(String)} method.
 * </p>
 * 
 * <p>
 * To use this class write code as follows:
 * </p>
 * 
 * <pre>
 * public class Person {
 *   String name;
 *   int age;
 *   boolean smoker;
 *   ...
 * 
 *   public Object clone() {
 *     return new CloneBuilder(this).
 *       append(&quot;name&quot;).
 *       append(&quot;age&quot;).
 *       append(smoker).
 *       toClone();
 *   }
 * }
 * </pre>
 * 
 * <p>
 * Alternatively, there is a method that uses reflection to determine the fields to clone.. Because these fields are
 * usually private, the method, <code>reflectionClone</code>, uses <code>AccessibleObject.setAccessible</code> to change
 * the visibility of the fields. This will fail under a security manager, unless the appropriate permissions are set up
 * correctly. It is also slower than testing explicitly.
 * </p>
 * 
 * <p>
 * A typical invocation for this method would look like:
 * </p>
 * 
 * <pre>
 * public Object clone() {
 * 	return CloneBuilder.reflectionClone(this);
 * }
 * </pre>
 * 
 * <p>
 * <b>NOTE:</b> This method only supports classes that have default parameter-less contructor.
 * </p>
 * 
 * @author Dave Meikle
 */
public class CloneBuilder {

	/**
	 * <p>
	 * The Object to be Cloned.
	 * </p>
	 */
	private final Object object;

	/**
	 * <p>
	 * The Cloned Object.
	 * </p>
	 */
	private final Object clone;

	/**
	 * <p>
	 * A Map containing all the fields from the Class and its Super Classes.
	 * </p>
	 */
	private final Map<String, String> fields;

	/**
	 * Public Contructor.
	 * 
	 * @param object
	 *            the Object to Clone.
	 */
	public CloneBuilder(Object object) {
		this.object = object;
		this.clone = getCloneObject(object);
		fields = getAllFields(this.object);
	}

	/**
	 * Appends the value of the passed field name to the Clone Object.
	 * 
	 * @param value
	 *            a <code>String</code> contaning the field name to append.
	 * @return the current <code>CloneBuilder</code> instance.
	 */
	public CloneBuilder append(String value) {
		if (fields.containsKey(value)) {
			String clazzName = fields.get(value);
			setFieldValue(value, clazzName, this.object, this.clone);
		} else {
			throw new IllegalArgumentException("Field " + value + " does not exist");
		}
		return this;
	}

	/**
	 * This method creates a clone of the passed <code>Object</code> by using reflection to access all the classes
	 * fields, including super-class fields.
	 * 
	 * @param object
	 *            the <code>Object</code> to clone.
	 * @return the new clone <code>Object</code>.
	 */
	public static Object reflectionClone(Object object) {
		Object clone = getCloneObject(object);
		reflectionCopy(clone, object);
		return clone;
	}

	public static void reflectionCopy(Object clone, Object object) {
		Map<String, String> destFields = getAllFields(clone);
		Map<String, String> sourceFields = getAllFields(object);
		Map<String, String> fields = filterSourceFields(sourceFields, destFields.keySet());
		Iterator<String> iter = fields.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = fields.get(key);
			setFieldValue(key, value, object, clone);
		}
	}

	private static Map<String, String> filterSourceFields(Map<String, String> sourceFields, Set<String> keySet) {
		Map<String, String> result = new HashMap<String, String>();
		for (Entry<String, String> entry : sourceFields.entrySet()) {
			if (keySet.contains(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Returns the Clone Built by the <code>CloneBuilder</code>..
	 * 
	 * @return the clone <code>Object</code> built by the <code>CloneBuilder</code>
	 */
	public Object toClone() {
		return this.clone;
	}

	/**
	 * <p>
	 * Gets a new instance of the passed <code>Object</code> to use as a clone.
	 * </p>
	 * <p>
	 * This method only supports classes that have default parameter-less contructor.
	 * </p>
	 * 
	 * @param object
	 *            the <code>Object</code> to get a new instance of.
	 * @return a new instance of the passed <code>Object</code>.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getCloneObject(T object) {
		try {
			// Check to see if it is an inner class. If so we need to pass the declaring class
			// as the first argument
			if (object.getClass().isMemberClass()) {
				// this is an inner class, so we have an extra parameter
				Constructor<T>[] csr = (Constructor<T>[]) object.getClass().getDeclaredConstructors();
				for (int i = 0; i < csr.length; i++) {
					if (csr[i].getParameterTypes().length == 1) {
						return csr[i].newInstance(new Object[] { object.getClass().getDeclaringClass().newInstance() });
					}
				}
				throw new InternalError("No Default Public Contructor");
			} else {
				return (T) object.getClass().newInstance();
			}
		} catch (Exception ex) {
			throw new UnhandledException(ex);
		}
	}

	/**
	 * This method is used internally to set the value of field in passed <code>Object</code> based on another passed
	 * object, identified by the field name provided. The Class name is also provided to allow the setting of the field
	 * using reflection.
	 * 
	 * @param fieldName
	 *            the name of the <code>Field</code> to be set.
	 * @param clazzName
	 *            the name of the <code>Class</code> this field belongs too.
	 * @param existingObject
	 *            the existing <code>Object</code> containing the value to use.
	 * @param newObject
	 *            the new <code>Object</code> to set the value on.
	 */
	private static void setFieldValue(String fieldName, String clazzName, Object existingObject, Object newObject) {
		try {
			Class<?> clazz = Class.forName(clazzName);
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (!Modifier.isStatic(field.getModifiers())) {
				field.set(newObject, field.get(existingObject));
			}
		} catch (Exception ex) {
			throw new UnhandledException(ex);
		}
	}

	/**
	 * <p>
	 * Get all the fields declared in a Class and its super-classes.
	 * </p>
	 * <p>
	 * This is used in the contructor and {@link CloneBuilder#reflectionClone(Object object)} method to get Field Name -
	 * Class Name pairs to allow the setting of the correct field value in the clone object.
	 * </p>
	 * 
	 * @param obj
	 *            the <code>Object</code> to get all the fields for..
	 * @return a <code>Map</code> containing a Field Name - Class Name Pair.
	 */
	private static Map<String, String> getAllFields(Object obj) {
		Class<?> clazz = obj.getClass();
		Map<String, String> map = new HashMap<String, String>();
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				map.put(fields[i].getName(), clazz.getName());
			}
			clazz = clazz.getSuperclass();
		}
		return map;
	}
}
