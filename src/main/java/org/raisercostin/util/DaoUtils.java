package org.raisercostin.util;

import java.util.List;

import org.junit.Assert;

public class DaoUtils {

	public static <T> T mandatoryResult(T result) {
		Assert.assertNotNull(result);
		return result;
	}

	public static <T> T optionalUniqueResult(List<T> result) {
		if (result.size() == 0) {
			return null;
		}
		if (result.size() > 1) {
			throw new RuntimeException("Too many rows found.");
		}
		return result.get(0);
	}
}
