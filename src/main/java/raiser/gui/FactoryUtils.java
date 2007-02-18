/*
 * Created on Nov 30, 2003
 */
package raiser.gui;

import javax.swing.AbstractButton;
import javax.swing.Action;

/**
 * @author raiser
 */
public class FactoryUtils {
	public static AbstractButton createAction(Class classType, Action action) {
		AbstractButton result;
		try {
			result = (AbstractButton) classType.newInstance();
			result.setAction(action);
			return result;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
