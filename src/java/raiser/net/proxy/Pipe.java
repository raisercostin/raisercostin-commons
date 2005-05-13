package raiser.net.proxy;
import java.io.*;

public class Pipe extends java.lang.Thread {
	InputStream is;
	OutputStream os;
	
    public Pipe(OutputStream os,InputStream is) {
		this.os=os;
		this.is=is;
	}
	/** Creates new Pipe */
    public Pipe(String name,OutputStream os,InputStream is) {
		super(name);
		this.os=os;
		this.is=is;
    }

	public void run() {
		try{
			int car;
			byte buff[]=new byte[1];
			while((car=is.read())>=0){
				buff[0]=(byte)car;
				os.write(buff,0,1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
