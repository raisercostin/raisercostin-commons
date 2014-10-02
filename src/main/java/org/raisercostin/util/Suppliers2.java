package org.raisercostin.util;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.*;

public class Suppliers2 {
	public static <T> Supplier<Optional<T>> memoizeWithExpirationOfNull(Supplier<Optional<T>> delegate, long duration,
			TimeUnit unit) {
		return new ExpiringNullValuesMemoizingSupplier<T>(delegate, duration, unit);
	}

	@VisibleForTesting
	static class ExpiringNullValuesMemoizingSupplier<T> implements Supplier<Optional<T>>, Serializable {
		final Supplier<Optional<T>> delegate;
		final long durationNanos;
		transient volatile Optional<T> value;
		// The special value 0 means "not yet initialized".
		transient volatile long expirationNanos;

		ExpiringNullValuesMemoizingSupplier(Supplier<Optional<T>> delegate, long duration, TimeUnit unit) {
			this.delegate = Preconditions.checkNotNull(delegate);
			this.durationNanos = unit.toNanos(duration);
			this.value = Optional.absent();
			Preconditions.checkArgument(duration > 0);
		}

		@Override
		public Optional<T> get() {
			// Another variant of Double Checked Locking.
			//
			// We use two volatile reads. We could reduce this to one by
			// putting our fields into a holder class, but (at least on x86)
			// the extra memory consumption and indirection are more
			// expensive than the extra volatile reads.
			long nanos = expirationNanos;
			long now = System.nanoTime();
			if (nanos == 0 || now - nanos >= 0) {
				synchronized (this) {
					if (nanos == expirationNanos) { // recheck for lost race
						if (!value.isPresent()) {
							Optional<T> t = delegate.get();
							value = t;
						}
						nanos = now + durationNanos;
						// In the very unlikely event that nanos is 0, set it to 1;
						// no one will notice 1 ns of tardiness.
						expirationNanos = (nanos == 0) ? 1 : nanos;
						return value;
					}
				}
			}
			return value;
		}

		private static final long serialVersionUID = 0;
	}
}
