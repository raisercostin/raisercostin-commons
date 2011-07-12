/*
 * Created on Jan 14, 2004
 */
package org.raisercostin.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * @author org.raisercostin
 */
public class OptionCellEditor extends DefaultCellEditor implements CellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4742346012502972376L;

	protected OptionCell currentValue = null;

	protected int row;

	protected int column;

	public OptionCellEditor(final JComboBox comboBox) {
		// Unfortunately, the constructor expects a check box, combo box, or
		// text field.
		super(new JCheckBox());
		editorComponent = comboBox;
		// This is usually 1 or 2.
		setClickCountToStart(1);
		comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		delegate = new EditorDelegate() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5334088163029050512L;

			@Override
			public void setValue(final Object value) {
				comboBox.setSelectedItem(value);
			}

			@Override
			public Object getCellEditorValue() {
				return comboBox.getSelectedItem();
			}

			@Override
			public boolean shouldSelectCell(final EventObject anEvent) {
				if (anEvent instanceof MouseEvent) {
					final MouseEvent e = (MouseEvent) anEvent;
					return e.getID() != MouseEvent.MOUSE_DRAGGED;
				}
				return true;
			}

			@Override
			public boolean stopCellEditing() {
				if (comboBox.isEditable()) {
					// Commit edited value.
					comboBox.actionPerformed(new ActionEvent(
							OptionCellEditor.this, 0, ""));
				}
				return super.stopCellEditing();
			}
		};
		comboBox.addActionListener(delegate);
	}

	@Override
	protected void fireEditingStopped() {
		final int fire = getJComboBox().getSelectedIndex();
		if (fire == -1) {
			if ((getJComboBox().getSelectedItem() != null)
					&& (!getJComboBox().getSelectedItem().equals(""))) {
				currentValue.addAndSelect(getJComboBox().getSelectedItem());
			}
		} else {
			currentValue.setSelected(fire);
		}
		fireEditingStoppedHelper();
	}

	protected void fireEditingStoppedHelper() {
		super.fireEditingStopped();
	}

	@Override
	public Object getCellEditorValue() {
		return currentValue;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table,
			final Object value, final boolean isSelected, final int row,
			final int column) {
		this.row = row;
		this.column = column;
		currentValue = (OptionCell) value;
		final DynamicComboBoxModel model = (DynamicComboBoxModel) getJComboBox()
				.getModel();
		model.removeAllElements();
		model.insertElementsAt(currentValue.getObjectsList(), 0);
		getJComboBox().setEditable(currentValue.isEditable());
		System.out.println("selected=<" + currentValue.getSelected() + ">");
		getJComboBox().setSelectedItem(currentValue.getSelected());
		// getJComboBox().setSelectedIndex(currentValue.getSelected());
		return editorComponent;
	}

	protected JComboBox getJComboBox() {
		return ((JComboBox) editorComponent);
	}
}