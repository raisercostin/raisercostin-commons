/*
 * Created on Jun 3, 2005
 */
package org.raisercostin.gui;

public class DataItem {
	private String label;

	private Object data;

	private Object key;

	public DataItem(final String label, final Object data, final Object key) {
		this.label = label;
		this.data = data;
		this.key = key;
	}

	public DataItem(final String label, final Object data) {
		this(label, data, null);
	}

	public Object getData() {
		return data;
	}

	public void setData(final Object data) {
		this.data = data;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(final Object key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
