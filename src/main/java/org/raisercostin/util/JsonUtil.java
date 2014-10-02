package org.raisercostin.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.*;
import com.google.gson.JsonParseException;

public class JsonUtil {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonUtil.class);
	private static final ObjectMapper mapper = create();

	private static ObjectMapper create() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		//mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	public static <T> String toJson(T data) {
		try {
			//ObjectMapper mapper = create();
			return mapper.writeValueAsString(data);
		} catch (IOException e) {
			LOG.warn("can't format a json object from [" + data + "]", e);
			return null;
		}
		//
		// return Json.stringify(Json.toJson(data));
	}

	public static <T> JsonNode toJsonNode(T data) {
		return mapper.valueToTree(data);
	}

	public static <T> T fromJson(String description, Class<T> theClass) {
		try {
			//ObjectMapper mapper = create();
			JsonNode parse = mapper.readValue(description, JsonNode.class);
			PlayUtils.fixClassloader(theClass);
			T fromJson = mapper.treeToValue(parse, theClass);
			return fromJson;
		} catch (JsonParseException e) {
			throw new RuntimeException("can't parse a json object of type " + theClass.getName() + " from ["
					+ shorter(description) + "]", e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("can't parse a json object of type " + theClass.getName() + " from ["
					+ shorter(description) + "]", e);
		} catch (IOException e) {
			throw new RuntimeException("can't parse a json object of type " + theClass.getName() + " from ["
					+ shorter(description) + "]", e);
		}
	}
	private static String shorter(String description) {
		int maxSize = 1000;
		if (description == null || description.length() < maxSize) {
			return description;
		}
		return description.substring(0, maxSize - 3) + "...";
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(T data) {
		return fromJson(toJson(data), (Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T object, String excludeFields) {
		// To exclude fields see http://wiki.fasterxml.com/JacksonJsonViews for performance improvements
		String exported = JsonUtil.toJson(object);
		Object obj = JsonUtil.fromJson(exported, object.getClass());
		return (T) obj;
	}
}