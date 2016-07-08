/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.2 $
 *     $Author: raisercostin $
 *       $Date: 2004/08/01 20:55:44 $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.raisercostin.io.FileUtils;


/**
 * @author: Costin Emilian GRIGORE
 */
public class URIUtil {
	public static String uriToAbsoluteFile(String uri) throws IOException,
			URISyntaxException {
		if (!isFileURI(uri)) {
			return uri;
		}
		String result = "";
		if (isRelative(uri)) {
			uri = uri.substring(10);
			if (uri.length() > 0) {
				uri = FileUtils.getFileSeparator() + uri;
			}
			result = new File("").getAbsolutePath() + uri;
		} else {
			result = new File(new URI(uri)).getAbsolutePath();
		}
		return result;
	}

	private static boolean isRelative(final String uri) {
		return isFileURI(uri) && uri.startsWith("file:///:/");
	}

	private static boolean isFileURI(final String uri) {
		return uri.startsWith("file://");
	}

	public static String fileToURI(final String file) throws URISyntaxException {
		if (isRelativeFile(file)) {
			return relativeFileToURI(file);
		}
		if (isAbsoluteFile(file)) {
			return absoluteFileToURI(file);
		}
		throw new URISyntaxException(file, "Invalid file name.");
	}

	private static boolean isAbsoluteFile(final String file) {
		return file.substring(1, 2).equals(":" + FileUtils.getFileSeparator())
				|| file.substring(0, 1).equals(FileUtils.getFileSeparator());
	}

	private static boolean isRelativeFile(final String file) {
		return !isAbsoluteFile(file);
	}

	public static String relativeFileToURI(final String file) {
		return "file:///:/" + file;
	}

	public static String absoluteFileToURI(final String file) {
		return "file:///" + file;
	}
}
