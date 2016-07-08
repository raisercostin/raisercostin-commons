/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.util;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.raisercostin.utils.ResourceUtils;
import org.springframework.core.io.Resource;

/**
 * @author org.raisercostin
 */
public class AssertUtil {
	public static final int ALL_CHUNKS = -1;

	public static void assertEquals(String message, final Resource expectedResource, final Resource actualResource) {
		String expectedValue = ResourceUtils.readAsString(expectedResource, "UTF-8", true);
		String actualValue = ResourceUtils.readAsString(actualResource, "UTF-8", true);
		Assert.assertEquals("When comparing expectedResource=" + expectedResource + " with actualResource="
				+ actualResource, expectedValue, actualValue);
	}

	public static void assertEqualsHexa(String message, final Resource expected, final Resource actual,
			final int from1, final int from2, final int chunkSize, final int maxChunks) {
		if (maxChunks == 0) {
			throw new RuntimeException("Chunks must be greater or equal with 1.");
		}
		try {
			final InputStream in1 = expected.getInputStream();
			final InputStream in2 = actual.getInputStream();
			in1.skip(from1);
			in2.skip(from2);
			final byte[] buffer1 = new byte[chunkSize];
			final byte[] buffer2 = new byte[chunkSize];
			int len1 = 0;
			int len2 = 0;
			int index = 0;
			int chunkIndex = 0;
			while (((len1 = in1.read(buffer1)) != -1) && ((len2 = in2.read(buffer2)) != -1)) {
				final int len = Math.min(len1, len2);
				for (int i = 0; i < len; i++) {
					if (buffer1[i] != buffer2[i]) {
						Assert.assertEquals("At index=" + (index + i),
								ArraysUtil.toString(buffer1, len1, index, "\n", true, true, true, true),
								ArraysUtil.toString(buffer2, len2, index, "\n", true, true, true, true));
					}
				}
				Assert.assertEquals("At index=" + index + " len1=" + len1 + " len2=" + len2, len1, len2);
				index += len;
				chunkIndex++;
				if ((maxChunks != ALL_CHUNKS) && (chunkIndex > maxChunks)) {
					break;
				}
			}
			if (!((maxChunks != ALL_CHUNKS) && (chunkIndex > maxChunks)) && (len1 == -1)) {
				len2 = in2.read();
			}
			Assert.assertEquals("At index=" + index + " len1=" + len1 + " len2=" + len2, len1, len2);
			in1.close();
			in2.close();
		} catch (IOException e) {
			throw new RuntimeException(
					"When comparing expectedResource=" + expected + " with actualResource=" + actual, e);
		}

	}

	public static void assertEquals(final byte[] expected, final byte[] result) {
		final int len1 = expected.length;
		final int len2 = result.length;
		int index = 0;

		final int len = Math.min(len1, len2);
		for (int i = 0; i < len; i++) {
			if (expected[i] != result[i]) {
				Assert.assertEquals("At index=" + (index + i),
						ArraysUtil.toString(expected, len1, index, "\n", true, true, true, true),
						ArraysUtil.toString(result, len2, index, "\n", true, true, true, true));
			}
		}
		Assert.assertEquals("At index=" + index, len1, len2);
		index += len;
	}
}