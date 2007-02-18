/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.net.ftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	public byte[] getBytes(String fileName) throws FtpProtocolException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = get(fileName);
		if (in == null) {
			return null;
		}
		try {
			Util.copyStream(in, out);
			try {
				in.close();
			} catch (IOException e1) {
				throw new FtpProtocolException(e1);
			}
		} catch (CopyStreamException e) {
			try {
				in.close();
			} catch (IOException e1) {
				throw new FtpProtocolException(e1);
			}
			throw new FtpProtocolException(e);
		}
		return out.toByteArray();
	}

	public void putBytes(String fileName, byte[] data)
			throws FtpProtocolException {
		OutputStream out = put(fileName);
		try {
			out.write(data);
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
		try {
			out.close();
		} catch (IOException e1) {
			throw new FtpProtocolException(e1);
		}
	}

	// PROFILE
	public boolean exists(String fileName) throws FtpProtocolException {
		try {
			return getBytes(fileName) != null;
		} catch (IOException e) {
			return false;
		}
	}
}
