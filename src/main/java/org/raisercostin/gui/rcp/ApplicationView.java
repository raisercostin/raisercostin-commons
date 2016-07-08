/*
 * Created on Jun 8, 2005
 */
package org.raisercostin.gui.rcp;

import org.raisercostin.gui.Menuable;

public interface ApplicationView extends Menuable {
	void setController(ApplicationController controller);

	void onJobStart(String message);

	void onJobStart(int size);

	void onJobFinished();

	void onJobAdvanceOneStep();

	void onModelChanged();

	void init();

	void setStatusInfo(String message);

	void setStatusWarn(String message);

	void setStatusError(String message);
}
