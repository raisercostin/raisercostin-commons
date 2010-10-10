package org.raisercostin.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.raisercostin.utils.ObjectUtils;

public class ObjectUtilsTest {

	@Test
	public void testToStringObjectBoolean() {
		ATest a = new ATest();
		Assert.assertEquals("ObjectUtilsTest.ATest[\n" + ".   testInside1=ObjectUtilsTest.BTest[\n" + ".   .   value=value1]\n" + ".   testInside2=ObjectUtilsTest.BTest[\n"
				+ ".   .   value=value2]\n" + ".   map=Map{\n.   .   key1=value 1\n.   .   key2=value 2\n.   .   key3=value 3}]".replaceAll("[\n\r]+", "\n"), a.toString().replaceAll("[\n\r]+", "\n"));
	}

	private static class ATest {
		@SuppressWarnings("unused")
		private final BTest testInside1;
		@SuppressWarnings("unused")
		private final BTest testInside2;
		private final Map<String, String> map;

		public ATest() {
			testInside1 = new BTest("value1");
			testInside2 = new BTest("value2");
			map = new LinkedHashMap<String, String>();
			map.put("key1", "value 1");
			map.put("key2", "value 2");
			map.put("key3", "value 3");
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}

	private static class BTest {
		public BTest(String value) {
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}

	@Test
	@Ignore
	public void testException() {
		Assert.assertEquals("", ObjectUtils.toString(new SomeException()));
	}

	private static class SomeException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6830122091326715468L;

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}
}
