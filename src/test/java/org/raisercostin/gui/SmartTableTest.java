/*******************************************************************************
 * *****************************************************************************
 * $Logfile: $ $Revision: $ $Author: $ $Date: $ $NoKeywords: $
 ******************************************************************************/
package org.raisercostin.gui;

import java.util.*;

import junit.framework.TestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

/**
 * @author org.raisercostin
 */
public class SmartTableTest extends TestCase {
	/*
	 * @see TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		table = new SmartTable<Task>(new Task(false, "Task 0", "?", 0),
						new Task(true, "Task 1", "gigel", 20),
						new Task(false, "Task 2", "?", 0),
						new Task(false, "Task 3", "costin", 40),
						new Task(true, "Task 4", "?", 50),
						new Task(false, "Task 5", "nana", 0),
						new Task(false, "Task 6", "baby", 100));

		final List<Task> data = new ArrayList<Task>();
		data.add(new Task(false, "Task 0", "?", 0));
		data.add(new Task(true, "Task 1", "gigel", 20));
		data.add(new Task(false, "Task 2", "?", 0));
		data.add(new Task(false, "Task 3", "costin", 40));
		data.add(new Task(true, "Task 4", "?", 50));
		data.add(new Task(false, "Task 5", "nana", 0));
		data.add(new Task(false, "Task 6", "baby", 100));
		table2 = new SmartTable<Task>(data);

		table3 = new SmartTable<Task>(Arrays.asList(new String[] { "completed",
				"description" }), data);
	}

	public void testGui1() throws InterruptedException {
		Console.run(table);
		table.addData(new Task(false, "Task new", "eu", 0));
		Console.run(table);
	}

	public void testGui2() throws InterruptedException {
		Console.run(table2);
	}

	public void testGui3() throws InterruptedException {
		Console.run(table3);
	}

	public void testRows() {
		assertEquals(7, table.getRowCount());
	}

	public void testColumns() {
		assertEquals(6, table.getColumnCount());
	}

	public void testAdd() {
		assertEquals(7, table.getRowCount());
		table.addData(new Task(false, "Task new", "eu", 0));
		assertEquals(8, table.getRowCount());
	}

	SmartTable<Task> table;

	SmartTable<Task> table2;

	SmartTable<Task> table3;
}
