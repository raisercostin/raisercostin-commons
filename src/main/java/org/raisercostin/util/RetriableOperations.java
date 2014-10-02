package org.raisercostin.util;

import java.util.ArrayList;
import java.util.List;

import org.raisercostin.utils.ObjectUtils;

public class RetriableOperations {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RetriableOperations.class);

	public static void doOperation(String operation, int attempts, Runnable runnable) {
		if (attempts == 0) {
			throw new RuntimeException("Attempts " + attempts + " should be greater than 0.");
		}
		LOG.debug("operation " + operation + " with " + attempts + " attempts ...");
		List<Throwable> errors = new ArrayList<Throwable>();
		for (int i = 0; i < attempts; i++) {
			try {
				runnable.run();
				break;
			} catch (Exception e) {
				LOG.warn("Tried to execute operation " + operation + " but failed miserably " + i + "th time with "
						+ e.getMessage());
				// e.fillInStackTrace();
				errors.add(e);
			}
		}
		if (errors.isEmpty()) {
			LOG.debug("operation " + operation + " with " + attempts + " attempts - done.");
		} else if (errors.size() < attempts) {
			LOG.debug("operation " + operation + " with " + attempts + " attempts - finished sucessfully after "
					+ errors.size() + " errors.");
		} else {
			throw new RuntimeException("operation " + operation + " with " + attempts
					+ " attempts - finished unsucessfully after all retries." + ObjectUtils.toString(errors),
					errors.get(0));
		}
	}
}
