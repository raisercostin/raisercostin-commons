package org.raisercostin.util;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FileUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FileUtils.class);

	public static String loadFileToString(String filename) {

		return loadFileToString(new File(filename));

	}

	public static String loadFileToString(File file) {
		try (InputStream is = new FileInputStream(file)) {
			// return IOUtils.toString(is, "UTF-8");
			byte[] byteArray = IOUtils.toByteArray(is);
			char[] encoded = Base64Coder.encode(byteArray);
			String string1 = new String(encoded);
			// String string = new String(byteArray,"UTF-8");
			// LOG.info(string1);
			// LOG.info(string);
			return string1;
		} catch (IOException e) {
			throw new RuntimeException("", e);
		}
	}

	public static void saveFileFromString(String content, String filename) {
		byte[] data = Base64Coder.decode(content);
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(data);
			fos.close();
		} catch (Exception ex) {
			throw new RuntimeException("", ex);
		}
	}

	public static void saveFileFromStringNotEncoded(String content, String filename) {
		byte[] data = content.getBytes();
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(data);
			fos.close();
		} catch (Exception ex) {
			throw new RuntimeException("", ex);
		}
	}

	public static void forceMkdirForParent(File file) throws IOException {
		File parentFile = file.getParentFile();
		if (parentFile != null) {
			org.apache.commons.io.FileUtils.forceMkdir(parentFile);
		}
	}

	public static String getPathAsString(Resource resource) {
		try {
			return resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			LOG.trace("Can't getAbsolutePath from resource " + resource, e);
			return resource.toString();
		}
	}

	public static String readFromClassPath(String path) {
		try {
			return org.raisercostin.utils.FileUtils.readToString(new ClassPathResource(path), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeToClassPath(String path, String content) {
		try {
			org.raisercostin.utils.FileUtils.writeFromString(new ClassPathResource(path), content, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Tests if file (text file) was created in WinOS thus lines ends with CRLF
	 * This method will read file until sequence of \r\n is found which means in
	 * worst case scenario when there are no CRLF characters whole file will be
	 * read
	 * 
	 * @param path
	 *            ClassPathResource pointing to the text file
	 * @return
	 */
	//Please note that all buffered readers implementations read lines omitting CRLF or LF characters.
	//It is impossible to verify using buffered readers or Scanner if file was created in WinOS
	public static boolean isWindowsFile(ClassPathResource path) {
		BufferedInputStream bis = null;
		byte[] buffer = new byte[1024*1024];
		char carriage = '\r';
		char newline = '\n';
		boolean found = false;
		try {
			bis = new BufferedInputStream(path.getInputStream());
			while (bis.read(buffer) > 0 && !found) {
				for(int i = 0; i < buffer.length; i++) {
					if(buffer[i] == carriage && buffer[i+1] == newline) {
						found = true;
						break;
					}
				}
			}
			return found;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					//do nothing
				}
			}
		}
	}
}
