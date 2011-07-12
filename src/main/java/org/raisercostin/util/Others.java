/**
 * @author cgrigore
 * @date Jul 3, 2002
 *
 * Copyright (c) 2002 Softwin SRL, Romania, Bucharest, All Rights Reserved.
 */
package org.raisercostin.util;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author cgrigore
 */
public class Others {
	public static String getFileName(final String path, final String implicit,
			final String extension) {
		final FileDialog fd = new FileDialog(new Frame("Open"));
		fd.setDirectory(path);
		fd.setFile(implicit);
		final String fileExtension = "." + extension;
		fd.setFilenameFilter(new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				return name.endsWith(fileExtension);
			}
		});
		fd.setVisible(true);
		return fd.getDirectory() + fd.getFile();
	}
}
