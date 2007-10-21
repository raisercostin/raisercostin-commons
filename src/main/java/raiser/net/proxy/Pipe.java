package raiser.net.proxy;

import java.io.InputStream;
import java.io.OutputStream;

public class Pipe extends java.lang.Thread {
	InputStream is;

	OutputStream os;

	public Pipe(final OutputStream os, final InputStream is) {
		this.os = os;
		this.is = is;
	}

	/** Creates new Pipe */
	public Pipe(final String name, final OutputStream os, final InputStream is) {
		super(name);
		this.os = os;
		this.is = is;
	}

	@Override
	public void run() {
		try {
			int car;
			final byte buff[] = new byte[1];
			while ((car = is.read()) >= 0) {
				buff[0] = (byte) car;
				os.write(buff, 0, 1);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
