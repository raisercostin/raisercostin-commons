/*
 * Created on Jan 14, 2004
 */
package raiser.gui;

import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * @author raiser
 */
public class DynamicComboBoxModel extends DefaultComboBoxModel implements
		ComboBoxModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8491488154585111490L;

	Vector<Object> objects;

	Object selectedObject;

	/**
	 * Constructs an empty DefaultComboBoxModel object.
	 */
	public DynamicComboBoxModel() {
		objects = new Vector<Object>();
	}

	/**
	 * Constructs a DefaultComboBoxModel object initialized with an array of
	 * objects.
	 * 
	 * @param items
	 *            an array of Object objects
	 */
	public DynamicComboBoxModel(final Object items[]) {
		objects = new Vector<Object>();
		objects.ensureCapacity(items.length);

		int i, c;
		for (i = 0, c = items.length; i < c; i++) {
			objects.addElement(items[i]);
		}

		if (getSize() > 0) {
			selectedObject = getElementAt(0);
		}
	}

	/**
	 * Constructs a DefaultComboBoxModel object initialized with a vector.
	 * 
	 * @param v
	 *            a Vector object ...
	 */
	public DynamicComboBoxModel(Vector<Object> v) {
		objects = v;

		if (getSize() > 0) {
			selectedObject = getElementAt(0);
		}
	}

	// implements javax.swing.ComboBoxModel
	/**
	 * Set the value of the selected item. The selected item may be null.
	 * <p>
	 * 
	 * @param anObject
	 *            The combo box value or null for no selection.
	 */
	@Override
	public void setSelectedItem(Object anObject) {
		if ((selectedObject != null && !selectedObject.equals(anObject))
				|| selectedObject == null && anObject != null) {
			selectedObject = anObject;
			fireContentsChanged(this, -1, -1);
		}
	}

	// implements javax.swing.ComboBoxModel
	@Override
	public Object getSelectedItem() {
		return selectedObject;
	}

	// implements javax.swing.ListModel
	@Override
	public int getSize() {
		return objects.size();
	}

	// implements javax.swing.ListModel
	@Override
	public Object getElementAt(int index) {
		if ((index >= 0) && (index < objects.size())) {
			return objects.elementAt(index);
		} else {
			return null;
		}
	}

	/**
	 * Returns the index-position of the specified object in the list.
	 * 
	 * @param anObject
	 * @return an int representing the index position, where 0 is the first
	 *         position
	 */
	@Override
	public int getIndexOf(Object anObject) {
		return objects.indexOf(anObject);
	}

	// implements javax.swing.MutableComboBoxModel
	@Override
	public void addElement(Object anObject) {
		objects.addElement(anObject);
		fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
		if ((objects.size() == 1) && (selectedObject == null)
				&& (anObject != null)) {
			setSelectedItem(anObject);
		}
	}

	// implements javax.swing.MutableComboBoxModel
	@Override
	public void insertElementAt(Object anObject, int index) {
		objects.insertElementAt(anObject, index);
		fireIntervalAdded(this, index, index);
	}

	// implements javax.swing.MutableComboBoxModel
	/**
	 * @param obj
	 * @param index
	 */
	public void insertElementsAt(Object[] obj, int index) {
		objects.ensureCapacity(objects.size() + obj.length);
		for (int i = 0; i < obj.length; i++) {
			objects.insertElementAt(obj[i], index + i);
		}
		fireIntervalAdded(this, index, index + obj.length);
	}

	// implements javax.swing.MutableComboBoxModel
	@Override
	public void removeElementAt(int index) {
		if (getElementAt(index) == selectedObject) {
			if (index == 0) {
				setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
			} else {
				setSelectedItem(getElementAt(index - 1));
			}
		}

		objects.removeElementAt(index);

		fireIntervalRemoved(this, index, index);
	}

	// implements javax.swing.MutableComboBoxModel
	@Override
	public void removeElement(Object anObject) {
		int index = objects.indexOf(anObject);
		if (index != -1) {
			removeElementAt(index);
		}
	}

	/**
	 * Empties the list.
	 */
	@Override
	public void removeAllElements() {
		if (objects.size() > 0) {
			int firstIndex = 0;
			int lastIndex = objects.size() - 1;
			objects.removeAllElements();
			selectedObject = null;
			fireIntervalRemoved(this, firstIndex, lastIndex);
		} else {
			selectedObject = null;
		}
	}

	/**
	 * @param objectsList
	 * @param i
	 */
	public void insertElementsAt(List objectsList, int index) {
		objects.ensureCapacity(objects.size() + objectsList.size());
		for (int i = 0; i < objectsList.size(); i++) {
			objects.insertElementAt(objectsList.get(i), index + i);
		}
		fireIntervalAdded(this, index, index + objectsList.size());
	}
}
