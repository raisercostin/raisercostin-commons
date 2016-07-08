package org.raisercostin.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedOrSystemDateTimeProvider implements DateTimeProvider {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FixedOrSystemDateTimeProvider.class);

	private DateTime dateTime;

	private boolean enableFixedDateTime;

	private String defaultFixedSimpleDateTimeFormatPattern;

	private String defaultFixedSimpleDateTimeFormatValue;

	public FixedOrSystemDateTimeProvider() {
		enableFixedDateTime = true;
	}

	public void setDefaultFixedSimpleDateTimeFormatPattern(String defaultFixedSimpleDateTimeFormatPattern) {
		this.defaultFixedSimpleDateTimeFormatPattern = defaultFixedSimpleDateTimeFormatPattern;
	}

	public void setDefaultFixedSimpleDateTimeFormatValue(String defaultFixedSimpleDateTimeFormatValue) {
		this.defaultFixedSimpleDateTimeFormatValue = defaultFixedSimpleDateTimeFormatValue;
	}

	@Override
	public DateTime getDateTime() {
		if (enableFixedDateTime) {
			if (dateTime == null) {
				dateTime = org.raisercostin.utils.DateTimeUtils.constructDateTimeUsingCustomFormat(defaultFixedSimpleDateTimeFormatPattern,
						defaultFixedSimpleDateTimeFormatValue);
				logger.info("A fixedDateTime wasn't set so the default value will be used for current DateTime: " + dateTime);
			}
			return dateTime;
		} else {
			return new DateTime();
		}
	}

	public void setDateTime(DateTime dateTime) {
		if (dateTime != null) {
			org.joda.time.DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());
			DateTimeZone.setDefault(dateTime.getZone());
		}
		this.dateTime = dateTime;
		enableFixedDateTime = true;
		if (logger.isInfoEnabled()) {
			logger.info("Enabled FixedDateTimeProvider. Current datetime:" + dateTime);
		}
	}

	public void enableSystemDateTimeProvider() {
		org.raisercostin.utils.DateTimeUtils.resetDateTimeAndZoneToPlatformDefaults();
		enableFixedDateTime = false;
	}
}
