package org.raisercostin.utils;

public class EnvironmentVariables {
	public static void main(String[] args) {
		System.out.println(System.getProperty("gigi"));
		System.out.println(System.getenv("gigi"));
	}
}
