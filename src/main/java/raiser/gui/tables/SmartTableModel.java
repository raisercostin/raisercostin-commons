/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.gui.tables;

import java.util.List;

/**
 * @author raisercostin
 */
public interface SmartTableModel<T> {
	void add(T object);

	void addData() throws InstantiationException, IllegalAccessException;

	void fireTableRowsInserted(int from, int to);

	void fireTableStructureChanged();

	void setData(List<T> data);

	void setColumns(List columns);

	void setDefaultNewRow(Class<? extends T> defaultNewRow);
}
