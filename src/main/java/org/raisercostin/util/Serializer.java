package org.raisercostin.util;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Serializer {
	public static <T> String encode(T theObject) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(theObject);
			oos.close();
			char[] encoded = Base64Coder.encode(baos.toByteArray());
			return new String(encoded);
		} catch (IOException e) {
			throw new RuntimeException("", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T decode(String request) {
		byte[] data = Base64Coder.decode(request);
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object theObject = ois.readObject();
			ois.close();
			return (T) theObject;
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

	public static <T> String jsonEncode(T theObject) {
		Gson gson = new Gson();
		return gson.toJson(theObject);
	}

	public static <T> T jsonDecode(String request) {
		Gson gson = new Gson();
		// http://stackoverflow.com/questions/2496494/library-to-encode-decode-from-json-to-java-util-map
		// return gson.fromJson(request, new TypeToken<T>() {}.getType());
		return gson.fromJson(request, new TypeToken<T>() {
			//empty?
		}.getType());
	}

}
