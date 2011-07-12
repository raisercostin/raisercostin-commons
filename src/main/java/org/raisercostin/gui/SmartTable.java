/*******************************************************************************
 * *****************************************************************************
 * $Logfile: $ $Revision: $ $Author: $ $Date: $ $NoKeywords: $
 ******************************************************************************/
package org.raisercostin.gui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.TableModel;

import org.raisercostin.gui.tables.PropertiesTableModel;
import org.raisercostin.gui.tables.SmartTableModel;


/**
 * @author org.raisercostin
 */
public class SmartTable<T> extends JTable2 {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1619416515523874767L;

	public SmartTable() {
		this(new Vector<T>());
	}

	public SmartTable(final List<T> data) {
		this(null, data);
	}

	public SmartTable(final TableModel model) {
		setModel(model);
	}

	public SmartTable(final List<String> columns, final List<T> data) {
		// setAutoCreateColumnsFromModel(true);
		setModel(new PropertiesTableModel<T>(columns, data));
	}

	public SmartTable(Class<T> theClass) {
		createUsingClass(theClass);
	}

	private void createUsingClass(Class<T> theClass) {
		Vector<T> data = new Vector<T>();
		try {
			data.add(theClass.newInstance());
			setModel(new PropertiesTableModel<T>(theClass, data));
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T2> SmartTable(T2... objects){
		if (objects.length == 1 && objects[0] instanceof Class) {
			createUsingClass((Class) objects[0]);
		} else {
			setModel(new PropertiesTableModel<T2>(objects));
		}
	}

	public void addData(final T object) {
		getSmartTableModel().add(object);
	}

	public void addData() throws InstantiationException, IllegalAccessException {
		getSmartTableModel().addData();
	}

	@SuppressWarnings("unchecked")
	private SmartTableModel<T> getSmartTableModel() {
		return (SmartTableModel<T>) getModel();
	}

	public void setDefaultNewRow(final Class<? extends T> defaultNewRow) {
		getSmartTableModel().setDefaultNewRow(defaultNewRow);
	}

	public void setColumns(final List<String> columns) {
		getSmartTableModel().setColumns(columns);
	}

	public void setData(final List<T> data) {
		getSmartTableModel().setData(data);
	}

	public void fireTableStructureChanged() {
		getSmartTableModel().fireTableStructureChanged();
	}
}
