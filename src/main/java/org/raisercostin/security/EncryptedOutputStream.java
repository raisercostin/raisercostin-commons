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
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author: Costin Emilian GRIGORE
 */
public class EncryptedOutputStream extends FilterOutputStream {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EncryptedOutputStream.class);

  /**
   * @param stream
   * @param i
   * @param password
   */
  public EncryptedOutputStream(String out, final OutputStream stream,
      final char[] password)
  {
    super(stream);
    setPassword(password);
    try {
      fo = new FileOutputStream(out);
    } catch (final FileNotFoundException e) {
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
  private void setPassword(final char[] passPhrase) {

    // 8-bytes Salt
    final byte[] salt = {
        (byte) 0xA9,
        (byte) 0x9B,
        (byte) 0xC8,
        (byte) 0x32,
        (byte) 0x56,
        (byte) 0x34,
        (byte) 0xE3,
        (byte) 0x03 };

    // Iteration count
    final int iterationCount = 19;

    try {
      final KeySpec keySpec = new PBEKeySpec(passPhrase, salt,
        iterationCount);
      final SecretKey key = SecretKeyFactory.getInstance(
        "PBEWithMD5AndDES").generateSecret(keySpec);

      ecipher = Cipher.getInstance(key.getAlgorithm());

      // Prepare the parameters to the cipthers
      final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
        iterationCount);

      ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

    } catch (final InvalidAlgorithmParameterException e) {
      log.info("EXCEPTION: InvalidAlgorithmParameterException", e);
    } catch (final InvalidKeySpecException e) {
      log.info("EXCEPTION: InvalidKeySpecException", e);
    } catch (final NoSuchPaddingException e) {
      log.info("EXCEPTION: NoSuchPaddingException", e);
    } catch (final NoSuchAlgorithmException e) {
      log.info("EXCEPTION: NoSuchAlgorithmException", e);
    } catch (final InvalidKeyException e) {
      log.info("EXCEPTION: InvalidKeyException", e);
    }
  }

  @Override
  public void flush() throws IOException {
    out.flush();
    fo.flush();
  }

  /*
   * (non-Javadoc)
   * @see java.io.FilterOutputStream#close()
   */
  @Override
  public void close() throws IOException {
    try {
      final byte[] data = ecipher.doFinal();
      if (data != null) {
        out.write(data);
      }
    } catch (final IllegalStateException e) {
      e.printStackTrace();
    } catch (final IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (final BadPaddingException e) {
      e.printStackTrace();
    }
    try {
      flush();
    } catch (final IOException ex) {
      log.info("ignored", ex);
    }
    out.close();
    fo.close();
  }

  @Override
  public void write(final byte[] b, final int off, final int len)
      throws IOException {
    fo.write(b, off, len);
    final byte[] data = ecipher.update(b, off, len);
    if (data != null) {
      out.write(data);
    }
  }

  @Override
  public void write(final byte[] b) throws IOException {
    fo.write(b);
    final byte[] data = ecipher.update(b);
    if (data != null) {
      out.write(data);
    }
  }

  @Override
  public void write(final int b) throws IOException {
    fo.write(b);
    temp[0] = (byte) (b & 0xff);
    final byte[] data = ecipher.update(temp);
    if (data != null) {
      out.write(data);
    }
  }

  byte[] temp = new byte[1];

  FileOutputStream fo;
}