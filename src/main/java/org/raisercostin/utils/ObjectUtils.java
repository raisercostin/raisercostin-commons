package org.raisercostin.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.exception.Nestable;
import org.raisercostin.utils.beans.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class ObjectUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectUtils.class);
	private static final String IGNORED_VALUE = "*****";
	// private static final MyStringStyle myStringStyle = new MyStringStyle();

	private static final String TO_STRING_METHOD = "toString";
	private static final int STEP = 4;
	private static final String all = ".   .   .   .   .   .   .   .   .   .   .   .   .   .   ";
	private static final boolean DEFAULT_TRANSIENTS = false;
	private static final Pattern XML_STRING_PATTERN = Pattern.compile("^<(\\w+)>.+</\\1>$", Pattern.DOTALL);
	// Thread local is needed because multiple cascaded toStrings could be invoked.
	private static final ThreadLocal<ObjectUtilsContext> contextOnThread = new ThreadLocal<ObjectUtilsContext>();
	private static final int MAX_SHORT_STRING = 10;
	private static final int MAX_DEEP = 6;

	// UTILITIES
	public static void copy(Object destination, Object source) {
		CloneBuilder.reflectionCopy(destination, source);
	}

	public static boolean equals(Object object1, Object object2) {
		return EqualsBuilder.reflectionEquals(object1, object2, false, Object.class, null);
	}

	public static int hashCode(Object object) {
		return HashCodeBuilder.reflectionHashCode(object, DEFAULT_TRANSIENTS);
	}

	public static int hashCode(Object object, String commaSeparatedExceptedFields) {
		return HashCodeBuilder.reflectionHashCode(17, 37, object, DEFAULT_TRANSIENTS, null,
				StringUtils.tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true));
	}

	public static BigDecimal normalize(BigDecimal rate) {
		if (rate != null) {
			return rate.stripTrailingZeros();
		}
		return null;
	}

	public static Object rethrow(Throwable e) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		if (e instanceof Error) {
			throw (Error) e;
		}
		throw new RuntimeException(e);
	}

	public static void validate(Object object) {
		BeanUtils.validate(object);
	}

	@SuppressWarnings("restriction")
	public static String formatXml(String unformattedXml) {
		try {
			final Document document = parseXmlFile(unformattedXml);
			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(120);
			format.setIndenting(true);
			format.setIndent(2);
			format.setOmitXMLDeclaration(!unformattedXml.startsWith("<?xml"));
			format.setOmitComments(false);
			format.setPreserveEmptyAttributes(true);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			String result = out.toString();
			if (result.endsWith("\r\n")) {
				result = result.substring(0, result.length() - "\r\n".length());
			}
			if (result.endsWith("\n")) {
				result = result.substring(0, result.length() - "\n".length());
			}
			return result;
		} catch (ParserConfigurationException e) {
			return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
		} catch (SAXException e) {
			return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
		} catch (IOException e) {
			return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
		}
	}

	private static Document parseXmlFile(String in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(in));
		return db.parse(is);
	}

	// TO STRING UTILITIES
	public static String toStringDump(Object object) {
		return internalToStringWithContext(object, false, false, false, "", "");
	}

	public static String toStringDump(Object object, String ignores) {
		return internalToStringWithContext(object, false, false, false, ignores, "");
	}

	public static String toStringDump(Object object, String ignores, String excludes) {
		return internalToStringWithContext(object, false, false, false, ignores, excludes);
	}

	public static String toString(Object object) {
		return internalToStringWithContext(object, false, true, false, "", "");
	}

	public static String toString(Object object, String ignores) {
		return internalToStringWithContext(object, false, true, false, ignores, "");
	}

	public static String toString(Object object, boolean singleLine, boolean useToString, boolean displayTypes) {
		return internalToStringWithContext(object, singleLine, useToString, displayTypes, "", "");
	}

	// TOSTRING WITH EXCLUDES - should be implemented in another way
	@Deprecated
	private static String toString(Object object, boolean singleLine, boolean classDecorators, String[] excludes) {
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object, new MyStringStyle(singleLine, true,
				classDecorators, false), null, null, false, false);
		builder.setExcludeFieldNames(excludes);
		return builder.toString();
	}

	@Deprecated
	private static String toStringWithExclusions(Object object, boolean singleLine, boolean classDecorators,
			String commaSeparatedExceptedFields) {
		return toStringWithExclusions(object, singleLine, classDecorators,
				StringUtils.tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true));
	}

	@Deprecated
	public static String toStringWithExclusions(Object object, boolean singleLine, boolean classDecorators,
			String... excludes) {
		return toString(object, singleLine, classDecorators, excludes);
	}

	// IMPLEMENTATION
	private static String internalToStringWithContext(Object object, boolean singleLine, boolean useToString,
			boolean displayTypes, String ignores, String excludes) {
		createContext(ignores, excludes);
		try {
			return internalToString(object, useToString, new MyStringStyle(singleLine, useToString, true, displayTypes));
		} catch (Throwable e) {
			LOG.warn("Generic toString operation failed.", new RuntimeException(e));
			return "<invalidToString>";
		} finally {
			removeContext();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String internalToString(Object object, boolean useToString, MyStringStyle toStringStyle) {
		if (getContext().isMaximumLevel()) {
			return "...(more)";
		}
		if (object == null) {
			return null;
		}
		if (isShortType(object.getClass())) {
			return object.toString();
		}
		if (object instanceof String && ((String) object).length() < MAX_SHORT_STRING) {
			return (String) object;
		}
		String old = getContext().find(object);
		if (old != null) {
			if (!getContext().isOriginalToStringCalled(object)) {
				return old;
			}
		}
		String id = getContext().save(object);
		if (object instanceof String) {
			return declaredToString(id, object);
		}
		String value = null;
		if (object instanceof Throwable) {
			value = throwableToString((Throwable) object, useToString, toStringStyle);
		} else if (object instanceof Map) {
			value = mapToString((Map<Object, Object>) object, useToString, toStringStyle);
		} else if (object instanceof Collection) {
			value = collectionToString((Collection<Object>) object, useToString, toStringStyle);
		} else if (object.getClass().isArray()) {
			value = arrayToString(object, useToString, toStringStyle);
		} else if ((object instanceof Class) && ((Class<?>) object).isEnum()) {
			value = enumToString((Class<Enum>) object, useToString, toStringStyle);
		} else {
			if (useToString && !getContext().isOriginalToStringCalled(object)
					&& !hasBadToStringImplementation(object.getClass())) {
				value = declaredToString(id, object);
			} else {
				value = reflectedToString(object, useToString, toStringStyle);
			}
		}
		return value;
	}

	private static String toIdentedString(String text) {
		text = text.replaceAll("\r\n|\n|\r", getContext().getRowEnd() + getContext().getRowStart());
		return text;
	}

	private static boolean isXml(Object object) {
		if (!(object instanceof String)) {
			return false;
		}
		String xml = (String) object;
		if (xml.startsWith("<?xml")) {
			return true;
		}
		if (XML_STRING_PATTERN.matcher(xml).matches()) {
			return true;
		}
		return false;
	}

	private static String declaredToString(String id, Object object) {
		if (isXml(object)) {
			return formatXml((String) object);
		}
		getContext().callOriginalToString(object);
		// return id + ":" +
		return callToString(object);
	}

	private static String callToString(Object object) {
		try {
			return (String) object.getClass().getMethod(TO_STRING_METHOD).invoke(object);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			return object.toString();
		}
	}

	private static String enumToString(@SuppressWarnings("rawtypes") Class<Enum> object, boolean useToString,
			MyStringStyle toStringStyle) {
		@SuppressWarnings("rawtypes")
		Enum[] constants = object.getEnumConstants();
		StringBuilder sb = new StringBuilder();
		sb.append(declaredToString("", object)).append(" enums=[");
		for (@SuppressWarnings("rawtypes")
		Enum oneConstant : constants) {
			sb.append(getContext().getRowEnd()).append(oneConstant.name()).append("(").append(oneConstant.ordinal())
					.append(")=")
					.append(ObjectUtils.toStringWithExclusions(oneConstant, true, false, "name", "ordinal"));
		}
		sb.append("]");
		return sb.toString();
	}

	// others

	private static void createContext(String ignores, String excludes) {
		if (contextOnThread.get() == null) {
			contextOnThread.set(new ObjectUtilsContext(STEP, ignores, excludes));
		}
		contextOnThread.get().incrementToStringCalls();
	}

	private static void removeContext() {
		ObjectUtilsContext context = contextOnThread.get();
		if (context == null) {
			throw new RuntimeException("Too many removeContext.");
		}
		if (context.decrementToStringCalls()) {
			contextOnThread.set(null);
		}
	}

	private static ObjectUtilsContext getContext() {
		return contextOnThread.get();
	}

	private static final Set<Class> SHORT_TYPES = new HashSet(Arrays.asList(Character.class, Byte.class, Short.class,
			Integer.class, Long.class, Float.class, Double.class, Void.class, Timestamp.class, Date.class,
			java.sql.Date.class, Time.class, Boolean.class));

	private static boolean isShortType(Class clazz) {
		return SHORT_TYPES.contains(clazz);
		// return clazz.getName().startsWith("java.lang");
		// || clazz.getName().startsWith("java") && hasOriginalToString(clazz) && !clazz.isAssignableFrom(Map.class)
		// && !clazz.isAssignableFrom(Collection.class) && !clazz.isAssignableFrom(Throwable.class);
	}

	private static boolean hasBadToStringImplementation(Class clazz) {
		try {
			return Throwable.class.isAssignableFrom(clazz)
					|| clazz.getMethod("toString").getDeclaringClass().equals(Object.class);
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	private static class ObjectUtilsContext {
		private int identation = 0;
		private final Map<Object, String> objects = new HashMap<Object, String>();
		private final Set<Object> toStringForSelf = new HashSet<Object>();
		private int toStringCallsCounter;
		private final int step;
		private final String excludes;
		private final String ignores;

		public ObjectUtilsContext(int step, String ignores, String excludes) {
			this.step = step;
			this.excludes = "," + excludes + ",";
			this.ignores = "," + ignores + ",";
			identation = 0;
			toStringCallsCounter = 0;
		}

		public boolean isMaximumLevel() {
			return toStringCallsCounter > MAX_DEEP;
		}

		public void incrementToStringCalls() {
			toStringCallsCounter++;
		}

		public boolean decrementToStringCalls() {
			toStringCallsCounter--;
			if (toStringCallsCounter < 0) {
				throw new RuntimeException("Too many removeContext [" + toStringCallsCounter + "].");
			}
			return toStringCallsCounter == 0;
		}

		public void deident() {
			identation -= step;
			if (identation < 0) {
				throw new RuntimeException("Too many deidents [" + identation + "].");
			}
		}

		public void ident() {
			identation += step;
		}

		public String find(Object key) {
			return objects.get(key);
		}

		public String save(Object object) {
			String value = objects.get(object);
			if (value != null) {
				return value;
			}
			String id = Integer.toString(objects.size());
			objects.put(object, "@" + id);
			return id;
		}

		public void callOriginalToString(Object object) {
			toStringForSelf.add(object);
		}

		public boolean isOriginalToStringCalled(Object object) {
			return toStringForSelf.contains(object);
		}

		public String getRowStart() {
			return all.substring(0, getIdentation());
		}

		public String getRowEnd() {
			return SystemUtils.LINE_SEPARATOR;
		}

		private int getIdentation() {
			return Math.min(identation, all.length());
		}

		public boolean accept(String field) {
			return !excludes.contains("," + field + ",");
		}

		public boolean shouldIgnore(String field) {
			return ignores.contains("," + field + ",");
		}

	}

	private static String reflectedToString(Object object, final boolean useToString, final MyStringStyle toStringStyle) {
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object, toStringStyle, null, null,
				DEFAULT_TRANSIENTS, false) {
			@Override
			protected boolean accept(Field f) {
				if (f.getName().equals("stackTrace")) {
					return false;
				}
				if (f.getName().equals("cause") && f.getType().isAssignableFrom(Throwable.class)) {
					return false;
				}
				if (!getContext().accept(f.getName())) {
					return false;
				}
				return super.accept(f);
			}

			@Override
			public ToStringBuilder append(String fieldName, Object object) {
				String value = null;
				if (getContext().shouldIgnore(fieldName)) {
					value = IGNORED_VALUE;
				} else {
					value = internalToString(object, useToString, toStringStyle);
				}
				return super.append(fieldName, value);
			}
		};
		return builder.toString();
	}

	private static class MyStringStyle extends ToStringStyle {
		private static final long serialVersionUID = -3053031248321811775L;
		private final boolean useToString;
		private final boolean displayTypes;

		public MyStringStyle(boolean singleLine, boolean useToString, boolean classDecorators, boolean displayTypes) {
			super();
			this.displayTypes = displayTypes;
			this.useToString = useToString;
			this.setUseShortClassName(false);
			this.setUseClassName(classDecorators);
			this.setUseIdentityHashCode(false);
			this.setFieldSeparatorAtEnd(false);
			if (!singleLine) {
				this.setFieldSeparatorAtStart(true);
				this.setContentStart("");
				this.setContentEnd("");
			} else {
				this.setContentStart("[");
				this.setContentEnd("]");
			}
		}

		@Override
		public void appendStart(StringBuffer buffer, Object object) {
			super.appendStart(buffer, object);
			getContext().ident();
		}

		@Override
		public void appendEnd(StringBuffer buffer, Object object) {
			getContext().deident();
		}

		@Override
		protected void appendFieldStart(StringBuffer buffer, String fieldName) {
			buffer.append(getContext().getRowEnd()).append(getContext().getRowStart());
			super.appendFieldStart(buffer, fieldName);
		}

		@Override
		protected void appendFieldEnd(StringBuffer buffer, String fieldName) {
			// buffer.append(getContext().getRowEnd());
		}

		@Override
		protected void appendFieldSeparator(StringBuffer buffer) {
		}

		@Override
		protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
			if (useToString) {
				super.appendDetail(buffer, fieldName, (displayTypes ? value.getClass().getName() + ":" : "") + value);
			} else {
				super.appendDetail(buffer, fieldName, (displayTypes ? value.getClass().getName() + ":" : "")
						+ ObjectUtils.toString(value));
			}
		}
	}

	private static String mapToString(Map<Object, Object> map, final boolean useToString,
			final MyStringStyle toStringStyle) {
		return collectionToDelimitedString(map.getClass(), map.entrySet(), "", "", "",
				new Mapper<Map.Entry<Object, Object>>() {
					@Override
					public String map(Entry<Object, Object> element) {
						if (getContext().shouldIgnore(element.getKey().toString())) {
							return element.getKey() + "=" + IGNORED_VALUE;
						}
						return element.getKey() + "="
								+ internalToString(element.getValue(), useToString, toStringStyle);
					}

					@Override
					public boolean accept(Entry<Object, Object> element) {
						return getContext().accept(element.getKey().toString());
					}
				});
	}

	private static String arrayToString(Object array, boolean useToString, MyStringStyle toStringStyle) {
		int length = Array.getLength(array);
		StringBuilder sb = new StringBuilder();
		sb.append("array[").append(array.getClass().getComponentType().getName()).append("]");
		getContext().ident();
		try {
			for (int i = 0; i < length; i++) {
				Object item = Array.get(array, i);
				sb.append(getContext().getRowEnd()).append(getContext().getRowStart())
						.append(internalToString(item, useToString, toStringStyle));
			}
			return sb.toString();
		} finally {
			getContext().deident();
		}
	}

	private static String collectionToString(Collection<Object> collection, final boolean useToString,
			final MyStringStyle toStringStyle) {
		return collectionToDelimitedString(collection.getClass(), collection, "", "", "", new Mapper<Object>() {
			@Override
			public String map(Object element) {
				return internalToString(element, useToString, toStringStyle);
			}

			@Override
			public boolean accept(Object element) {
				return true;
			}
		});
	}

	private static <T> String collectionToDelimitedString(Class clazz, Collection<T> coll, String delim,
			String elementPrefix, String elementSuffix, Mapper<T> mapper) {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName());
		getContext().ident();
		try {
			if (CollectionUtils.isEmpty(coll)) {
				return sb.toString();
			}
			Iterator<T> it = coll.iterator();
			while (it.hasNext()) {
				T entry = it.next();
				if (mapper.accept(entry)) {
					sb.append(getContext().getRowEnd()).append(getContext().getRowStart()).append(elementPrefix)
							.append(mapper.map(entry)).append(elementSuffix);
					if (it.hasNext()) {
						sb.append(delim);
					}
				}
			}
			return sb.toString();
		} finally {
			getContext().deident();
		}
	}

	static interface Mapper<T> {
		boolean accept(T element);

		String map(T element);
	}

	// TO STRING SPECIALS

	private static String throwableToString(Throwable throwable, boolean useToString, MyStringStyle toStringStyle) {
		return getFullStackTrace(throwable, useToString, toStringStyle);
	}

	private static String getFullStackTrace(Throwable throwable, boolean useToString, MyStringStyle toStringStyle) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		Throwable[] ts = getThrowables(throwable);
		for (int i = 0; i < ts.length; i++) {
			_printStackTrace(ts[i], pw, useToString, toStringStyle);
			if (isNestedThrowable(ts[i])) {
				break;
			}
		}
		return sw.getBuffer().toString();
	}

	private static void _printStackTrace(Throwable t, PrintWriter printwriter, boolean useToString,
			MyStringStyle toStringStyle) {
		printwriter.print(reflectedToString(t, useToString, toStringStyle));
		printwriter.print(getContext().getRowEnd());
		getContext().ident();
		printwriter.print(getContext().getRowStart());
		printwriter.print("Stacktrace:");
		StackTraceElement astacktraceelement[] = t.getStackTrace();
		getContext().ident();
		for (int i = 0; i < astacktraceelement.length; i++) {
			printwriter.print(getContext().getRowEnd());
			printwriter.print(getContext().getRowStart());
			printwriter.print("at ");
			printwriter.print(astacktraceelement[i]);
		}
		getContext().deident();
		getContext().deident();

		Throwable throwable = t.getCause();
		if (throwable != null)
			_printStackTraceAsCause(throwable, printwriter, astacktraceelement, useToString, toStringStyle);
	}

	private static void _printStackTraceAsCause(Throwable throwable, PrintWriter printstream,
			StackTraceElement[] astacktraceelement, boolean useToString, MyStringStyle toStringStyle) {
		StackTraceElement astacktraceelement1[] = throwable.getStackTrace();
		int i = astacktraceelement1.length - 1;
		for (int j = astacktraceelement.length - 1; i >= 0 && j >= 0
				&& astacktraceelement1[i].equals(astacktraceelement[j]); j--)
			i--;

		int k = astacktraceelement1.length - 1 - i;
		printstream.print(getContext().getRowEnd());
		getContext().ident();
		printstream.print(getContext().getRowStart());
		printstream.print("Caused by: ");
		printstream.print(reflectedToString(throwable, useToString, toStringStyle));
		printstream.print(getContext().getRowEnd());
		getContext().ident();
		printstream.print(getContext().getRowStart());
		printstream.print("Stacktrace:");
		getContext().ident();
		for (int l = 0; l <= i; l++) {
			printstream.print(getContext().getRowEnd());
			printstream.print(getContext().getRowStart());
			printstream.print("at ");
			printstream.print(astacktraceelement1[l]);
		}

		if (k != 0) {
			printstream.print(getContext().getRowEnd());
			printstream.print(getContext().getRowStart());
			printstream.print("... ");
			printstream.print(k);
			printstream.print(" more");
		}
		getContext().deident();
		getContext().deident();
		getContext().deident();

		Throwable throwable2 = throwable.getCause();
		if (throwable2 != null)
			_printStackTraceAsCause(throwable2, printstream, astacktraceelement1, useToString, toStringStyle);
	}

	private static Throwable[] getThrowables(Throwable throwable) {
		List list = getThrowableList(throwable);
		return (Throwable[]) list.toArray(new Throwable[list.size()]);
	}

	private static List getThrowableList(Throwable throwable) {
		List list = new ArrayList();
		while (throwable != null && list.contains(throwable) == false) {
			list.add(throwable);
			throwable = ExceptionUtils.getCause(throwable);
		}
		return list;
	}

	private static boolean isNestedThrowable(Throwable throwable) {
		if (throwable == null) {
			return false;
		}

		if (throwable instanceof Nestable) {
			return true;
		} else if (throwable instanceof SQLException) {
			return true;
		} else if (throwable instanceof InvocationTargetException) {
			return true;
		} else if (isThrowableNested()) {
			return true;
		}

		Class cls = throwable.getClass();
		synchronized (CAUSE_METHOD_NAMES_LOCK) {
			for (int i = 0, isize = CAUSE_METHOD_NAMES.length; i < isize; i++) {
				try {
					Method method = cls.getMethod(CAUSE_METHOD_NAMES[i]);
					if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
						return true;
					}
				} catch (NoSuchMethodException ignored) {
					// exception ignored
				} catch (SecurityException ignored) {
					// exception ignored
				}
			}
		}

		try {
			Field field = cls.getField("detail");
			if (field != null) {
				return true;
			}
		} catch (NoSuchFieldException ignored) {
			// exception ignored
		} catch (SecurityException ignored) {
			// exception ignored
		}

		return false;
	}

	/**
	 * <p>
	 * The names of methods commonly used to access a wrapped exception.
	 * </p>
	 */
	private static String[] CAUSE_METHOD_NAMES = { "getCause", "getNextException", "getTargetException",
			"getException", "getSourceException", "getRootCause", "getCausedByException", "getNested",
			"getLinkedException", "getNestedException", "getLinkedCause", "getThrowable",
			// costin: added for batch sql exceptions
			"getNextException" };

	private static boolean isThrowableNested() {
		return THROWABLE_CAUSE_METHOD != null;
	}

	/**
	 * <p>
	 * The Method object for Java 1.4 getCause.
	 * </p>
	 */
	private static final Method THROWABLE_CAUSE_METHOD;

	/**
	 * <p>
	 * The Method object for Java 1.4 initCause.
	 * </p>
	 */
	private static final Method THROWABLE_INITCAUSE_METHOD;

	// Lock object for CAUSE_METHOD_NAMES
	private static final Object CAUSE_METHOD_NAMES_LOCK = new Object();
	static {
		Method causeMethod;
		try {
			causeMethod = Throwable.class.getMethod("getCause", (Class[]) null);
		} catch (Exception e) {
			causeMethod = null;
		}
		THROWABLE_CAUSE_METHOD = causeMethod;
		try {
			causeMethod = Throwable.class.getMethod("initCause", new Class[] { Throwable.class });
		} catch (Exception e) {
			causeMethod = null;
		}
		THROWABLE_INITCAUSE_METHOD = causeMethod;
	}
}
