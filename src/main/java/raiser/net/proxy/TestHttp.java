package raiser.net.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 
 * @author cgr
 * @version
 */
public class TestHttp {
	/** Creates new WebServer */
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

	public static void main(final String argv[]) {
		try {
			final TestHttp th = new TestHttp();
			System.out
					.println(th.getContent(new URL("http://raiser.home.ro/")));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		System.out.println("gata");
	}
}
