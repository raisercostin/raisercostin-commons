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
public interface SmartTableModel
{
    void add(Object object);
    void addData() throws InstantiationException, IllegalAccessException;
    void fireTableRowsInserted(int from, int to);
    void fireTableStructureChanged();
    void setData(List data);
    void setColumns(List columns);
    void setDefaultNewRow(Class defaultNewRow);
}
