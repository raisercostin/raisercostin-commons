package org.raisercostin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.util.DigestUtils;

public class CheckSumUtils {

	public static String computeMD5Checksum(byte[] content) {
		if (content == null) {
			throw new RuntimeException("Null doesn't have an MD5 checksum :P");
		}
		MessageDigest fragmentDigest;
		try {
			fragmentDigest = MessageDigest.getInstance("MD5");
			fragmentDigest.update(content);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return DigestUtils.md5DigestAsHex(fragmentDigest.digest());
	}

}
