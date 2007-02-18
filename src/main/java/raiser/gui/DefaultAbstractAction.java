/*
 * Created on Nov 30, 2003
 */
package raiser.gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public abstract class DefaultAbstractAction extends AbstractAction {
	public DefaultAbstractAction(String text, ImageIcon icon, String desc,
			Integer mnemonic) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
	}
}
