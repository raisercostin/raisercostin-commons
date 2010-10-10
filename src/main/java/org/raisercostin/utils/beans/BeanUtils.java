package org.raisercostin.utils.beans;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.raisercostin.utils.ExceptionNotificationHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;


public class BeanUtils {
	public static final String STRING_ANY_VALUE = BeanProcessor.STRING_ANY_VALUE;

	public static final String VALUE = BeanProcessor.VALUE;

	private static BeanProcessor defaultBeanProcessor = new DefaultBeanProcessor();

	public static void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		defaultBeanProcessor.setBeanFactory(beanFactory);
	}

	public static void fixBean(Object bean, StringTranslator stringTranslator, ExceptionNotificationHandler exceptionNotificationHandler) {
		defaultBeanProcessor.fixBean(bean, stringTranslator, exceptionNotificationHandler);
	}

	public static void fixBean(Object bean, StringTranslator stringTranslator) {
		defaultBeanProcessor.fixBean(bean, stringTranslator, null);
	}

	public static void fixBeanOnlyTruncate(Object bean, StringTranslator stringTranslator) {
		defaultBeanProcessor.fixBeanOnlyTruncate(bean, stringTranslator, null);
	}

	public static void fixBeanOnlyTranslate(Object bean, StringTranslator stringTranslator) {
		defaultBeanProcessor.fixBeanOnlyTranslate(bean, stringTranslator, null);
	}

	public static List<String> checkMandatory(Object bean) {
		return defaultBeanProcessor.checkMandatory(bean);
	}

	public static Map<String, String> checkLength(Object bean) {
		return defaultBeanProcessor.checkLength(bean);
	}
	
	public static String checkLength(Object bean, String fieldName) {
		return defaultBeanProcessor.checkLength(bean, fieldName);
	}

	public static void assertStringContains(String message, String expectedContainedSubstrings, String actualTargetString) {
		defaultBeanProcessor.assertStringContains(message, expectedContainedSubstrings, actualTargetString);
	}

	public static boolean canCreateObject(OrderedIndexedMap<String, String> parameters, Class<?> returnClass) {
		return defaultBeanProcessor.canCreateObject(parameters, returnClass);
	}

	public static boolean checkStringContains(String expectedContainedSubstrings, String actualTargetString, StringBuilder errorMessage, String message) {
		return defaultBeanProcessor.checkStringContains(expectedContainedSubstrings, actualTargetString, errorMessage, message);
	}

	public static void compareBeans(OrderedIndexedMap<String, String> expected, Object actual, String exceptionContextMessage) {
		defaultBeanProcessor.compareBeans(expected, actual, exceptionContextMessage);
	}

	public static void compareParameters(OrderedIndexedMap<String, String> expected, String parameterPrefix, Class<?> expectedType, Object actualValue, String exceptionContextMessage) {
		defaultBeanProcessor.compareParameters(expected, parameterPrefix, expectedType, actualValue, exceptionContextMessage);
	}

	public static void compareRequests(String expectedRequestName, OrderedIndexedMap<String, String> expected, Method method, Object[] actualArguments, String exceptionContextMessage) {
		defaultBeanProcessor.compareRequests(expectedRequestName, expected, method, actualArguments, exceptionContextMessage);
	}

	public static <T> T createFromString(Class<T> setClass, String value) throws ParseException {
		return defaultBeanProcessor.createFromString(setClass, value);
	}

	public static <T> List<T> createList(List<OrderedIndexedMap<String, String>> parameterSets, int transactionType, Class<T> classT, boolean allowNulls) {
		return defaultBeanProcessor.createList(parameterSets, transactionType, classT, allowNulls);
	}

	public static <T> T createObject(Class<T> class1, OrderedIndexedMap<String, String> parameters, String path, boolean allowNulls) {
		return defaultBeanProcessor.createObject(class1, parameters, path, allowNulls);
	}

	public static Object createObject(OrderedIndexedMap<String, String> parameters, Method method, String exceptionContextMessage) {
		return defaultBeanProcessor.createObject(parameters, method, exceptionContextMessage);
	}

	public static List<String> describeObject(Class<?> objectClass) {
		return defaultBeanProcessor.describeObject(objectClass);
	}

	public static List<String> extractParameterNames(OrderedIndexedMap<String, String> map) {
		return defaultBeanProcessor.extractParameterNames(map);
	}

	public static Object[] getAsRequestParameters(Method method, OrderedIndexedMap<String, String> requestParameters) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return defaultBeanProcessor.getAsRequestParameters(method, requestParameters);
	}

	public static Method getClassMethod(Class<?> theClass, String methodName) {
		return defaultBeanProcessor.getClassMethod(theClass, methodName);
	}

	public static String getLocalhost() {
		return defaultBeanProcessor.getLocalhost();
	}

	public static <T> T getValue(Object object, String path, Class<T> returningClass, OrderedIndexedMap<String, String> parameters) {
		return defaultBeanProcessor.getValue(object, path, returningClass, parameters);
	}

	public static <T> T getValue(Object object, String path, Class<T> returningClass) {
		return defaultBeanProcessor.getValue(object, path, returningClass);
	}

	public static Object getValue(Object object, String path, OrderedIndexedMap<String, String> parameters) {
		return defaultBeanProcessor.getValue(object, path, parameters);
	}

	public static Object getValue(Object object, String path) {
		return defaultBeanProcessor.getValue(object, path);
	}

	public static void invokeSetter(Object parent, Object value, String paramName, Class c) {
		defaultBeanProcessor.invokeSetter(parent, value, paramName, c);
	}

	public static void setObjectValues(Object object, OrderedIndexedMap<String, String> parameters, Class<?> outerType) {
		defaultBeanProcessor.setObjectValues3("", object, parameters, outerType);
	}

	public static Object setValue(Class<?> rawType, Object object, String path, Object value, OrderedIndexedMap<String, String> parameters) {
		return defaultBeanProcessor.setValue(rawType, object, path, value, parameters);
	}

	public static Object setValue(Class<?> rawType, Object object, String path, Object value) {
		return defaultBeanProcessor.setValue(rawType, object, path, value);
	}

	public static Object setValue(Object object, String path, Object value, OrderedIndexedMap<String, String> parameters) {
		return defaultBeanProcessor.setValue(object, path, value, parameters);
	}

	public static Object setValue(Object object, String path, Object value) {
		return defaultBeanProcessor.setValue(object, path, value);
	}

	public static void assertStringContainsCaseInsensitive(String message, String expectedContainedSubstrings, String actualTargetString) {
		defaultBeanProcessor.assertStringContainsCaseInsensitive(message, expectedContainedSubstrings, actualTargetString);
	}

	public static String toString(Object value) {
		return defaultBeanProcessor.toString(value);
	}

	public static List<String> validateFields(Object bean, String pattern) {
		return defaultBeanProcessor.validateFields(bean, pattern);
	}

	public static void validate(Object bean) {
		defaultBeanProcessor.validate(bean);
	}
}
