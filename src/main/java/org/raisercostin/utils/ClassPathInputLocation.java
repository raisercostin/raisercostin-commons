package org.raisercostin.utils;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class ClassPathInputLocation {
	private String resource;

	public ClassPathInputLocation(String resource) {
		this.resource = resource;
	}

	public String readContent() {
		try (BufferedInputStream b = new BufferedInputStream(
				ClassPathInputLocation.class.getClassLoader().getResourceAsStream(resource))) {
			return IOUtils.toString(b);
		} catch (IOException e) {
			throw new RuntimeException("Can't read resource [" + resource + "]", e);
		}
	}
}
