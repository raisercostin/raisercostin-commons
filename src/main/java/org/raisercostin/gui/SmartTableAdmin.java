package org.raisercostin.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class SmartTableAdmin<T> extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5859327718732448580L;

	public SmartTableAdmin(SmartTable<?> smartTable) {
		super(new BorderLayout());
		add(new JScrollPane(smartTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);
	}

	public <T2> SmartTableAdmin(T2... objects){
		this(new SmartTable<T2>(objects));
	}
}
