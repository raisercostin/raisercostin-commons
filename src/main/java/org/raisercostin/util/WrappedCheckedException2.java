package org.raisercostin.util;

import java.net.ConnectException;

public class WrappedCheckedException2 extends RuntimeException {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WrappedCheckedException2.class);

	private static final long serialVersionUID = 3405430293049568292L;

	public WrappedCheckedException2(String message, Throwable cause) {
		super(message, cause);
		checkCause(cause);
	}

	public WrappedCheckedException2(Throwable cause) {
		super(cause);
		checkCause(cause);
	}

	private void checkCause(Throwable cause) {
		if (cause == null) {
			throw new RuntimeException("You should wrap an existing exception not a null value.");
		}
		if (cause instanceof RuntimeException) {
			throw new RuntimeException("You should directly throw your runtime exception and not wrap it up.", cause);
		}
		if (!(cause instanceof Exception)) {
			throw new RuntimeException("You shouldn't catch an Throwable and try to wrap it.", cause);
		}
	}

	public void rethrow() throws Throwable {
		throw getCause();
	}

	public void catchIf(Class<ConnectException> clazz, Runnable runnable) {
		if (clazz.isInstance(getCause())) {
			LOG.trace("An exception was thrown.", this);
			runnable.run();
		} else {
			throw this;
		}
	}
}
