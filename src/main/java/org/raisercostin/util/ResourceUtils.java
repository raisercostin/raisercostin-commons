package org.raisercostin.util;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.Objects;

import org.springframework.core.io.*;

public class ResourceUtils {
	public static Path toPath(Resource resource) {
		try {
			return toPath(resource.getURL());
		} catch (IOException e) {
			throw new RuntimeException("Can't convert " + resource + " to a nio Path.", e);
		}
	}

	public static Path toPath(URL url) {
		try {
			Objects.requireNonNull(url, "Resource URL cannot be null");
			URI uri = url.toURI();

			String scheme = uri.getScheme();
			if (scheme.equals("file")) {
				return Paths.get(uri);
			}

			if (!scheme.equals("jar")) {
				throw new IllegalArgumentException("Cannot convert to Path: " + uri);
			}

			String s = uri.toString();
			int separator = s.indexOf("!/");
			String entryName = s.substring(separator + 2);
			URI fileURI = URI.create(s.substring(0, separator));

			try (FileSystem fs = FileSystems.newFileSystem(fileURI, Collections.<String, Object> emptyMap())) {
				return fs.getPath(entryName);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException("Can't convert URL " + url + " to a nio Path.", e);
		} catch (IOException e) {
			throw new RuntimeException("Can't convert URL " + url + " to a nio Path.", e);
		}
	}

	public static Resource createClassPathJarResource(String file, String entry) {
		try {
			return new UrlResource(new URL("jar:"+ new ClassPathResource(file).getURI() + "!/" + entry));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Resource get5MResource() {
		return createClassPathJarResource("xmls_sizes.zip", "Sickness.ClaimKind-1-1-5MB.xml");
	}

	@Deprecated
	// still to find 20 mb
	public static Resource get20MResource() {
		return createClassPathJarResource("xmls_sizes.zip", "Sickness.ClaimKind-1-1-50MB.xml");
	}

	public static Resource get50MResource() {
		return createClassPathJarResource("xmls_sizes.zip", "Sickness.ClaimKind-1-1-50MB.xml");
	}

	public static Resource get100MResource() {
		return createClassPathJarResource("xmls_sizes.zip", "Sickness.ClaimKind-1-1-100MB.xml");
	}

	public static Resource get200MResource() {
		return createClassPathJarResource("xmls_sizes.zip", "Sickness.ClaimKind-1-1-200MB.xml");
	}

	@Deprecated
	// still to find 500 mb
	public static Resource get500MResource() {
		return createClassPathJarResource("xmls_sizes.zip", "Sickness.ClaimKind-1-1-200MB.xml");
	}
}
