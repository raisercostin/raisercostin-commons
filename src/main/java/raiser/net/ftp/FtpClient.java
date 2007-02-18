/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.net.ftp;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * FtpClient interface. The raw ftp commands are:
 * http://www.nsftools.com/tips/RawFTP.htm
 * 
 * @author: Costin Emilian GRIGORE
 */
public interface FtpClient {
	void connect(String host, int port) throws FtpProtocolException;

	void connect(String host) throws FtpProtocolException;

	void login(String userName, String password) throws FtpProtocolException;

	void binary() throws FtpProtocolException;

	void passive() throws FtpProtocolException;

	OutputStream put(String fileName) throws FtpProtocolException;

	void putBytes(String fileName, byte[] data) throws FtpProtocolException;

	InputStream get(String fileName) throws FtpProtocolException;

	byte[] getBytes(String fileName) throws FtpProtocolException;

	void disconnect() throws FtpProtocolException;

	// void quit() throws FtpProtocolException;
	boolean exists(String fileName) throws FtpProtocolException;
}
