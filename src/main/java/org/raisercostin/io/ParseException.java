package org.raisercostin.io;

public class ParseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 244930738179907457L;

	public ParseException() {
		super();
	}

	public ParseException(final String arg0) {
		super(arg0);
	}

	public ParseException(final String message, final Exception detail) {
		super(message, detail);
	}

	public ParseException(final Exception e) {
		super(e);
	}
}
