package raiser.net.proxy;
import java.io.*;
import java.net.Socket;
/**
 *
 * @author  cgr
 * @version 
 */
public class TestRequest
{
    final static char CR = 13;
    final static char LF = 10;

    /** Creates new WebServer */
    public String getContent(String req) throws IOException
    {

        Socket client = new Socket("http://raiser.home.ro", 80);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter writer =
            new PrintWriter(
                new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())),
                true);
        System.out.println("Cerere:\n(" + req + ")");
        writer.print(req);

        System.out.println("gata");
        StringBuffer buf = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null)
        {
            buf.append(line + "\n");
            System.out.println("Primesc:(" + line + ")");
        }
        return buf.toString();
    }

    public static void main(String argv[])
    {
        try
        {
            TestRequest th = new TestRequest();
            System.out.println(
                th.getContent(
                    "GET http://raiser.home.ro/ HTTP/1.0" + CR + LF + CR + LF));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("gata");
    }
}
