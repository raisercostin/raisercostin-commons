package org.raisercostin.utils;

import org.joda.time.DateTime;


public class FixedDateTimeProvider implements DateTimeProvider {
    private DateTime dateTime;

    public FixedDateTimeProvider() {
        // default constructor
    }

    public FixedDateTimeProvider(DateTime dateTime) {
        setDateTime(dateTime);
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
	public DateTime getDateTime() {
        return dateTime;
    }
}
