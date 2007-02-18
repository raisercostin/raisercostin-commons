/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.security;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author: Costin Emilian GRIGORE
 */
public class EncryptedOutputStream extends FilterOutputStream {
	/**
	 * @param stream
	 * @param i
	 * @param password
	 */
	public EncryptedOutputStream(OutputStream stream, char[] password) {
		super(stream);
		setPassword(password);
		try {
			fo = new FileOutputStream("xml.out");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Cipher ecipher;

	/**
	 * Constructor used to create this object. Responsible for setting and
	 * initializing this object's encrypter and decrypter Chipher instances
	 * given a Pass Phrase and algorithm.
	 * 
	 * @param passPhrase
	 *            Pass Phrase used to initialize both the encrypter and
	 *            decrypter instances.
	 */
	private void setPassword(char[] passPhrase) {

		// 8-bytes Salt
		byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
				(byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03 };

		// Iteration count
		int iterationCount = 19;

		try {
			KeySpec keySpec = new PBEKeySpec(passPhrase, salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
					.generateSecret(keySpec);

			ecipher = Cipher.getInstance(key.getAlgorithm());

			// Prepare the parameters to the cipthers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iterationCount);

			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		} catch (InvalidAlgorithmParameterException e) {
			System.out.println("EXCEPTION: InvalidAlgorithmParameterException");
		} catch (InvalidKeySpecException e) {
			System.out.println("EXCEPTION: InvalidKeySpecException");
		} catch (NoSuchPaddingException e) {
			System.out.println("EXCEPTION: NoSuchPaddingException");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("EXCEPTION: NoSuchAlgorithmException");
		} catch (InvalidKeyException e) {
			System.out.println("EXCEPTION: InvalidKeyException");
		}
	}

	@Override
	public void flush() throws IOException {
		out.flush();
		fo.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterOutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			byte[] data = ecipher.doFinal();
			if (data != null) {
				out.write(data);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		try {
			flush();
		} catch (IOException ignored) {
		}
		out.close();
		fo.close();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		fo.write(b, off, len);
		byte[] data = ecipher.update(b, off, len);
		if (data != null) {
			out.write(data);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		fo.write(b);
		byte[] data = ecipher.update(b);
		if (data != null) {
			out.write(data);
		}
	}

	@Override
	public void write(int b) throws IOException {
		fo.write(b);
		temp[0] = (byte) (b & 0xff);
		byte[] data = ecipher.update(temp);
		if (data != null) {
			out.write(data);
		}
	}

	byte[] temp = new byte[1];

	FileOutputStream fo;
}