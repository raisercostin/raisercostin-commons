/*
 * Created on Jul 18, 2003
 */
package org.raisercostin.io;

import java.io.*;

/**
 * @author org.raisercostin
 */
public class CommentReader extends BufferedReader {

	private final String commentBegin;

	public CommentReader(final Reader in, final int sz,
			final String commentBegin) {
		super(in, sz);
		this.commentBegin = commentBegin;
	}

	public CommentReader(final Reader in, final String commentBegin) {
		super(in);
		this.commentBegin = commentBegin;
	}

	@Override
	public String readLine() throws IOException {
		String result;
		while (((result = super.readLine()) != null)
				&& (result.startsWith(commentBegin))) {
			// System.out.println("<" + result + ">");
		}
		return result;
	}
}