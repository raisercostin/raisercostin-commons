package org.raisercostin.util;

import com.google.common.base.Supplier;

/**
 * Taken from here @see
 * http://codereview.stackexchange.com/questions/18056/ability-to-forget-the-memoized-supplier-value
 */
public interface ForgettableSupplier<T> extends Supplier<T> {
	/** Returns the old value. */
	public T getAndForget();
	public boolean isPresent();
}