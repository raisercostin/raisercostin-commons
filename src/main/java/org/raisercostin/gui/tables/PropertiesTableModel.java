/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.gui.tables;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.beanutils.PropertyUtils;

public class PropertiesTableModel<T> extends AbstractTableModel implements SmartTableModel<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2254858775537494543L;

	private List<PropertyDescriptor> descriptors;

	private List<T> data;

	public List<PropertyDescriptor> columns;

	private List<String> initialColumns;

	private Class<? extends T> defaultNewRow;

	public PropertiesTableModel() {
		this((List<String>) null, new ArrayList<T>());
	}

	public PropertiesTableModel(final List<T> data) {
		this((List<String>) null, data);
	}

	public PropertiesTableModel(final List<String> columnNames, final List<T> data) {
		super();
		setData(data);
		setColumns(columnNames);
		fireTableStructureChanged();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T2> PropertiesTableModel(final T2[] data) {
		super();
		List asList = Arrays.asList(data);
		setData(new ArrayList(asList));
		setColumns(null);
		fireTableStructureChanged();
	}

	public PropertiesTableModel(final Class<T> defaultNewRow, final List<T> data) {
		super();
		setData(data);
		setDefaultNewRow(defaultNewRow);
		fireTableStructureChanged();
	}

	private void init() {
		if (data == null) {
			data = new ArrayList<T>();
		}
		fireTableStructureChanged();
	}

	@Override
	public void fireTableStructureChanged() {
		computeDescriptors();
		super.fireTableStructureChanged();
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		for (int i = 0; i < getRowCount(); i++) {
			if (getValueAt(i, columnIndex) != null) {
				return getValueAt(i, columnIndex).getClass();
			}
		}
		return super.getColumnClass(columnIndex);
	}

	private List<PropertyDescriptor> getDescriptors() {
		if (descriptors == null) {
			init();
		}
		return descriptors;
	}

	@Override
	public int getColumnCount() {
		return getDescriptors().size();
	}

	@Override
	public String getColumnName(final int column) {
		return descriptors.get(column).getName();
	}

	@Override
	public int getRowCount() {
		return getData().size();
	}

	private List<?> getData() {
		if (data == null) {
			init();
		}
		return data;
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		return getProperty(getObject(row), getColumnName(column));
	}

	@Override
	public void setValueAt(final Object aValue, final int row, final int column) {
		setProperty(getObject(row), getColumnName(column), aValue);
	}

	private Object getObject(final int row) {
		return data.get(row);
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return descriptors.get(column).getWriteMethod() != null;
	}

	private void computeDescriptors() {
		final Class<?> infoClass = data.size() == 0 ? getDefaultNewRow() : data.get(0).getClass();
		System.out.println("computeDescriptors infoClass=" + infoClass + " default=" + getDefaultNewRow());
		columns = null;
		descriptors = getColumns(infoClass, initialColumns);
	}

	public Object getProperty(final Object object, final String name) {
		try {
			return PropertyUtils.getProperty(object, name);
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final NoSuchMethodException e) {
			return "****";
		}
		return "";
	}

	public void setProperty(final Object object, final String name, final Object value) {
		try {
			PropertyUtils.setProperty(object, name, value);
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException("Can't set property '" + name + "' with value '" + value + "'.", e);
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public List<PropertyDescriptor> getColumns(final Class<?> infoClass, final List<String> columns2) {
		if (columns == null) {
			columns = computeColumns(infoClass, columns2);
		}
		return columns;
	}

	/**
	 * @param infoClass
	 */
	private static List<PropertyDescriptor> computeColumns(final Class<?> infoClass, final List<String> columns2) {
		final PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(infoClass);
		final List<PropertyDescriptor> columns = new ArrayList<PropertyDescriptor>();
		if (columns2 != null) {
			for (final String columnName : columns2) {
				final PropertyDescriptor temp = find(desc, columnName);
				if (temp != null) {
					columns.add(temp);
				}
			}
		} else {
			for (final PropertyDescriptor element : desc) {
				if (!element.getName().equals("class")) {
					// if ((columns2 == null)
					// || (columns2.indexOf(element.getName()) != -1)) {
					columns.add(element);
					// }
				}
			}
			if (columns.size() == 0) {
				throw new RuntimeException("No public property was discovered to the class [" + infoClass + "].");
			}
			Collections.sort(columns, new Comparator<PropertyDescriptor>() {
				@Override
				public int compare(final PropertyDescriptor o1, final PropertyDescriptor o2) {
					return o1.getName().compareToIgnoreCase(o1.getName());
				}
			});
		}
		return columns;
	}

	/**
	 * @param desc
	 * @param columnName
	 * @return
	 */
	private static PropertyDescriptor find(final PropertyDescriptor[] desc, final String columnName) {
		for (final PropertyDescriptor element : desc) {
			if (columnName.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raisercostin.gui.tables.SmartTableModel#add(java.lang.Object)
	 */
	@Override
	public void add(final T object) {
		data.add(object);
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	@Override
	public void addData() throws InstantiationException, IllegalAccessException {
		data.add(getDefaultNewRow().newInstance());
		fireTableStructureChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raisercostin.gui.tables.SmartTableModel#setData(java.util.List)
	 */
	@Override
	public void setData(final List<T> data) {
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raisercostin.gui.tables.SmartTableModel#setColumns(java.util.List)
	 */
	@Override
	public void setColumns(final List<String> columns) {
		this.initialColumns = columns;
	}

	/*
	 * (non-Javadoc) @param defaultNewRow The defaultNewRow to set.
	 */
	@Override
	public void setDefaultNewRow(final Class<? extends T> defaultNewRow) {
		this.defaultNewRow = defaultNewRow;
	}

	/**
	 * @return Returns the defaultNewRow.
	 */
	private Class<? extends T> getDefaultNewRow() {
		// if (defaultNewRow == null) {
		// defaultNewRow = Object.class;
		// }
		return defaultNewRow;
	}

	public void removeElementAt(final int index) {
		data.remove(index);
		fireTableStructureChanged();
	}
}
