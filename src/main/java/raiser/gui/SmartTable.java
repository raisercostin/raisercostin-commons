/*******************************************************************************
 * *****************************************************************************
 * $Logfile: $ $Revision: $ $Author: $ $Date: $ $NoKeywords: $
 ******************************************************************************/
package raiser.gui;

import java.util.*;

import javax.swing.table.TableModel;

import raiser.gui.tables.*;

/**
 * @author raiser
 */
public class SmartTable extends JTable2
{
    public SmartTable()
    {
        this(new Vector());
    }
    public SmartTable(List data)
    {
        this(null,data);
    }
    public SmartTable(TableModel model)
    {
        setModel(model);
    }

    public SmartTable(List columns, List data)
    {
        //setAutoCreateColumnsFromModel(true);
        setModel(new PropertiesTableModel(columns,data));
    }
    public void addData(Object object)
    {
        getSmartTableModel().add(object);
    }
    public void addData() throws InstantiationException, IllegalAccessException
    {
        getSmartTableModel().addData();
    }
    private SmartTableModel getSmartTableModel()
    {
        return (SmartTableModel) getModel();
    }
    public void setDefaultNewRow(Class defaultNewRow)
    {
        getSmartTableModel().setDefaultNewRow(defaultNewRow);
    }
    public void setColumns(List columns)
    {
        getSmartTableModel().setColumns(columns);
    }
    public void setData(List data)
    {
        getSmartTableModel().setData(data);
    }
    public void fireTableStructureChanged()
    {
        getSmartTableModel().fireTableStructureChanged();
    }
}
