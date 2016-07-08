/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.net.ftp;

import java.io.*;

import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.io.Util;

/**
 * @author: Costin Emilian GRIGORE
 */
public abstract class BaseFtpClient implements FtpClient {
	/**
	 * @return null - if the fileName don't exists. new byte[0] - if the
	 *         fileName is empty. all data into a byte array if fileName exists
	 *         and is not empty.
	 */
	@Override
	public byte[] getBytes(final String fileName) throws FtpProtocolException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final InputStream in = get(fileName);
		if (in == null) {
			return null;
		}
		try {
			Util.copyStream(in, out);
			try {
				in.close();
			} catch (final IOException e1) {
				throw new FtpProtocolException(e1);
			}
		} catch (final CopyStreamException e) {
			try {
				in.close();
			} catch (final IOException e1) {
				throw new FtpProtocolException(e1);
			}
			throw new FtpProtocolException(e);
		}
		return out.toByteArray();
	}

	@Override
	public void putBytes(final String fileName, final byte[] data)
			throws FtpProtocolException {
		final OutputStream out = put(fileName);
		try {
			out.write(data);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
		try {
			out.close();
		} catch (final IOException e1) {
			throw new FtpProtocolException(e1);
		}
	}

	// PROFILE
	@Override
	public boolean exists(final String fileName) throws FtpProtocolException {
		try {
			return getBytes(fileName) != null;
		} catch (final IOException e) {
			return false;
		}
	}
}
