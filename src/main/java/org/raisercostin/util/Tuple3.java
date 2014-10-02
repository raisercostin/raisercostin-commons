package org.raisercostin.util;
import org.raisercostin.utils.annotations.Nullable;

import com.google.common.base.Function;

final public class Tuple3<T1, T2, T3> {
	public final T1 _1;
	public final T2 _2;
	public final T3 _3;

	@SuppressWarnings("unused")
	private Tuple3() {
		_1 = null;
		_2 = null;
		_3 = null;
	}

	public Tuple3(T1 p1, T2 p2, T3 p3) {
		this._1 = p1;
		this._2 = p2;
		this._3 = p3;
	}

	@Override
	public String toString() {
		return "Tuple3[" + _1 + "," + _2 + "," + _3 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
		result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
		result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Tuple3<T1, T2, T3> other = (Tuple3<T1, T2, T3>) obj;
		if (_1 == null) {
			if (other._1 != null) {
				return false;
			}
		} else if (!_1.equals(other._1)) {
			return false;
		}
		if (_2 == null) {
			if (other._2 != null) {
				return false;
			}
		} else if (!_2.equals(other._2)) {
			return false;
		}
		if (_3 == null) {
			if (other._3 != null) {
				return false;
			}
		} else if (!_3.equals(other._3)) {
			return false;
		}
		return true;
	}

	public static <T1, T2, T3> Tuple3<T1, T2, T3> create(T1 p1, T2 p2, T3 p3) {
		return new Tuple3<T1, T2, T3>(p1, p2, p3);
	}
	public static <T1, T2, T3> Function<Tuple3<T1, T2, T3>, T1> _1() {
		return new Function<Tuple3<T1, T2, T3>, T1>() {
			@Override
			@Nullable
			public T1 apply(@Nullable Tuple3<T1, T2, T3> tuple) {
				return tuple._1;
			}
		};
	}
	public static <T1, T2, T3> Function<Tuple3<T1, T2, T3>, T2> _2() {
		return new Function<Tuple3<T1, T2, T3>, T2>() {
			@Override
			@Nullable
			public T2 apply(@Nullable Tuple3<T1, T2, T3> tuple) {
				return tuple._2;
			}
		};
	}
	public static <T1, T2, T3> Function<Tuple3<T1, T2, T3>, T3> _3() {
		return new Function<Tuple3<T1, T2, T3>, T3>() {
			@Override
			@Nullable
			public T3 apply(@Nullable Tuple3<T1, T2, T3> tuple) {
				return tuple._3;
			}
		};
	}
}