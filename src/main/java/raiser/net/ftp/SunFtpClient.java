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

	public void connect(String host, int port) throws FtpProtocolException {
		try {
			client = new sun.net.ftp.FtpClient(host, port);
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void connect(String host) throws FtpProtocolException {
		try {
			client = new sun.net.ftp.FtpClient(host);
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void login(String userName, String password)
			throws FtpProtocolException {
		try {
			client.login(userName, password);
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void binary() throws FtpProtocolException {
		try {
			client.binary();
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void passive() throws FtpProtocolException {
		/*
		 * try { client.passive(); } catch (IOException e) { throw new
		 * FtpProtocolException(e); }
		 */
	}

	public OutputStream put(String fileName) throws FtpProtocolException {
		try {
			return client.put(fileName);
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public InputStream get(String fileName) throws FtpProtocolException {
		try {
			return client.get(fileName);
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void quit() throws FtpProtocolException {
		try {
			client.closeServer();
		} catch (IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void disconnect() throws FtpProtocolException {
		quit();
	}
}
