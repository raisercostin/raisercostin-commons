/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.net.ftp;

/**
 * @author: Costin Emilian GRIGORE
 */
public class FtpClientFactory {
	public static FtpClient createFtpClient() {
		return createApacheFtpClient();
	}

	public static FtpClient createApacheFtpClient() {
		return new ApacheFtpClient();
	}

	public static FtpClient createEnterpriseDistributedTechnologiesFtpClient() {
		return new EnterpriseDistributedTechnologiesFtpClient();
	}
}
