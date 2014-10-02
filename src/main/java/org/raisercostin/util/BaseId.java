package org.raisercostin.util;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.raisercostin.utils.ObjectUtils;

//@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseId<T extends BaseId<?>> implements Serializable, Comparable<T> {
	private static final long serialVersionUID = 3604994768885075508L;
	private String id;

	protected BaseId() {
	}

	public BaseId(String id) {
		if (id == null) {
			throw new RuntimeException("Can't have an id with null value.");
		}
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.equals(this, obj);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + id + "]";
	}

	@Override
	public int compareTo(T other) {
		return id.compareTo(other.getId());
	}

	public String getId() {
		return id;
	}
}