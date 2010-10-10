package org.raisercostin.utils;

import org.joda.time.DateTime;


public class SystemDateTimeProvider implements DateTimeProvider {
    public DateTime getDateTime() {
        return new DateTime();
    }
}
