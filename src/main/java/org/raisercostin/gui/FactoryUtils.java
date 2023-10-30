/*
 * Created on Nov 30, 2003
 */
package org.raisercostin.gui;

import javax.swing.AbstractButton;
import javax.swing.Action;

/**
 * @author org.raisercostin
 */
public class FactoryUtils {
  public static AbstractButton createAction(final Class<?> classType,
      final Action action) {
    AbstractButton result;
    try {
      result = (AbstractButton) classType.newInstance();
      if (result == null) {
        return null;
      }
      result.setAction(action);
      return result;
    } catch (final InstantiationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (final IllegalAccessException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
