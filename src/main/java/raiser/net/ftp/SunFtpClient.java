/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: Costin Emilian GRIGORE
 */
public class SunFtpClient extends BaseFtpClient implements FtpClient {
	public sun.net.ftp.FtpClient client;

	public SunFtpClient() {
	}

	public void connect(final String host, final int port)
			throws FtpProtocolException {
		try {
			client = new sun.net.ftp.FtpClient(host, port);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void connect(final String host) throws FtpProtocolException {
		try {
			client = new sun.net.ftp.FtpClient(host);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void login(final String userName, final String password)
			throws FtpProtocolException {
		try {
			client.login(userName, password);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void binary() throws FtpProtocolException {
		try {
			client.binary();
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void passive() throws FtpProtocolException {
		/*
		 * try { client.passive(); } catch (IOException e) { throw new
		 * FtpProtocolException(e); }
		 */
	}

	public OutputStream put(final String fileName) throws FtpProtocolException {
		try {
			return client.put(fileName);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public InputStream get(final String fileName) throws FtpProtocolException {
		try {
			return client.get(fileName);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void quit() throws FtpProtocolException {
		try {
			client.closeServer();
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void disconnect() throws FtpProtocolException {
		quit();
	}
}
