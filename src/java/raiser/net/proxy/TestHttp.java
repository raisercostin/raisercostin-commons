package raiser.net.proxy;
import java.io.*;
import java.net.URL;
/**
 *
 * @author  cgr
 * @version 
 */
public class TestHttp{
	/** Creates new WebServer */
	public String getContent(URL url) throws IOException{
		StringBuffer buf=new StringBuffer();
		InputStream is = url.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = in.readLine()) != null)
			buf.append(line + "\n");
		return buf.toString();
	}
	
	public static void main(String argv[]){
		try{
			TestHttp th=new TestHttp();
			System.out.println(th.getContent(new URL("http://raiser.home.ro/")));
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("gata");
	}
}
