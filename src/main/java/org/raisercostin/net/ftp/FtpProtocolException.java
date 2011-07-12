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

/**
 * @author: Costin Emilian GRIGORE
 */
public class FtpProtocolException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737567664320626038L;

	public FtpProtocolException() {
		super();
	}

	public FtpProtocolException(final String message) {
		super(message);
	}

	public FtpProtocolException(final String message, final Throwable cause) {
		this(message);
		initCause(cause);
	}

	public FtpProtocolException(final Throwable cause) {
		this();
		initCause(cause);
	}
}
