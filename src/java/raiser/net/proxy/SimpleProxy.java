package raiser.net.proxy;
import java.io.*;
import java.net.*;
/**
 *
 * @author  cgr
 * @version 
 */
public class SimpleProxy{
	public static final String SHUTDOWN_SERVER="raiser.shutdown.raiser";
	ServerSocket socketFactory;
	String host;
	int port;
	
	
	/** Creates new WebServer */
    public SimpleProxy() throws IOException{
		this(7777,"home.ro",80);
	}
    public SimpleProxy(int proxyPort, String host,int port) throws IOException{
		this.socketFactory=new ServerSocket(proxyPort);
		this.host=host;
		this.port=port;
    }
	public String getContent(URL url) throws IOException{
		StringBuffer buf=new StringBuffer();
		InputStream is = url.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = in.readLine()) != null)
			buf.append(line + "\n");
		return buf.toString();
	}
/*
	public Properties getParameters(BufferedReader br){
		Properties res=new Properties();
		try{
			String line;
			StringBuffer sb=new StringBuffer();
			int pos;
			for(boolean first=true;!(line=br.readLine()).equals("");first=false){
				if(first){
					res.setProperty("raiser.cmd",line);
					sb.append(line+"\n");
				}else{
					pos=line.indexOf(':');
					if(pos>0)
						res.setProperty(line.substring(0,pos).trim(),line.substring(pos+1).trim());
					else if(pos<=0)
						res.setProperty("raiser.unknown",line);
					sb.append(line+"\n");
				}
				System.out.println("("+line+")");
			}
			res.setProperty("raiser.content",sb.toString());
			res.setProperty("raiser.server","ctache");
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
		return res;
	}
*/
	public void run(){
		Socket clientSocket;
		Socket serverSocket;
		while(true){
			try{
				//accept o conexiune din exterior
				clientSocket=socketFactory.accept();
				//deschid o conexiune cu cine a fost fixat server
				serverSocket=new Socket(host,port);
				//formez doua pipe-uri
				
				Pipe p1=new Pipe("1",serverSocket.getOutputStream(),clientSocket.getInputStream());
				Pipe p2=new Pipe("2",clientSocket.getOutputStream(),serverSocket.getInputStream());
				p1.start();
				p2.start();

				try{
					p1.join();
					p2.join();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				serverSocket.shutdownInput();
				serverSocket.shutdownOutput();
/*				BufferedReader reader;
				PrintWriter writer;
				//preiau canalele de comunicatie
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				//afisez informatiile din cerere
				//par.list(System.out);
				writer.println(getContent(new URL(host)));
*/
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String argv[]){
		try{
			//initializez proxiul sa asculte pe portul 7777 si sa redirecteze traficul cu raiser.home.ro
			SimpleProxy proxy=new SimpleProxy(7777,"home.ro",80);
			proxy.run();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
