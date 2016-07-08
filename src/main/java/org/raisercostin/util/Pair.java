package org.raisercostin.util;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Pair<T1, T2> {
	public T1 _1;
	public T2 _2;

	public Pair() {
		//for serialization
	}
	
	@JsonCreator
	public Pair(@JsonProperty("_1") T1 p1, @JsonProperty("_2") T2 p2) {
		this._1 = p1;
		this._2 = p2;
	}

	@Override
	public String toString() {
		return "Pair[" + _1 + "," + _2 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
		result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
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
		Pair<T1, T2> other = (Pair<T1, T2>) obj;
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
		return true;
	}

	public static <T1, T2> Pair<T1, T2> create(T1 p1, T2 p2) {
		return new Pair<T1, T2>(p1, p2);
	}
}