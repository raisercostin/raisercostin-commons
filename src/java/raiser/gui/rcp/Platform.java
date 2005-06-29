/*
 * Created on Jun 8, 2005
 */
package raiser.gui.rcp;

import java.io.IOException;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import raiser.gui.Console;

public class Platform {
    public static void start(ApplicationController controller, ApplicationModel model,
            ApplicationView view) {
        try {
            controller.setView(view);
            controller.setModel(model);
            controller.setSplashVisible(true);
            controller.initModel();
            controller.initView();
            controller.loadProperties();
            controller.setConsole(new Console(model.getApplicationName(), model
                    .getApplicationPreferredWidth(), model.getApplicationPreferredHeight(), true));
            controller.startApplication();
            controller.join();
            controller.saveProperties();
            controller.setSplashVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setWindowsLookAndFeel() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
    }

    public static void setDefaultLookAndFeel() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());
    }
}
