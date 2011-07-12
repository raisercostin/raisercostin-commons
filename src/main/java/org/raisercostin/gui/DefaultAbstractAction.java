/*
 * Created on Nov 30, 2003
 */
package org.raisercostin.gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public abstract class DefaultAbstractAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5347665895354947197L;

	public DefaultAbstractAction(final String text, final ImageIcon icon,
			final String desc, final Integer mnemonic) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
	}
}
