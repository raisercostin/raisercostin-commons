package org.raisercostin.util;

//@formatter:off
/**
 * HACK fix classloader
 * 
 * @author costin
 * @see http://stackoverflow.com/questions/16794298/upgrade-to-playframework-2-1-1-creates-json-deserialization-problems-with-jeckso/16794883#16794883
 */
//@formatter:on
public class PlayUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PlayUtils.class);

	public static <T> void fixClassloader(Class<T> theClass) {
		fixClassloader();
	}

	private static ClassLoader fullClassloader;

	public static void fixClassloader() {
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		if (current != fullClassloader && fullClassloader != null) {
			LOG.info("replace classloader " + currentClassloader() + " with " + fullClassloader);
			Thread.currentThread().setContextClassLoader(fullClassloader);
		}
	}

	public static void storeClassLoader(ClassLoader classloader) {
		if (classloader.toString().startsWith("ReloadableClassLoader")) {
			LOG.info("store classloader " + classloader);
			fullClassloader = classloader;
		}
	}

	public static void dumpClassloader() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("classpath: " + currentClassloader().substring(0, 20));
			for (String item : currentClassloader().split(",")) {
				LOG.debug(item);
			}
		}
	}

	private static String currentClassloader() {
		if (Thread.currentThread().getContextClassLoader() == null) {
			return "No classloader on thread.";
		}
		return Thread.currentThread().getContextClassLoader().toString();
	}
}
