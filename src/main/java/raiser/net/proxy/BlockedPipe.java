package raiser.net.proxy;
import java.io.*;

public class BlockedPipe extends java.lang.Thread {
	BufferedReader reader;
	PrintWriter writer;
	
	/** Creates new Pipe */
    public BlockedPipe(OutputStream os, InputStream is) {
		reader=new BufferedReader(new InputStreamReader(is));
		writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)),true);
    }

	public void run() {
		try{
			String line;
			while((line=reader.readLine())!=null){
				System.out.println(currentThread()+":"+line);
				writer.println(line);
			}
			writer.println("\n\n\n");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
