/*
 * Created on Jan 16, 2004
 */
package org.raisercostin.io;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author org.raisercostin
 */
public class FileUtils {
  /**
   * Logger for this class
   */
  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

  /** File separator ("/" on Unix). */
  public static String getFileSeparator() {
    return System.getProperty("file.separator");
  }

  /** Path separator (":" on Unix). */
  public static String getPathSeparator() {
    return System.getProperty("path.separator");
  }

  /** Line separator ("\n" on Unix). */
  public static String getLineSeparator() {
    return System.getProperty("line.separator");
  }

  /**
   * @param path
   * @return
   */
  public static String getPath(final String path) {
    return path.endsWith(getFileSeparator()) ? path : path + getFileSeparator();
  }

  /**
   * Copy from <tt>source</tt> to <tt>destination</tt>
   * 
   * @param source
   *            Source file
   * @param dest
   *            Destination file (no directory!)
   */
  public static void copy(final String sourceFileName, final String destinationFileName) throws IOException {
    copy(sourceFileName, destinationFileName, true);
  }

  public static void copy(final String sourceFileName, final String destinationFileName,
      final boolean createDirectoriesIfNecessary) throws IOException {
    subfile(sourceFileName, destinationFileName, createDirectoriesIfNecessary, 0, -1);
  }

  /**
   * An url is < <protocol>>:// < <path with / separators>>/ <<file>>
   * 
   * @param destinationFileName
   * @return
   */
  public static String parsePath(final String url) {
    String separator = null;
    if (url.indexOf(getFileSeparator()) != -1) {
      separator = getFileSeparator();
    } else if (url.indexOf('/') != -1) {
      separator = "/";
    } else if (url.indexOf('\\') != -1) {
      separator = "\\";
    }
    return url.substring(0, url.lastIndexOf(separator));
  }

  /**
   * @param destinationFileName
   */
  public static boolean makedir(final String path) {
    final File file = new File(path);
    if (file.exists()) {
      if (file.isDirectory()) {
        log.debug("Path [" + extractCanonicalName(file) + "] is already created.");
        return true;
      }
      if (file.isDirectory()) {
        log.debug("Path [" + extractCanonicalName(file)
            + "] can't be created, a file with the same name already exists.");
        return false;
      }
    }
    final String pathString = extractCanonicalName(file);
    if (log.isDebugEnabled()) {
      log.debug("makedir(" + pathString + ")");
    }
    final boolean result = file.mkdirs();
    if (log.isDebugEnabled()) {
      log.debug("makedir(" + pathString + ")=>" + result);
    }
    return result;
  }

  private static String extractCanonicalName(final File file) {
    String pathString = null;
    try {
      pathString = file.getCanonicalPath();
    } catch (final IOException e) {
      log.info("ignored", e);
      pathString = file.getAbsolutePath();
    }
    return pathString;
  }

  /**
   * On some JVMs there is a bug in delete method.
   */
  public static boolean delete(final File file) throws IOException {
    return delete(file, 1000, 10, true);
  }

  /**
   * On some JVMs there is a bug in delete method.
   */
  public static boolean delete(final String fileName) throws IOException {
    return delete(new File(fileName), 1000, 10, true);
  }

  public static boolean delete(final String fileName, final boolean errorIfNotExists) throws IOException {
    return delete(new File(fileName), 100, 10, errorIfNotExists);
  }

  /**
   * On some JVMs there is a bug in delete method.
   */
  static boolean delete(final File file, final int timeout, final int tries, final boolean errorIfNotExists)
      throws IOException {
    try {
      if (!file.exists()) {
        if (errorIfNotExists) {
          throw new IOException("File " + file.getAbsolutePath()
              + " doesn't exist. Can't delete non existent files.");
        } else {
          return true;
        }
      }
      if (file.delete()) {
        return true;
      }
      System.err.println("Directly delete of existing file " + file.getAbsolutePath()
          + " failed. You should consider closing all streams connected on this file.");
      boolean result = false;
      int pause = timeout / tries;
      for (int i = 0; (i < tries) && (!(result = file.delete())); i++) {
        System.gc();
        Thread.sleep(pause);
      }
      System.err.println("After some hacks the file "
          + file.getAbsolutePath()
          + (result ? " was deleted."
              : " was NOT deleted. I tried " + tries + " times x " + +pause
                  + " miliseconds."));
      return result;
    } catch (final InterruptedException e) {
      throw new IOException("The process of deleting file " + file.getAbsolutePath() + " was interrupted.", e);
    }
  }

