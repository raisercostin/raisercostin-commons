/*
 * Created on Jun 1, 2005
 */
package raiser.util;

import java.util.*;
import junit.framework.TestCase;

public class FormatterTest extends TestCase {
    public void testParseDate() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57),Formatter.parseDate("Wed Apr 02 12:42:57 2003"));
    }
    public void testParseDateYear() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57).get(GregorianCalendar.YEAR),Formatter.parseDate("Wed Apr 02 12:42:57 2003").get(GregorianCalendar.YEAR));
    }
    public void testParseDateMonth() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57).get(GregorianCalendar.MONTH),Formatter.parseDate("Wed Apr 02 12:42:57 2003").get(GregorianCalendar.MONTH));
    }
    public void testParseDateDay() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57).get(GregorianCalendar.DAY_OF_MONTH),Formatter.parseDate("Wed Apr 02 12:42:57 2003").get(GregorianCalendar.DAY_OF_MONTH));
    }
    public void testParseDateHour() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57).get(GregorianCalendar.HOUR_OF_DAY),Formatter.parseDate("Wed Apr 02 12:42:57 2003").get(GregorianCalendar.HOUR_OF_DAY));
    }
    public void testParseDateMinute() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57).get(GregorianCalendar.MINUTE),Formatter.parseDate("Wed Apr 02 12:42:57 2003").get(GregorianCalendar.MINUTE));
    }
    public void testParseDateSecond() {
        assertEquals(new GregorianCalendar(2003,03,02,12,42,57).get(GregorianCalendar.SECOND),Formatter.parseDate("Wed Apr 02 12:42:57 2003").get(GregorianCalendar.SECOND));
    }
}
