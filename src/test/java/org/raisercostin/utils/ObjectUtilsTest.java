package org.raisercostin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {

	@Test
	public void testHttpRequest() {
		Assert.assertEquals("true", ObjectUtils.toStringDump(true));
	}

	@Test
	public void testXml2() {
		Assert.assertEquals(
				"<SedEnvelope>\n  <sedHeader>\n    <ackIssue>false</ackIssue>\n  </sedHeader>\n  <sed>\n    <sedData>&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;\n"
						+ "&lt;ns2:ack xmlns:ns2=\"http://messages.system.eessi.dgempl.ec.eu/\"&gt;\n"
						+ "    &lt;type&gt;1&lt;/type&gt;\n"
						+ "    &lt;code&gt;43&lt;/code&gt;\n"
						+ "    &lt;ackMessage&gt;LocalSEDID does not exist&lt;/ackMessage&gt;\n"
						+ "&lt;/ns2:ack&gt;\n"
						+ "</sedData>\n  </sed>\n</SedEnvelope>",
				ObjectUtils
						.toString("<SedEnvelope><sedHeader><ackIssue>false</ackIssue></sedHeader><sed><sedData>&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;\n"
								+ "&lt;ns2:ack xmlns:ns2=\"http://messages.system.eessi.dgempl.ec.eu/\"&gt;\n"
								+ "    &lt;type&gt;1&lt;/type&gt;\n"
								+ "    &lt;code&gt;43&lt;/code&gt;\n"
								+ "    &lt;ackMessage&gt;LocalSEDID does not exist&lt;/ackMessage&gt;\n"
								+ "&lt;/ns2:ack&gt;\n" + "</sedData></sed></SedEnvelope>"));
	}

	@Test
	public void testSimpleTypes() {
		Assert.assertTrue(ObjectUtils.toString(new RuntimeException("mesaj"), false, false, true).contains(
				"detailMessage=java.lang.String:mesaj"));
		Assert.assertTrue(ObjectUtils.toString(new RuntimeException("mesaj")).contains("detailMessage=mesaj"));
		Assert.assertEquals("aa", ObjectUtils.toString("aa"));
		Assert.assertEquals("2", ObjectUtils.toString(new Integer(2)));
	}

	@Test
	public void testException() {
		RuntimeException e0 = new RuntimeException("exception0");
		RuntimeException e1 = new RuntimeException("exception1", e0);
		RuntimeException e2 = new RuntimeException("exception2", e1);
		SomeException2 e3 = new SomeException2("exception3", e2);
		Assert.assertTrue(ObjectUtils.toString(e3).contains("detailMessage=exception0"));
	}

	@Test
	public void testLineEnd() {
		int[][] a = new int[2][3];
		a[0] = new int[] { 1, 2, 3 };
		a[1] = new int[] { 4, 5, 6, 7 };
		assertStringEquals(
				"array[[I]\n.   array[int]\n.   .   1\n.   .   2\n.   .   3\n.   array[int]\n.   .   4\n.   .   5\n.   .   6\n.   .   7",
				ObjectUtils.toStringDump(a));
	}

	@Test
	public void testXml() {
		Assert.assertEquals(
				"!xmlFormatingFailed org.xml.sax.SAXParseException: XML document structures must start and end within the same entity. xml=[<?xml version=\"1.0\"?><catalog><book id=\"bk101\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>]",
				ObjectUtils
						.toString("<?xml version=\"1.0\"?><catalog><book id=\"bk101\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>"));
		Assert.assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<catalog>\n  <book id=\"bk101\">\n    <author>Gambardella, Matthew</author>\n    <title>XML Developer's Guide</title>\n    <genre>Computer</genre>\n  </book>\n</catalog>",
				ObjectUtils
						.toString("<?xml version=\"1.0\"?><catalog><book id=\"bk101\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre></book></catalog>"));
		Assert.assertEquals("<casa><etaj>1</etaj><etaj>2</etaj><etaj>3</etaj></casa2>",
				ObjectUtils.toString("<casa><etaj>1</etaj><etaj>2</etaj><etaj>3</etaj></casa2>"));
		Assert.assertEquals("<casa>\n  <etaj>1</etaj>\n  <etaj>2</etaj>\n  <etaj>3</etaj>\n</casa>",
				ObjectUtils.toString("<casa><etaj>1</etaj><etaj>2</etaj><etaj>3</etaj></casa>"));
	}


	@Test
	public void testToStringFromEnum() {
		EnumTest a = new EnumTest(SomeEnum.VALUE1);
		assertStringEquals(
				"org.raisercostin.utils.ObjectUtilsTest$EnumTest\n.   theenum=org.raisercostin.utils.ObjectUtilsTest$SomeEnum\n.   .   field=a\n.   .   dtest=org.raisercostin.utils.ObjectUtilsTest$DTest\n.   .   .   value=1\n.   .   name=VALUE1\n.   .   ordinal=0",
				ObjectUtils.toStringDump(a));
		assertStringEquals(
				"java.util.Arrays$ArrayList\n.   org.raisercostin.utils.ObjectUtilsTest$EnumTest\n.   .   theenum=org.raisercostin.utils.ObjectUtilsTest$SomeEnum\n.   .   .   field=a\n.   .   .   dtest=org.raisercostin.utils.ObjectUtilsTest$DTest\n.   .   .   .   value=1\n.   .   .   name=VALUE1\n.   .   .   ordinal=0",
				ObjectUtils.toStringDump(Arrays.asList(new Object[] { a })));
	}

	@Test
	public void testSelfReferenced() {
		assertStringEquals("aa", ObjectUtils.toStringDump("aa"));
		assertStringEquals("org.raisercostin.utils.ObjectUtilsTest$ETest\n.   value=value_e",
				ObjectUtils.toStringDump(new ETest("value_e")));
		CTest c = new CTest("aha", new BTest("value_b"), new DTest("value_d"), new ETest("value_e"));
		// Assert.assertEquals("", ReflectionToStringBuilder.toString(c));
		assertStringEquals(
				"org.raisercostin.utils.ObjectUtilsTest$CTest\n.   value=aha\n.   me=@0\n.   b=org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   value=value_b\n.   d=org.raisercostin.utils.ObjectUtilsTest$DTest\n.   .   value=value_d\n.   e=org.raisercostin.utils.ObjectUtilsTest$ETest\n.   .   value=value_e",
				ObjectUtils.toStringDump(c));
	}

	@Test
	public void testToStringUsingToString() {
		ATest a = new ATest();
		assertStringEquals(
				"org.raisercostin.utils.ObjectUtilsTest$ATest\n.   testInside1=org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   value=value1\n.   testInside2=org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   value=value2\n.   map=java.util.LinkedHashMap\n.   .   key1=value 1\n.   .   key2=value 2\n.   .   key3=value 3\n.   .   key4pass=*****\n.   .   key4=noToString\n.   prop=java.util.Properties\n.   .   pass2=*****\n.   .   p4=v4\n.   .   p3=v3\n.   .   p1=v1\n"
						+ ".   list=java.util.ArrayList\n.   .   field1\n.   .   org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   .   value=value3\n.   .   field2\n.   .   org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   .   value=value1\n.   .   field3",
				ObjectUtils.toString(a, "key4pass,pass2"));
	}

	@Test
	public void testToStringDump() {
		BTest b = new BTest("a");
		assertStringEquals("org.raisercostin.utils.ObjectUtilsTest$BTest\n.   value=a", ObjectUtils.toStringDump(b));
		ATest a = new ATest();
		assertStringEquals(
				"org.raisercostin.utils.ObjectUtilsTest$ATest\n.   testInside1=org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   value=value1\n.   testInside2=org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   value=value2\n.   map=java.util.LinkedHashMap\n.   .   key1=value 1\n.   .   key2=value 2\n.   .   key3=value 3\n.   .   key4pass=*****\n.   .   key4=org.raisercostin.utils.ObjectUtilsTest$DTest\n.   .   .   value=value4\n.   prop=java.util.Properties\n.   .   pass2=*****\n.   .   p4=v4\n.   .   p3=v3\n.   .   p1=v1\n"
						+ ".   list=java.util.ArrayList\n.   .   field1\n.   .   org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   .   value=value3\n.   .   field2\n.   .   @1\n.   .   field3",
				ObjectUtils.toStringDump(a, "key4pass,pass2"));
	}

	@Test
	public void testToStringDumpWithExcludes() {
		ATest a = new ATest();
		assertStringEquals(
				"org.raisercostin.utils.ObjectUtilsTest$ATest\n.   testInside1=org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   value=value1\n.   map=java.util.LinkedHashMap\n.   .   key1=value 1\n.   .   key2=value 2\n.   .   key4pass=*****\n.   .   key4=org.raisercostin.utils.ObjectUtilsTest$DTest"
						+ "\n.   .   .   value=value4\n.   prop=java.util.Properties\n.   .   pass2=*****\n.   .   p4=v4\n.   .   p3=v3\n.   .   p1=v1\n"
						+ ".   list=java.util.ArrayList\n.   .   field1\n.   .   org.raisercostin.utils.ObjectUtilsTest$BTest\n.   .   .   value=value3\n.   .   field2\n.   .   @1\n.   .   field3",
				ObjectUtils.toStringDump(a, "key4pass,pass2", "key3,testInside2"));
	}

	@Test
	public void testToStringArrays() {
		EnumTest a = new EnumTest(SomeEnum.VALUE1);
		assertStringEquals("array[int]\n.   1\n.   2\n.   3\n.   4", ObjectUtils.toStringDump(new int[] { 1, 2, 3, 4 }));
		assertStringEquals("array[double]\n.   1.3\n.   2.1\n.   3.0\n.   4.0",
				ObjectUtils.toStringDump(new double[] { 1.3, 2.1, 3, 4 }));
		assertStringEquals("array[java.lang.Integer]\n.   1\n.   2\n.   3\n.   4",
				ObjectUtils.toStringDump(new Integer[] { 1, 2, 3, 4 }));
		assertStringEquals(
				"array[java.lang.Object]\n.   org.raisercostin.utils.ObjectUtilsTest$EnumTest\n.   .   theenum=org.raisercostin.utils.ObjectUtilsTest$SomeEnum\n.   .   .   field=a\n.   .   .   dtest=org.raisercostin.utils.ObjectUtilsTest$DTest\n.   .   .   .   value=1\n.   .   .   name=VALUE1\n.   .   .   ordinal=0",
				ObjectUtils.toStringDump(new Object[] { a }));
	}

	private static class ATest {
		@SuppressWarnings("unused")
		private final BTest testInside1;
		@SuppressWarnings("unused")
		private final BTest testInside2;
		private final Map<String, Object> map;
		private final Properties prop;
		private final List<Object> list;

		public ATest() {
			testInside1 = new BTest("value1");
			testInside2 = new BTest("value2");
			map = new LinkedHashMap<String, Object>();
			prop = new Properties();
			prop.put("p1", "v1");
			prop.put("pass2", "v2");
			prop.put("p3", "v3");
			prop.put("p4", "v4");
			map.put("key1", "value 1");
			map.put("key2", "value 2");
			map.put("key3", "value 3");
			map.put("key4pass", "value 4");
			map.put("key4", new DTest("value4"));
			list = new ArrayList<Object>();
			list.add("field1");
			list.add(new BTest("value3"));
			list.add("field2");
			list.add(testInside1);
			list.add("field3");
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}

	private static class EnumTest {
		private final SomeEnum theenum;

		public EnumTest(SomeEnum a) {
			this.theenum = a;
		}
	}

	private static enum SomeEnum {
		VALUE1("a", new DTest("1")), Value2("b", new DTest("2"));
		private final String field;
		private final DTest dtest;

		SomeEnum(String value, DTest dtest) {
			this.field = value;
			this.dtest = dtest;
		}
	}

	private static class BTest {
		@SuppressWarnings("unused")
		private final String value;

		public BTest(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}

	private static class DTest {
		@SuppressWarnings("unused")
		private final String value;

		public DTest(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "noToString";
		}
	}

	private static class ETest {
		@SuppressWarnings("unused")
		private final String value;

		public ETest(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "noToString";
		}
	}

	private static class CTest {
		@SuppressWarnings("unused")
		private final String value;
		private final CTest me;
		private final BTest b;
		private final DTest d;
		private final ETest e;

		public CTest(String value, BTest b, DTest d, ETest e) {
			this.value = value;
			this.me = this;
			this.b = b;
			this.d = d;
			this.e = e;
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}

	private static class SomeException1 extends RuntimeException {
		private static final long serialVersionUID = -2110372322263810384L;
		private final String field = "mess1";

		public SomeException1() {
			super();
		}

		public SomeException1(String s, Throwable throwable) {
			super(s, throwable);
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}

	private static class SomeException2 extends RuntimeException {
		private static final long serialVersionUID = 2953928302453781778L;
		private final String field = "mess2";

		public SomeException2() {
			super();
		}

		public SomeException2(String s, Throwable throwable) {
			super(s, throwable);
		}

	}

	private void assertStringEquals(String expected, String actual) {
		Assert.assertEquals(escape(expected), escape(actual));
	}

	private String escape(String string) {
		return string.replaceAll("\r", "");
	}
}
