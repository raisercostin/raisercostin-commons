/*
 * Created on Jun 7, 2005
 */
package org.raisercostin.gui.rcp;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.*;

import org.raisercostin.gui.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultApplicationController implements ApplicationController {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultApplicationController.class);

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
			aboutDialog = getAboutOptionPane().createDialog(console.getFrame(),
					"About mp3renamer");
			aboutDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
			aboutOptionPane
					.setMessageType(javax.swing.JOptionPane.INFORMATION_MESSAGE);
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

	@Override
	public void fireShowAboutDialog() {
		getAboutDialog().setVisible(true);
	}

	@Override
	public void fireActionExit() {
		console.exit();
	}

	public void fireCloseAboutDialog() {
		if (getAboutDialog() != null) {
			getAboutDialog().dispose();
		}
	}

	@Override
	public void setConsole(final Console console) {
		this.console = console;
		console.setConfirmOnExit(true);
	}

	@Override
	public void setModel(final ApplicationModel model) {
		this.model = model;
		model.setController(this);
	}

	@Override
	public void setView(final ApplicationView view) {
		this.view = view;
		view.setController(this);
	}

	@Override
	public void startApplication() {
		setSplashVisible(false);
		console.show(view);
	}

	JWindow splash;

	Frame frame = new Frame();

	JWindow getSplash() {
		if (splash == null) {
			splash = Console.createSplash(frame, model.getVersionHTML(),
					"luckmedia.powerd.png");
		}
		return splash;
	}

	@Override
	public void setSplashVisible(final boolean visible) {
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

	@Override
	public void saveProperties() throws IOException {
		model.saveProperties();
	}

	@Override
	public void join() throws InterruptedException {
		console.join();
	}

	@Override
	public void loadProperties() {
		final Thread t = new Thread() {
			@Override
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

	public void fireJobStart(final String message) {
		view.onJobStart(message);
	}

	public void fireJobStart(final int size) {
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

	@Override
	public void firePropertiesLoaded() {
		model.onPropertiesLoaded();
	}

	@Override
	public void initModel() {
		model.init();
	}

	@Override
	public void initView() {
		view.init();
	}

	@Override
	public void setStatusInfo(final String message) {
		view.setStatusInfo(message);
	}

	@Override
	public void setStatusWarn(final String message) {
		view.setStatusWarn(message);
	}

	@Override
	public void setStatusError(final String message) {
		view.setStatusError(message);
	}
}
