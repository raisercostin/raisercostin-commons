package org.raisercostin.utils.beans;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.raisercostin.utils.ExceptionNotificationHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;


public interface BeanProcessor {

	public static final String STRING_ANY_VALUE = "*";
	public static final String VALUE = "Value";

	public abstract void setBeanFactory(BeanFactory beanFactory) throws BeansException;

	public abstract List<String> validateFields(Object bean, String pattern);

	public abstract void fixBean(Object bean, StringTranslator stringTranslator, ExceptionNotificationHandler exceptionNotificationHandler);

	void fixBeanOnlyTruncate(Object bean, StringTranslator stringTranslator, ExceptionNotificationHandler exceptionNotificationHandler);

	void fixBeanOnlyTranslate(Object bean, StringTranslator stringTranslator, ExceptionNotificationHandler exceptionNotificationHandler);

	/**
	 * Returns a list of properties that are mandatory but empty
	 * 
	 * @param bean
	 * @return
	 */

	List<String> checkMandatory(Object bean);

	Object getValue(Object object, String path);

	Object getValue(Object object, String path, OrderedIndexedMap<String, String> parameters);

	Object setValue(Object object, String path, Object value);

	Object setValue(Object object, String path, Object value, OrderedIndexedMap<String, String> parameters);

	Object setValue(Class<?> rawType, Object object, String path, Object value, OrderedIndexedMap<String, String> parameters);

	Object setValue(Class<?> rawType, Object object, String path, Object value);

	<T> T getValue(Object object, String path, Class<T> returningClass, OrderedIndexedMap<String, String> parameters);

	<T> T getValue(Object object, String path, Class<T> returningClass);

	List<String> describeObject(Class<?> objectClass);

	Method getClassMethod(Class<?> theClass, String methodName);

	void invokeSetter(Object parent, Object value, String paramName, Class c);

	void compareRequests(String expectedRequestName, OrderedIndexedMap<String, String> expected, Method method, Object[] actualArguments, String exceptionContextMessage);

	void compareBeans(OrderedIndexedMap<String, String> expected, Object actual, String exceptionContextMessage);

	void compareParameters(OrderedIndexedMap<String, String> expected, String parameterPrefix, Class<?> expectedType, Object actualValue, String exceptionContextMessage);

	boolean checkStringContains(String expectedContainedSubstrings, String actualTargetString, StringBuilder errorMessage, String message);

	void assertStringContains(String message, String expectedContainedSubstrings, String actualTargetString);

	void assertStringContainsCaseInsensitive(String message, String expectedContainedSubstrings, String actualTargetString);

	List<String> extractParameterNames(OrderedIndexedMap<String, String> map);

	String toString(Object value);

	<T> T createObject(Class<T> class1, OrderedIndexedMap<String, String> parameters, String path, boolean allowNulls);

	boolean canCreateObject(OrderedIndexedMap<String, String> parameters, Class<?> returnClass);

	Object createObject(OrderedIndexedMap<String, String> parameters, Method method, String exceptionContextMessage);

	void setObjectValues3(String path, Object object, OrderedIndexedMap<String, String> parameters, Class<?> outerType);

	/**
	 * The string values will be trimmed. If the cell after trimming, starts and ends with quote, the quotes are removed. After quotes removing the text is not trimmed anymore. Some special values for
	 * cells are: null - to be sure the value is null, no matter what the parameter type is: string or not. "" - to define an empty string. If the parameter type is not a string, an exception will be
	 * generated. "null" - to define a text containing letters 'n' 'u' 'l' 'l'. " text that starts with spaces" - a text that will not be trimmed
	 * 
	 * @param value
	 * @param setClass
	 * @return
	 * @throws ParseException
	 */

	<T> T createFromString(Class<T> setClass, String value) throws ParseException;

	String getLocalhost();

	<T> List<T> createList(List<OrderedIndexedMap<String, String>> parameterSets, int transactionType, Class<T> classT, boolean allowNulls);

	/**
	 * Gets a list of instantiated request parameters based on how many root prefixes there are in requestParameters.
	 * 
	 * @param method
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	Object[] getAsRequestParameters(Method method, OrderedIndexedMap<String, String> requestParameters) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	Map<String, String> checkLength(Object bean);
	
	String checkLength(Object bean, String fieldName);

	/**
	 * Validate bean according to common field annotations.
	 * 
	 * @param bean
	 */
	void validate(Object bean);	
}