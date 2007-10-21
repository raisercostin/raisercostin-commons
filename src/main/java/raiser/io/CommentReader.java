/*
 * Created on Jul 18, 2003
 */
package raiser.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author raiser
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