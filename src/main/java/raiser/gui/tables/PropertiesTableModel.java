/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.gui.tables;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.beanutils.PropertyUtils;

public class PropertiesTableModel extends AbstractTableModel implements SmartTableModel {
    private List descriptors;
    private List data;
    private List initialColumns;
    private Class defaultNewRow;

    public PropertiesTableModel() {
        this(null, new ArrayList());
    };

    public PropertiesTableModel(List data) {
        this(null, data);
    };

    public PropertiesTableModel(List columnNames, List data) {
        super();
        setData(data);
        setColumns(columns);
        fireTableStructureChanged();
    }

    private void init() {
        if (data == null) {
            data = new ArrayList();
        }
        fireTableStructureChanged();
    }

    public void fireTableStructureChanged() {
        computeDescriptors();
        super.fireTableStructureChanged();
    }

    public Class getColumnClass(int columnIndex) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getValueAt(i, columnIndex) != null) {
                return getValueAt(i, columnIndex).getClass();
            }
        }
        return super.getColumnClass(columnIndex);
    }

    private List getDescriptors() {
        if (descriptors == null) {
            init();
        }
        return descriptors;
    }

    public int getColumnCount() {
        return getDescriptors().size();
    }

    public String getColumnName(int column) {
        return ((PropertyDescriptor) descriptors.get(column)).getName();
    }

    public int getRowCount() {
        return getData().size();
    }

    private List getData() {
        if (data == null) {
            init();
        }
        return data;
    }

    public Object getValueAt(int row, int column) {
        return getProperty(getObject(row), getColumnName(column));
    }

    public void setValueAt(Object aValue, int row, int column) {
        setProperty(getObject(row), getColumnName(column), aValue);
    }

    private Object getObject(int row) {
        return data.get(row);
    }

    public boolean isCellEditable(int row, int column) {
        return ((PropertyDescriptor) descriptors.get(column)).getWriteMethod() != null;
    }

    private void computeDescriptors() {
        System.out.println("computeDescriptors");
        Class infoClass = data.size() == 0 ? getDefaultNewRow() : data.get(0).getClass();
        columns = null;
        descriptors = getColumns(infoClass, initialColumns);
    }

    public Object getProperty(Object object, String name) {
        try {
            return PropertyUtils.getProperty(object, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            return "****";
        }
        return "";
    }

    public void setProperty(Object object, String name, Object value) {
        try {
            PropertyUtils.setProperty(object, name, value);
        } catch (IllegalArgumentException e) {
            System.err.println("Can't set property '" + name + "' with value '" + value + "'.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public List getColumns(Class infoClass, List columns2) {
        if (columns == null) {
            columns = computeColumns(infoClass, columns2);
        }
        return columns;
    }

    /**
     * @param infoClass
     */
    private static List computeColumns(Class infoClass, List columns2) {
        PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(infoClass);
        List columns = new ArrayList();
        if (columns2 != null) {
            for (Iterator iter = columns2.iterator(); iter.hasNext();) {
                String columnName = (String) iter.next();
                PropertyDescriptor temp = find(desc, columnName);
                if (temp != null) {
                    columns.add(temp);
                }
            }
        } else {
            for (int i = 0; i < desc.length; i++) {
                if (!desc[i].getName().equals("class")) {
                    if ((columns2 == null) || (columns2.indexOf(desc[i].getName()) != -1)) {
                        columns.add(desc[i]);
                    }
                }
            }
            Collections.sort(columns, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((PropertyDescriptor) o1).getName().compareToIgnoreCase(
                            ((PropertyDescriptor) o1).getName());
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
    private static PropertyDescriptor find(PropertyDescriptor[] desc, String columnName) {
        for (int i = 0; i < desc.length; i++) {
            if (columnName.equals(desc[i].getName())) {
                return desc[i];
            }
        }
        return null;
    }

    public List columns;

    /*
     * (non-Javadoc)
     *
     * @see raiser.gui.tables.SmartTableModel#add(java.lang.Object)
     */
    public void add(Object object) {
        data.add(object);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void addData() throws InstantiationException, IllegalAccessException {
        data.add(getDefaultNewRow().newInstance());
        fireTableStructureChanged();
    }

    /*
     * (non-Javadoc)
     *
     * @see raiser.gui.tables.SmartTableModel#setData(java.util.List)
     */
    public void setData(List data) {
        this.data = data;
    }

    /*
     * (non-Javadoc)
     *
     * @see raiser.gui.tables.SmartTableModel#setColumns(java.util.List)
     */
    public void setColumns(List columns) {
        this.columns = columns;
        this.initialColumns = columns;
    }

    /*
     * (non-Javadoc) @param defaultNewRow The defaultNewRow to set.
     */
    public void setDefaultNewRow(Class defaultNewRow) {
        this.defaultNewRow = defaultNewRow;
    }

    /**
     * @return Returns the defaultNewRow.
     */
    private Class getDefaultNewRow() {
        if (defaultNewRow == null) {
            defaultNewRow = Object.class;
        }
        return defaultNewRow;
    }

    public void removeElementAt(int index) {
        data.remove(index);
        fireTableStructureChanged();
    }
}
