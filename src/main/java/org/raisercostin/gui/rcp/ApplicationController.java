/*
 * Created on Jun 8, 2005
 */
package org.raisercostin.gui.rcp;

import java.io.IOException;

import org.raisercostin.gui.Console;


public interface ApplicationController {
	void setModel(ApplicationModel model);

	void setView(ApplicationView view);

	void setConsole(Console console);

	void setSplashVisible(boolean b);

	void fireShowAboutDialog();

	void fireActionExit();

	void loadProperties() throws IOException;

	void startApplication();

	void join() throws InterruptedException;

	void saveProperties() throws IOException;

	void firePropertiesLoaded();

	void initModel();

	void initView();

	void setStatusInfo(String message);

	void setStatusWarn(String message);

	void setStatusError(String message);
}
