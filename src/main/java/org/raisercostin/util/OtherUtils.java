package org.raisercostin.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang.time.FastDateFormat;
import org.raisercostin.utils.ObjectUtils;

public class OtherUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OtherUtils.class);

	private static final AtomicLong counter = new AtomicLong(0);

	public static String getTimestamp() {
		return FastDateFormat.getInstance("yyyyMMdd'T'HHmmss").format(new Date());
	}

	public static long getUniqueSequenceNumberInJvm() {
		return counter.incrementAndGet();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getField(Object object, String path) {
		try {
			return (T) getField2(object, "", path);
		} catch (RuntimeException e) {
			LOG.trace("Can't get value for path [" + path + "] for object " + ObjectUtils.toStringDump(object), e);
			return null;
		}
	}

	private static Object getField2(Object object, String prefixPath, String fieldPath) {
		if (isLastField(fieldPath)) {
			return getValue(object, prefixPath, fieldPath);
		} else {
			return getField2(getValue(object, prefixPath, firstField(fieldPath)), prefixPath + "."
					+ firstField(fieldPath), remainingFields(fieldPath));
		}
	}

	public static void setField(Object object, String fieldPath, Object newValue) {
		boolean changed = false;
		// BeanUtils.setValue(object, fieldPath, newValue);
		try {
			changeField2(object, "", fieldPath, newValue);
			changed = true;
		} catch (FieldSetException e) {
			throw e;
		} catch (RuntimeException e) {
			LOG.trace("attempt to change " + fieldPath + " with [" + newValue + "]: but failed.", e);
		}
		LOG.info("attempt to change " + fieldPath + " with [" + newValue + "]:" + (changed ? "ok" : "failed"));
	}

	private static void changeField2(Object object, String prefixPath, String fieldPath, Object newValue) {
		if (isLastField(fieldPath)) {
			setValue(object, prefixPath, fieldPath, newValue);
		} else {
			changeField2(getValue(object, prefixPath, firstField(fieldPath)), prefixPath + "." + firstField(fieldPath),
					remainingFields(fieldPath), newValue);
		}
	}

	private static String remainingFields(String fieldPath) {
		return fieldPath.substring(fieldPath.indexOf(".") + 1);
	}

	private static boolean isLastField(String fieldPath) {
		return !fieldPath.contains(".");
	}

	private static String firstField(String fieldPath) {
		return fieldPath.substring(0, fieldPath.indexOf("."));
	}

	private static Object getValue(Object object, String prefixPath, String fieldName) {
		if (object == null) {
			return null;
		}
		try {
			Field declaredField = getField(object.getClass(), fieldName);
			declaredField.setAccessible(true);
			Object value = declaredField.get(object);
			declaredField.setAccessible(false);
			return value;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Can't access " + prefixPath + "." + fieldName + " getter.", e);
		} catch (SecurityException e) {
			throw new RuntimeException("Can't access " + prefixPath + "." + fieldName + " getter.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Can't access " + prefixPath + "." + fieldName + " getter.", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Can't access " + prefixPath + "." + fieldName + " getter.", e);
		}
	}

	static private class FieldSetException extends RuntimeException {
		private static final long serialVersionUID = -2522927521472432103L;

		public FieldSetException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		@SuppressWarnings("unused")
		public FieldSetException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		@SuppressWarnings("unused")
		public FieldSetException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}

	}

	private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}

	private static void setValue(Object object, String prefixPath, String fieldName, Object newValue) {
		if (object == null) {
			return;
		}
		try {
			Field declaredField = getField(object.getClass(), fieldName);
			declaredField.setAccessible(true);
			declaredField.set(object, newValue);
			declaredField.setAccessible(false);
		} catch (IllegalArgumentException e) {
			throw new FieldSetException("Can't access " + prefixPath + "." + fieldName + " setter.", e);
		} catch (SecurityException e) {
			throw new FieldSetException("Can't access " + prefixPath + "." + fieldName + " setter.", e);
		} catch (IllegalAccessException e) {
			throw new FieldSetException("Can't access " + prefixPath + "." + fieldName + " setter.", e);
		} catch (NoSuchFieldException e) {
			throw new FieldSetException("Can't access " + prefixPath + "." + fieldName + " setter.", e);
		}
	}

	public static String createServerId(String string, String serverId) {
		return "RT-" + serverId;// + "-server-s" + OtherUtils.getUniqueSequenceNumberInJvm();
	}
	public static void addAnnotation(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotation) {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc;
		try {
			cc = pool.getCtClass(clazz.getName());
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
		ClassFile cf = cc.getClassFile();
		ConstPool cp = cf.getConstPool();
		Annotation a = new Annotation(annotation.getName(), cp);
		AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
		attr.setAnnotation(a);
		cf.addAttribute(attr);
		try {
			@SuppressWarnings("unused")
			Class<?> clazz2 = cc.toClass();
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
		// cf.setVersionToJava5();
	}
}
