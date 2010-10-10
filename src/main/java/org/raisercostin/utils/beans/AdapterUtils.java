package org.raisercostin.utils.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * A utility class to help with:
 * <ul>
 * <li>getting a message digest checksum for a string or input stream</li>
 * <li>getting a debug string for a map</li>
 * <li>converting a document object into an XML string</li>
 * <li>masking out a string to hide sensitive information</li>
 * <li>converting a byte array into a hex string</li>
 * <li>a generic visiting mechanism for iterable objects</li>
 * </ul>
 */
public class AdapterUtils {

    /**
     * An enumeration of supported message digest algorithms that can be used by the AdapterUtils.getChecksum() methods.
     * @see AdapterUtils#getChecksum(String,MessageDigestAlgorithm)
     * @see AdapterUtils#getChecksum(byte[],MessageDigestAlgorithm)
     * @see AdapterUtils#getChecksum(InputStream,MessageDigestAlgorithm)
     */
    public enum MessageDigestAlgorithm {
        MD2, MD5, SHA, // same as SHA_1
        SHA_1, // same as SHA
        SHA_256, SHA_384, SHA_512;
        @Override
        public String toString() {
            return super.toString().replace('_', '-');
        }
    }

    private static final char MASKING_CHAR = '#';

    private AdapterUtils() {
        // static class
    }

    /**
     * Get a trimmed, upper case string from the original string. This method is null safe, returning null when the
     * original string is null.
     * @param s the original string to trim and upper case
     * @return a trimmed, upper case string or null if the original is null
     */
    public static String trimUpperCase(String s) {
        return s != null ? s.trim().toUpperCase() : s;
    }

    /**
     * Mask out a string value, returning a string of '#' characters that is the same length as the original string.
     * This method is null safe, returning null if the original string is null.
     * @param original the string to mask out
     * @return a string of '#' characters that is the same length as the original or null if the original string is null
     * @see #maskOut(String,char)
     */
    public static String maskOut(final String original) {
        return maskOut(original, MASKING_CHAR);
    }

    /**
     * Mask out a string value, returning a string of masking characters that is the same length as the original string.
     * This method is null safe, returning null if the original string is null.
     * @param original the string to mask out
     * @param maskingChar the character used for masking
     * @return a string of masking characters that is the same length as the original or null if the original string is
     *         null
     */
    public static String maskOut(final String original, final char maskingChar) {
        if (original == null) {
            return null;
        }

        if (original.length() == 0) {
            return "";
        }

        final char[] chars = new char[original.length()];
        Arrays.fill(chars, maskingChar);
        return new String(chars);
    }

    /**
     * Given a map, return a string containing its keys, values, and types.
     * @param map a map
     * @return a string containing the map keys, values, and types
     */
    public static String getDebugString(final Map<?, ?> map) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(out);
        try {
            MapUtils.debugPrint(printStream, null, map);
        } finally {
            printStream.close();
        }
        return out.toString();
    }

    /**
     * Get a checksum for a string using the specified algorithm.
     * @param string the string to checksum
     * @param algorithm the algorithm to use
     * @return a checksum string for the provided string or null if string is null
     * @throws RuntimeException when a particular cryptographic algorithm is requested but is not available in the
     *             environment
     */
    public static String getChecksum(String string, MessageDigestAlgorithm algorithm) {
        return string == null ? null : getChecksum(string.getBytes(), algorithm);
    }

    /**
     * Get a checksum for an array of bytes using the specified algorithm.
     * @param bytes the bytes to checksum
     * @param algorithm the algorithm to use
     * @return a checksum string for the provided String or null if bytes is null
     * @throws RuntimeException when a particular cryptographic algorithm is requested but is not available in the
     *             environment
     */
    public static String getChecksum(byte[] bytes, MessageDigestAlgorithm algorithm) {
        if (bytes == null) {
            return null;
        }

        try {
            return getChecksum(new ByteArrayInputStream(bytes), algorithm);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read bytes", e);
        }
    }

    /**
     * Get a checkdum for an input stream using the specified algorithm.
     * @param inputStream the input stream to checksum
     * @param algorithm the algorithm to use
     * @return a checksum string for the provided input stream or null if inputStream is null
     * @throws RuntimeException when a particular cryptographic algorithm is requested but is not available in the
     *             environment
     * @throws IOException if the input stream cannot be read
     */
    public static String getChecksum(InputStream inputStream, MessageDigestAlgorithm algorithm) throws IOException {
        if (inputStream == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.toString());
            md.reset();
            int length;
            byte[] buffer = new byte[1024 * 8];
            while ((length = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return toHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert an array of bytes into a representative hexadecimal string.
     * @param bytes the bytes
     * @return a hexadecimal string or the empty string if bytes is null
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder checksum = new StringBuilder();
        if (bytes != null) {
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    checksum.append('0');
                }
                checksum.append(hex);
            }
        }
        return checksum.toString();
    }

    /**
     * Get a DOM4J document as an XML string using compact format.
     * @param document a DOM4j document
     * @return an XML string
     */
    public static String getDocumentAsString(Document document) {
        return getDocumentAsString(document, false);
    }

    /**
     * Get a DOM4J document as an XML string using pretty format.
     * @param document a DOM4j document
     * @return an XML string
     */
    public static String getDocumentAsStringPretty(Document document) {
        return getDocumentAsString(document, true);
    }

    /**
     * Get a DOM4J document as an XML string.
     * @param document a DOM4j document
     * @param pretty true for pretty format, false for compact format
     * @return an XML string
     */
    public static String getDocumentAsString(Document document, boolean pretty) {
        StringWriter sw = new StringWriter();
        XMLWriter w = new XMLWriter(sw, pretty ? OutputFormat.createPrettyPrint() : OutputFormat.createCompactFormat());
        try {
            w.write(document);
            w.flush();
            return sw.toString();
        } catch (IOException e) {
            // shouldn't happen since we are writing to StringWriter
            throw new RuntimeException(e);
        } finally {
            try {
                w.close();
            } catch (IOException e) {
                // don't care
            }
        }
    }
}
