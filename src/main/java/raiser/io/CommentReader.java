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

	private String commentBegin;

	public CommentReader(Reader in, int sz, String commentBegin) {
		super(in, sz);
		this.commentBegin = commentBegin;
	}

	public CommentReader(Reader in, String commentBegin) {
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