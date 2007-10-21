/**
 * @author cgrigore
 * @date Jul 3, 2002
 *
 * Copyright (c) 2002 Softwin SRL, Romania, Bucharest, All Rights Reserved.
 */
package raiser.util;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author cgrigore
 */
public class Others {
	public static String getFileName(String path, String implicit,
			String extension) {
		FileDialog fd = new FileDialog(new Frame("Open"));
		fd.setDirectory(path);
		fd.setFile(implicit);
		final String fileExtension = "." + extension;
		fd.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(fileExtension);
			}
		});
		fd.setVisible(true);
		return fd.getDirectory() + fd.getFile();
	}
}