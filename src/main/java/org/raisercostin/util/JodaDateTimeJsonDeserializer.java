package org.raisercostin.util;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

// http://blog.bdoughan.com/2011/05/jaxb-and-joda-time-dates-and-times.html
public class JodaDateTimeJsonDeserializer extends JsonDeserializer<DateTime> {
	@Override
	public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String date = jp.getText();
		if (!date.contains("-")) {
			return new DateTime(jp.getLongValue());
		}
		return DateTime.parse(date);
	}
}