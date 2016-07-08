package org.raisercostin.util;

import javax.xml.datatype.*;

import org.joda.time.DateTime;

public class Conversions {
	private static DatatypeFactory instance = createInstance();

	private static DatatypeFactory createInstance() {
		try {
			DatatypeFactory newInstance = DatatypeFactory.newInstance();
			return newInstance;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static XMLGregorianCalendar toCalendar(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		} else {
			return instance.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
		}
	}

	public static DateTime toDateTime(XMLGregorianCalendar dateTime) {
		if (dateTime == null) {
			return null;
		}
		return new DateTime(dateTime.toGregorianCalendar());
	}
}
