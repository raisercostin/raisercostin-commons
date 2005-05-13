/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.net.ftp;

import java.io.*;

import com.enterprisedt.net.ftp.*;
import com.enterprisedt.net.ftp.FTPClient;

/**
 * @author: Costin Emilian GRIGORE
 */
public class EnterpriseDistributedTechnologiesFtpClient extends BaseFtpClient implements FtpClient
{
    public FTPClient client;
    public EnterpriseDistributedTechnologiesFtpClient()
    {
    }

    public void connect(String host, int port) throws FtpProtocolException
    {
        try
        {
            client = new FTPClient(host, port);
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
        catch (FTPException e)
        {
            throw new FtpProtocolException(e);
        }
    }
    public void connect(String host) throws FtpProtocolException
    {
        try
        {
            client = new FTPClient(host);
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
        catch (FTPException e)
        {
            throw new FtpProtocolException(e);
        }
    }

    public void login(String userName, String password)
        throws FtpProtocolException
    {
        try
        {
            client.login(userName, password);
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
        catch (FTPException e)
        {
            throw new FtpProtocolException(e);
        }
    }

    public void binary() throws FtpProtocolException
    {
        try
        {
            client.setType(FTPTransferType.BINARY);
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
        catch (FTPException e)
        {
            throw new FtpProtocolException(e);
        }
    }
    public void passive()
    {
        client.setConnectMode(FTPConnectMode.PASV);
    }

    public OutputStream put(final String fileName) throws FtpProtocolException
    {
        try
        {
            final PipedOutputStream pos = new PipedOutputStream();
            final PipedInputStream pis = new PipedInputStream();
            pos.connect(pis);
            new Thread()
            {
                public void run()
                {
                    try{
                        try
                        {
                            client.put(pis, fileName);
                        }
                        catch (IOException e)
                        {
                            throw new FtpProtocolException(e);
                        }
                        catch (FTPException e)
                        {
                            throw new FtpProtocolException(e);
                        }
                    }catch(FtpProtocolException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
            return pos;
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
    }

    public InputStream get(final String fileName) throws FtpProtocolException
    {
        try
        {
            final PipedOutputStream pos = new PipedOutputStream();
            final PipedInputStream pis = new PipedInputStream();
            pos.connect(pis);
            new Thread()
            {
                public void run()
                {
                    try{
                        try
                        {
                            client.get(pos, fileName);
                        }
                        catch (IOException e)
                        {
                            throw new FtpProtocolException(e);
                        }
                        catch (FTPException e)
                        {
                            throw new FtpProtocolException(e);
                        }
                    }catch(FtpProtocolException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        pos.close();
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                    try
                    {
                        pis.close();
                    }
                    catch (IOException e2)
                    {
                        e2.printStackTrace();
                    }
                }
            }.start();
            return pis;
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
    }

    public void quit() throws FtpProtocolException
    {
        /*
        */
    }

    public void disconnect() throws FtpProtocolException
    {
        try
        {
            client.quit();
        }
        catch (IOException e)
        {
            throw new FtpProtocolException(e);
        }
        catch (FTPException e)
        {
            throw new FtpProtocolException(e);
        }
    }
}
