package org.raisercostin.net.proxy;

import java.io.*;

public class BlockedPipe extends java.lang.Thread {
	BufferedReader reader;

	PrintWriter writer;

	/** Creates new Pipe */
	public BlockedPipe(final OutputStream os, final InputStream is) {
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
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
