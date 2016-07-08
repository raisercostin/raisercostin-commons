package org.raisercostin.util;

import java.security.GeneralSecurityException;

public class HexUtils {

	public static String bytesToHex(byte[] data) throws GeneralSecurityException {
		if (data == null) {
			throw new GeneralSecurityException("Input byte table is null");
		} else {
			int len = data.length;
			String str = "";
			for (int i = 0; i < len; i++) {
				if ((data[i] & 0xFF) < 16) {
					str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
				} else {
					str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
				}
			}
			return str.toUpperCase();
		}
	}

	public static byte[] hexToBytes(String str) throws GeneralSecurityException {
		if (str == null) {
			throw new GeneralSecurityException("Input string is null");
		} else if (str.length() < 2) {
			throw new GeneralSecurityException("Input string is too short. Must be over 2 characters long");
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}
	}
}
