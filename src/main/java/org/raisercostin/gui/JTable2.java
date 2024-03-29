/*******************************************************************************
 * $Logfile: $
 * $Revision: $
 * $Author: $
 * $Date: $
 * $NoKeywords: $
 ******************************************************************************/
package org.raisercostin.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * A JTable with a few bugfixes. This class is the class FTable taken from
 * http://www.chka.de/swing/table/ .
 *
 * @since 2004.12.15
 */
public class JTable2 extends JTable {
  /**
   *
   */
  private static final long serialVersionUID = -9134857903021817731L;

  /**
   *
   */
  public JTable2() {
    super();
    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  /**
   * @param numRows
   * @param numColumns
   */
  public JTable2(final int numRows, final int numColumns) {
    super(numRows, numColumns);
    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  /**
   * @param rowData
   * @param columnNames
   */
  public JTable2(final Object[][] rowData, final Object[] columnNames) {
    super(rowData, columnNames);
    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  /**
   * @param rowData
   * @param columnNames
   */
  public JTable2(final Vector<? extends Vector> rowData,
      final Vector<?> columnNames)
  {
    super(rowData, columnNames);
    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  /**
   * @param dm
   * @param cm
   * @param sm
   */
  public JTable2(final TableModel dm, final TableColumnModel cm,
      final ListSelectionModel sm)
  {
    super(dm, cm, sm);
    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  // private boolean hasRowModel;

  private Listener listener;

  public JTable2(final TableModel data) {
    super(data);

    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  public JTable2(final TableModel data, final TableColumnModel columns) {
    super(data, columns);

    ToolTipManager.sharedInstance().unregisterComponent(this);
  }

  @Override
  protected void configureEnclosingScrollPane() {
    if (getTableHeader() != null) {
      super.configureEnclosingScrollPane();
    }
  }

  @Override
  protected void unconfigureEnclosingScrollPane() {
    if (getTableHeader() != null) {
      super.unconfigureEnclosingScrollPane();
    }
  }

  @Override
  public void setTableHeader(final JTableHeader h) {
    unconfigureEnclosingScrollPane();

    super.setTableHeader(h);

    configureEnclosingScrollPane();
  }

  @Override
  public void changeSelection(final int row, final int column,
      final boolean toggle, final boolean extend) {
    final ListSelectionModel rows = getSelectionModel();
    final ListSelectionModel columns = getColumnModel().getSelectionModel();

    final boolean selected = isCellSelected(row, column);

    final int anchorRow = rows.getAnchorSelectionIndex(), anchorColumn = columns
      .getAnchorSelectionIndex();

    // This is done wrongly even in 1.4
    final boolean anchorSelected = (anchorRow != -1)
        && (anchorColumn != -1)
        && isCellSelected(anchorRow, anchorColumn);

    TableColumnModels.change(columns, column, toggle, extend, selected,
      anchorSelected);
    TableColumnModels.change(rows, row, toggle, extend, selected,
      anchorSelected);

    // if you like it (I don't) ...
    scrollRectToVisible(getCellRect(row, column, true));
  }

  @Override
  public void setColumnModel(final TableColumnModel c) {
    if (c == null) {
      throw new IllegalArgumentException("null");
    }

    if (c == columnModel) {
      return;
    }

    finishEditing();

    if (columnModel != null) {
      columnModel.removeColumnModelListener(listener());
    }

    super.setColumnModel(c);

    columnModel.removeColumnModelListener(this);
    columnModel.addColumnModelListener(listener());
  }

  @Override
  public void setSelectionModel(final ListSelectionModel s) {
    if (s == null) {
      throw new IllegalArgumentException("null");
    }

    if (s == selectionModel) {
      return;
    }

    if (selectionModel != null) {
      selectionModel.removeListSelectionListener(listener());
    }

    super.setSelectionModel(s);

    selectionModel.removeListSelectionListener(this);
    selectionModel.addListSelectionListener(listener());
  }

  // super.setModel will call tableChanged()
  // and the editor will be removed there.
  @Override
  public void setModel(final TableModel data) {
    if (data == null) {
      throw new IllegalArgumentException("null");
    }

    if (data == dataModel) {
      return;
    }

    if (dataModel != null) {
      dataModel.removeTableModelListener(listener());
    }

    super.setModel(data);

    dataModel.removeTableModelListener(this);
    dataModel.addTableModelListener(listener());
  }

  @Override
  public void setRowHeight(final int height) {
    // hasRowModel = false;
    super.setRowHeight(height);
  }

  @Override
  public void setRowHeight(final int row, final int height) {
    // hasRowModel = true;
    super.setRowHeight(row, height);
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return (getParent() instanceof JViewport)
        && (getParent().getHeight() > getPreferredSize().height);
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return (getAutoResizeMode() != AUTO_RESIZE_OFF)
        || ((getParent() instanceof JViewport) && (getParent()
          .getWidth() > getPreferredSize().width));
  }

  protected TableColumn resizingColumn() {
    return getTableHeader() != null ? getTableHeader().getResizingColumn()
        : null;
  }

  // <1.4: choked with no rows or no columns
  // 1.4beta: removed editor unnecessarily, wrong behaviour
  // with no rows/column (#4466930)
  // should retain anchor cell?
  @Override
  public void selectAll() {
    if (getRowCount() > 0) {
      setRowSelectionInterval(0, getRowCount() - 1);
    }

    if (getColumnCount() > 0) {
      setColumnSelectionInterval(0, getColumnCount() - 1);
    }
  }

  @Override
  public void columnAdded(final TableColumnModelEvent e) {
    final int index = e.getToIndex();

    if ((editingColumn != -1) && (index <= editingColumn)) {
      setEditingColumn(editingColumn + 1);
    }

    resizeAndRepaint();
  }

  @Override
  public void columnRemoved(final TableColumnModelEvent e) {
    if (editingColumn != -1) {
      final int from = e.getFromIndex();

      if (editingColumn == from) {
        // The event doesn't carry the TableColumn,
        // so we cannot not the model index to store the
        // value into.
        // If the model index were known, could call
        // manually set the value (but not call finishEditing).
        removeEditor();
      } else if (editingColumn > from) {
        setEditingColumn(editingColumn - 1);
      }
    }

    resizeAndRepaint();
  }

  @Override
  public void columnMoved(final TableColumnModelEvent e) {
    if (editingColumn != -1) {
      final int from = e.getFromIndex(), to = e.getToIndex();

      // Need to adjust editing column first. The value may
      // stored there below.

      if (editingColumn == from) {
        setEditingColumn(to);
      } else if (from > to) {
        if ((editingColumn >= to) && (editingColumn < from)) {
          setEditingColumn(editingColumn + 1);
        }
      } else {
        if ((editingColumn > from) && (editingColumn <= to)) {
          setEditingColumn(editingColumn - 1);
        }
      }

      final TableColumn c = tableHeader != null ? tableHeader
        .getDraggedColumn() : null;

      // remove the editor because it appears on top of the moved column
      // if it is not contained there
      // As JTable cannot know whether the UI does that, the editor must
      // be removed here.

      // One could wait until the dragged column actually overlapped
      // the editing column, but that is more like magic and also
      // has overhead.

      // Actually, the editor could be restored when the drag has
      // finished.
      // That still doesn't solve the problem that invalid contents
      // (i.e. those that do not pass stopCellEditing()) will be lost.
      // so it is of limited usefulness.

      if ((c != null) && (tableHeader.getDraggedDistance() != 0)
          && (editingColumn != from)) {
        finishEditing();
      }

    }

    repaint();
  }

  // Fix for rtl width assumption.
  @Override
  public Rectangle getCellRect(final int row, final int column,
      boolean includeSpacing) {
    final Rectangle result = super.getCellRect(row, column, includeSpacing);

    if (!getComponentOrientation().isLeftToRight() && (column >= 0)
        && (column < getColumnCount())) {
      final TableColumnModel columns = getColumnModel();

      result.width = columns.getColumn(column).getWidth();

      result.x = getWidth() - result.width;

      for (int i = 0; i < column; i++) {
        result.x -= columns.getColumn(i).getWidth();
      }

      if (!includeSpacing && (row >= 0) && (row < getRowCount())) {
        final int margin = getColumnModel().getColumnMargin();

        result.x -= margin / 2;
        result.width -= margin;
      }
    }

    return result;
  }

  // This is similar to the strategy of 1.4. Unlike prior resize strategies
  // it actually works when the columns have minimum/maximums sizes set.
  // But it will always layout twice (doLayout() will call setWidth() on
  // the columns, typically these have changed, then columnMarginChanged
  // will be called-back again, which will revalidate again. Hopefully
  // the second time no widths are changed anymore.
  @Override
  public void columnMarginChanged(final ChangeEvent e) {
    final TableColumn c = resizingColumn();

    if ((c != null) && (autoResizeMode == AUTO_RESIZE_OFF)) {
      c.setPreferredWidth(c.getWidth());
    }

    resizeAndRepaint();
  }

  // See column margin changed. I see no reason to make sizeColumnsToFit()
  // with a argument different from -1 obsolete - it is the only way to
  // resize columns if one does not use the standard JTableHeader.

  // AUTO_RESIZE_OFF will use the preferred widths *absolutely*
  // and not scale them if the width happens to be different.
  @Override
  public void doLayout() {
    if (getAutoResizeMode() == AUTO_RESIZE_OFF) {
      for (final Enumeration<TableColumn> e = getColumnModel()
        .getColumns(); e.hasMoreElements();) {
        final TableColumn d = e.nextElement();

        if (d.getWidth() != d.getPreferredWidth()) {
          d.setWidth(d.getPreferredWidth());
        }
      }

      return;
    }

    final TableColumn c = resizingColumn();

    if (c == null) {
      super.doLayout();
    } else {
      final int column = TableColumnModels.indexOf(getColumnModel(), c);

      sizeColumnsToFit(column);

      /**
       * This is what 1.4 does. May this infinite loop?
       */

      final int delta = getWidth()
          - getColumnModel().getTotalColumnWidth();

      if (delta != 0) {
        c.setWidth(c.getWidth() + delta);
      }
    }
  }

  @Override
  public void tableChanged(final TableModelEvent e) {
    final int first = e.getFirstRow(), last = e.getLastRow();

    if (e.getType() == TableModelEvent.UPDATE) {
      if ((first == TableModelEvent.HEADER_ROW)
          || ((first == 0) && (last == Integer.MAX_VALUE))) {
        removeEditor();

        super.tableChanged(e);

        // Fix for 1.3 broken variable row heights
        // rowHeight may be 0 because this is called from the
        // constructor.
        if (getRowHeight() != 0) {
          setRowHeight(getRowHeight());
        }
      }

      else {
        int column = e.getColumn();

        if (column == TableModelEvent.ALL_COLUMNS) {
          if (editingRow != -1) {
            if ((editingRow >= first) && (editingRow <= last)) {
              removeEditor();
            }
          }

          Rectangle r = getCellRect(first, -1, false);

          if (last != first) {
            r = r.union(getCellRect(last, -1, false));
          }

          r.width = getWidth();

          repaint(r);
        } else {
          column = convertColumnIndexToView(column);

          if (column != -1) {
            if (editingColumn == column) {
              if ((editingRow >= first) && (editingRow <= last)) {
                removeEditor();
              }
            }

            Rectangle r = getCellRect(first, column, false);

            if (last != first) {
              r = r.union(getCellRect(last, column, false));
            }

            repaint(r);
          }
        }
      }
    } else if (e.getType() == TableModelEvent.INSERT) {
      // Will remove editor in 1.4 beta 1
      super.tableChanged(e);

      if (editingRow != -1) {
        if (editingRow >= first) {
          setEditingRow(editingRow + 1 + last - first);
        }
      }
    } else {
      // Will remove editor in 1.4 beta 1
      super.tableChanged(e);

      if (editingRow != -1) {
        if (editingRow >= first) {
          if (editingRow <= last) {
            removeEditor();
          } else {
            setEditingRow(editingRow - 1 + last - first);
          }
        }
      }
    }
  }

  // Fix for cancelCellEditing not being called
  // from removeEditor().

  // This makes assumptions about what the superclass does.
  // (But what is there more to do?).
  // Need to replace removeEditor() by super.removeEditor().

  @Override
  public void editingStopped(final ChangeEvent e) {
    final TableCellEditor f = getCellEditor();

    if (f != null) {
      // JTable implementation is the other way around:
      // setValueAt before removeEditor.
      // That way, removeEditor() would be called from
      // tableChanged() again and try to cancel editing while
      // the editor has already stopped and is the process of
      // distributing the events.

      // these change by removeEditor()
      final int row = editingRow, column = editingColumn;

      originalRemoveEditor();

      setValueAt(f.getCellEditorValue(), row, column);
    }
  }

  /**
   * If cell editing is in progress: try to stop it, if that fails, cancel it.
   * When this method returns, the JTable isn't editing.
   */
  public void finishEditing() {
    final TableCellEditor e = getCellEditor();

    if (e != null) {
      if (!e.stopCellEditing()) {
        e.cancelCellEditing();
      }
    }
  }

  // While at it, just as well provide these two methods.

  /**
   * If cell editing is in progress: try to stop it.
   *
   * @returns true iff the JTable isn't editing (anymore).
   */
  public boolean stopEditing() {
    final TableCellEditor e = getCellEditor();

    return (e == null) || e.stopCellEditing();
  }

  /**
   * If cell editing is in progress: cancel it. When this method returns, the
   * JTable isn't editing.
   */
  // This now does the same as removeEditor(), but that has a less
  // obvious method name.
  public void cancelEditing() {
    final TableCellEditor e = getCellEditor();

    if (e != null) {
      e.cancelCellEditing();
    }
  }

  @Override
  public void editingCanceled(final ChangeEvent e) {
    originalRemoveEditor();
  }

  private void originalRemoveEditor() {
    final TableCellEditor e = getCellEditor();

    if (e != null) {
      e.removeCellEditorListener(listener());
    }

    super.removeEditor();
  }

  @Override
  public void removeEditor() {
    final TableCellEditor e = getCellEditor();

    if (e != null) {
      e.cancelCellEditing();
    }
  }

  @Override
  public boolean editCellAt(final int row, final int column,
      final EventObject e) {
    // Don't call super.editCellAt() which installs the weird focus
    // lost editing *canceler* (1.4beta2).

    if ((row < 0) || (row >= getRowCount()) || (column < 0)
        || (column >= getColumnCount())) {
      throw new IllegalArgumentException("row=" + row + " col=" + column
          + " e=" + e);
    }

    if (!isCellEditable(row, column)) {
      return false;
    }

    if ((cellEditor != null) && !cellEditor.stopCellEditing()) {
      return false;
    }

    // Insert better focus logic later

    final TableCellEditor editor = getCellEditor(row, column);

    if ((editor == null) || !editor.isCellEditable(e)) {
      return false;
    }

    editorComp = prepareEditor(editor, row, column);

    if (editorComp == null) {
      return false;
    }

    setCellEditor(editor);
    setEditingRow(row);
    setEditingColumn(column);

    add(editorComp);
    editorComp.setBounds(getCellRect(row, column, false));
    editorComp.validate();

    // This is missing from the super class implementation.
    // It usually doesn't show.
    editorComp.repaint();

    // No need to *re*attach listener, since we can do it here
    // right in the first place
    editor.addCellEditorListener(listener());

    // It is very confusing to see another cell appear focused.
    // (If the focus is always given away to the editor, it doesn't show).
    getSelectionModel().setAnchorSelectionIndex(row);
    getColumnModel().getSelectionModel().setAnchorSelectionIndex(column);

    // This is not correct and partly a matter of taste:
    // (Maybe a property should be introduced like
    // surrendersFocusOwnKeystroke).
    // It is actually possible for editors to work properly even
    // if they initially don't have the focus (which already is the
    // usual case), but that is more complicated.
    // a) Some subcomponent may want the focus instead; the editor
    // component may not even be focusable. The same problems
    // has JTable with surrendersFocusOnKeystroke, it also requests
    // focus on the editor component. There should be method
    // in TableCellEditor to know the default focus component;
    // trying whether the component really is a JComponent and then
    // calling requestDefaultFocus() seems more like a heuristic
    // than something that has business in real code.
    // b) Should only do this if the focus is on the JTable?
    // But what if the JTable isn't in the focused window? Then one
    // cannot know whether it "has" focus in that window context
    // or not.
    getEditorComponent().requestFocus();

    return true;
  }

  @Override
  public String getToolTipText(final MouseEvent e) {
    String result = null;

    final Point p = e.getPoint();

    final int row = rowAtPoint(p), column = columnAtPoint(p);

    if ((column != -1) && (row != -1)) {
      final Rectangle r = getCellRect(row, column, false);

      if (r.contains(p)) // may be inside the intercell spacing
      {
        final TableCellRenderer s = getCellRenderer(row, column);
        Component c = prepareRenderer(s, row, column);

        p.translate(-r.x, -r.y);

        c.setSize(r.width, r.height);

        if (c instanceof Container) {
          c.validate();

          final Component d = ((Container) c).findComponentAt(p);

          for (Component cc = d; cc != c; cc = cc.getParent()) {
            p.translate(-cc.getX(), -cc.getY());
          }

          c = d;
        }

        if (c instanceof JComponent) {
          final MouseEvent f = new MouseEvent(c, e.getID(), e
            .getWhen(), e.getModifiers(), p.x, p.y,
            e
              .getClickCount(),
            e.isPopupTrigger());

          result = ((JComponent) c).getToolTipText(f);
        }
      }
    }

    if (result == null) {
      result = getToolTipText();
    }

    return result;
  }

  private static class TableColumnModels {
    private static int indexOf(final TableColumnModel columns,
        final TableColumn c) {
      for (int i = columns.getColumnCount() - 1; i >= 0; --i) {
        if (c.equals(columns.getColumn(i))) {
          return i;
        }
      }

      return -1;
    }

    /*
     * private static void change(ListSelectionModel selection, int index,
     * boolean toggle, boolean extend) { int anchor =
     * selection.getAnchorSelectionIndex();
     * change(selection, index, toggle, extend, selection
     * .isSelectedIndex(index), (anchor != -1) &&
     * selection.isSelectedIndex(anchor)); }
     */

    /**
     * toggle extend selected index becomes anchor lead
     * ======================================================================
     * false false irrelevant select (only) index x x true false false
     * select index x x true false true deselect index x x true true
     * irrelevant x otherwise: false true* irrelevant x if (anchorSelected)
     * deselect range between anchor and lead then select range between
     * anchor and index else not (select range between anchor and lead)+
     * then deselect range between anchor and index for single selection
     * mode this implies: false true* irrelevant ? x if (anchor selected)
     * select index (will implicitly deselect anchor if different) else
     * deselect index (will change no selection if anchor != index) ? :
     * dependent on what the list selection model does (whether it ignores
     * the first argument to set/removeSelection- Interval altogether in
     * single selection mode) if anchor is -1, regarded as false + this
     * isn't done because it is very unintuitive. For symmetry it should be
     * done, but ListSelectionModel isn't symmetric between selected and
     * unselected indices in the first place. Indices that were never
     * selected suddenly becoming it by changing a totally different cell
     * would be strange.
     */
    private static void change(final ListSelectionModel selection,
        final int index, final boolean toggle, final boolean extend,
        final boolean selected, final boolean anchorSelected) {
      if (toggle) {
        if (extend) {
          selection.setAnchorSelectionIndex(index);
        } else {
          if (selected) {
            selection.removeSelectionInterval(index, index);
          } else {
            selection.addSelectionInterval(index, index);
          }
        }
      } else {
        if (extend) {
          final int anchor = selection.getAnchorSelectionIndex();

          if (anchor == -1) {
            if (selected) {
              selection.removeSelectionInterval(index, index);
            } else {
              selection.addSelectionInterval(index, index);
            }
          } else {
            int lead = selection.getLeadSelectionIndex();

            if (lead == -1) {
              lead = index;
            }

            if (anchorSelected) {
              final boolean old = selection.getValueIsAdjusting();

              selection.setValueIsAdjusting(true);

              // This has some redundant tests, but minimizes
              // the number of potentially expensive calls to
              // remove-
              // SelectionInterval.

              if (anchor <= index) {
                if (index < lead) {
                  selection.removeSelectionInterval(
                    index + 1, lead);
                } else if (lead < anchor) {
                  selection.removeSelectionInterval(lead,
                    anchor - 1);
                }
              }
              if (index <= anchor) {
                if (lead < index) {
                  selection.removeSelectionInterval(lead,
                    index - 1);
                } else if (anchor < lead) {
                  selection.removeSelectionInterval(
                    anchor + 1, lead);
                }
              }

              selection.addSelectionInterval(anchor, index);

              selection.setValueIsAdjusting(old);
            } else {
              selection.removeSelectionInterval(anchor, index);
            }
          }
        } else {
          selection.setSelectionInterval(index, index);
        }
      }
    }
  }

  private boolean processKeyBindingImpl(final KeyStroke k, final KeyEvent e,
      final int condition, final boolean pressed) {
    if (isEnabled()) {
      final Object binding = getInputMap(condition).get(k);

      if (binding == null) {
        return false;
      }

      final Action a = getActionMap().get(binding);

      if (a != null) {
        return SwingUtilities.notifyAction(a, k, e, this, e
          .getModifiers());
      }
    }

    return false;
  }

  @Override
  protected boolean processKeyBinding(final KeyStroke k, final KeyEvent e,
      final int condition, final boolean pressed) {
    if ((condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) && !isEditing()) {
      return processKeyBindingImpl(k, e, condition, pressed);
    } else {
      return super.processKeyBinding(k, e, condition, pressed);
    }
  }

  // cf. Thread 'DefaultCellEditor key handling' in comp.lang.java.gui.
  // postpone starting the editor after all other parties have been
  // given chance to handle/consume the event.
  @Override
  protected void processKeyEvent(final KeyEvent e) {
    super.processKeyEvent(e);

    if (!e.isConsumed() && !isEditing()) {
      final KeyStroke k = KeyStroke.getKeyStrokeForEvent(e);

      if (super.processKeyBinding(k, e,
        WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
        e.getID() == KeyEvent.KEY_PRESSED)) {
        e.consume();
      }
    }
  }

  private void readObject(final ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();

    // If these are null, things are broken anyway.

    if (dataModel != null) {
      dataModel.addTableModelListener(listener());
    }

    if (selectionModel != null) {
      selectionModel.addListSelectionListener(listener());
    }

    if (columnModel != null) {
      columnModel.addColumnModelListener(listener());
    }

    if (cellEditor != null) {
      cellEditor.addCellEditorListener(listener());
    }
  }

  private Listener listener() {
    if (listener == null) {
      listener = createListener();
    }

    return listener;
  }

  private Listener createListener() {
    return new Listener();
  }

  // Delegates methods back to JTable, but is not Serializable.
  private class Listener implements TableModelListener,
      TableColumnModelListener, ListSelectionListener, CellEditorListener {
    @Override
    public void tableChanged(final TableModelEvent e) {
      JTable2.this.tableChanged(e);
    }

    @Override
    public void columnAdded(final TableColumnModelEvent e) {
      JTable2.this.columnAdded(e);
    }

    @Override
    public void columnRemoved(final TableColumnModelEvent e) {
      JTable2.this.columnRemoved(e);
    }

    @Override
    public void columnMoved(final TableColumnModelEvent e) {
      JTable2.this.columnMoved(e);
    }

    @Override
    public void columnMarginChanged(final ChangeEvent e) {
      JTable2.this.columnMarginChanged(e);
    }

    @Override
    public void columnSelectionChanged(final ListSelectionEvent e) {
      JTable2.this.columnSelectionChanged(e);
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
      JTable2.this.valueChanged(e);
    }

    @Override
    public void editingStopped(final ChangeEvent e) {
      JTable2.this.editingStopped(e);
    }

    @Override
    public void editingCanceled(final ChangeEvent e) {
      JTable2.this.editingCanceled(e);
    }
  }
}
