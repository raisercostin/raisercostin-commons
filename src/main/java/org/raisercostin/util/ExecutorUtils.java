package org.raisercostin.util;

import java.util.concurrent.Callable;

import org.raisercostin.utils.ObjectUtils;

public class ExecutorUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ExecutorUtils.class);

	public static Runnable dumpException(final Runnable runnable) {
		final RuntimeException source = new RuntimeException("caller");
		return new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (RuntimeException e) {
					LOG.error(
							"In thread an exception was thrown. The caller is from " + ObjectUtils.toStringDump(source),
							e);
					throw e;
				} catch (Throwable e) {
					LOG.error(
							"In thread an exception was thrown. The caller is from " + ObjectUtils.toStringDump(source),
							e);
					throw new RuntimeException(e);
				}
			}
		};
	}

	public static <T> Callable<T> dumpException(final String description, final Callable<T> callable) {
		final RuntimeException source = new RuntimeException("caller");
		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				try {
					return callable.call();
				} catch (Exception e) {
					LOG.error(
							description + "In thread an exception was thrown. The caller is from "
									+ ObjectUtils.toStringDump(source), e);
					throw e;
				} catch (Throwable e) {
					LOG.error(
							description + "In thread an exception was thrown. The caller is from "
									+ ObjectUtils.toStringDump(source), e);
					throw new RuntimeException(description, e);
				}
			}
		};
	}
}
