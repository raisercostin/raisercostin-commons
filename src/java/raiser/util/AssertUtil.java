/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.io.*;

import junit.framework.Assert;

/**
 * @author raiser
 */
public class AssertUtil
{
    public static void assertEquals(File file1, File file2, int from1, int from2, int chunkSize)
            throws IOException
    {
        InputStream in1 = new FileInputStream(file1);
        InputStream in2 = new FileInputStream(file2);
        in1.skip(from1);
        in2.skip(from2);
        byte[] buffer1 = new byte[chunkSize];
        byte[] buffer2 = new byte[chunkSize];
        int len1 = 0;
        int len2 = 0;
        int index = 0;
        while (((len1 = in1.read(buffer1)) != -1)
                && ((len2 = in2.read(buffer2)) != -1))
        {
            int len = Math.min(len1, len2);
            for (int i = 0; i < len; i++)
            {
                if (buffer1[i] != buffer2[i])
                {
                    Assert.assertEquals("At index=" + (index + i), ArraysUtil
                            .toString(buffer1, len1, index, "\n", true, true,
                                    true, true), ArraysUtil.toString(buffer2,
                            len2, index, "\n", true, true, true, true));
                }
            }
            Assert.assertEquals("At index=" + index, len1, len2);
            index += len;
        }
        if (len1 == -1)
        {
            len2 = in2.read();
        }
        Assert.assertEquals("At index=" + index, len1, len2);
        in1.close();
        in2.close();
    }

    public static void assertEquals(File file1, File file2, int chunkSize)
            throws IOException
    {
        InputStream in1 = new FileInputStream(file1);
        InputStream in2 = new FileInputStream(file2);
        byte[] buffer1 = new byte[chunkSize];
        byte[] buffer2 = new byte[chunkSize];
        int len1 = 0;
        int len2 = 0;
        int index = 0;
        while (((len1 = in1.read(buffer1)) != -1)
                && ((len2 = in2.read(buffer2)) != -1))
        {
            int len = Math.min(len1, len2);
            for (int i = 0; i < len; i++)
            {
                if (buffer1[i] != buffer2[i])
                {
                    Assert.assertEquals("At index=" + (index + i), ArraysUtil
                            .toString(buffer1, len1, index, "\n", true, true,
                                    true, true), ArraysUtil.toString(buffer2,
                            len2, index, "\n", true, true, true, true));
                }
            }
            Assert.assertEquals("At index=" + index, len1, len2);
            index += len;
        }
        if (len1 == -1)
        {
            len2 = in2.read();
        }
        Assert.assertEquals("At index=" + index, len1, len2);
        in1.close();
        in2.close();
    }

    public static void assertEquals(byte[] expected, byte[] result)
    {
        int len1 = expected.length;
        int len2 = result.length;
        int index = 0;

        int len = Math.min(len1, len2);
        for (int i = 0; i < len; i++)
        {
            if (expected[i] != result[i])
            {
                Assert.assertEquals("At index=" + (index + i), ArraysUtil
                        .toString(expected, len1, index, "\n", true, true,
                                true, true), ArraysUtil.toString(result, len2,
                        index, "\n", true, true, true, true));
            }
        }
        Assert.assertEquals("At index=" + index, len1, len2);
        index += len;
    }

    public static void assertEqualsFrom(byte[] expected, byte[] result)
    {
        int len1 = expected.length;
        int len2 = result.length;
        int index = 0;

        int len = Math.min(len1, len2);
        for (int i = 0; i < len; i++)
        {
            if (expected[i] != result[i])
            {
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