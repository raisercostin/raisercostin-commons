/*
 * Created on Jan 14, 2004
 */
package raiser.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * @author raiser
 */
public class OptionCell {
	public static final String VALUE_NONE = "";

	public static final String VALUE_UNMODIFIED = "< unmodified >";

	private EventListenerList listenerList;

	private ChangeEvent changeEvent;

	private Object editedValue;

	private boolean editable;

	private List<Object> objects;

	private int selected;

	public OptionCell(Object objects[], int selected, boolean acceptNewValues) {
		this.objects = new ArrayList<Object>();
		this.selected = selected;
		editedValue = null;
		editable = acceptNewValues;
		add(Arrays.asList(objects));
		listenerList = new EventListenerList();
	}

	public OptionCell(Object objects[]) {
		this(objects, 0, false);
	}

	public OptionCell() {
		this(new Object[0], 0, true);
	}

	public void setSelected(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("Bad index=" + index + ".");
		}
		if (index >= getSize()) {
			throw new IndexOutOfBoundsException("Bad index=" + index + ".");
		}
		if (selected == index) {
			return;
		}
		selected = index;
		editedValue = null;
		fireChangeListener();
	}

	/**
	 * @return
	 */
	private int getSize() {
		return objects.size();
	}

	public int getSelectedIndex() {
		return selected;
	}

	public void setEditable(boolean acceptNewValues) {
		editable = acceptNewValues;
		editedValue = null;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setObjects(Object[] objects) {
		this.objects = new ArrayList<Object>(Arrays.asList(objects));
	}

	public void addAndSelect(Object value) {
		if (isEditable()) {
			if (value == null) {
				return;
			}
			if (value.equals(editedValue)) {
				return;
			}
			editedValue = value;
			selected = -1;
			fireChangeListener();
		}
	}

	public Object getSelected() {
		if ((isEditable()) && (editedValue != null)) {
			return editedValue;
		}
		if (selected >= getSize()) {
			return editedValue;
		}
		return objects.get(selected);
	}

	@Override
	public String toString() {
		if ((isEditable()) && (editedValue != null)) {
			return editedValue.toString();
		}
		if ((getObjectsList() == null) || (getSize() == 0)) {
			return VALUE_NONE;
		}
		if (objects.get(selected) == null) {
			return VALUE_NONE;
		}
		return objects.get(selected).toString();
	}

	public void addChangeListener(ChangeListener changeListener) {
		listenerList.add(ChangeListener.class, changeListener);
	}

	public void removeFooListener(ChangeListener changeListener) {
		listenerList.remove(ChangeListener.class, changeListener);
	}

	protected void fireChangeListener() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				((ChangeListener) listeners[i + 1])
						.stateChanged(getChangeEvent());
			}
		}
	}

	private ChangeEvent getChangeEvent() {
		// Lazily create the event:
		if (changeEvent == null) {
			changeEvent = new ChangeEvent(this);
		}
		return changeEvent;
	}

	/**
	 * @return
	 */
	public List getObjectsList() {
		return objects;
	}

	public void clear() {
		objects.clear();
		add(VALUE_UNMODIFIED);
		add(VALUE_NONE);
		selected = 0;
		editedValue = null;
	}

	public void add(OptionCell cell) {
		add(cell.objects);
	}

	private void add(Object value) {
		if (objects.indexOf(value) == -1) {
			objects.add(value == null ? VALUE_NONE : value.toString().trim());
		}
	}

	private void add(List values) {
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			Object element = iter.next();
			add(element);
		}
	}

	/**
	 * 
	 */
	public void selectFirstNotEmpty() {
		setSelected(getFirstNotEmptyIndex());
	}

	private int getFirstNotEmptyIndex() {
		int position = 0;
		for (Iterator iter = objects.iterator(); iter.hasNext(); position++) {
			Object element = iter.next();
			String value = element.toString().trim();
			if ((!VALUE_NONE.equals(value))
					&& (!VALUE_UNMODIFIED.equals(value))) {
				return position;
			}
		}
		return 0;
	}
}
