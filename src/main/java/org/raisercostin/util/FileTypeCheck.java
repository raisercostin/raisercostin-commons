package org.raisercostin.util;

import java.io.File;

import org.apache.commons.lang3.NotImplementedException;

public class FileTypeCheck {

	public boolean IsExecutableExtension(String filename) {
		return filename.endsWith(".exe") || filename.endsWith(".msi");
	}

	public boolean IsExecutable(File file) {
		throw new NotImplementedException("");
	}

}
