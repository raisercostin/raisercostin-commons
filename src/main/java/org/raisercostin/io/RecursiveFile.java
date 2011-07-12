/*
 * Created on Jan 16, 2004
 */
package org.raisercostin.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.raisercostin.util.JobController;


/**
 * @author org.raisercostin
 */
public class RecursiveFile extends File {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5277183192180381782L;

	private JobController controller;

	/**
	 * @param pathname
	 */
	public RecursiveFile(final String pathname) {
		super(pathname);
	}

	public RecursiveFile(final String pathname, final JobController controller) {
		super(pathname);
		this.controller = controller;
	}

	/**
	 * @param uri
	 */
	public RecursiveFile(final URI uri) {
		super(uri);
	}

	/**
	 * @param parent
	 * @param child
	 */
	public RecursiveFile(final File parent, final String child) {
		super(parent, child);
	}

	/**
	 * @param parent
	 * @param child
	 */
	public RecursiveFile(final String parent, final String child) {
		super(parent, child);
	}

	private void list(final List<File> v, final File directory) {
		v.add(directory);
		final File[] files = directory.listFiles();
		if (files == null) {
			return;
		}
		for (final File element : files) {
			if (!canContinueRunning()) {
				break;
			}
			if (element.isFile()) {
				v.add(element);
			} else {
				list(v, element);
			}
		}
	}

	private boolean canContinueRunning() {
		return (controller == null) || controller.canContinueRunning();
	}

	public String[] listRecursively() {
		final List<File> ss = new Vector<File>();
		list(ss, this);
		final String[] result = new String[ss.size()];
		for (int i = 0; i < result.length; i++) {
			if (!canContinueRunning()) {
				break;
			}
			try {
				result[i] = (ss.get(i)).getCanonicalPath();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String[] listRecursively(final FilenameFilter filter) {
		final List<File> ss = new Vector<File>();
		list(ss, this);
		final ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < ss.size(); i++) {
			if (!canContinueRunning()) {
				break;
			}
			if ((filter == null) || filter.accept(this, (ss.get(i)).getName())) {
				try {
					v.add(ss.get(i).getCanonicalPath());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return (v.toArray(new String[0]));
	}

	public File[] listFilesRecursively() {
		final List<File> ss = new Vector<File>();
		list(ss, this);
		return ss.toArray(new File[0]);
	}

	public File[] listFilesRecursively(final FileFilter filter) {
		final List<File> ss = new Vector<File>();
		list(ss, this);
		final ArrayList<File> v = new ArrayList<File>();
		for (int i = 0; i < ss.size(); i++) {
			if (!canContinueRunning()) {
				break;
			}
			if ((filter == null) || filter.accept(ss.get(i))) {
				v.add(ss.get(i));
			}
		}
		return (v.toArray(new File[0]));
	}

	public File[] listFilesRecursively(final FilenameFilter filter) {
		final List<File> ss = new Vector<File>();
		list(ss, this);
		final ArrayList<File> v = new ArrayList<File>();
		for (int i = 0; i < ss.size(); i++) {
			if (canContinueRunning()) {
				break;
			}
			if ((filter == null) || filter.accept(this, (ss.get(i)).getName())) {
				v.add(ss.get(i));
			}
		}
		return (v.toArray(new File[0]));
	}

	/** @deprecated Use FileUtils.getFileSeparator() instead. */
	@Deprecated
	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	/** @deprecated Use FileUtils.getPathSeparator() instead. */
	@Deprecated
	public static String getPathSeparator() {
		return System.getProperty("path.separator");
	}

	/** @deprecated Use FileUtils.getLineSeparator() instead. */
	@Deprecated
	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * @deprecated Use FileUtils.getPath() instead.
	 * @return
	 */
	@Deprecated
	public static String getPath(final String path) {
		return FileUtils.getPath(path);
	}

}