  /**
   * Checks if directory chosen exists and is writable
   */
  private static boolean checkTempLocation(final String dir) {
    final java.io.File test = new java.io.File(dir);
    if (test.isDirectory() && test.canWrite()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param string
   * @param file2
   * @return
   * @throws IOException
   */
  public static String getTempFile(final String id, final String near) throws IOException {
    return getTempFile(id, new File(near)).getCanonicalPath();
  }

  /**
   * Create a uniquely named temporary file of the form XXXnnnnn.tmp.
   * 
   * @param id
   *            a string prepended on the file generated. Should you fail to delete it later, the id will help
   *            identify where it came from. null and "" also allowed.
   * @param near
   *            Directory to create file in. Can also be a file, then temporary file is created in same directory. If
   *            null, one of these locations is used (sorted by preference; : is used as path.separator, / as
   *            file.separator ): 1) /tmp 2) /var/tmp 3) c:/temp 4) c:/windows/temp 5) / 6) current directory
   * @return a temporary File with a unique name of the form XXXnnnnn.tmp.
   */
  public static java.io.File getTempFile(final String id,
      final java.io.File near) throws java.io.IOException {
    final String prepend = (id != null) ? id : "";
    // Find location for temp file
    String temp_loc = null;
    if (near != null) {
      if (near.isDirectory()) {
        temp_loc = near.getAbsolutePath();
      } else {
        final java.io.File tmp = new java.io.File(near
          .getAbsolutePath());
        temp_loc = tmp.getParent();
      }
    }
    if ((near == null)
        || (checkTempLocation(temp_loc) == false)) {
      final String pathSep = System.getProperty("path.separator");
      final String fileSep = System.getProperty("file.separator");
      if (checkTempLocation(fileSep + "tmp") == true) {
        temp_loc = fileSep + "tmp";
      } else if (checkTempLocation(fileSep + "var" + fileSep + "tmp") == true) {
        temp_loc = fileSep + "var" + fileSep + "tmp";
      } else if (checkTempLocation("c" + pathSep + fileSep + "temp") == true) {
        temp_loc = "c" + pathSep + fileSep + "temp";
      } else if (checkTempLocation("c" + pathSep + fileSep + "windows"
          + fileSep + "temp") == true) {
        temp_loc = "c" + pathSep + fileSep + "windows" + fileSep
            + "temp";
      } else if (checkTempLocation(fileSep) == true) {
        temp_loc = fileSep;
      } else if (checkTempLocation(".") == true) {
        temp_loc = ".";
      } else {
        // give up
        throw new java.io.IOException(
          "Could not find directory for temporary file");
      }
    }
    final java.util.Random wheel = new java.util.Random(); // seeded from
    // the
    // clock
    java.io.File tempFile = null;
    do {
      // generate random a number 10,000 .. 99,999
      final int unique = ((wheel.nextInt() & Integer.MAX_VALUE) % 90000) + 10000;
      tempFile = new java.io.File(temp_loc, prepend
          + Integer.toString(unique) + ".tmp");
    } while (tempFile.exists());
    new java.io.FileOutputStream(tempFile).close();
    return tempFile;
  }

  public static void rename(final File srcFile, final File dstFile, final boolean checkDestination,
      final boolean deleteDestination) throws IOException {
    if (checkDestination) {
      if (exists(dstFile, true)) {
        if (deleteDestination) {
          dstFile.delete();
        } else {
          throw new IOException("Can't rename. Destination exists [" + dstFile + "].");
        }
      }
    }
    if (!renameFixed(srcFile, dstFile)) {
      copy(srcFile.getAbsolutePath(), dstFile.getAbsolutePath());
      if (!delete(srcFile)) {
        throw new IOException("Can't delete source file[" + srcFile.getAbsolutePath() + "].");
      }
    }
  }

  private static boolean exists(final File dstFile, final boolean caseSensitive) throws IOException {
    boolean result = dstFile.exists();
    if (caseSensitive && result) {
      result = dstFile.getCanonicalPath().equals(dstFile.getAbsolutePath());
    }
    return result;
  }

  private static boolean renameFixed(final File srcFile, final File dstFile) {
    boolean result = true;
    // windows case sensitive rename
    if (SystemUtils.isWindows() && srcFile.getAbsolutePath().equalsIgnoreCase(dstFile.getAbsolutePath())) {
      final File dstFile2 = new File(dstFile.getAbsolutePath() + "tempdfwaetw2134213rwefsda");
      result &= srcFile.renameTo(dstFile2);
      result &= dstFile2.renameTo(dstFile);
    } else {
      result = srcFile.renameTo(dstFile);
    }
    return result;
  }

  /**
   * Rename srcFile to dstFile. If can't rename because srcFile must be moved copies srcFile to dstFile and then
   * deletes srcFile.
   * 
   * @param srcFile
   * @param dstFile
   * @throws IOException
   */
  public static void rename(final File srcFile, final File dstFile) throws IOException {
    rename(srcFile, dstFile, false, false);
  }

  public static byte[] readFile(final File file) throws IOException {
    final InputStream in = new FileInputStream(file);
    final byte[] buffer = new byte[(int) file.length()];
    in.read(buffer);
    in.close();
    return buffer;
  }

  public static byte[] readFile(final String htmlFile) throws IOException {
    return readFile(new File(htmlFile));
  }

  public static String getCurrentDirectory() {
    final String result = new File(".").getAbsolutePath();
    return result.substring(0, result.length() - 1);
  }

  public static void subfile(final String sourceFileName, final String destinationFileName,
      final boolean createDirectoriesIfNecessary, final long from, final long to) throws IOException {
    if (createDirectoriesIfNecessary) {
      makedir(parsePath(destinationFileName));
    }
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new BufferedInputStream(new FileInputStream(sourceFileName));
      out = new BufferedOutputStream(new FileOutputStream(destinationFileName));
      final byte str[] = new byte[1024];
      long index = 0;
      int nb;
      int max;
      in.skip(from);
      while ((nb = in.read(str)) >= 0) {
        if (to == -1) {
          max = nb;
        } else {
          max = (int) (to - index);
        }
        out.write(str, 0, max);
        index += nb;
      }
      out.close();
      in.close();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } finally {
        if (out != null) {
          out.close();
        }
      }
    }
  }

