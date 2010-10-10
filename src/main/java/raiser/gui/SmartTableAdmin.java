package raiser.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class SmartTableAdmin<T> extends JPanel {
	public SmartTableAdmin(SmartTable smartTable) {
		super(new BorderLayout());
		add(new JScrollPane(smartTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);
	}

	public <T> SmartTableAdmin(T... objects) throws InstantiationException, IllegalAccessException {
		this(new SmartTable<T>(objects));
	}
}
