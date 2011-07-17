package org.raisercostin.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.raisercostin.utils.beans.BeanUtils;
import org.springframework.util.StringUtils;

public class ObjectUtils {
	// private static final MyStringStyle myStringStyle = new MyStringStyle();

	private static final int STEP = 4;
	private static final String all = ".   .   .   .   .   .   .   .   .   .   .   .   .   .   ";
	private static final Set<Class<?>> categories = new HashSet<Class<?>>();
	private static final Map<Object, String> used = new HashMap<Object, String>();
	private static int id = 0;
	// Thread local is needed because multiple toStrings could be invoked.
	private static final ThreadLocal<MutableInt> identationThreadLocal = new ThreadLocal<MutableInt>();

	public static boolean shouldUseGenericToString(Object object) {
		return categories.contains(object.getClass());
	}

	private static final boolean DEFAULT_TRANSIENTS = false;

	public static void registerForGenericToString(Class<?> theClass) {
		categories.add(theClass);
	}

	public static String toString(Object object, String supplementalMessage, boolean singleLine) {
		return toString(object, singleLine, true) + " - " + supplementalMessage;
	}

	public static String toString(Object object, String supplementalMessage) {
		return toString(object) + " - " + supplementalMessage;
	}

	public static String toStringDump(Object object) {
		return toString(object, false, false);
	}

	public static String toString(Object object) {
		return toString(object, false, true);
	}

	public static String toString(Object object, FieldDecorator fieldDecorator) {
		return toString(object, false, true, fieldDecorator);
	}

	private static String toString(Object object, boolean singleLine, boolean useToString, FieldDecorator fieldDecorator) {
		return toString(object, useToString, fieldDecorator, new MyStringStyle(singleLine, useToString, true));
	}

	public static String toString(Object object, boolean singleLine, boolean useToString) {
		return toString(object, singleLine, useToString, new FieldDecorator() {
			@Override
			public boolean accept(Field f) {
				return !f.getName().contains("password") && !f.getName().contains("IBAN");
			}

			@Override
			public void addSupplementalFields(Map<String, Object> supplementalFields) {
			}
		});
	}

	private static class MyStringStyle extends ToStringStyle {
		private static final long serialVersionUID = -3053031248321811775L;
		private final boolean useToString;

		public MyStringStyle(boolean singleLine, boolean useToString, boolean classDecorators) {
			super();
			this.useToString = useToString;
			this.setUseShortClassName(true);
			this.setUseClassName(classDecorators);
			this.setUseIdentityHashCode(false);
			this.setContentStart("[");
			if (!singleLine) {
				this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + all.substring(0, getIdentation(0)));
				this.setFieldSeparatorAtStart(true);
			}
			// this.setContentEnd(SystemUtils.LINE_SEPARATOR + all.substring(0, getIdentation(-STEP)) + "]");
			this.setContentEnd("]");
		}

		@Override
		public void appendStart(StringBuffer buffer, Object object) {
			super.appendStart(buffer, object);
			ident(STEP);
		}

		@Override
		public void appendEnd(StringBuffer buffer, Object object) {
			super.appendEnd(buffer, object);
			ident(-STEP);
		}

