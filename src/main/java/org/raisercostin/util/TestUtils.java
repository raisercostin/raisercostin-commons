package org.raisercostin.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;

import com.google.common.base.Preconditions;

public class TestUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestUtils.class);

	public static class Condition {
		public static Condition init() {
			return new Condition(false, false, "", "", "");
		}

		public final boolean iterateMore;
		public final boolean isExpectedState;
		private final String waitingMessage;
		private final String validMessage;
		private final String invalidMessage;

		public Condition(boolean iterateMore, boolean isExpectedState, String waitingMessage, String validMessage,
				String invalidMessage) {
			this.iterateMore = iterateMore;
			this.isExpectedState = isExpectedState;
			this.waitingMessage = waitingMessage;
			this.validMessage = validMessage;
			this.invalidMessage = invalidMessage;
		}
		public static Condition create(boolean isExpectedState, boolean isInvalidState, String waitingMessage,
				String validMessage, String invalidMessage) {
			boolean stopIteration = isInvalidState || isExpectedState;
			boolean iterateMore = !stopIteration;
			return new Condition(iterateMore, isExpectedState, waitingMessage, validMessage, invalidMessage);
		}
	}

	@Deprecated
	public static void waitForCondition(long waitTimeInMiliseconds, long totalWaitTimeInMiliseconds,
			Callable<Condition> condition) {
		Preconditions.checkArgument(totalWaitTimeInMiliseconds > 0);
		Condition result = Condition.init();
		long time = 0;
		try {
			while (time <= totalWaitTimeInMiliseconds && ((result = condition.call()).iterateMore)) {
				// ((!expectedState.equals(actualSimpleState = (actualState = callable.call()).p2)) &&
				// isStillValid(actualSimpleState, invalidStates))) {
				LOG.info(result.waitingMessage);
				sleep(waitTimeInMiliseconds, time, totalWaitTimeInMiliseconds);
				time += waitTimeInMiliseconds;
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new WrappedCheckedException2(e);
		}
		if (result.isExpectedState) {
			LOG.info(result.validMessage);
		} else {
			Assert.fail(result.invalidMessage);
		}
	}

	public static void sleep(long sleepInMiliseconds, long waitedTillNowInMiliseconds, long totalWaitTimeInMiliseconds) {
		LOG.info("wait " + (sleepInMiliseconds / 1000) + "s - " + (waitedTillNowInMiliseconds / 1000) + "s/"
				+ (totalWaitTimeInMiliseconds / 1000) + "s = "
				+ (waitedTillNowInMiliseconds * 100 / totalWaitTimeInMiliseconds) + "% ...");
		try {
			Thread.sleep(sleepInMiliseconds);
		} catch (InterruptedException e) {
			throw new WrappedCheckedException2(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2> Callable<Condition> createCondition(final String serverId, final T2 validState,
			final Callable<Pair<T1, T2>> callable, final T2... invalidStates) {
		return createCondition(serverId, Arrays.asList(validState), callable, invalidStates);
	}
	@SuppressWarnings("unchecked")
	public static <T1, T2> Callable<Condition> createCondition(final String serverId, final T2[] validStates,
			final Callable<Pair<T1, T2>> callable, final T2... invalidStates) {
		return createCondition(serverId, Arrays.asList(validStates), callable, invalidStates);
	}
	@SuppressWarnings("unchecked")
	private static <T1, T2> Callable<Condition> createCondition(final String serverId, final List<T2> validStates,
			final Callable<Pair<T1, T2>> callable, final T2... invalidStates) {
		return new Callable<Condition>() {
			@Override
			public Condition call() throws Exception {
				Pair<T1, T2> actualState;
				actualState = callable.call();
				T2 actualSimpleState = actualState._2;
				boolean isInvalidState = contained(actualSimpleState, invalidStates);
				boolean isExpectedState = validStates.contains(actualSimpleState);
				String waitingMessage = serverId + "> Waiting to have " + validStates + " and got " + actualSimpleState
						+ " with full state [" + actualState + "]";
				String validMessage = serverId + "> Waited  to have " + validStates + " and got " + actualSimpleState
						+ " with full state [" + actualState + "]";
				String invalidMessage = serverId
						+ "> Waited  to have "
						+ validStates
						+ " and got "
						+ actualSimpleState
						+ " with full state ["
						+ actualState
						+ "]"
						+ (isInvalidState ? (" that is one of the invalidStates " + org.springframework.util.StringUtils
								.arrayToCommaDelimitedString(invalidStates)) : "");
				Condition c = Condition.create(isExpectedState, isInvalidState, waitingMessage, validMessage,
						invalidMessage);
				return c;
			}
		};
	}
	@SuppressWarnings("unchecked")
	private static <T2> boolean contained(T2 actualState, T2... invalidStates) {
		for (T2 t2 : invalidStates) {
			if (actualState.equals(t2)) {
				return true;
			}
		}
		return false;
	}
}
