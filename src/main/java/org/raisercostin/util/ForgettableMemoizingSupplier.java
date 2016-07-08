package org.raisercostin.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

public final class ForgettableMemoizingSupplier<T> implements ForgettableSupplier<T> {

	private final Supplier<T> delegate;

	private final AtomicReference<T> resourceRef = new AtomicReference<T>();

	private final ReentrantLock lock = new ReentrantLock();

	public ForgettableMemoizingSupplier(final Supplier<T> delegate) {
		this.delegate = Preconditions.checkNotNull(delegate, "delegate cannot be null");
	}

	@Override
	public T getAndForget() {
		return resourceRef.getAndSet(null);
	}

	@Override
	public T get() {
		final T resource = resourceRef.get();
		if (resource != null) {
			return resource;
		}
		return getAndSetResource();
	}

	private T getAndSetResource() {
		lock.lock();
		try {
			final T resource = resourceRef.get();
			if (resource != null) {
				return resource;
			}
			final T newResource = delegate.get();
			resourceRef.set(newResource);
			return newResource;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isPresent() {
		return resourceRef.get() != null;
	}
}