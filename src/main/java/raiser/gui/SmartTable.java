/*******************************************************************************
 * *****************************************************************************
 * $Logfile: $ $Revision: $ $Author: $ $Date: $ $NoKeywords: $
 ******************************************************************************/
package raiser.gui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.TableModel;

import raiser.gui.tables.PropertiesTableModel;
import raiser.gui.tables.SmartTableModel;

/**
 * @author raiser
 */
public class SmartTable<T> extends JTable2 {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1619416515523874767L;

	public SmartTable() {
		this(new Vector<T>());
	}

	public SmartTable(List<T> data) {
		this(null, data);
	}

	public SmartTable(TableModel model) {
		setModel(model);
	}

	public SmartTable(List columns, List<T> data) {
		// setAutoCreateColumnsFromModel(true);
		setModel(new PropertiesTableModel<T>(columns, data));
	}

	public void addData(T object) {
		getSmartTableModel().add(object);
	}

	public void addData() throws InstantiationException, IllegalAccessException {
		getSmartTableModel().addData();
	}

	@SuppressWarnings("unchecked")
	private SmartTableModel<T> getSmartTableModel() {
		return (SmartTableModel) getModel();
	}

	public void setDefaultNewRow(Class<? extends T> defaultNewRow) {
		getSmartTableModel().setDefaultNewRow(defaultNewRow);
	}

	public void setColumns(List columns) {
		getSmartTableModel().setColumns(columns);
	}

	public void setData(List<T> data) {
		getSmartTableModel().setData(data);
	}

	public void fireTableStructureChanged() {
		getSmartTableModel().fireTableStructureChanged();
	}
}
