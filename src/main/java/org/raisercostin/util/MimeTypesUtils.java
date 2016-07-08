package org.raisercostin.util;

import java.io.File;
import java.net.URLConnection;
import java.nio.file.Path;

import org.springframework.core.io.Resource;

public class MimeTypesUtils {

	public static String getMimeType(File file) {
		String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());
		if (mimeType == null) {
			return "application/octet-stream";
		}
		return mimeType;
		// Function0<String> f = new AbstractFunction0<String>() {
		// @Override
		// public String apply() {
		// return "application/octet-stream";
		// }
		// };
		// return MimeTypes.forFileName(file.getAbsolutePath()).getOrElse(f);
	}

	public static String getMimeType(Resource resource) {
		try {
			String mimeType = URLConnection.getFileNameMap().getContentTypeFor(
					convert(resource).getFileName().toString());
			if (mimeType == null) {
				return "application/octet-stream";
			}
			return mimeType;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// Function0<String> f = new AbstractFunction0<String>() {
		// @Override
		// public String apply() {
		// return "application/octet-stream";
		// }
		// };
		// return MimeTypes.forFileName(resource.getFilename()).getOrElse(f);
	}

	private static Path convert(Resource resource) {
		return ResourceUtils.toPath(resource);
	}
}
