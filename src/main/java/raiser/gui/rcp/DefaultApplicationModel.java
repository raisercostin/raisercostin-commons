/*
 * Created on Jun 8, 2005
 */
package raiser.gui.rcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public abstract class DefaultApplicationModel implements ApplicationModel {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(DefaultApplicationModel.class);

	private ApplicationController controller;

	public DefaultApplicationModel() {
		configuration = new Configuration();
	}

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
				+ "(c) Copyright raiser 2004.  All rights reserved.<br>\n"
				+ "<br>\n"
				+ "<a href=\"http://raisercostin.no-ip.org/projects/mp3renamer\">"
				+ "http://raisercostin.no-ip.org/projects/mp3renamer</a><br>\n"
				// + "<br>\n"
				// + "This product includes software developed by the<br>\n"
				// + "Apache Software Foundation http://www.apache.org/"+
				+ "</html>";
	}

	public String getApplicationBuildType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApplicationBuildDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApplicationBuild() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApplicationVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApplicationUniqueName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApplicationName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getApplicationPreferredWidth() {
		return 1000;
	}

	public int getApplicationPreferredHeight() {
		return 600;
	}

	public void setController(ApplicationController controller) {
		this.controller = controller;
	}

	public void saveProperties() throws IOException {
		OutputStream os = new FileOutputStream("configure.properties");
		configuration.save(os);
		os.close();
		logger.info("Configuration saved.");
	}

	public ApplicationController getController() {
		return controller;
	}

	private Configuration configuration;

	public void loadProperties() throws IOException, ClassNotFoundException {
		InputStream is = null;
		try {
			is = new FileInputStream("configure.properties");
			logger.info("Found configure.properties file: "
					+ new File("configure.properties").getAbsolutePath());
		} catch (FileNotFoundException e) {
			is = getClass().getClassLoader().getResourceAsStream(
					"configure.properties");
			logger.info("Found configure.properties file in classpath.");
		}
		configuration.load(is);
		is.close();
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

	public Object getProperty(String key) {
		return configuration.get(key);
	}

	public Object getProperty(String key, Object defaultValue) {
		Object result = configuration.get(key);
		if (result == null) {
			logger.info("Can't find a value for property [" + key
					+ "]. Using the default value [" + defaultValue + "]");
			result = defaultValue;
		}
		return result;
	}

	public boolean setProperty(String key, Object value) {
		Object already = configuration.get(key);
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

	public void init() {
		initProperties();
	}
}
