/*
 * Created on Jun 1, 2005
 */
package org.raisercostin.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class FormatterTest extends TestCase {
	public void testParseDate() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57), Formatter
				.parseDate("Wed Apr 02 12:42:57 2003"));
	}

	public void testParseDateYear() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57)
				.get(Calendar.YEAR), Formatter.parseDate(
				"Wed Apr 02 12:42:57 2003").get(Calendar.YEAR));
	}

	public void testParseDateMonth() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57)
				.get(Calendar.MONTH), Formatter.parseDate(
				"Wed Apr 02 12:42:57 2003").get(Calendar.MONTH));
	}

	public void testParseDateDay() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57)
				.get(Calendar.DAY_OF_MONTH), Formatter.parseDate(
				"Wed Apr 02 12:42:57 2003").get(Calendar.DAY_OF_MONTH));
	}

	public void testParseDateHour() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57)
				.get(Calendar.HOUR_OF_DAY), Formatter.parseDate(
				"Wed Apr 02 12:42:57 2003").get(Calendar.HOUR_OF_DAY));
	}

	public void testParseDateMinute() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57)
				.get(Calendar.MINUTE), Formatter.parseDate(
				"Wed Apr 02 12:42:57 2003").get(Calendar.MINUTE));
	}

	public void testParseDateSecond() {
		assertEquals(new GregorianCalendar(2003, 03, 02, 12, 42, 57)
				.get(Calendar.SECOND), Formatter.parseDate(
				"Wed Apr 02 12:42:57 2003").get(Calendar.SECOND));
	}
}
