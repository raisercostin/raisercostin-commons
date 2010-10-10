package org.raisercostin.utils;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);

	public static long lastGenerated = 0;

	public static long counter = 0;

	public static final long MAX_COUNTER_IN_ONE_MILISECOND = 100;

	public static final int SECONDS_PER_MINUTE = 60;

	public static final int SECONDS_PER_HOUR = 3600;

	public static final int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;

	public static DateTime constructDateTimeUsingCustomFormat(String simpleDateFormatPattern, String dateTimeValue) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(simpleDateFormatPattern);
		formatter = formatter.withOffsetParsed();
		DateTime parseDateTime = formatter.parseDateTime(dateTimeValue);
		return parseDateTime;
		// DateFormat df = new SimpleDateFormat(simpleDateFormatPattern);
		// try {
		// df.setLenient(true);
		// Date dateObject = df.parse(dateTimeValue);
		// DateTime result = new DateTime(dateObject);
		// return result;
		// } catch (ParseException e) {
		// throw new RuntimeException("Could not convert dateTime:[" + dateTimeValue + "] using dateTimeFormat:["
		// + simpleDateFormatPattern + "].", e);
		// }
	}

	public static long generateSequenceNumber() {
		long now = org.joda.time.DateTimeUtils.currentTimeMillis();
		if (now == lastGenerated) {
			counter++;
		} else {
			lastGenerated = now;
			counter = 0;
		}
		return now * MAX_COUNTER_IN_ONE_MILISECOND + counter;
	}

	public static String formatDaysHoursMinutesSeconds(int periodInSeconds) {
		int days = periodInSeconds / SECONDS_PER_DAY;
		int remaining = periodInSeconds % SECONDS_PER_DAY;
		int hours = remaining / SECONDS_PER_HOUR;
		remaining = remaining % SECONDS_PER_HOUR;
		int minutes = remaining / SECONDS_PER_MINUTE;
		int seconds = remaining % SECONDS_PER_MINUTE;
		String daysStr = (days == 0) ? "" : String.valueOf(days) + "d";
		String hoursStr = ((days == 0) && (hours == 0)) ? "" : String.valueOf(hours) + "h";
		String minutesStr = ((days == 0) && (hours == 0) && (minutes == 0)) ? "" : String.valueOf(minutes) + "m";
		String secondsStr = String.valueOf(seconds) + "s";
		return daysStr + hoursStr + minutesStr + secondsStr;
	}

	public static void resetDateTimeAndZoneToPlatformDefaults() {
		org.joda.time.DateTimeUtils.setCurrentMillisOffset(0);
		DateTimeZone.setDefault(DateTimeZone.forID(Calendar.getInstance().getTimeZone().getID()));
		if (logger.isInfoEnabled()) {
			logger.info("Joda DateTime reseted to platform defaults. Current DateTime is " + new DateTime());
		}
	}
}
