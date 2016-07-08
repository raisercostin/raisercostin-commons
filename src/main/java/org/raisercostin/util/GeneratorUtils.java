package org.raisercostin.util;

import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.math.DoubleMath;

public class GeneratorUtils {
	private static final int scaleFactorInCaseResetingIsProlongued = 100;
	private static final AtomicLong counter = new AtomicLong(0);
	private static final long resetAt = 10000;
	private static final String FORMAT = "%s%s-C-%0" + maxDigits(resetAt * scaleFactorInCaseResetingIsProlongued)
			+ "d-%s";
	private static volatile String oldTimestamp;
	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss-SSS");

	public static void reset() {
		counter.set(0);
		oldTimestamp = null;
	}
	public static String generateUniqueSortableId(String prefix, String suffix) {
		long andIncrement = counter.getAndIncrement();
		String newTimestamp = TIMESTAMP_FORMAT.print(DateTimeUtils.currentTimeMillis());
		if (andIncrement >= resetAt && !newTimestamp.equals(oldTimestamp)) {
			// start counter from zero if timestamp is already changed and counter is too big
			counter.set(1);
			andIncrement = 0;
		}
		String result = String.format(FORMAT, prefix, newTimestamp, Long.valueOf(andIncrement), suffix);
		oldTimestamp = newTimestamp;
		return result;
	}
	private static int maxDigits(long max) {
		return DoubleMath.roundToInt(Math.log10(max) + 1, RoundingMode.FLOOR);
	}
	public static String generateUniqueId(String prefix) {
		return prefix + getTimestamp() + ".C." + counter.getAndIncrement();
		// return new MessageId(UUID.randomUUID().toString());
	}

	private static String getTimestamp() {
		return new DateTime(ISOChronology.getInstance()).toString("yyyyMMdd'T'HHmmss");
	}

	public static String generateUniqueTimestamp() {
		return counter.getAndIncrement() + "_" + getMomentAsString();
	}

	private static String getMomentAsString() {
		return DateTime.now().toString("yyyyMMddHHmmss");
	}
}
