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

/**
 * @author: Costin Emilian GRIGORE
 */
public class FtpProtocolException extends IOException
{
    
    public FtpProtocolException()
    {
        super();
    }
    public FtpProtocolException(String message)
    {
        super(message);
    }
    public FtpProtocolException(String message, Throwable cause) {
        this(message);
        initCause(cause);
    }
    public FtpProtocolException(Throwable cause) {
        this();
        initCause(cause);
    }
}
