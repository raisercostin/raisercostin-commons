package org.raisercostin.util;

/**
The ExceptionConverter changes a checked exception into an
unchecked exception.
*/
public class RuntimeExceptionWrapper extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2524174587937781448L;

	/** we keep a handle to the wrapped exception */
	private final Exception ex;

	public RuntimeExceptionWrapper(Exception ex) {
		this.ex = ex;
	}

	/** and allow the user of ExceptionConverter to get a handle to it. */
	public Exception getException() {
		return ex;
	}

	/** We print the message of the checked exception */
	public String getMessage() {
		return ex.getMessage();
	}

	/** and make sure we also produce a localized version */
	public String getLocalizedMessage() {
		return ex.getLocalizedMessage();
	}

	/** The toString() is changed to be prefixed with ExceptionConverter */
	public String toString() {
		return "ExceptionConverter: " + ex;
	}

	/** we have to override this as well */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/** here we prefix, with s.print(), not s.println(), the stack
	  trace with "ExceptionConverter:" */
	public void printStackTrace(java.io.PrintStream s) {
		synchronized (s) {
			s.print("ExceptionConverter: ");
			ex.printStackTrace(s);
		}
	}

	/** Again, we prefix the stack trace with "ExceptionConverter:" */
	public void printStackTrace(java.io.PrintWriter s) {
		synchronized (s) {
			s.print("ExceptionConverter: ");
			ex.printStackTrace(s);
		}
	}

	/** requests to fill in the stack trace we will have to ignore
	(I think)  We can't throw an exception here, as this method
	is called by the constructor of Throwable */
	public Throwable fillInStackTrace() {
		return this;
	}
}