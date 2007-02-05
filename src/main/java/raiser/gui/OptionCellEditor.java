/*
 * Created on Jan 14, 2004
 */
package raiser.gui;

import java.awt.Component;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;

/**
 * @author raiser
 */
public class OptionCellEditor extends DefaultCellEditor implements CellEditor
{
    protected OptionCell currentValue = null;
    protected int row;
    protected int column;

    public OptionCellEditor(final JComboBox comboBox)
    {
        //Unfortunately, the constructor expects a check box, combo box, or text field.
        super(new JCheckBox());
        editorComponent = comboBox;
        //This is usually 1 or 2.
        setClickCountToStart(1);
        comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        delegate = new EditorDelegate()
        {
            public void setValue(Object value)
            {
                comboBox.setSelectedItem(value);
            }

            public Object getCellEditorValue()
            {
                return comboBox.getSelectedItem();
            }

            public boolean shouldSelectCell(EventObject anEvent)
            {
                if (anEvent instanceof MouseEvent)
                {
                    MouseEvent e = (MouseEvent) anEvent;
                    return e.getID() != MouseEvent.MOUSE_DRAGGED;
                }
                return true;
            }

            public boolean stopCellEditing()
            {
                if (comboBox.isEditable())
                {
                    // Commit edited value.
                    comboBox.actionPerformed(new ActionEvent(
                            OptionCellEditor.this, 0, ""));
                }
                return super.stopCellEditing();
            }
        };
        comboBox.addActionListener(delegate);
    }

    protected void fireEditingStopped()
    {
        int fire = getJComboBox().getSelectedIndex();
        if (fire == -1)
        {
            if ((getJComboBox().getSelectedItem() != null)
                    && (!getJComboBox().getSelectedItem().equals("")))
            {
                currentValue.addAndSelect(getJComboBox().getSelectedItem());
            }
        }
        else
        {
            currentValue.setSelected(fire);
        }
        fireEditingStoppedHelper();
    }
    protected void fireEditingStoppedHelper()
    {
        super.fireEditingStopped();
    }

    public Object getCellEditorValue()
    {
        return currentValue;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column)
    {
        this.row = row;
        this.column = column;
        currentValue = (OptionCell) value;
        DynamicComboBoxModel model = (DynamicComboBoxModel) getJComboBox()
                .getModel();
        model.removeAllElements();
        model.insertElementsAt(currentValue.getObjectsList(), 0);
        getJComboBox().setEditable(currentValue.isEditable());
        System.out.println("selected=<" + currentValue.getSelected() + ">");
        getJComboBox().setSelectedItem(currentValue.getSelected());
        //getJComboBox().setSelectedIndex(currentValue.getSelected());
        return editorComponent;
    }

    protected JComboBox getJComboBox()
    {
        return ((JComboBox) editorComponent);
    }
}