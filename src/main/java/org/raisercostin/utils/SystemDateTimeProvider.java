package org.raisercostin.utils;

import org.joda.time.DateTime;


public class SystemDateTimeProvider implements DateTimeProvider {
    @Override
	public DateTime getDateTime() {
        return new DateTime();
    }
}
