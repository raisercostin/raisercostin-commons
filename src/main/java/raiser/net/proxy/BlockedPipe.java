package raiser.net.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class BlockedPipe extends java.lang.Thread {
	BufferedReader reader;

	PrintWriter writer;

	/** Creates new Pipe */
	public BlockedPipe(OutputStream os, InputStream is) {
		reader = new BufferedReader(new InputStreamReader(is));
		writer = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(os)), true);
	}

	@Override
	public void run() {
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(currentThread() + ":" + line);
				writer.println(line);
			}
			writer.println("\n\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
