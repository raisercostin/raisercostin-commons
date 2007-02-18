package raiser.io;

public class ParseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 244930738179907457L;

	public ParseException() {
		super();
	}

	public ParseException(String arg0) {
		super(arg0);
	}

	public ParseException(String message, Exception detail) {
		super(message, detail);
	}

	public ParseException(Exception e) {
		super(e);
	}
}
