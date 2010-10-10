/*
 * Created on Jun 8, 2005
 */
package raiser.gui.rcp;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import raiser.gui.Console;

public class Platform {
	public static void start(final ApplicationController controller,
			final ApplicationModel model, final ApplicationView view) {
		try {
			controller.setView(view);
			controller.setModel(model);
			controller.setSplashVisible(true);
			controller.initModel();
			controller.initView();
			controller.loadProperties();
			controller.setConsole(new Console(model.getApplicationName(), model
					.getApplicationPreferredWidth(), model
					.getApplicationPreferredHeight(), true));
			controller.startApplication();
			controller.join();
			controller.saveProperties();
			controller.setSplashVisible(false);
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	public static void setWindowsLookAndFeel() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	}

	public static void setDefaultLookAndFeel() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());
	}
}
