/*
 * Created on Jun 8, 2005
 */
package org.raisercostin.gui.rcp;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultApplicationModel implements ApplicationModel {
  /**
   * Logger for this class
   */
  private static final Logger logger = LoggerFactory
    .getLogger(DefaultApplicationModel.class);

  private ApplicationController controller;

  public DefaultApplicationModel() {
    configuration = new Configuration();
  }

  @Override
  public String getVersionHTML() {
    return "<html>"
        + "Project: "
        + getApplicationUniqueName()
        + "<br>\n"
        + "Version: "
        + getApplicationVersion()
        + "<br>\n"
        + "Build: "
        + getApplicationBuild()
        + "<br>\n"
        + "BuildDate: "
        + getApplicationBuildDate()
        + "<br>\n"
        + "BuildType: "
        + getApplicationBuildType()
        + "<br>\n"
        + "(c) Copyright org.raisercostin 2004.  All rights reserved.<br>\n"
        + "<br>\n"
        + "<a href=\"http://raisercostin.no-ip.org/projects/mp3renamer\">"
        + "http://raisercostin.no-ip.org/projects/mp3renamer</a><br>\n"
        // + "<br>\n"
        // + "This product includes software developed by the<br>\n"
        // + "Apache Software Foundation http://www.apache.org/"+
        + "</html>";
  }

  @Override
  public String getApplicationBuildType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getApplicationBuildDate() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getApplicationBuild() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getApplicationVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getApplicationUniqueName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getApplicationName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getApplicationPreferredWidth() {
    return 1000;
  }

  @Override
  public int getApplicationPreferredHeight() {
    return 600;
  }

  @Override
  public void setController(final ApplicationController controller) {
    this.controller = controller;
  }

  @Override
  public void saveProperties() throws IOException {
    final OutputStream os = new FileOutputStream("configure.properties");
    configuration.save(os);
    os.close();
    logger.info("Configuration saved.");
  }

  public ApplicationController getController() {
    return controller;
  }

  private final Configuration configuration;

  @Override
  public void loadProperties() throws IOException, ClassNotFoundException {
    try (InputStream is = new FileInputStream("configure.properties");) {
      logger.info("Found configure.properties file: "
          + new File("configure.properties").getAbsolutePath());
      configuration.load(is);
    } catch (final FileNotFoundException e) {
      try (InputStream is2 = getClass().getClassLoader()
        .getResourceAsStream(
          "configure.properties")) {
        logger.info("Found configure.properties file in classpath.", e);
        configuration.load(is2);
      }
    }
    // setInputPath(getProperty("input.path", ""));
    // setInputFilter(getProperty("input.filter", ".*[.]mp3"));
    // setOutputPath(getProperty("output.path", ""));
    // setOutputFormat(getProperty("output.format", "{0}{1} - {2} - {3} -
    // {4} - {5}.{8}"));
    // setKeepOldFile(getProperty("keepOldFile", "true").equals("true"));
    // setSaveId3v1(search(getProperty("output.saveID3v1Version",
    // TagFactory.ID3V_1_NONE), getId3v1Options()));
    // setSaveId3v2(search(getProperty("output.saveID3v2Version",
    // TagFactory.ID3V_2_4_0), getId3v2Options()));
    logger.info("Configuration loaded.");
    controller.firePropertiesLoaded();
  }

  public void initProperties() {
  }

  public Object getProperty(final String key) {
    return configuration.get(key);
  }

  public Object getProperty(final String key, final Object defaultValue) {
    Object result = configuration.get(key);
    if (result == null) {
      logger.info("Can't find a value for property [" + key
          + "]. Using the default value [" + defaultValue + "]");
      result = defaultValue;
    }
    return result;
  }

  public boolean setProperty(final String key, final Object value) {
    final Object already = configuration.get(key);
    if ((already == null) && (value == null)) {
      return false;
    }
    if (value == null) {
      configuration.remove(key);
      return true;
    }
    if (value.equals(already)) {
      return false;
    }
    configuration.set(key, value);
    return true;
  }

  @Override
  public void init() {
    initProperties();
  }
}
