/*
 * Created on Jun 7, 2005
 */
package raiser.gui.rcp;

import java.awt.Frame;
import java.io.IOException;
import javax.swing.*;
import org.apache.log4j.Logger;
import raiser.gui.Console;

public class DefaultApplicationController implements ApplicationController {
    private static final Logger logger = Logger.getLogger(DefaultApplicationController.class);
    public JDialog aboutDialog;
    public javax.swing.JOptionPane aboutOptionPane = null;
    private Console console;
    private ApplicationModel model;
    private ApplicationView view;

    /**
     * This method initializes aboutDialog
     *
     * @return javax.swing.JDialog
     */
    public javax.swing.JDialog getAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = getAboutOptionPane().createDialog(console.getFrame(), "About mp3renamer");
            aboutDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        }
        return aboutDialog;
    }
    /**
     * This method initializes aboutOptionPane
     *
     * @return javax.swing.JOptionPane
     */
    public javax.swing.JOptionPane getAboutOptionPane() {
        if (logger.isDebugEnabled()) {
            logger.debug("getAboutOptionPane() - get");
        }
        if (aboutOptionPane == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAboutOptionPane() - create");
            }
            aboutOptionPane = new javax.swing.JOptionPane();
            aboutOptionPane.setName(model.getApplicationName());
            aboutOptionPane.setMessage(new JLabel(getVersionHTML()));
            aboutOptionPane.setMessageType(javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
        return aboutOptionPane;
    }
    public Frame getFrame() {
        return Console.getFrame(view);
    }
    public ApplicationModel getModel() {
        return model;
    }
    public String getVersionHTML() {
        return model.getVersionHTML();
    }
    public ApplicationView getView() {
        return view;
    }
    public void fireShowAboutDialog() {
        getAboutDialog().setVisible(true);
    }
    public void fireActionExit() {
        console.exit();
    }
    public void fireCloseAboutDialog() {
        if (getAboutDialog() != null) {
            getAboutDialog().dispose();
        }
    }
    public void setConsole(Console console) {
        this.console = console;
        console.setConfirmOnExit(true);
    }
    public void setModel(ApplicationModel model) {
        this.model = model;
        model.setController(this);
    }
    public void setView(ApplicationView view) {
        this.view = view;
        view.setController(this);
    }
    public void startApplication() {
        setSplashVisible(false);
        console.show(view);
    }

    JWindow splash;
    Frame frame = new Frame();

    JWindow getSplash() {
        if (splash == null) {
            splash = Console.createSplash(frame, model.getVersionHTML(), "luckmedia.powerd.png");
        }
        return splash;
    }
    public void setSplashVisible(boolean visible) {
        if (visible) {
            getSplash().setVisible(true);
        } else {
            getSplash().setVisible(false);
            getSplash().dispose();
            if (frame != null) {
                frame.dispose();
            }
            splash = null;
            frame = null;
        }
    }
    public void saveProperties() throws IOException {
        model.saveProperties();
    }
    public void join() throws InterruptedException {
        console.join();
    }
    public void loadProperties() {
        Thread t = new Thread() {
            public void run() {
                try {
                    model.loadProperties();
                } catch (IOException e) {
                    logger.error("run()", e);
                } catch (ClassNotFoundException e) {
                    logger.error("run()", e);
                }
            }
        };
        t.start();
    }
    public void fireJobStart(String message) {
        view.onJobStart(message);
    }
    public void fireJobStart(int size) {
        view.onJobStart(size);
    }
    public void fireJobFinished() {
        view.onJobFinished();
    }
    public void fireJobAdvanceOneStep() {
        view.onJobAdvanceOneStep();
    }
    public void fireModelChanged() {
        view.onModelChanged();
    }
    public void firePropertiesLoaded() {
        model.onPropertiesLoaded();
    }
    public void initModel() {
        model.init();
    }
    public void initView() {
        view.init();
    }
    public void setStatusInfo(String message) {
        view.setStatusInfo(message);
    }
    public void setStatusWarn(String message) {
        view.setStatusWarn(message);
    }
    public void setStatusError(String message) {
        view.setStatusError(message);
    }
}