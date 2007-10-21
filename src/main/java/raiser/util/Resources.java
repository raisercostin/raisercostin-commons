/*
 * Created on Nov 10, 2003
 */
package raiser.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;

/**
 * @author raiser
 */
public class Resources extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1967495532014762035L;

	/**
	 * Get an url for a relativePath. If the current application is an applet
	 * tries to resolve path in the current jar, or using codeBase. If the
	 * application is stand-alone then the relativePath is from current
	 * directory.
	 * 
	 * @param relativePath
	 * @return
	 */
	public static URL getResource(final String path) {
		return getBaseDir(path);
	}

	private static URL getBaseDir(final String path) {
		URL baseDir = null;
		if (baseDir == null) {
			baseDir = getBaseDirFromSystemResources(path);
		}
		if (baseDir == null) {
			baseDir = getBaseDirFromLocalDirectory(path);
		}
		return baseDir;
	}

	private static URL getBaseDirFromSystemResources(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return Resources.class.getResource(path);
	}

	private static URL getBaseDirFromLocalDirectory(String path) {
		URL baseDir = null;
		try {
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			baseDir = new URL("file:/" + new File(path).getAbsolutePath());
		} catch (final java.security.AccessControlException e) {
		} catch (final MalformedURLException e) {
		}
		return baseDir;
	}

	public static void main(final String[] args) {
	}

	@Override
	public void init() {
	}
}
