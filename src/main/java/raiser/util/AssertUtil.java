/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

/**
 * @author raiser
 */
public class AssertUtil {
	public static final int ALL_CHUNKS = -1;

	public static void assertEquals(final File file1, final File file2,
			final int from1, final int from2, final int chunkSize)
			throws IOException {
		assertEquals(file1, file2, from1, from2, chunkSize, -1);
	}

	public static void assertEquals(final File file1, final File file2,
			final int from1, final int from2, final int chunkSize,
			final int chunks) throws IOException {
		if (chunks == 0) {
			throw new RuntimeException(
					"Chunks must be greater or equal with 1.");
		}
		final InputStream in1 = new FileInputStream(file1);
		final InputStream in2 = new FileInputStream(file2);
		in1.skip(from1);
		in2.skip(from2);
		final byte[] buffer1 = new byte[chunkSize];
		final byte[] buffer2 = new byte[chunkSize];
		int len1 = 0;
		int len2 = 0;
		int index = 0;
		int chunkIndex = 0;
		while (((len1 = in1.read(buffer1)) != -1)
				&& ((len2 = in2.read(buffer2)) != -1)) {
			final int len = Math.min(len1, len2);
			for (int i = 0; i < len; i++) {
				if (buffer1[i] != buffer2[i]) {
					Assert.assertEquals("At index=" + (index + i), ArraysUtil
							.toString(buffer1, len1, index, "\n", true, true,
									true, true), ArraysUtil.toString(buffer2,
							len2, index, "\n", true, true, true, true));
				}
			}
			Assert.assertEquals("At index=" + index + " len1=" + len1
					+ " len2=" + len2, len1, len2);
			index += len;
			chunkIndex++;
			if ((chunks != ALL_CHUNKS) && (chunkIndex > chunks)) {
				break;
			}
		}
		if (!((chunks != ALL_CHUNKS) && (chunkIndex > chunks)) && (len1 == -1)) {
			len2 = in2.read();
		}
		Assert.assertEquals("At index=" + index + " len1=" + len1 + " len2="
				+ len2, len1, len2);
		in1.close();
		in2.close();
	}

	public static void assertEquals(final File file1, final File file2,
			final int chunkSize) throws IOException {
		final InputStream in1 = new FileInputStream(file1);
		final InputStream in2 = new FileInputStream(file2);
		final byte[] buffer1 = new byte[chunkSize];
		final byte[] buffer2 = new byte[chunkSize];
		int len1 = 0;
		int len2 = 0;
		int index = 0;
		while (((len1 = in1.read(buffer1)) != -1)
				&& ((len2 = in2.read(buffer2)) != -1)) {
			final int len = Math.min(len1, len2);
			for (int i = 0; i < len; i++) {
				if (buffer1[i] != buffer2[i]) {
					Assert.assertEquals("At index=" + (index + i), ArraysUtil
							.toString(buffer1, len1, index, "\n", true, true,
									true, true), ArraysUtil.toString(buffer2,
							len2, index, "\n", true, true, true, true));
				}
			}
			Assert.assertEquals("At index=" + index, len1, len2);
			index += len;
		}
		if (len1 == -1) {
			len2 = in2.read();
		}
		Assert.assertEquals("At index=" + index, len1, len2);
		in1.close();
		in2.close();
	}

	public static void assertEquals(final byte[] expected, final byte[] result) {
		final int len1 = expected.length;
		final int len2 = result.length;
		int index = 0;

		final int len = Math.min(len1, len2);
		for (int i = 0; i < len; i++) {
			if (expected[i] != result[i]) {
				Assert.assertEquals("At index=" + (index + i), ArraysUtil
						.toString(expected, len1, index, "\n", true, true,
								true, true), ArraysUtil.toString(result, len2,
						index, "\n", true, true, true, true));
			}
		}
		Assert.assertEquals("At index=" + index, len1, len2);
		index += len;
	}

	public static void assertEqualsFrom(final byte[] expected,
			final byte[] result) {
		final int len1 = expected.length;
		final int len2 = result.length;
		int index = 0;

		final int len = Math.min(len1, len2);
		for (int i = 0; i < len; i++) {
			if (expected[i] != result[i]) {
				Assert.assertEquals("At index=" + (index + i), ArraysUtil
						.toString(expected, len1, index, "\n", true, true,
								true, true), ArraysUtil.toString(result, len2,
						index, "\n", true, true, true, true));
			}
		}
		Assert.assertEquals("At index=" + index, len1, len2);
		index += len;
	}

}