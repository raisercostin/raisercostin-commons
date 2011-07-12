/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;

/**
 * @author: Costin Emilian GRIGORE
 */
public class EnterpriseDistributedTechnologiesFtpClient extends BaseFtpClient
		implements FtpClient {
	public FTPClient client;

	public EnterpriseDistributedTechnologiesFtpClient() {
	}

	public void connect(final String host, final int port)
			throws FtpProtocolException {
		try {
			client = new FTPClient(host, port);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		} catch (final FTPException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void connect(final String host) throws FtpProtocolException {
		try {
			client = new FTPClient(host);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		} catch (final FTPException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void login(final String userName, final String password)
			throws FtpProtocolException {
		try {
			client.login(userName, password);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		} catch (final FTPException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void binary() throws FtpProtocolException {
		try {
			client.setType(FTPTransferType.BINARY);
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		} catch (final FTPException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void passive() {
		client.setConnectMode(FTPConnectMode.PASV);
	}

	public OutputStream put(final String fileName) throws FtpProtocolException {
		try {
			final PipedOutputStream pos = new PipedOutputStream();
			final PipedInputStream pis = new PipedInputStream();
			pos.connect(pis);
			new Thread() {
				@Override
				public void run() {
					try {
						try {
							client.put(pis, fileName);
						} catch (final IOException e) {
							throw new FtpProtocolException(e);
						} catch (final FTPException e) {
							throw new FtpProtocolException(e);
						}
					} catch (final FtpProtocolException e) {
						e.printStackTrace();
					}
				}
			}.start();
			return pos;
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public InputStream get(final String fileName) throws FtpProtocolException {
		try {
			final PipedOutputStream pos = new PipedOutputStream();
			final PipedInputStream pis = new PipedInputStream();
			pos.connect(pis);
			new Thread() {
				@Override
				public void run() {
					try {
						try {
							client.get(pos, fileName);
						} catch (final IOException e) {
							throw new FtpProtocolException(e);
						} catch (final FTPException e) {
							throw new FtpProtocolException(e);
						}
					} catch (final FtpProtocolException e) {
						e.printStackTrace();
					}
					try {
						pos.close();
					} catch (final IOException e1) {
						e1.printStackTrace();
					}
					try {
						pis.close();
					} catch (final IOException e2) {
						e2.printStackTrace();
					}
				}
			}.start();
			return pis;
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		}
	}

	public void quit() throws FtpProtocolException {
		/*
		 */
	}

	public void disconnect() throws FtpProtocolException {
		try {
			client.quit();
		} catch (final IOException e) {
			throw new FtpProtocolException(e);
		} catch (final FTPException e) {
			throw new FtpProtocolException(e);
		}
	}
}
