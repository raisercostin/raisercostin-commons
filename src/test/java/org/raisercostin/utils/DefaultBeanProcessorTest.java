package org.raisercostin.utils;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.raisercostin.utils.annotations.Length;
import org.raisercostin.utils.beans.DefaultBeanProcessor;

public class DefaultBeanProcessorTest {

	private String testField;

	@Length(2)
	public String getTestField() {
		return testField;
	}

	public void setTestField(String testField) {
		this.testField = testField;
	}

	@Test
	public void testSearchAnnotation1() throws SecurityException, NoSuchMethodException {
		Assert.assertEquals(String.class, new DefaultBeanProcessor().searchAnnotation1(A.class.getMethod("m1")));
	}

	public static class A {
		public List<String> m1() {
			return null;
		}
	}

	@Test
	public void testLength() {
		DefaultBeanProcessorTest bean = new DefaultBeanProcessorTest();
		bean.setTestField("123456789");

		Assert.assertEquals("[123456789,9>2]", new DefaultBeanProcessor().checkLength(bean, "testField"));

		bean.setTestField("12");
		Assert.assertEquals(null, new DefaultBeanProcessor().checkLength(bean, "testField"));

	}

	@Test
	public void testLength2() {
		DefaultBeanProcessorTest bean = new DefaultBeanProcessorTest();
		bean.setTestField("123456789");
		bean.setTestField("12");
		try {
			new DefaultBeanProcessor().checkLength(bean, "unknownField");
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertTrue("Message [" + e.getMessage() + "] should contain the word " + "unknownField", e
					.getMessage().contains("unknownField"));
		}
	}
}
