package org.raisercostin.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class FileUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public static void cleanDirectory(Resource directory) throws IOException {
		cleanDirectory(directory.getFile());
	}

	/**
	 * Cleans a directory. Creates it if is necessary.
	 * 
	 * @param directory
	 * @throws IOException
	 */
	public static final void cleanDirectory(File directory) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Clean folder [" + directory.getAbsolutePath() + "].");
		}
		if (directory.exists()) {
			org.apache.commons.io.FileUtils.cleanDirectory(directory);
		} else {
			forceMkdir(directory);

		}
	}

	public static File forceMkdir(String folderName) throws IOException {
		return forceMkdir(new File(folderName));
	}

	public static File forceMkdir(File directory) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Create folder [" + directory.getAbsolutePath()
					+ "] if doesn't exists.");
		}
		org.apache.commons.io.FileUtils.forceMkdir(directory);
		return directory;
	}

	/**
	 * Moves all files from sourceFolder to destinationFolder, by renaming them.
	 * 
	 * @param sourceFolder
	 * @param destinationFolder
	 * @return the list of files moved into destinationFolder
	 * @throws IOException
	 */
	public static List<File> moveContent(Resource sourceFolder,
			Resource destinationFolder) throws IOException {
		File[] files = sourceFolder.getFile().listFiles();
		return moveContent(Arrays.asList(files), destinationFolder);
	}

	// public static List<File> moveContent(List<File> files, Resource
	// destinationFolder)
	// throws IOException{
	// return moveContent((File[])files.toArray(new File[0]),
	// destinationFolder);
	// }

	/**
	 * Moves all files to destinationFolder, by renaming them. If it fails on
	 * some point, tries to revert the change, and move back the files.
	 * 
	 * @param sourceFolder
	 * @param destinationFolder
	 * @return the list of files moved into destinationFolder
	 * @throws IOException
	 */
	public static List<File> moveContent(List<File> files,
			Resource destinationFolder) throws IOException {
		List<File> result = new ArrayList<File>();
		Map<File, File> movedFiles = new HashMap<File, File>();
		try {
			for (File file : files) {
				if (file.isFile()) {
					File destFile = new File(getFileName(destinationFolder,
							file));
					if (forceRename(file, destFile)) {
						result.add(destFile);
						movedFiles.put(destFile, file);
					}
				}
			}
		} catch (IOException e) {
			moveContent(movedFiles);
			throw e;
		} catch (RuntimeException e) {
			moveContent(movedFiles);
			throw e;
		}
		return result;
	}

	/**
	 * Move files defined as key into files defined in value.
	 * 
	 * @param movedFiles
	 */
	public static void moveContent(Map<File, File> movedFiles) {
		for (Map.Entry<File, File> entryFile : movedFiles.entrySet()) {
			try {
				forceRename(entryFile.getKey(), entryFile.getValue());
			} catch (IOException e) {
				logger.warn("Couldn't rename from ["
						+ entryFile.getKey().getAbsolutePath() + "] to ["
						+ entryFile.getValue().getAbsolutePath() + "].", e);
			}
		}
	}

	/**
	 * Archive files from the set.
	 * 
	 * @param movedFiles
	 */
	public static void archiveContent(Set<File> archiveFiles, File destFolder)
			throws IOException {
		byte[] b = new byte[512];
		for (File file : archiveFiles) {
			String archiveName = file.getName() + ".zip";
			ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(
					new File(destFolder, archiveName)));
			InputStream in = new FileInputStream(file);
			ZipEntry e = new ZipEntry(file.getName());
			zout.putNextEntry(e);
			int len = 0;
			while ((len = in.read(b)) != -1) {
				zout.write(b, 0, len);
			}
			zout.closeEntry();
			zout.close();
			in.close();
			// org.apache.commons.io.FileUtils.copyFile(file, new
			// File(destFolder, file.getName()));
		}

	}

	public static void cleanup(List<File> result) {
		for (File file : result) {
			try {
				org.apache.commons.io.FileUtils.forceDelete(file);
			} catch (IOException e) {
				logger.warn(
						"Couldn't remove [" + file.getAbsolutePath() + "].", e);
			}
		}
	}

	public static String getFileName(Resource folder, File file)
			throws IOException {
		return folder.getFile().getAbsolutePath() + File.separatorChar
				+ file.getName();
	}

	public static String readToString(Resource resource, String encoding)
			throws IOException {
		if (resource instanceof UrlResource) {
			return getUrlResourceAsString((UrlResource) resource, encoding);
		} else {
			return org.apache.commons.io.FileUtils.readFileToString(
					resource.getFile(), encoding);
		}
	}

	private static String getUrlResourceAsString(UrlResource urlRes,
			String encoding) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStream in = null;
		try {
			in = urlRes.getInputStream();
			Reader reader = new InputStreamReader(in, encoding);
			int c;
			while ((c = reader.read()) != -1) {
				sb.append((char) c);
			}
		} finally {
			try {
				in.close();
			} catch (Exception exc) {
			}
		}

		return sb.toString();
	}

	public static String readFromClasspathResourceToString(
			ClassPathResource resource, String encoding, String lineEnding)
			throws IOException {
		@SuppressWarnings("unchecked")
		List<String> lines = org.apache.commons.io.IOUtils.readLines(
				resource.getInputStream(), encoding);
		String result = "";
		for (String line : lines) {
			result += line + lineEnding;
		}
		return result;
	}

	public static void writeFromString(Resource resource, String data,
			String encoding) throws IOException {
		getParentFile(resource.getFile()).mkdirs();
		org.apache.commons.io.FileUtils.writeStringToFile(resource.getFile(),
				data, encoding);
	}

	public static void copyResourceToFolder(Resource srcResource, File destFile)
			throws IOException {
		if (srcResource.getURL().getProtocol().equals("file")) {
			org.apache.commons.io.FileUtils.copyFile(srcResource.getFile(),
					new File(destFile, srcResource.getFilename()));
		} else {
			copyResourceFromJar(srcResource, destFile);
		}
	}

	/**/
	private static void copyResourceFromJar(Resource res, File destinationFolder)
			throws IOException {
		BufferedInputStream in = new BufferedInputStream(res.getInputStream());
		File f = new File(destinationFolder.getCanonicalFile() + "/"
				+ res.getFilename());
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(f));

		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}

		try {
			in.close();
			out.close();
		} catch (IOException e) {
		}
	}

	public static File getParentFile(File file) throws IOException {
		String path = file.getCanonicalPath();
		int lastIndexOf = path.lastIndexOf(File.separator, path.length() - 2);
		if (lastIndexOf == -1) {
			return null;
		}
		return new File(path.substring(0, lastIndexOf));
	}

	public static boolean forceRename(File srcFile, File destFile)
			throws IOException {
		boolean renamed = srcFile.renameTo(destFile);
		if (!renamed) {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
			renamed = srcFile.delete();
			throw new IOException("Couldn't rename ["
					+ srcFile.getAbsolutePath() + "] to ["
					+ destFile.getAbsolutePath() + "].");
		}
		return renamed;
	}

	public static void renameFolder(File srcFolder, File destFolder)
			throws IOException {
		org.apache.commons.io.FileUtils.moveDirectoryToDirectory(srcFolder,
				destFolder, true);
	}

	public static String readLine(Resource resource, int lineNumber)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				resource.getInputStream()));
		List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		int i = 0;
		while ((line != null) && (i < lineNumber)) {
			list.add(line);
			line = reader.readLine();
			i++;
		}
		if (i == lineNumber) {
			return line;
		}
		return null;
	}
}
