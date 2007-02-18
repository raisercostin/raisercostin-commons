/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * -----------------------------------------------------------------------------
 * The following example implements a class for encrypting and decrypting
 * strings using several Cipher algorithms. The class is created with a key and
 * can be used repeatedly to encrypt and decrypt strings using that key. Some of
 * the more popular algorithms are: Blowfish DES DESede PBEWithMD5AndDES
 * PBEWithMD5AndTripleDES TripleDES
 * -----------------------------------------------------------------------------
 */

public class EncryptedInputStream extends FilterInputStream {

	/**
	 * @param in
	 * @param i
	 * @param passPhrase
	 */
	public EncryptedInputStream(InputStream in, char[] password) {
		super(in);
		setPassword(password);
		finished = false;
	}

	private Cipher dcipher;

	/**
	 * Constructor used to create this object. Responsible for setting and
	 * initializing this object's encrypter and decrypter Chipher instances
	 * given a Pass Phrase and algorithm.
	 * 
	 * @param passPhrase
	 *            Pass Phrase used to initialize both the encrypter and
	 *            decrypter instances.
	 */
	void setPassword(char[] password) {

		// 8-bytes Salt
		byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
				(byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03 };

		// Iteration count
		int iterationCount = 19;
		try {
			KeySpec keySpec = new PBEKeySpec(password, salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
					.generateSecret(keySpec);
			dcipher = Cipher.getInstance(key.getAlgorithm());
			// Prepare the parameters to the cipthers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iterationCount);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

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
	public int available() throws IOException {
		if (finished) {
			return 0;
		}
		return 1;
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new UnsupportedOperationException();
		// super.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = off; i < off + len; i++) {
			int value = read();
			if (value == -1) {
				// HACK
				return i - off;
			}
			b[i] = (byte) value;
		}
		return len;
	}

	@Override
	public int read() throws IOException {
		int result = readInternaly();
		// System.err.println("read "+(count++)+":"+result+" "+((char)result));
		return result;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private int readInternaly() throws IOException {
		if ((finished) && (isResultBufferEmpty())) {
			return -1;
		}
		try {
			while (isResultBufferEmpty()) {
				int result = super.read();
				if (result == -1) {
					if (finished) {
						return -1;
					}
					finished = true;
					writeResultBuffer(dcipher.doFinal());
				} else {
					temp[0] = (byte) result;
					writeResultBuffer(dcipher.update(temp));
				}
			}
			return readResultBuffer();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return -1;
	}

	static int count = 0;

	private int readResultBuffer() {
		return last[offset++] & 0xff;
	}

	private void writeResultBuffer(byte[] bs) {
		last = bs;
		offset = 0;
	}

	private boolean isResultBufferEmpty() {
		return (last == null) || (offset == last.length);
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new UnsupportedOperationException();
		// super.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		throw new UnsupportedOperationException();
		// return super.skip(n);
	}

	private byte[] last;

	private int offset;

	private boolean finished;

	private byte temp[] = new byte[1];
}