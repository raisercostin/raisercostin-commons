package org.raisercostin.net.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 * 
 * @author cgr
 * @version
 */
public class SimpleProxy {
	public static final String SHUTDOWN_SERVER = "org.raisercostin.shutdown.raiser";

	ServerSocket socketFactory;

	String host;

	int port;

	/** Creates new WebServer */
	public SimpleProxy() throws IOException {
		this(7777, "home.ro", 80);
	}

	public SimpleProxy(final int proxyPort, final String host, final int port)
			throws IOException {
		socketFactory = new ServerSocket(proxyPort);
		this.host = host;
		this.port = port;
	}

	public String getContent(final URL url) throws IOException {
		final StringBuffer buf = new StringBuffer();
		final InputStream is = url.openStream();
		final BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = in.readLine()) != null) {
			buf.append(line + "\n");
		}
		return buf.toString();
	}

	/*
	 * public Properties getParameters(BufferedReader br){ Properties res=new
	 * Properties(); try{ String line; StringBuffer sb=new StringBuffer(); int
	 * pos; for(boolean
	 * first=true;!(line=br.readLine()).equals("");first=false){ if(first){
	 * res.setProperty("org.raisercostin.cmd",line); sb.append(line+"\n"); }else{
	 * pos=line.indexOf(':'); if(pos>0)
	 * res.setProperty(line.substring(0,pos).trim(),line.substring(pos+1).trim());
	 * else if(pos<=0) res.setProperty("org.raisercostin.unknown",line);
	 * sb.append(line+"\n"); } System.out.println("("+line+")"); }
	 * res.setProperty("org.raisercostin.content",sb.toString());
	 * res.setProperty("org.raisercostin.server","ctache"); }catch(IOException e){
	 * e.printStackTrace(); return null; } return res; }
	 */
	public void run() {
		Socket clientSocket;
		Socket serverSocket;
		while (true) {
			try {
				// accept o conexiune din exterior
				clientSocket = socketFactory.accept();
				// deschid o conexiune cu cine a fost fixat server
				serverSocket = new Socket(host, port);
				// formez doua pipe-uri

				final Pipe p1 = new Pipe("1", serverSocket.getOutputStream(),
						clientSocket.getInputStream());
				final Pipe p2 = new Pipe("2", clientSocket.getOutputStream(),
						serverSocket.getInputStream());
				p1.start();
				p2.start();

				try {
					p1.join();
					p2.join();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				serverSocket.shutdownInput();
				serverSocket.shutdownOutput();
				/*
				 * BufferedReader reader; PrintWriter writer; //preiau canalele
				 * de comunicatie reader=new BufferedReader(new
				 * InputStreamReader(socket.getInputStream())); writer=new
				 * PrintWriter(new BufferedWriter(new
				 * OutputStreamWriter(socket.getOutputStream())),true); //afisez
				 * informatiile din cerere //par.list(System.out);
				 * writer.println(getContent(new URL(host)));
				 */
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(final String argv[]) {
		try {
			// initializez proxiul sa asculte pe portul 7777 si sa redirecteze
			// traficul cu org.raisercostin.home.ro
			final SimpleProxy proxy = new SimpleProxy(7777, "home.ro", 80);
			proxy.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
