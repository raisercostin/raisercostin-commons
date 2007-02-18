/*
 * Created on Jan 16, 2004
 */
package raiser.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import raiser.util.JobController;

/**
 * @author raiser
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
	public RecursiveFile(String pathname) {
		super(pathname);
	}

	public RecursiveFile(String pathname, JobController controller) {
		super(pathname);
		this.controller = controller;
	}

	/**
	 * @param uri
	 */
	public RecursiveFile(URI uri) {
		super(uri);
	}

	/**
	 * @param parent
	 * @param child
	 */
	public RecursiveFile(File parent, String child) {
		super(parent, child);
	}

	/**
	 * @param parent
	 * @param child
	 */
	public RecursiveFile(String parent, String child) {
		super(parent, child);
	}

	private void list(List<File> v, File directory) {
		v.add(directory);
		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}
		for (File element : files) {
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
		List<File> ss = new Vector<File>();
		list(ss, this);
		String[] result = new String[ss.size()];
		for (int i = 0; i < result.length; i++) {
			if (!canContinueRunning()) {
				break;
			}
			try {
				result[i] = ((File) ss.get(i)).getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String[] listRecursively(FilenameFilter filter) {
		List<File> ss = new Vector<File>();
		list(ss, this);
		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < ss.size(); i++) {
			if (!canContinueRunning()) {
				break;
			}
			if ((filter == null)
					|| filter.accept(this, ((File) ss.get(i)).getName())) {
				try {
					v.add(ss.get(i).getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return (String[]) (v.toArray(new String[0]));
	}

	public File[] listFilesRecursively() {
		List<File> ss = new Vector<File>();
		list(ss, this);
		return ss.toArray(new File[0]);
	}

	public File[] listFilesRecursively(FileFilter filter) {
		List<File> ss = new Vector<File>();
		list(ss, this);
		ArrayList<File> v = new ArrayList<File>();
		for (int i = 0; i < ss.size(); i++) {
			if (!canContinueRunning()) {
				break;
			}
			if ((filter == null) || filter.accept((File) ss.get(i))) {
				v.add(ss.get(i));
			}
		}
		return (File[]) (v.toArray(new File[0]));
	}

	public File[] listFilesRecursively(FilenameFilter filter) {
		List<File> ss = new Vector<File>();
		list(ss, this);
		ArrayList<File> v = new ArrayList<File>();
		for (int i = 0; i < ss.size(); i++) {
			if (canContinueRunning()) {
				break;
			}
			if ((filter == null)
					|| filter.accept(this, ((File) ss.get(i)).getName())) {
				v.add(ss.get(i));
			}
		}
		return (File[]) (v.toArray(new File[0]));
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
	public static String getPath(String path) {
		return FileUtils.getPath(path);
	}

}
