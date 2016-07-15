package org.raisercostin.utils;

public class Locations {
	public static ClassPathInputLocation classpath(String resource) {
		return new ClassPathInputLocation(resource);
	}
}
