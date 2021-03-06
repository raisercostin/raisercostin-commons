/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.security;

import java.io.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import junit.framework.TestCase;

/**
 * @author: Costin Emilian GRIGORE
 */
public class EncryptedStreamsTest extends TestCase {

	/**
	 * Constructor for EncryptedStreamsTest.
	 * 
	 * @param arg0
	 */
	public EncryptedStreamsTest(final String arg0) {
		super(arg0);
	}

	/**
	 * The following method is used for testing the String Encrypter class. This
	 * method is responsible for encrypting and decrypting a sample String using
	 * using a Pass Phrase.
	 */
	public static void testUsingPassPhrase() throws IllegalStateException,
			IllegalBlockSizeException, BadPaddingException, IOException {
		final String secretString = "Attack at dawn!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
		final String passPhrase = "parola";
		// Create encrypter/decrypter class
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		final OutputStream out = new EncryptedOutputStream("target/xml.out",bout, passPhrase
				.toCharArray());

		out.write(secretString.getBytes());
		out.close();

		// Encrypt the string
		final byte[] desEncrypted = bout.toByteArray();

		final ByteArrayInputStream in = new ByteArrayInputStream(desEncrypted);
		final InputStream desDecrypter = new EncryptedInputStream(in,
				passPhrase.toCharArray());

		// Decrypt the string
		final StringBuffer sb = new StringBuffer();
		int value;
		while ((value = desDecrypter.read()) != -1) {
			sb.append((char) value);
		}
		final String desDecrypted = sb.toString();

		// Print out values
		System.out.println("PBEWithMD5AndDES Encryption algorithm");
		System.out.println("    Original String  : <" + secretString + ">");
		System.out.println("    Encrypted String : <"
				+ new String(desEncrypted) + ">");
		System.out.println("    Decrypted String : <" + desDecrypted + ">");
		System.out.println();
		assertEquals(secretString, desDecrypted);
	}

	public void test2() throws IOException {
		mySetUp(createData(1013));
		assertEquals(expected.length, resultedLength);
	}

	public void test6() throws IOException {
		mySetUp(createData(1));
		assertEquals(expected.length, resultedLength);
	}

	public void test3() throws IOException {
		mySetUp(createData(1013));
		final int max = Math.max(expected.length, resultedLength);
		for (int i = 0; i < max; i++) {
			assertEquals("Char " + i + ": ", expected[i], resulted[i]);
		}
	}

	String data2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
			+ "<java version=\"1.4.2_02\" class=\"java.beans.XMLDecoder\"> \n"
			+ " <object class=\"java.util.ArrayList\"/> \n" + "</java> \n";

	public void test4() throws IOException {
		mySetUp(data2.getBytes());
		assertEquals(expected.length, resultedLength);
	}

	public void test5() throws IOException {
		/*
		 * byte[] buffer = new byte[10000]; InputStream in = new
		 * EncryptedInputStream(new FileInputStream(
		 * "d:\\workspace\\selfim\\xml.out"), "costin"); resultedLength =
		 * in.read(buffer); in.close();
		 * 
		 * System.out.println(new String(buffer,0,resultedLength));
		 * 
		 */
		mySetUp(data2.getBytes());
		final int max = Math.max(expected.length, resultedLength);
		for (int i = 0; i < max; i++) {
			assertEquals("Char " + i + ": ", expected[i], resulted[i]);
		}
	}

	byte[] expected;

	byte[] resulted;

	int resultedLength;

	private void mySetUp(final byte[] expected) throws IOException {
		this.expected = expected;
		final String password = "a mers";
		final OutputStream out = new EncryptedOutputStream("target/out.xml",
				new FileOutputStream("target/test.encrypted"), password.toCharArray());
		out.write(expected);
		out.close();

		resulted = new byte[2000];
		final InputStream in = new EncryptedInputStream(new FileInputStream(
				"target/test.encrypted"), password.toCharArray());
		resultedLength = in.read(resulted);
		in.close();
	}

	private byte[] createData(final int size) {
		final byte[] result = new byte[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) ((i % 256) & 0xf);
		}
		return result;
	}
}