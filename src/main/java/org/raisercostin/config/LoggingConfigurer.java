package org.raisercostin.config;

import static org.apache.log4j.helpers.FileWatchdog.DEFAULT_DELAY;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * <p>
 * Class that configures the Log4j logging system (does not support configuration file in a jar).
 * </p>
 * <p>
 * Define this class as your first Spring bean and it will take care of setting up logging.
 * </p>
 * The class depends on {@link ResourceFinder ResourceFinder} to find a resource depending on profile, host, username.
 * <p/>
 */
public class LoggingConfigurer implements InitializingBean {
	private static final String HOSTNAME = "HOSTNAME";

	private static final String DEFAULT_PROPERTIES = "log4j.properties";

	private static final String INITIAL_DEFAULT_PROPERTIES = "initial-log4j.properties";

	private static boolean initialized;

	private String protocolAndPath = "classpath:";

	private String configurationFileName = DEFAULT_PROPERTIES;

	private String initialConfigurationFileName = INITIAL_DEFAULT_PROPERTIES;

	private long watchInterval = DEFAULT_DELAY;

	private boolean watchForChanges = true;

	private ResourceFinder resourceFinder;

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	/**
	 * Initialize the log4j system. <p/> By default the log4j system is configured from a log4j.properties located at
	 * the root of the classpath. However, you may override the location using the {@link #setProtocolAndPath(String)}
	 * and {@link #setConfigurationFileName(String)} methods. <p/> Similarly, the default is to periodically check the
	 * configuration file for changes. You may change this behavior using the {@link #setWatchForChanges(boolean)} and
	 * {@link #setWatchInterval(long)} methods.
	 * 
	 * @throws IllegalStateException
	 *             If the configuration file could not be found.
	 * @throws IOException
	 *             if the properties file is a file system resource and could not be found
	 */
	public synchronized void init() throws IOException {
		if (!initialized) {
			ClassPathResource initialLog4jResource = new ClassPathResource(initialConfigurationFileName);
			PropertyConfigurator.configure(initialLog4jResource.getURL());
			Logger LOG = Logger.getLogger(LoggingConfigurer.class);
			LOG.info("Initialized log4j from " + initialLog4jResource + " meaning [" + initialLog4jResource.getURL()
					+ "].");

			// initialize the log4j system
			Resource resource = resourceFinder.findResource(configurationFileName.trim(), false);
			if (isWatchForChanges() && (resource instanceof FileSystemResource)) {
				PropertyConfigurator.configureAndWatch(resource.getFile().getCanonicalPath(), getWatchInterval());
			} else {
				PropertyConfigurator.configure(resource.getURL());
			}

			// log initialization status
			Logger LOG2 = Logger.getLogger(LoggingConfigurer.class);
			LOG2.info("Initialized log4j from " + resource);
			if (isWatchForChanges() && (resource instanceof FileSystemResource)) {
				LOG2.info("Watching for changes every " + getWatchInterval() + " ms");
			}
			initialized = true;
		}
	}

	/**
	 * Get the file name of the log4j configuration file. <p/> <code>log4j.properties</code>
	 * 
	 * @return The file name of the log4j configuration file.
	 */
	public String getConfigurationFileName() {
		return configurationFileName;
	}

	/**
	 * Set the file name of the log4j configuration file.
	 * 
	 * @param configurationFileName
	 *            The file name of the log4j configuration file.
	 */
	public void setConfigurationFileName(String configurationFileName) {
		this.configurationFileName = configurationFileName;
	}

	/**
	 * Get the protocol used to load the log4j configuration file. Defaults to "classpath:".
	 * 
	 * @return The protocol used to load the configuration file.
	 * @see PathMatchingResourcePatternResolver
	 */
	public String getProtocolAndPath() {
		return protocolAndPath;
	}

	/**
	 * Set the protocol and path used to load the log4j configuration file. Use "classpath:" to load from the classpath
	 * or "file:" to load from the file system.
	 * 
	 * @param protocolAndPath
	 *            The protocol used to load the configuration file.
	 * @see PathMatchingResourcePatternResolver
	 */
	public void setProtocolAndPath(String protocolAndPath) {
		this.protocolAndPath = protocolAndPath;
	}

	/**
	 * Get whether or not to watch the log4j configuration file for changes.
	 * 
	 * @return Whether or not to watch for changes.
	 */
	public boolean isWatchForChanges() {
		return watchForChanges;
	}

	/**
	 * Set whether or not to watch the log4j configuration file for changes. Default is true.
	 * 
	 * @param watchForChanges
	 *            Whether or not to watch for changes.
	 */
	public void setWatchForChanges(boolean watchForChanges) {
		this.watchForChanges = watchForChanges;
	}

	/**
	 * Get the interval of time in milliseconds between periodic checks for changes to the log4j configuration file.
	 * 
	 * @return The interval in milliseconds to wait between each check.
	 */
	public long getWatchInterval() {
		return watchInterval;
	}

	/**
	 * Set the interval in milliseconds to periodically check the log4j configuration file for changes. Default is
	 * {@link FileWatchdog#DEFAULT_DELAY}.
	 * 
	 * @param watchInterval
	 *            The interval in milliseconds to wait between each check.
	 * @see FileWatchdog
	 */
	public void setWatchInterval(long watchInterval) {
		this.watchInterval = watchInterval;
	}

	@Required
	public void setResourceFinder(ResourceFinder resourceFinder) {
		this.resourceFinder = resourceFinder;
	}

	public void setInitialConfigurationFileName(String initialConfigurationFileName) {
		this.initialConfigurationFileName = initialConfigurationFileName;
	}
}
