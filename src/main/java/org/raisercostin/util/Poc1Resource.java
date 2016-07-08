package org.raisercostin.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.springframework.core.io.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Poc1Resource {
	private final String path;

	@SuppressWarnings("unused")
	private Poc1Resource() {
		this.path = null;
	}

	public Poc1Resource(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public Resource getSpringResource() {
		if (path.startsWith("classpath:")) {
			return new ClassPathResource(path.substring("classpath:".length()));
		} else {
			return new FileSystemResource(path);
		}
	}

}
