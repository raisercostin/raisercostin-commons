package org.raisercostin.utils.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.raisercostin.utils.ExceptionNotificationHandler;
import org.raisercostin.utils.annotations.CustomTranslator;
import org.raisercostin.utils.annotations.Length;
import org.raisercostin.utils.annotations.Mandatory;
import org.raisercostin.utils.annotations.Trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


public class DefaultBeanProcessor implements BeanProcessor {
    private static final String HAS_CONSTRUCTOR_FROM_STRING = "Value";

    private String localhost = null;

    private final Object localhostLock = new Object();

    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanProcessor.class);

    private static final String DEFAULT_DATE_FORMAT = "EEE MMM d HH:mm:ss yyyy";

    private static final String ANY_EXPECTED_VALUE = "*";

    private static final String REQUEST_NAME = "RequestName";

    private static final String RESPONSE_FOR_REQUEST_NAME = "ResponseForRequestName";

    private static final Object RESPONSE_EXCEPTION_TYPE = "exceptionType";

    private static final Object RESPONSE_EXCEPTION_MESSAGE = "exceptionMessage";

    private static final Pattern number = Pattern.compile("\\d+");

    private final List<Class<?>> ignoredClasses = ignoreClass();

    private BeanFactory beanFactory;

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#setBeanFactory(org.springframework .beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private InstantiationResolver getInstantiationResolver() {
        return (InstantiationResolver) beanFactory.getBean("instantiationResolver");
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#validateFields(java.lang.Object, java.lang.String)
     */
    public List<String> validateFields(Object bean, String pattern) {
        Method[] methods = bean.getClass().getMethods();
        List<String> result = new ArrayList<String>();
        Pattern p = Pattern.compile(pattern);
        for (Method m : methods) {
            if ((m.getReturnType() == String.class) && isGetter(m)) {
                try {
                    String value = (String) m.invoke(bean);
                    if (!validate(value, p)) {
                        String name = m.getName();
                        result.add(name.substring(3, name.length()));
                    }
                } catch (ClassCastException e) {
                    ; // can get this when field is not String
                } catch (Exception e) {
                    logger.warn("Exception occured during validating field: " + m.getName() + " of object " + bean, e);
                }
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#fixBean(java.lang.Object,
     * com.xoom.integration.util.StringTranslator)
     */
    public void fixBean(Object bean, StringTranslator stringTranslator,
            ExceptionNotificationHandler exceptionNotificationHandler) {
        fixBeanCustom(bean, stringTranslator, true, true, exceptionNotificationHandler);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#fixBeanOnlyTruncate(java.lang .Object,
     * com.xoom.integration.util.StringTranslator)
     */
    public void fixBeanOnlyTruncate(Object bean, StringTranslator stringTranslator,
            ExceptionNotificationHandler exceptionNotificationHandler) {
        fixBeanCustom(bean, stringTranslator, false, true, exceptionNotificationHandler);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#fixBeanOnlyTranslate(java.lang .Object,
     * com.xoom.integration.util.StringTranslator)
     */
    public void fixBeanOnlyTranslate(Object bean, StringTranslator stringTranslator,
            ExceptionNotificationHandler exceptionNotificationHandler) {
        fixBeanCustom(bean, stringTranslator, true, false, exceptionNotificationHandler);
    }

    private void fixBeanCustom(Object bean, StringTranslator stringTranslator, boolean translate, boolean truncate,
            ExceptionNotificationHandler exceptionNotificationHandler) {
        if (!translate && !truncate) {
            return;
        }
        Class c = bean.getClass();
        Method[] methods = c.getMethods();
        for (Method m : methods) {
            if ((m.getReturnType() == String.class) && isGetter(m)) {
                try {
                    String initial = (String) m.invoke(bean);
                    if (initial == null) {
                        continue;
                    }
                    String fixed = initial;
                    if (translate) {
                        CustomTranslator customTranslatorAnnot = m.getAnnotation(CustomTranslator.class);
                        if (customTranslatorAnnot != null) {
                            String customTranslatorName = customTranslatorAnnot.translator();
                            StringTranslator customTranslator = (StringTranslator) beanFactory
                                    .getBean(customTranslatorName);
                            fixed = customTranslator.translateString(initial);
                        } else {
                            fixed = stringTranslator.translateString(initial);
                        }
                    }
                    String name = m.getName();
                    name = name.substring(3, name.length());

                    if (truncate) {
                        if (m.getAnnotation(Trim.class) != null) {
                            fixed = org.apache.commons.lang.StringUtils.trim(fixed);
                        }
                        Length lengthAnnotation = m.getAnnotation(Length.class);
                        if ((m.getAnnotation(Truncate.class) != null) && (lengthAnnotation != null)) {
                            if (lengthAnnotation.value() > 0) {
                                fixed = stringTranslator.truncateString(fixed, lengthAnnotation.value());
                            }
                        }
                    }

                    if (!fixed.equals(initial)) {
                        logger
                                .debug("Replace value of field [" + name + "] from [" + initial + "] to [" + fixed
                                        + "].");
                        Method method = null;
                        try {
                            method = c.getMethod("set" + name, String.class);
                        } catch (Exception e) {
                            throw new RuntimeException("Couldn't configure field " + name
                                    + "] to change the value from [" + initial + "] to [" + fixed + "].", e);
                        }
                        method.invoke(bean, fixed);
                    }
                } catch (ClassCastException e) {
                    ; // can get this when field is not String
                } catch (Exception e) {
                    if (exceptionNotificationHandler != null) {
                        exceptionNotificationHandler.notifyException(e);
                    } else {
                        logger.warn("Exception occured during fixing field: " + m.getName() + " of object " + bean, e);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#checkMandatory(java.lang.Object)
     */

    public List<String> checkMandatory(Object bean) {
        Class c = bean.getClass();
        Method[] methods = c.getMethods();
        List<String> emptyMandatoryProperties = new ArrayList<String>();

        for (Method m : methods) {
            if (isGetter(m)) {
                try {
                    if (m.getAnnotation(Mandatory.class) != null) {
                        String value = (String) m.invoke(bean);
                        if ((value == null) || ((m.getReturnType() == String.class) && ((value).trim().length() == 0))) {
                            String name = m.getName();
                            name = name.substring(3, name.length());
                            emptyMandatoryProperties.add(name);
                        }
                    }
                } catch (ClassCastException e) {
                    ; // can get this when field is not String
                } catch (Exception e) {
                    logger.warn("Exception occured during fixing field: " + m.getName() + " of object " + bean, e);
                }
            }
        }
        return emptyMandatoryProperties;
    }

    public Map<String, String> checkLength(Object bean) {
        Class c = bean.getClass();
        Method[] methods = c.getMethods();
        Map<String, String> mapLengthPropertiesExceedMax = new HashMap<String, String>();

        for (Method m : methods) {
            if (isGetter(m)) {
                try {
                    String initial = (String) m.invoke(bean);
                    if (initial == null) {
                        continue;
                    }
                    Length lengthAnnotation = m.getAnnotation(Length.class);
                    if ((lengthAnnotation != null)) {
                        if (lengthAnnotation.value() > 0) {
                            if (initial.length() > lengthAnnotation.value()) {
                                String name = m.getName();
                                name = name.substring(3, name.length());
                                mapLengthPropertiesExceedMax.put(name, new String("[" + initial + ","
                                        + new Integer(initial.length()).toString() + ">"
                                        + new Integer(lengthAnnotation.value()).toString())
                                        + "]");
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    ; // can get this when field is not String
                } catch (Exception e) {
                    logger.warn("Exception occured during fixing field: " + m.getName() + " of object " + bean, e);
                }
            }
        }
        return mapLengthPropertiesExceedMax;
    }
    
    public String checkLength(Object bean, String fieldName) {    	        
        try {
        	Method getter = bean.getClass().getMethod("get" + StringUtils.capitalize(fieldName));
            String initial = (String) getter.invoke(bean);
            if (initial == null) {
                return null;
            }
            Length lengthAnnotation = getter.getAnnotation(Length.class);
            if ((lengthAnnotation != null)) {
                if (lengthAnnotation.value() > 0) {
                    if (initial.length() > lengthAnnotation.value()) {
                        String name = getter.getName();
                        name = name.substring(3, name.length());
                        return new String("[" + initial + ","
                                + new Integer(initial.length()).toString() + ">"
                                + new Integer(lengthAnnotation.value()).toString())
                                + "]";
                    }
                }
            }
        } catch (ClassCastException e) {
            ; // can get this when field is not String
        } catch (Exception e) {
            logger.warn("Exception occured during checking field length: " + fieldName + " of object " + bean, e);
        }
    	return null;
    }

    private boolean isGetter(Method m) {
        return m.getName().startsWith("get") && (m.getParameterTypes().length == 0)
                && !m.getReturnType().equals(void.class) && !Modifier.isStatic(m.getModifiers())
        /* removed the following check because we need truncate on transient fields as well (transnetwork for example) */
        /* && !isTransient(m) */;
    }

    private boolean isTransient(Method m) {
        Transient annotation = m.getAnnotation(Transient.class);
        return annotation != null;
    }

    private boolean validate(String strSource, Pattern p) {
        if ((strSource == null) || (strSource.trim().length() == 0)) {
            return true;
        }
        Matcher m = p.matcher(strSource);
        return m.matches();
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#getValue(java.lang.Object, java.lang.String)
     */
    public Object getValue(Object object, String path) {
        return getValueInternal(object, path.split("\\/"), path, null);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#getValue(java.lang.Object, java.lang.String,
     * com.xoom.integration.util.OrderedIndexedMap)
     */
    public Object getValue(Object object, String path, OrderedIndexedMap<String, String> parameters) {
        return getValueInternal(object, path.split("\\/"), path, parameters);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#setValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public Object setValue(Object object, String path, Object value) {
        return setValueInternal(object, value, path.split("\\/"), path, null);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#setValue(java.lang.Object, java.lang.String, java.lang.Object,
     * com.xoom.integration.util.OrderedIndexedMap)
     */
    public Object setValue(Object object, String path, Object value, OrderedIndexedMap<String, String> parameters) {
        return setValueInternal(object, value, path.split("\\/"), path, parameters);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#setValue(java.lang.Class, java.lang.Object, java.lang.String,
     * java.lang.Object, com.xoom.integration.util.OrderedIndexedMap)
     */
    public Object setValue(Class<?> rawType, Object object, String path, Object value,
            OrderedIndexedMap<String, String> parameters) {
        return setValueInternal(rawType, object, value, path.split("\\/"), path, parameters);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#setValue(java.lang.Class, java.lang.Object, java.lang.String,
     * java.lang.Object)
     */
    public Object setValue(Class<?> rawType, Object object, String path, Object value) {
        return setValueInternal(rawType, object, value, path.split("\\/"), path, null);
    }

    public <T> T getValue(Object object, String path, Class<T> returningClass,
            OrderedIndexedMap<String, String> parameters) {
        return (T) getValueInternal(object, path.split("\\/"), path, parameters);
    }

    public <T> T getValue(Object object, String path, Class<T> returningClass) {
        return (T) getValueInternal(object, path.split("\\/"), path, null);
    }

    public List<String> describeObject(Class<?> objectClass) {
        List<String> result = new ArrayList<String>();
        describeObject("", result, objectClass, 5, null);
        return result;
    }

    private void describeObject(String prefix, List<String> result, Class<?> objectClass, int deep, Method parentMethod) {
        if (deep == 0) {
            return;
        }
        if (objectClass.getComponentType() != null) {
            // is array
            String path = (prefix.length() > 0 ? prefix + "/" : prefix) + "1";
            describeObject(path, result, objectClass.getComponentType(), deep - 1, null);
        } else {
            boolean hasProperties = false;
            Method[] methods = objectClass.getMethods();
            for (Method element : methods) {
                if (element.getName().startsWith("get")) {
                    if (element.getParameterTypes().length == 0) {
                        String path = (prefix.length() > 0 ? prefix + "/" : prefix) + element.getName().substring(3);
                        Class<?> returnType = element.getReturnType();
                        if (!atomicClass(returnType)) {
                            describeObject(path, result, returnType, deep - 1, element);
                        } else if (!ignoreClass(returnType)) {
                            result.add(path);
                            hasProperties = true;
                        }
                    } else if (element.getParameterTypes().length == 1) {
                        String path = (prefix.length() > 0 ? prefix + "/" : prefix) + "1";
                        if (parentMethod != null) {
                            Class<?> annotation = getReturnedGenericType(parentMethod);
                            if (annotation != null) {
                                describeObject(path, result, annotation, deep - 1, element);
                                hasProperties = true;
                            } else {
                                result.add(path);
                                hasProperties = true;
                            }
                        }
                    }
                }
            }
            if (!hasProperties) {
                result.add(prefix);
            }
        }
    }

    private boolean ignoreClass(Class<?> type) {
        return ignoredClasses.contains(type);
    }

    private boolean atomicClass(Class<?> type) {
        if (type.getName().startsWith("java.lang")) {
            return true;
        }
        if (type.getName().startsWith("org.joda.time")) {
            return true;
        }
        return false;
    }

    private List<Class<?>> ignoreClass() {
        List<Class<?>> result = new ArrayList<Class<?>>();
        result.add(Class.class);
        return result;
    }

    private Object getValueInternal(Object object, String[] attributeNames, String path,
            OrderedIndexedMap<String, String> parameters) {
        try {
            Object currentObject = object;
            LastMethod lastMethod = new LastMethod();
            int current = 0;
            for (String element : attributeNames) {
                current += attributeNames.length + 1;
                currentObject = invokeGetter(currentObject, element, false, lastMethod, path.substring(0, current - 1),
                        parameters);
            }
            return currentObject;
        } catch (SecurityException e) {
            throwGetterException(object, path, e);
        } catch (NoSuchMethodException e) {
            throwGetterException(object, path, e);
        } catch (IllegalArgumentException e) {
            throwGetterException(object, path, e);
        } catch (IllegalAccessException e) {
            throwGetterException(object, path, e);
        } catch (InvocationTargetException e) {
            throwGetterException(object, path, e);
        } catch (InstantiationException e) {
            throwGetterException(object, path, e);
        } catch (ParseException e) {
            throwGetterException(object, path, e);
        }
        return null;
    }

    private Object invokeSetter(Object object, String attribute, Object value) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            ParseException {
        Object result = null;
        if (!ANY_EXPECTED_VALUE.equals(value)) {
            if (attribute.matches("[0-9]+")) {
                if (List.class.isAssignableFrom(object.getClass())) {
                    List list = (List) object;
                    int index = Integer.parseInt(attribute) - 1;
                    list.add(index, value);
                } else {
                    throw new RuntimeException("Can't have numbers in path since you don't have a List.");
                }
            } else {
                Method setter = findSetter(object, attribute);
                if (setter == null) {
                    throw new RuntimeException("Couldn't find a stter for attribute [" + attribute + "].");
                }
                if (setter.getParameterTypes().length != 1) {
                    throw new RuntimeException("The setter " + setter + "should have only one parameter.");
                }
                Class<?> setterType = setter.getParameterTypes()[0];
                result = getCurrentValue(object, attribute);

                if (value instanceof String) {
                    value = createFromString(setterType, (String) value);
                }
                if ((value == null) && setterType.isPrimitive()) {
                    throw new IllegalArgumentException("Can't pass null to a setter for a primitive type "
                            + object.getClass().getName() + ".set" + StringUtils.capitalize(attribute) + "(null)");
                }
                object.getClass().getMethod("set" + StringUtils.capitalize(attribute), setterType)
                        .invoke(object, value);
            }
        }
        return result;
    }

    private Object getCurrentValue(Object object, String attribute) throws IllegalAccessException,
            InvocationTargetException {
        Object result = null;
        Method getter = null;
        try {
            getter = object.getClass().getMethod("get" + StringUtils.capitalize(attribute));
        } catch (NoSuchMethodException e) {
            try {
                getter = object.getClass().getMethod("is" + StringUtils.capitalize(attribute));
            } catch (NoSuchMethodException e2) {
                logger.debug("Couldn't find a getter (get/is) for property " + StringUtils.capitalize(attribute)
                        + " on setter. Return null.");
                getter = null;
            }
        }
        if (getter != null) {
            result = getter.invoke(object);
        }
        return result;
    }

    private Method findSetter(Object object, String attribute) {
        return getClassMethod(object.getClass(), "set" + StringUtils.capitalize(attribute));
    }

    private Object invokeGetter(Object object, String attribute, boolean create, LastMethod parentMethod, String path,
            OrderedIndexedMap<String, String> parameters) throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            ParseException {
        Object result = null;
        Method method = null;
        if (attribute.matches("[0-9]+")) {
            if (List.class.isAssignableFrom(object.getClass())) {
                if (parentMethod.getRawType() == null) {
                    throw new IllegalArgumentException("Couldn't get rawType for [" + path + "] neither from a ["
                            + "hint:rawtype" + "] property neither from an [@" + GenericType.class + "] annotation.");
                }
                List<Object> list = (List<Object>) object;
                int size = list.size();
                int index = Integer.parseInt(attribute) - 1;
                if (index >= size) {
                    if (create) {
                        for (int i = size; i < index; i++) {
                            list.add(null);
                        }
                        list.add(invokeConstructor2(parentMethod.getRawType(), parameters, path, false));
                    }
                }
                result = list.get(index);
            } else if(object.getClass().isArray()) {            	
                Object[] array = (Object[]) object;
                int size = array.length;
                int index = Integer.parseInt(attribute) - 1;
                if(index >= size) {
                	throw new RuntimeException("Array size is ["+size+"], but index is ["+index+"]. Path is ["+path+"] and component type is ["+object.getClass().getComponentType()+"].");
                }
                if (create & array[index] == null) {
                	array[index] = invokeConstructor2(object.getClass().getComponentType(), parameters, path, false);
                }                
                result = array[index];
            }
            else {
                throw new RuntimeException("Can't have numbers in path since you don't have a List or Array.");
            }
        } else {
            try {
                method = object.getClass().getMethod("get" + StringUtils.capitalize(attribute));
            } catch (NoSuchMethodException e) {
                try {
                    method = object.getClass().getMethod("is" + StringUtils.capitalize(attribute));
                } catch (NoSuchMethodException e2) {
                    throw new RuntimeException("No getter(with get or is) is found.", e);
                }
            }

            result = method.invoke(object);
            if (create && (result == null)) {
                result = invokeConstructor2(method.getReturnType(), parameters, path, false);
            }
            if (result != null) {
                invokeSetter(object, attribute, result);
            }
        }
        parentMethod.setMethod(method);
        return result;
    }

    private class LastMethod {
        private Method method;

        private Class<?> rawType;

        public LastMethod(Class<?> rawType) {
            this.rawType = rawType;
        }

        public LastMethod() {
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Class<?> getRawType() {
            if (method == null) {
                return rawType;
            }
            Class<?> annotation = getReturnedGenericType(method);
            if (annotation == null) {
                throw new RuntimeException(
                        "The method "
                                + method.getDeclaringClass().getName()
                                + "."
                                + method.getName()
                                + "() should have an @GenericType(<type>.class) annotation because returns a list with unknown(at runtime) raw type.");
            }
            return annotation;
        }

        public Method getMethod() {
            return method;
        }
    }

    private Object invokeConstructor(String beanClassName, OrderedIndexedMap<String, String> parameters, String path,
            boolean allowNulls) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> theClass = Class.forName(beanClassName);
        return invokeConstructor2(theClass, parameters, path, allowNulls);
    }

    private Object invokeConstructor(String beanClassName, String parameters) throws ClassNotFoundException,
            IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Class<?> theClass = Class.forName(beanClassName);
        return invokeConstructor3(theClass, parameters);
    }

    private Object invokeConstructor2(Class<?> type, OrderedIndexedMap<String, String> parameters, String path,
            boolean allowNulls) throws InstantiationException, IllegalAccessException {
        Object result = null;

        if (List.class.isAssignableFrom(type)) {
            result = new ArrayList<Object>();
        }
        else if(type.isArray()) {
        	int size = getArraySize(type, parameters, path);
        	result = Array.newInstance(type.getComponentType(), size);
        }
        else {           
            String value = null;
            if (parameters != null) {
                value = parameters.get(path);
                if (value == null) {
                    value = parameters.get(VALUE);
                }
            }
            if (value != null) {
                try {
                    result = createObjectFromString(type, value);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if ((parameters != null) && parameters.containsKey(VALUE) && (parameters.get(VALUE) == null)) {
                result = null;
            } else {
                try {
                    result = type.newInstance();
                } catch (InstantiationException e) {
                    if (parameters != null) {
                        value = parameters.get(VALUE);
                    }
                    try {
                        result = createObjectFromString(type, value);
                        if (result == null) {
                            result = getInstantiationResolver().newInstance(type, value, parameters, path);
                        }
                    } catch (ParseException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            }
        }
        if (!allowNulls && (result == null)) {
            throw new RuntimeException("The constructor shouldn't return a null value.");
        }
        return result;
    }

    private int getArraySize(Class<?> type, OrderedIndexedMap<String, String> parameters, String path) {
		int size = 0;
		String regex = path + "/[0-9]+/.*";
		List<String> paramKeys = parameters.keyList();
		for (String keyParam : paramKeys) {
			if(keyParam.matches(regex)) {
				int pos = keyParam.indexOf(path);
				int pos2 = keyParam.indexOf('/', pos+path.length()+1);
				String number = keyParam.substring(pos+path.length()+1, pos2);
				int index = Integer.parseInt(number);
				if(index>size) {
					size = index;
				}
			}
			
		}
		
		return size;
	}

	private Object invokeConstructor3(Class<?> theClass, String parameter) throws IllegalArgumentException,
            SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (List.class.isAssignableFrom(theClass)) {
            return new ArrayList<Object>();
        }
        if (theClass.isArray()) {
            throw new RuntimeException("Can't create objects of type array: [" + theClass.getName() + "].");
        }
        try {
            Constructor<?> constructor = theClass.getConstructor(String.class);
            return constructor.newInstance(parameter);
        } catch (NoSuchMethodException e) {
            Object result = invokeAnyConstructorWithAtLeastOneStringParameter(theClass, parameter);
            if (result != null) {
                return result;
            }
            throw e;
        }
    }

    private Object invokeAnyConstructorWithAtLeastOneStringParameter(Class<?> theClass, String parameter)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] constructor = theClass.getConstructors();
        Object result = null;
        for (Constructor<?> element : constructor) {
            Class<?>[] parameterTypes = element.getParameterTypes();
            int size = parameterTypes.length;
            int pos = -1;
            for (int j = 0; j < size; j++) {
                if (parameterTypes[j].equals(String.class)) {
                    pos = j;
                    break;
                }
            }
            if (pos != -1) {
                Object[] parameters = new Object[size];
                for (int j = 0; j < size; j++) {
                    if (parameterTypes[j].equals(String.class)) {
                        parameters[j] = parameter;
                    } else {
                        parameters[j] = null;
                    }
                }
                result = element.newInstance(parameters);
                break;
            }
        }
        return result;
    }

    private Object setValueInternal(Object object, Object value, String[] attributeNames, String path,
            OrderedIndexedMap<String, String> parameters) {
        return setValueInternal(null, object, value, attributeNames, path, parameters);
    }

    private Object setValueInternal(Class<?> rawType, Object object, Object value, String[] attributeNames,
            String path, OrderedIndexedMap<String, String> parameters) {
        try {
            Object currentObject = object;
            LastMethod lastMethod = new LastMethod(rawType);
            int current = 0;
            for (int i = 0; i < attributeNames.length - 1; i++) {
                current += attributeNames[i].length() + 1;
                String newPath = path.substring(0, current);
                if (newPath.endsWith("/")) {
                    newPath = newPath.substring(0, newPath.length() - 1);
                }
                currentObject = invokeGetter(currentObject, attributeNames[i], true, lastMethod, newPath, parameters);
            }
            return invokeSetter(currentObject, attributeNames[attributeNames.length - 1], value);
        } catch (SecurityException e) {
            throwSetterException(object, value, path, e);
        } catch (NoSuchMethodException e) {
            throwSetterException(object, value, path, e);
        } catch (IllegalArgumentException e) {
            throwSetterException(object, value, path, e);
        } catch (IllegalAccessException e) {
            throwSetterException(object, value, path, e);
        } catch (InvocationTargetException e) {
            throwSetterException(object, value, path, e);
        } catch (InstantiationException e) {
            throwSetterException(object, value, path, e);
        } catch (ParseException e) {
            throwSetterException(object, value, path, e);
        } catch (RuntimeException e) {
            throwSetterException(object, value, path, e);
        }
        return null;
    }

    private void throwGetterException(Object object, String path, Exception e) {
        throw new RuntimeException(e.getMessage() + " when tring to get value from [" + object.getClass()
                + "] using path=[" + path + "].", e);
    }

    private void throwSetterException(Object object, Object value, String path, Exception e) {
        Class<? extends Object> objectClass = null;
        if (object != null) {
            objectClass = object.getClass();
        }
        throw new RuntimeException(e.getMessage() + " when trying to set value on [" + objectClass + "] using path=["
                + path + "] with value of type=[" + value + "].", e);
    }

    @Deprecated
    private String getDtoClassName(Map<String, String> parameters) {
        String dtoClassName = null;
        if ((parameters != null) && !parameters.isEmpty()) {
            for (String paramName : parameters.keySet()) {
                if (paramName.startsWith("dto:")) {
                    if (dtoClassName != null) {
                        throw new RuntimeException("More than 1 parameter start with \"dto.\"!");
                    }
                    dtoClassName = paramName.substring(4, paramName.length());
                }
            }
        }
        return dtoClassName;
    }

    @Deprecated
    private Object constructDto12(Map<String, String> firstSet, String dtoClassName, String exceptionContextMessage) {
        if (dtoClassName == null) {
            return null;
        }
        Object dto = null;

        String[] constructorArgs = null;
        if (dtoClassName.contains("(")) {
            constructorArgs = getConstructorArgs(dtoClassName);
            dtoClassName = dtoClassName.substring(0, dtoClassName.indexOf("("));
        }
        if (dtoClassName.equals("java.util.List")) {
            dto = new ArrayList<Object>();
        } else {
            Class dtoClass = null;
            try {
                dtoClass = Class.forName(dtoClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Exception loading class [" + dtoClassName + "]", e);
            }
            try {
                if (constructorArgs != null) {
                    Class[] types = new Class[constructorArgs.length];
                    for (int i = 0; i < constructorArgs.length; i++) {
                        types[i] = String.class;
                    }
                    dto = dtoClass.getConstructor(types).newInstance(constructorArgs);
                } else {
                    dto = dtoClass.newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception instantiating class [" + dtoClassName + "]", e);
            }
        }
        try {
            setBeanValues(dto, firstSet, RESPONSE_FOR_REQUEST_NAME, exceptionContextMessage);
        } catch (Exception e) {
            throw new RuntimeException("Exception instantiating class [" + dtoClassName + "]", e);
        }
        return dto;
    }

    @Deprecated
    private String[] getConstructorArgs(String dtoClassName) {
        String args = dtoClassName.substring(dtoClassName.indexOf("(") + 1, dtoClassName.indexOf(")"));
        return args.split(",");
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#getClassMethod(java.lang.Class, java.lang.String)
     */
    public Method getClassMethod(Class<?> theClass, String methodName) {
        int counter = 0;
        Method result = null;
        for (Method method : theClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                counter++;
                result = method;
            }
        }
        if (counter == 0) {
            throw new IllegalStateException("Couldn't find: " + theClass.getName() + "." + methodName + "(...) method.");
        } else if (counter == 1) {
            return result;
        } else {
            throw new IllegalStateException("Found " + theClass.getName() + "." + methodName
                    + "(...) too many methods.");
        }
    }

    @Deprecated
    private void setBeanValues(Object object, Map<String, String> values, String excludeProperty,
            String exceptionContextMessage) throws InstantiationException, IllegalAccessException, ParseException {
        for (String paramName : values.keySet()) {
            if (paramName.contains(":")) {
                continue;
            }
            if ((excludeProperty != null) && excludeProperty.equals(paramName)) {
                continue;
            }
            Class paramType = getParameterType(object, paramName, exceptionContextMessage);
            Object convertedValue = null;

            // TODO: also when paramType is a List
            if (paramType.isArray()) {
                convertedValue = constructArray(values, paramName, paramType, exceptionContextMessage);
            } else {
                convertedValue = createFromString(paramType, values.get(paramName));
            }
            invokeSetter(object, convertedValue, paramName, paramType);
        }
    }

    private Object constructArray(Map<String, String> firstSet, String paramName, Class paramType,
            String exceptionContextMessage) throws InstantiationException, IllegalAccessException, ParseException {
        Object convertedValue;
        Class elementClass = paramType.getComponentType();
        Object element = null;
        convertedValue = Array.newInstance(elementClass, getArraySize(paramName, firstSet));
        int elementIndex = 0;
        for (String elementName : firstSet.keySet()) {
            if (!elementName.startsWith(paramName) || elementName.equals(paramName)) {
                continue;
            }
            String str = elementName.substring(paramName.length() + 1, elementName.length());
            int index = -1;
            String elementParamName = null;
            if (str.contains(":")) {
                index = new Integer(str.substring(0, str.lastIndexOf(":"))).intValue();
                elementParamName = elementName.substring(elementName.lastIndexOf(":") + 1, elementName.length());
            } else {
                index = new Integer(str).intValue();
            }

            if (index != elementIndex) {
                if (element != null) {
                    Array.set(convertedValue, elementIndex - 1, element);
                }
                Object previouseElem = Array.get(convertedValue, index - 1);
                if (previouseElem != null) {
                    element = previouseElem;
                } else {
                    element = elementClass.newInstance();
                }
                elementIndex = index;
            }
            if (elementParamName != null) {
                Class elementParamType = getParameterType(element, elementParamName, exceptionContextMessage);
                Object elementParam = createFromString(elementParamType, firstSet.get(elementName));
                invokeSetter(element, elementParam, elementParamName, elementParamType);
            } else {
                element = createFromString(elementClass, firstSet.get(elementName));
            }
        }
        if (element != null) {
            Array.set(convertedValue, elementIndex - 1, element);
        }
        return convertedValue;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#invokeSetter(java.lang.Object, java.lang.Object, java.lang.String,
     * java.lang.Class)
     */
    public void invokeSetter(Object parent, Object value, String paramName, Class c) {
        String setterName = constructSetterName(paramName);
        Method setter = null;
        try {
            setter = parent.getClass().getMethod(setterName, c);
            if (setter == null) {
                if (parent instanceof ConfigurableByParameters) {
                    ((ConfigurableByParameters) parent).addParameter(paramName, value);
                } else {
                    throw new RuntimeException("No setter [" + setterName + "] found in class ["
                            + parent.getClass().getName() + "]");
                }
            }
        } catch (NoSuchMethodException e) {
            if (parent instanceof ConfigurableByParameters) {
                ((ConfigurableByParameters) parent).addParameter(paramName, value);
                return;
            } else {
                throw new RuntimeException("No setter [" + setterName + "] found in class ["
                        + parent.getClass().getName() + "]", e);
            }
        }
        try {
            setter.invoke(parent, value);
        } catch (Exception e) {
            throw new RuntimeException("Could not invoke setter [" + setterName + "] for class ["
                    + parent.getClass().getName() + "]", e);
        }
    }

    private int getArraySize(String paramName, Map<String, String> firstSet) {
        int size = 0;
        for (String elementName : firstSet.keySet()) {
            if (!elementName.startsWith(paramName) || elementName.equals(paramName)) {
                continue;
            }
            String str = elementName.substring(paramName.length() + 1, elementName.length());
            int index = -1;
            if (str.contains(":")) {
                index = new Integer(str.substring(0, str.lastIndexOf(":"))).intValue();
            } else {
                index = new Integer(str).intValue();
            }

            if (index > size) {
                size = index;
            }
        }
        return size;
    }

    private String constructSetterName(String paramName) {
        if (paramName.contains(".")) {
            String[] parameters = paramName.split("[.]");
            StringBuilder result = new StringBuilder("set");
            for (String element : parameters) {
                result.append(StringUtils.capitalize(element));
            }
            return result.toString();
        } else {
            return "set" + StringUtils.capitalize(paramName);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#compareRequests(java.lang.String,
     * com.xoom.integration.util.OrderedIndexedMap, java.lang.reflect.Method, java.lang.Object[], java.lang.String)
     */
    public void compareRequests(String expectedRequestName, OrderedIndexedMap<String, String> expected, Method method,
            Object[] actualArguments, String exceptionContextMessage) {
        if (expected != null) {
            String requestName = expectedRequestName;
            if (requestName == null) {
                // throw new IllegalArgumentException("An undeclared call to "
                // + getAdapterName() + "partner was made: "
                // + getAdapterName() + "." + method.getName() + "("
                // + StringUtils.arrayToCommaDelimitedString(actualArguments)
                // + ")");
                compareBeans(expected, actualArguments[0], exceptionContextMessage);
            } else {
                Assert.assertEquals("[" + REQUEST_NAME + "] was different. " + exceptionContextMessage, requestName,
                        method.getName());
                if (actualArguments.length == 1) {
                    compareParameters(expected, "", method.getParameterTypes()[0], actualArguments[0],
                            exceptionContextMessage);
                } else {
                    List<String> expectedParameters = extractParameterNames(expected);
                    for (int i = 0, j = 0, maxi = expectedParameters.size(); i < maxi; i++) {
                        if (!expectedParameters.get(i).equals(REQUEST_NAME)) {
                            if (j < actualArguments.length) {
                                compareParameters(expected, expectedParameters.get(i), method.getParameterTypes()[j],
                                        actualArguments[j], exceptionContextMessage);
                                j++;
                            } else {
                                compareParameters(expected, expectedParameters.get(i), null, null,
                                        exceptionContextMessage);
                                j++;
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#compareBeans(com.xoom.integration .util.OrderedIndexedMap,
     * java.lang.Object, java.lang.String)
     */
    public void compareBeans(OrderedIndexedMap<String, String> expected, Object actual, String exceptionContextMessage) {
        if (expected == null) {
            Assert.assertNull("Comparing expected=[" + expected + "], actual=[" + actual + "] "
                    + exceptionContextMessage, actual);
        } else if (actual == null) {
            Assert.assertNull("Comparing expected=[" + expected + "], actual=[" + actual + "] "
                    + exceptionContextMessage, expected);
        } else {
            try {
                for (String paramName : expected.keyList()) {
                    if (!STRING_ANY_VALUE.equals(expected.get(paramName))) {
                        if (paramName.length() == 0) {
                            Object expectedObject = createFromString(actual.getClass(), expected.get(paramName));
                            checkField(expectedObject, actual, paramName, false, exceptionContextMessage);
                        } else if (paramName.contains("dto:java.lang.String")) {
                            // String case only
                            String value = paramName.substring(21, paramName.length() - 1);
                            checkField(value, actual, "dto:java.lang.String", false, exceptionContextMessage);
                        } else {
                            if (!paramName.contains(":")) {
                                Class paramType = getParameterType(actual, paramName, exceptionContextMessage);
                                Object expectedProperty = null;
                                String getterName = "get" + StringUtils.capitalize(paramName);
                                if (paramType.equals(boolean.class)) {
                                    getterName = "is" + StringUtils.capitalize(paramName);
                                }
                                Method getter = actual.getClass().getMethod(getterName);
                                Object actualProperty = getter.invoke(actual, new Object[0]);

                                if (paramType.isArray()) {
                                    // TODO: this is not fully functional, so far we do not
                                    // have
                                    // this case
                                    expectedProperty = constructArray(expected, paramName, paramType,
                                            exceptionContextMessage);
                                    for (int i = 0; i < Array.getLength(expectedProperty); i++) {
                                        Object expectedElement = Array.get(expectedProperty, i);
                                        Object actualElement = Array.get(actualProperty, i);
                                        compareObjects(expectedElement, actualElement, false, exceptionContextMessage);
                                    }
                                } else {
                                    expectedProperty = createFromString(paramType, expected.get(paramName));
                                    checkField(expectedProperty, actualProperty, paramName, false,
                                            exceptionContextMessage);
                                }
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * (non-Javadoc) @seecom.xoom.integration.util.BeanProcessor#compareParameters(com.xoom.
     * integration.util.OrderedIndexedMap, java.lang.String, java.lang.Class, java.lang.Object, java.lang.String)
     */
    public void compareParameters(OrderedIndexedMap<String, String> expected, String parameterPrefix,
            Class<?> expectedType, Object actualValue, String exceptionContextMessage) {
        try {
            if ((expected != null) && expected.containsKey(HAS_CONSTRUCTOR_FROM_STRING) && (expected.size() == 1)) {
                if (expected.get(HAS_CONSTRUCTOR_FROM_STRING).equals("empty")) {
                    if (actualValue instanceof List) {
                        int size = ((List) actualValue).size();
                        if (size > 0) {
                            throw new RuntimeException("The returned list should be empty, but contained " + size
                                    + " elements. First one was [" + ((List) actualValue).get(0)
                                    + exceptionContextMessage);
                        }
                    }
                } else {
                    try {
                        compareParameter(actualValue.getClass(), expected.get(HAS_CONSTRUCTOR_FROM_STRING),
                                actualValue, "", false, exceptionContextMessage);
                    } catch (ParseException e) {
                        throw new RuntimeException("Couldn't create [" + expectedType + "] expected field. "
                                + e.getMessage() + exceptionContextMessage, e);
                    }
                }
            } else {
                if (parameterPrefix.equals(REQUEST_NAME)) {
                    return;
                }
                if ((parameterPrefix.length() > 0) && expected.containsKey(parameterPrefix)) {
                    try {
                        compareParameter(expectedType, expected.get(parameterPrefix), actualValue, parameterPrefix,
                                false, exceptionContextMessage);
                    } catch (ParseException e) {
                        throw new RuntimeException("Couldn't create [" + parameterPrefix + "] expected field. "
                                + e.getMessage() + exceptionContextMessage, e);
                    }
                } else {
                    int start = parameterPrefix.length() + 1;
                    if (parameterPrefix.length() == 0) {
                        start = 0;
                    }

                    if (expected != null) {
                        junit.framework.Assert.assertNotNull("ActualValue is null, while expected is [" + expected
                                + "] " + exceptionContextMessage, actualValue);
                        for (String paramName : expected.keyList()) {
                            String path = paramName;
                            String expectedString = preProcessString(expected.get(path));
                            if (!ANY_EXPECTED_VALUE.equals(expectedString)) {
                                try {
                                    if (paramName.contains(":")) {
                                        throw new RuntimeException("Dto style of declaring is not supported anymore.");
                                        // continue;
                                    }
                                    if (paramName.contains(REQUEST_NAME)) {
                                        continue;
                                    }
                                    if (!paramName.startsWith(parameterPrefix)) {
                                        continue;
                                    }

                                    paramName = paramName.substring(start);

                                    boolean useContainsOperator = false;
                                    if (paramName.endsWith("Contains")) {
                                        paramName = paramName.substring(0, paramName.length() - "Contains".length());
                                        useContainsOperator = true;
                                    }
                                    Class paramType = null;
                                    Object expectedValue = null;
                                    Object actualProperty = actualValue;
                                    String[] tokens = paramName.split("/");
                                    StringBuilder path2 = new StringBuilder();

                                    for (String element : tokens) {
                                        if (actualProperty == null) {
                                            junit.framework.Assert.assertNotNull("For expected value ["
                                                    + expectedString + "] at [" + path + "] found a null value at ["
                                                    + path2 + "]" + exceptionContextMessage, actualProperty);
                                        }
                                        if ((actualProperty instanceof List) && number.matcher(element).find()) {
                                            int index = Integer.parseInt(element) - 1;
                                            List list = (List) actualProperty;
                                            String assertMessage = "The value [" + expectedString + "] with index["
                                                    + path + "] could not be extracted from the list with size ["
                                                    + list.size() + "]" + exceptionContextMessage;
                                            junit.framework.Assert.assertTrue(assertMessage, list.size() > index);
                                            actualProperty = list.get(index);
                                        } else {
                                            paramType = getParameterType(actualProperty, element,
                                                    exceptionContextMessage);
                                            Method getter = findGetter(actualProperty, element, exceptionContextMessage);
                                            if (getter != null) {
                                                actualProperty = getter.invoke(actualProperty, new Object[0]);
                                            }
                                        }
                                        path2.append("/").append(element);
                                    }

                                    if ((paramType != null) && paramType.isArray()) {
                                        // TODO: this is not fully functional, so far we
                                        // do not
                                        // have
                                        // this case
                                        expectedValue = constructArray(expected, paramName, paramType,
                                                exceptionContextMessage);
                                        for (int i = 0; i < Array.getLength(expectedValue); i++) {
                                            Object expectedElement = Array.get(expectedValue, i);
                                            Object actualElement = Array.get(actualProperty, i);
                                            compareObjects(expectedElement, actualElement, useContainsOperator,
                                                    exceptionContextMessage);
                                        }
                                    } else {
                                        checkField(expectedString, actualProperty, path, useContainsOperator,
                                                exceptionContextMessage);
                                    }
                                } catch (IllegalArgumentException e) {
                                    throw new RuntimeException("Couldn't create [" + path + "] expected field. "
                                            + e.getMessage() + exceptionContextMessage, e);
                                } catch (SecurityException e) {
                                    throw new RuntimeException("Couldn't create [" + path + "] expected field. "
                                            + e.getMessage() + exceptionContextMessage, e);
                                } catch (InstantiationException e) {
                                    throw new RuntimeException("Couldn't create [" + path + "] expected field. "
                                            + e.getMessage() + exceptionContextMessage, e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException("Couldn't create [" + path + "] expected field. "
                                            + e.getMessage() + exceptionContextMessage, e);
                                } catch (ParseException e) {
                                    throw new RuntimeException("Couldn't create [" + path + "] expected field. "
                                            + e.getMessage() + exceptionContextMessage, e);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException("Couldn't create [" + path + "] expected field. "
                                            + e.getMessage() + exceptionContextMessage, e);
                                }
                            }
                        }
                    } else {
                        junit.framework.Assert.assertNull("Comparing expected=[" + expected + "], actual=["
                                + actualValue + "] " + exceptionContextMessage, actualValue);
                    }
                }
            }
        } catch (junit.framework.ComparisonFailure e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage() + " when compare expected=[" + expected + "], actual=["
                    + actualValue + "], parameterPrefix=[" + parameterPrefix + "], " + exceptionContextMessage, e);
        }
    }

    private Class getParameterType(Object object, String attribute, String exceptionContextMessage) {
        return findGetter(object, attribute, exceptionContextMessage).getReturnType();
    }

    private Method findGetter(Object object, String attribute, String exceptionContextMessage) {
        Method getter = findMethod(object, "get" + StringUtils.capitalize(attribute));
        if (getter == null) {
            getter = findMethod(object, "is" + StringUtils.capitalize(attribute));
        }
        // if (getter == null) {
        // getter = findMethod(object, "has" + StringUtils.capitalize(attribute));
        // }
        if (getter == null) {
            // if (!attribute.equalsIgnoreCase(VALUE)) {
            throw new RuntimeException("Nor get/is/has methods where found for attribute " + attribute + " on class "
                    + object.getClass());
            // }
        }
        return getter;
    }

    private Method findMethod(Object object, String methodName) {
        try {
            return object.getClass().getMethod(methodName);
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private void compareParameter(Class<?> expectedType, String expectedString, Object actualValue, String path,
            boolean useContainsOperator, String exceptionContextMessage) throws ParseException {
        if (!expectedString.equals(STRING_ANY_VALUE)) {
            Object expectedValue = createFromString(expectedType, expectedString);
            checkField(expectedValue, actualValue, path, useContainsOperator, exceptionContextMessage);
        }
    }

    private void checkField(Object expectedProperty, Object actualProperty, String paramName,
            boolean useContainsOperator, String exceptionContextMessage) {
        String expected = getTransformedExpectedProperty(expectedProperty, actualProperty);
        String actual = toString(actualProperty);
        if (useContainsOperator) {
            assertStringContainsCaseInsensitive(exceptionContextMessage, expected, actual, false);
        } else {
            junit.framework.Assert.assertEquals("Comparing field [" + paramName + "] : expected=[" + expectedProperty
                    + "], actual=[" + actualProperty + "] " + exceptionContextMessage, expected, actual);
        }
    }

    private void compareObjects(Object expected, Object actual, boolean useContainsOperator,
            String exceptionContextMessage) {
        Method methods[] = expected.getClass().getMethods();
        for (Method m : methods) {
            if (isGetter(m)) {
                String fieldName = m.getName().substring(3, m.getName().length());
                if (setterExists(methods, fieldName)) {
                    Object expectedProperty = null;
                    Object actualProperty = null;
                    try {
                        expectedProperty = m.invoke(expected, new Object[0]);
                        actualProperty = m.invoke(actual, new Object[0]);
                    } catch (Exception e) {
                        throw new RuntimeException("Exception invocking getter [" + m.getName() + "] on class ["
                                + expected.getClass().getName() + "]", e);
                    }
                    if (expectedProperty != null) {
                        // todo fix assert equals for every object
                        checkField(expectedProperty, actualProperty, fieldName, useContainsOperator,
                                exceptionContextMessage);
                    }
                }
            }
        }
    }

    private boolean setterExists(Method[] methods, String fieldName) {
        for (Method m : methods) {
            if (m.getName().equals("set" + fieldName)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#checkStringContains(java.lang .String, java.lang.String,
     * java.lang.StringBuilder, java.lang.String)
     */
    public boolean checkStringContains(String expectedContainedSubstrings, String actualTargetString,
            StringBuilder errorMessage, String message) {
        if ((expectedContainedSubstrings == null) && (actualTargetString == null)) {
            return true;
        }
        if (expectedContainedSubstrings == null) {
            errorMessage.append(message + "The expected contained text [" + expectedContainedSubstrings
                    + "] wasn't found in the actual text [" + actualTargetString + "] as defined.");
            return false;
        }
        if (actualTargetString == null) {
            errorMessage.append(message + "The expected contained text [" + expectedContainedSubstrings
                    + "] wasn't found in the actual text [" + actualTargetString + "] as defined.");
            return false;
        }
        for (String messageContained : expectedContainedSubstrings.split("[ ]AND[ ]")) {
            if (!actualTargetString.contains(messageContained)) {
                errorMessage.append(message + "The expected contained text \n\n\n[" + messageContained
                        + "] wasn't found in the actual text \n[" + actualTargetString + "]\n\n as defined.");
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#assertStringContains(java.lang .String, java.lang.String,
     * java.lang.String)
     */
    public void assertStringContains(String message, String expectedContainedSubstrings, String actualTargetString) {
        assertStringContainsCaseInsensitive(message, expectedContainedSubstrings, actualTargetString, true);
    }

    public void assertStringContainsCaseInsensitive(String message, String expectedContainedSubstrings,
            String actualTargetString) {
        assertStringContainsCaseInsensitive(message, expectedContainedSubstrings, actualTargetString, false);
    }

    public void assertStringContainsCaseInsensitive(String exceptionContextMessage, String expected, String actual,
            boolean caseSensitive) {
        if (!STRING_ANY_VALUE.equals(expected)) {
            for (String messageContained : expected.split("[ ]AND[ ]")) {
                if ((actual == null) || !contains(actual, messageContained, caseSensitive)) {
                    throw new AssertionFailedError("\n--------The text: \n\t[" + messageContained
                            + "]\n-------- wasn't found in the actual text:\n\t[" + actual
                            + "]\n-------- as defined.\n" + exceptionContextMessage);
                }
            }
        }
    }

    private boolean contains(String string, String substring, boolean caseSensitive) {
        if (caseSensitive) {
            return string.contains(substring);
        } else {
            return string.toLowerCase().contains(substring.toLowerCase().trim());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#extractParameterNames(com.xoom .integration.util.OrderedIndexedMap)
     */
    public List<String> extractParameterNames(OrderedIndexedMap<String, String> map) {
        List<String> parameterNames = new ArrayList<String>();
        String oldPrefix = null;
        for (int i = 0, maxi = map.size(); i < maxi; i++) {
            String key = map.getKeyAt(i);
            int indexOf = key.indexOf("/");
            if (indexOf == -1) {
                indexOf = key.length();
            }
            String newPrefix = key.substring(0, indexOf);
            if (!newPrefix.equals(oldPrefix)) {
                parameterNames.add(newPrefix);
            }
            oldPrefix = newPrefix;
        }
        return parameterNames;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#toString(java.lang.Object)
     */
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        return value.toString();
    }

    private String getTransformedExpectedProperty(Object expectedValue, Object actualValue) {
        if (expectedValue == null) {
            return null;
        }
        if (actualValue == null) {
            return expectedValue.toString();
        }
        if (actualValue.getClass().isAssignableFrom(DateTime.class)) {
            try {
                DateTime dt = createFromString(DateTime.class, expectedValue.toString());
                if (dt == null)
                    return null;
                return dt.toString();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return expectedValue.toString();
    }

    public <T> T createObject(Class<T> class1, OrderedIndexedMap<String, String> parameters, String path,
            boolean allowNulls) {
        return (T) constructNewDto(parameters, path, class1.getName(), allowNulls);
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#canCreateObject(com.xoom.integration .util.OrderedIndexedMap,
     * java.lang.Class)
     */
    public boolean canCreateObject(OrderedIndexedMap<String, String> parameters, Class<?> returnClass) {
        if (isNewStyle(parameters)) {
            return !returnClass.equals(void.class) || isExceptionDeclaredToBeThrwon(parameters);
        }
        return true;
    }

    private boolean isExceptionDeclaredToBeThrwon(OrderedIndexedMap<String, String> parameters) {
        if (parameters == null) {
            return false;
        }
        return parameters.get(RESPONSE_EXCEPTION_TYPE) != null;
    }

    public Object createObject(OrderedIndexedMap<String, String> parameters, Method method,
            String exceptionContextMessage) {
        if (parameters == null) {
            return null;
        }
        if (isNewStyle(parameters)) {
            // Method method = getMethod(adapterClient,
            // parameters.get(RESPONSE_FOR_REQUEST_NAME));
            if (parameters.get(RESPONSE_EXCEPTION_TYPE) != null) {
                String message = parameters.get(RESPONSE_EXCEPTION_MESSAGE);
                try {
                    if (message == null) {
                        return invokeConstructor(parameters.get(RESPONSE_EXCEPTION_TYPE), parameters, null, false);
                    } else {
                        return invokeConstructor(parameters.get(RESPONSE_EXCEPTION_TYPE), message);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            // throw new IllegalStateException("A parameter [" + REQUEST_NAME
            // + "] or [" + RESPONSE_FOR_REQUEST_NAME
            // + "] should be defined.");
            String beanClassName = method.getReturnType().getName();
            Class<?> annotation = getReturnedGenericType(method);
            if (annotation != null) {
                parameters.put("hint:rawtype", annotation.getName());
            }
            if (isCollection(beanClassName) && !parameters.containsKey("hint:rawtype")) {
                throw new InvalidParameterException("Service method " + method.getReturnType() + "." + method.getName()
                        + "(...) returns a collection but doesn't declare any raw type with a [@"
                        + GenericType.class.getName() + "(<type>.class)] annotation.");
            }
            return constructNewDto(parameters, "", beanClassName, false);
        } else {
            String beanClassName = getDtoClassName(parameters);
            return constructDto12(parameters, beanClassName, exceptionContextMessage);
        }
    }

    private Class<?> getReturnedGenericType(Method method) {
        return searchAnnotation1(method);
    }

    public Class<?> searchAnnotation1(Method m) {
        Type t = m.getGenericReturnType();
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            Type[] actualGenericParameters = pt.getActualTypeArguments();
            return (Class<?>) actualGenericParameters[0];
        } else {
            return null;
        }
    }

    private boolean isCollection(String beanClassName) {
        if ("java.util.List".equals(beanClassName)) {
            return true;
        }
        return false;
    }

    private Object constructNewDto(OrderedIndexedMap<String, String> parameters, String path, String beanClassName,
            boolean allowNulls) {
        try {
            // construct a response
            if (!StringUtils.hasText(beanClassName)) {
                throw new InvalidParameterException("BeanClassName (returning expected type) should not be null.");
            }
            String rawType = parameters.get("hint:rawtype");
            Class<?> outerType = null;
            if (rawType != null) {
                outerType = Class.forName(rawType);
            }
            try {
                Object object = createBean(path, beanClassName, parameters, outerType, allowNulls);
                return object;
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException("When trying to instantiate [" + path + "] of type ["
                        + beanClassName + "] using parameters [" + parameters + "] an exception occured.", e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object createBean(String path, String beanClassName, OrderedIndexedMap<String, String> parameters,
            Class<?> outerType, boolean allowNulls) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        Object object = invokeConstructor(beanClassName, parameters, path, allowNulls);
        setObjectValues3(path, object, parameters, outerType);
        return object;
    }

    private Object createBean(String path, Class<?> beanClass, OrderedIndexedMap<String, String> parameters,
            Class<?> outerType) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (parameters.get(path) != null) {
            Object result;
            try {
                result = createObjectFromString(beanClass, parameters.get(path));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return result;
        } else {
            Object object = invokeConstructor2(beanClass, parameters, path, false);
            setObjectValues3(path, object, parameters, outerType);
            return object;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#setObjectValues(java.lang.Object,
     * com.xoom.integration.util.OrderedIndexedMap, java.lang.Class)
     */
    public void setObjectValues3(String path, Object object, OrderedIndexedMap<String, String> parameters,
            Class<?> outerType) {
        if (parameters == null) {
            return;
        }
        if (object == null) {
            return;
        }
        String key = null;
        String value = null;
        for (int i = 0, maxi = parameters.size(); i < maxi; i++) {
            key = parameters.getKeyAt(i);
            if (!isInternalParameter(key)) {
                if (key.startsWith(path)) {
                    value = parameters.get(key);
                    String newPath = key;
                    if (path.length() > 0) {
                        if (newPath.startsWith(path)) {
                            newPath = newPath.substring(path.length());
                            if (newPath.startsWith("/")) {
                                newPath = newPath.substring(1);
                            }
                        } else {
                            throw new RuntimeException("The analysed key " + key + " should start with " + path + ".");
                        }
                    }
                    setValue(outerType, object, newPath, value, parameters);
                }
            }
        }
    }

    private boolean isInternalParameter(String key) {
        return key.contains(":") || RESPONSE_FOR_REQUEST_NAME.equals(key) || REQUEST_NAME.equals(key)
                || VALUE.equals(key);
    }

    private boolean isNewStyle(OrderedIndexedMap<String, String> parameters) {
        // doesn't contain dto:
        if (parameters == null) {
            return true;
        }
        if (parameters.size() == 0) {
            return true;
        }
        return !parameters.getKeyAt(0).contains("dto:") && !parameters.containsKey("destFileName");
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#createFromString(java.lang.Class, java.lang.String)
     */

    @SuppressWarnings("unchecked")
    public <T> T createFromString(Class<T> setClass, String value) throws ParseException {
        String processedValue = preProcessString(value);
        return (T) createObjectFromString(setClass, processedValue);
    }

    private <T> String preProcessString(String value) {
        String processedValue = value;
        if (value == null) {
            processedValue = null;
        } else {
            processedValue = value.trim();
            if (processedValue.equals("null")) {
                processedValue = null;
            } else {
                if (processedValue.startsWith("\"") && processedValue.endsWith("\"")) {
                    processedValue = processedValue.substring(1, processedValue.length() - 1);
                }
            }
        }
        return processedValue;
    }

    @SuppressWarnings("unchecked")
    private <T> Object createObjectFromString(Class<T> setClass, String value) throws ParseException {
        Object converted = null;
        if (value == null) {
            converted = null;
        } else if ((value.trim().length() == 0) && !setClass.equals(String.class)) {
            converted = null;
        } else if (setClass.equals(String.class)) {
            converted = value;
        } else if (Date.class.isAssignableFrom(setClass)) {
            DateTime converted2 = extractDateTime(value);
            converted = converted2.toDate();
        } else if (DateTime.class.isAssignableFrom(setClass)) {
            converted = extractDateTime(value);
        } else if (BigDecimal.class.isAssignableFrom(setClass)) {
            converted = parseNumber(value, BigDecimal.class);
        } else if (Float.class.isAssignableFrom(setClass)) {
            converted = parseNumber(value, Float.class);
        } else if (Double.class.isAssignableFrom(setClass)) {
            converted = parseNumber(value, Double.class);
        } else if (int.class.isAssignableFrom(setClass)) {
            converted = ((Integer) parseNumber(value, Integer.class)).intValue();
        } else if (double.class.isAssignableFrom(setClass)) {
            converted = ((Double) parseNumber(value, Double.class)).doubleValue();
        } else if (long.class.isAssignableFrom(setClass)) {
            converted = ((Long) parseNumber(value, Long.class)).longValue();
        } else if (short.class.isAssignableFrom(setClass)) {
            converted = ((Short) parseNumber(value, Short.class)).shortValue();
        } else if (Number.class.isAssignableFrom(setClass)) {
            converted = parseNumber(value, setClass);
        } else if (boolean.class.isAssignableFrom(setClass)) {
            converted = new Boolean(value).booleanValue();
        } else if (Boolean.class.isAssignableFrom(setClass)) {
            converted = new Boolean(value);
        } else if (List.class.isAssignableFrom(setClass)) {
            converted = new ArrayList();
            if (!value.equals("empty")) {
                throw new RuntimeException("Can't create a list from value [" + value + "].");
            }
        } else {
            try {
                try {
                    Constructor<T> constructor = setClass.getConstructor(String.class);
                    if (constructor != null) {
                        converted = constructor.newInstance(value);
                    }
                } catch (SecurityException e) {
                    throw e;
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Don't know how to parse a value of type [" + setClass
                            + "] from value [" + value + "]");
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (InstantiationException e) {
                    throw e;
                } catch (IllegalAccessException e) {
                    throw e;
                } catch (InvocationTargetException e) {
                    throw e;
                }
            } catch (Exception e) {
                throw new RuntimeException("Don't know how to parse a value of type [" + setClass + "] from value ["
                        + value + "]", e);
            }
        }
        return converted;
    }

    private DateTime extractDateTime(String value) throws ParseException {
        DateTime result;
        String[] values = value.split("/");
        if (values.length != 2) {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            Date converted = sdf.parse(value);
            result = new DateTime(converted.getTime());
            // throw new IllegalStateException(
            // "A dateTime provider must be initialized using a value as [DateTimeFormat/DateTimeValue]. The actual value was ["
            // + value + "].");
        } else {
            result = DateTimeFormat.forPattern(values[0]).parseDateTime(values[1]);
        }
        return result;
    }

    private Object parseNumber(String value, Class fieldClass) {
        Class[] arguments = new Class[1];
        arguments[0] = String.class;
        Object[] callArguments = new Object[1];
        callArguments[0] = value;

        try {
            String processedValue = value;
            callArguments[0] = processedValue;
            return fieldClass.getConstructor(arguments).newInstance(callArguments);
        } catch (Exception e) {
            throw new RuntimeException("Could not create an instance of [" + fieldClass.getName()
                    + "] having the value [" + value + "]", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#getLocalhost()
     */
    public String getLocalhost() {
        synchronized (localhostLock) {
            if (localhost == null) {
                try {
                    localhost = InetAddress.getLocalHost().getHostName();
                } catch (UnknownHostException e) {
                    logger.warn("Unable to get local host name", e);
                    localhost = "Unknown host";
                }
            }
        }
        return localhost;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#createList(java.util.List, int, java.lang.Class)
     */
    public <T> List<T> createList(List<OrderedIndexedMap<String, String>> parameterSets, int transactionType,
            Class<T> classT, boolean allowNulls) {
        List<T> result = new ArrayList<T>();
        int index = 1;
        for (OrderedIndexedMap<String, String> parameterSet : parameterSets) {
            T transactionDetail = createObject(classT, parameterSet, Integer.toString(index) + "/", allowNulls);
            result.add(transactionDetail);
            index++;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.xoom.integration.util.BeanProcessor#getAsRequestParameters(java.lang .reflect.Method,
     * com.xoom.integration.util.OrderedIndexedMap)
     */
    public Object[] getAsRequestParameters(Method method, OrderedIndexedMap<String, String> requestParameters)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        int expectedSize = method.getParameterTypes().length;
        if (requestParameters == null) {
            if (expectedSize == 0) {
                return null;
            }
            throw new RuntimeException(
                    "Expected ["
                            + expectedSize
                            + "] parameters but you defined none. Maybe you forgot to add a [request/] prefix to the parameters ?");
        }
        // requestParameters = removePrefixFromKeys(requestParameters,
        // "request/");
        List<String> expectedParameters = extractParameterNames(requestParameters);
        Assert.assertEquals("You should define more or less parameters for the method call [" + method
                + "]. Identified parameter names where [" + expectedParameters + "].", expectedSize, expectedParameters
                .size());
        Object[] result = new Object[expectedParameters.size()];
        int index = 0;
        for (String parameterName : expectedParameters) {
            result[index] = createBean(parameterName, method.getParameterTypes()[index], requestParameters,
                    getCollectionRawType(method, index));
            index++;
        }
        return result;
    }

    private Class<?> getCollectionRawType(Method method, int parameterIndex) {
        Class<?> rawType = getCollectionRawType(method.getGenericParameterTypes()[parameterIndex]);
        if (rawType == null) {
            Annotation[] annotations = method.getParameterAnnotations()[parameterIndex];
            for (Annotation annotation : annotations) {
                if (annotation instanceof GenericType) {
                    rawType = ((GenericType) annotation).value();
                    break;
                }
            }
        }
        return rawType;
    }

    private Class<?> getCollectionRawType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] actualGenericParameters = pt.getActualTypeArguments();
            return (Class<?>) actualGenericParameters[0];
        }
        return null;
    }

    public void validate(Object bean) {
        ClassValidator<Object> classValidator = new ClassValidator<Object>((Class<Object>) bean.getClass());
        InvalidValue[] invalidValues = classValidator.getInvalidValues(bean);
        if (invalidValues.length > 0) {
            throw new RuntimeException("The bean " + bean + "\n-------- is invalid:\n"
                    + convertInvalidValuesToString(invalidValues, ",\n") + ".");
        }
    }

    private String convertInvalidValuesToString(InvalidValue[] invalidValues, String delim) {
        if (ObjectUtils.isEmpty(invalidValues)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < invalidValues.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(convertInvalidValueToString(invalidValues[i]));
        }
        return sb.toString();
    }

    private String convertInvalidValueToString(InvalidValue invalidValue) {
        return org.raisercostin.utils.ObjectUtils.toString(invalidValue, new String[] { "bean", "rootBean" });
    }
}
