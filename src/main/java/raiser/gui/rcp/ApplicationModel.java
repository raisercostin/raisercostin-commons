/*
 * Created on Jun 8, 2005
 */
package raiser.gui.rcp;

import java.io.IOException;

public interface ApplicationModel {
    String getApplicationName();

    int getApplicationPreferredWidth();

    int getApplicationPreferredHeight();

    String getApplicationBuildType();

    String getApplicationBuildDate();

    String getApplicationBuild();

    String getApplicationVersion();

    String getApplicationUniqueName();

    String getVersionHTML();

    void setController(ApplicationController controller);

    void saveProperties() throws IOException;

    void loadProperties() throws IOException, ClassNotFoundException;

    void init();

    void onPropertiesLoaded();
}
