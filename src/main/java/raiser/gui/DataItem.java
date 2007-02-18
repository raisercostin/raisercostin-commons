/*
 * Created on Jun 3, 2005
 */
package raiser.gui;

public class DataItem {
	private String label;

	private Object data;

	private Object key;

	public DataItem(String label, Object data, Object key) {
		this.label = label;
		this.data = data;
		this.key = key;
	}

	public DataItem(String label, Object data) {
		this(label, data, null);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