  public static void rename(final String temp, final String fileName, final boolean checkDestination,
      final boolean deleteDestination) throws IOException {
    rename(new File(temp), new File(fileName), checkDestination, deleteDestination);
  }

  public static void rename(final String temp, final String fileName) throws IOException {
    rename(new File(temp), new File(fileName));
  }

  public static void create(final String file, final byte[] data) throws IOException {
    final FileOutputStream f = new FileOutputStream(file);
    f.write(data);
    f.close();
  }

  public static String getRealPath(final String fileName) throws IOException {
    final String name = parseFileName(fileName);
    final String dir2 = parsePath(new File(fileName).getAbsolutePath()) + "\\";
    final File dir = parseDirectory(new File(fileName));
    final String[] result = dir.list(new FilenameFilter()
      {
        @Override
        public boolean accept(File dir, String fileName2) {
          return name.equalsIgnoreCase(fileName2);
        }
      });
    if (result.length == 0) {
      throw new IOException("File [" + fileName + "] doesn't exists.");
    }
    if (result.length == 1) {
      return dir2 + result[0];
    }
    for (final String element : result) {
      if (element.equals(name)) {
        return dir2 + fileName;
      }
    }
    throw new IOException("File [" + fileName + "] doesn't exists. But some file with other case exists: ["
        + Arrays.asList(result) + "]");
  }

  private static String parseFileName(final String url) {
    String separator = null;
    if (url.indexOf(getFileSeparator()) != -1) {
      separator = getFileSeparator();
    } else if (url.indexOf('/') != -1) {
      separator = "/";
    } else if (url.indexOf('\\') != -1) {
      separator = "\\";
    }
    return url.substring(url.indexOf(separator) + 1);
  }

  public static String parseDirectory(final String fileName) {
    return parseDirectory(new File(fileName)).getAbsolutePath();
  }

  public static File parseDirectory(final File file) {
    if (file.isDirectory()) {
      return file;
    }
    return file.getParentFile();
  }

  public static boolean create(final String fileName) throws IOException {
    return new File(fileName).createNewFile();
  }

  public static File[] list(final String path, final String regexp) {
    final Pattern p = Pattern.compile(regexp);
    return new File(path).listFiles(new FileFilter()
      {
        @Override
        public boolean accept(final File pathname) {
          return p.matcher(pathname.getAbsolutePath()).matches();
        }
      });
  }

  public static String getFileName(final File file) {
    return file.getName().substring(0, file.getName().lastIndexOf('.'));
  }

  public static String readString(final File file) throws IOException {
    final FileReader in = new FileReader(file);
    try {
      final char[] buffer = new char[(int) file.length() / 2];
      in.read(buffer);
      return new String(buffer);
    } finally {
      in.close();
    }
  }

  public static void copy(final org.springframework.core.io.Resource source, final String destination)
      throws IOException {
    copy(source.getFile().getAbsolutePath(), destination);
  }
}
