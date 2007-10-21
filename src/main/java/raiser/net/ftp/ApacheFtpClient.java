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
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author: Costin Emilian GRIGORE
 */
public class ApacheFtpClient extends BaseFtpClient implements FtpClient {
	private static final int DEFAULT_FTP_PORT = 21;

	public FTPClient client;

	public ApacheFtpClient() {
	}

	public void connect(final String host, final int port)
			throws FtpProtocolException {
		client = new FTPClient();
		try {
			client.connect(host, port);
			// After connection attempt, you should check the reply code to
			// verify
			// success.
			final int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				client = null;
				throw new FtpProtocolException("FTP server refused connection.");
			}
		} catch (final IOException e) {
			if (client.isConnected()) {
				try {
					client.disconnect();
				} catch (final IOException e2) {
				}
			}
			client = null;
			throw new FtpProtocolException(e);
		}
	}

	public void connect(final String host) throws FtpProtocolException {
		connect(host, DEFAULT_FTP_PORT);
	}

	public void login(final String userName, final String password)
			throws FtpProtocolException {
		try {
			client.login(userName, password);
		} catch (final SocketException e) {
			throw new FtpProtocolException(e);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void binary() throws FtpProtocolException {
		try {
			client.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (final SocketException e) {
			throw new FtpProtocolException(e);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void passive() throws FtpProtocolException {
		try {
			client.mode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public OutputStream put(final String fileName) throws FtpProtocolException {
		try {
			return client.storeFileStream(fileName);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public InputStream get(final String fileName) throws FtpProtocolException {
		try {
			return client.retrieveFileStream(fileName);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void disconnect() throws FtpProtocolException {
		try {
			client.disconnect();
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void quit() throws FtpProtocolException {
		try {
			client.quit();
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}
}