		@Override
		protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
			if (shouldUseGenericToString(value)) {
				super.appendDetail(buffer, fieldName, ObjectUtils.toString(value));
			} else {
				super.appendDetail(buffer, fieldName, value);
			}
		}

		public String customToString(Object object) {
			if (object == null) {
				return null;
			}
			return customToStringInternal(object, useToString, null, null);
		}
	}

	private static void ident(int diff) {
		ObjectUtils.getIdentation().add(diff);
	}

	private static int getIdentation(int diff) {
		int intValue = getIdentation().intValue();
		return Math.min(intValue + diff, all.length());
	}

	private static MutableInt getIdentation() {
		MutableInt mutableInt = identationThreadLocal.get();
		if (mutableInt == null) {
			mutableInt = new MutableInt(STEP);
			identationThreadLocal.set(mutableInt);
		}
		return mutableInt;
	}

	public static interface FieldDecorator {
		boolean accept(Field f);

		void addSupplementalFields(Map<String, Object> supplementalFields);
	}

	public static String toIdentedString(String text, int diff) {
		// text = text.replaceAll("[\n\r]+", "\n");
		text = text.replaceAll("[\n\r]+", SystemUtils.LINE_SEPARATOR + all.substring(0, getIdentation(diff)));
		return text;
	}

	private static String toIdentedString(String text) {
		return toIdentedString(text, 0);
	}

	private static String collectionToString(Collection<Object> object) {
		StringBuilder sb = new StringBuilder();
		sb.append(object.getClass().getName()).append("[").append(object.size()).append("]{");
		int max = 10;
		int i = 0;
		for (Object object2 : object) {
			sb.append("\n").append(object2);
			if (i == max) {
				break;
			}
		}
		if (object.size() > max) {
			sb.append("\n...");
		}
		sb.append("}");
		return sb.toString();
	}

	private static String mapToString(Map<Object, Object> object) {
		StringBuilder sb = new StringBuilder();
		sb.append("Map{");
		for (Map.Entry<Object, Object> entry : object.entrySet()) {
			sb.append("\n" + entry.getKey() + "=" + entry.getValue());
		}
		sb.append("}");
		return sb.toString();
	}

	private static String throwableToString(Object object) {
		return ExceptionUtils.getFullStackTrace((Throwable) object);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String customToStringInternal(Object object, boolean useToString, FieldDecorator fieldDecorator,
			MyStringStyle toStringStyle) {
		String value = null;
		if (object == null) {
			value = null;
		} else if (object instanceof Throwable) {
			value = toIdentedString(throwableToString(object));
		} else if (object instanceof Map) {
			value = toIdentedString(mapToString((Map<Object, Object>) object));
		} else if (object instanceof Collection) {
			value = toIdentedString(collectionToString((Collection<Object>) object));
		} else if ((object instanceof Class) && ((Class<?>) object).isEnum()) {
			value = toIdentedString(enumToString((Class<Enum>) object));
		} else {
			if (useToString) {
				value = toOriginalString(object);
			} else {
				value = toStringUsingReflection(object, fieldDecorator, toStringStyle);
			}
		}
		return value;
	}

	private static String toOriginalString(Object object) {
		if (object == null) {
			return null;
		}
		String id = used.get(object);
		if (id == null) {
			id = createNewId();
			used.put(object, id);
			return id + ":" + object.toString();
		} else {
			return "@" + id;
		}
	}

	private static String createNewId() {
		return id + "";
	}

	private static String enumToString(@SuppressWarnings("rawtypes") Class<Enum> object) {
		@SuppressWarnings("rawtypes")
		Enum[] constants = object.getEnumConstants();
		StringBuilder sb = new StringBuilder();
		sb.append(toOriginalString(object)).append(" enums=[\n");
		for (@SuppressWarnings("rawtypes")
		Enum oneConstant : constants) {
			sb.append(oneConstant.name()).append("(").append(oneConstant.ordinal()).append(")=")
					.append(ObjectUtils.toStringWithExclusionsWithoutDecorators(oneConstant, "name,ordinal", true))
					.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}

	private static String toString(Object object, boolean useToString, final FieldDecorator fieldDecorator,
			final MyStringStyle toStringStyle) {
		return customToStringInternal(object, useToString, fieldDecorator, toStringStyle);
	}

	private static String toStringUsingReflection(Object object, final FieldDecorator fieldDecorator,
			final MyStringStyle toStringStyle) {
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object, toStringStyle, null, null,
				DEFAULT_TRANSIENTS, false) {
			boolean firstTime = true;

			@Override
			protected boolean accept(Field f) {
				return super.accept(f) && (fieldDecorator == null || fieldDecorator.accept(f));
			}

			@Override
			public ToStringBuilder append(String fieldName, Object object) {
				if (firstTime) {
					firstTime = false;
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					if (fieldDecorator != null) {
						fieldDecorator.addSupplementalFields(map);
					}
					for (Map.Entry<String, Object> field : map.entrySet()) {
						super.append(field.getKey(), field.getValue());
					}
				}
				String value = toStringStyle.customToString(object);
				return super.append(fieldName, value);
			}
		};
		return builder.toString();
	}

	public static String toString(Object object, String[] excludes, boolean singleLine, boolean classDecorators) {
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object, new MyStringStyle(singleLine, true,
				classDecorators), null, null, false, false);
		builder.setExcludeFieldNames(excludes);
		return builder.toString();
	}

	public static String toString(Object object, String[] excludes) {
		return toString(object, excludes, false, true);
	}

	public static boolean equals(Object object1, Object object2) {
		return EqualsBuilder.reflectionEquals(object1, object2, false, Object.class, null);
	}

	public static BigDecimal normalize(BigDecimal rate) {
		if (rate != null) {
			return rate.stripTrailingZeros();
		}
		return null;
	}

	public static void copy(Object destination, Object source) {
		CloneBuilder.reflectionCopy(destination, source);
	}

	public static String toSingleLineString(Object object) {
		return toString(object, true, true);
	}

	public static String toStringWithExclusions(Object object, String commaSeparatedExceptedFields, boolean singleLine) {
		return toString(object, StringUtils.tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true),
				singleLine, true);
	}

	public static String toStringWithExclusionsWithoutDecorators(Object object, String commaSeparatedExceptedFields,
			boolean singleLine) {
		return toString(object, StringUtils.tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true),
				singleLine, false);
	}

	public static String toStringWithExclusions(Object object, String commaSeparatedExceptedFields) {
		return toString(object, StringUtils.tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true),
				false, true);
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

	public static int hashCode(Object object) {
		return HashCodeBuilder.reflectionHashCode(object, DEFAULT_TRANSIENTS);
	}

	public static int hashCode(Object object, String commaSeparatedExceptedFields) {
		return HashCodeBuilder.reflectionHashCode(17, 37, object, DEFAULT_TRANSIENTS, null,
				StringUtils.tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true));
	}
}
