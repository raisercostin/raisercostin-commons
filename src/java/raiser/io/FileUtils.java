/*
 * Created on Jan 16, 2004
 */
package raiser.io;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author raiser
 */
public class FileUtils {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(FileUtils.class);

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
    public static String getPath(String path) {
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
    public static void copy(String sourceFileName, String destinationFileName) throws IOException {
        copy(sourceFileName, destinationFileName, true);
    }
    public static void copy(String sourceFileName, String destinationFileName,
            boolean createDirectoriesIfNecessary) throws IOException {
        subfile(sourceFileName, destinationFileName, createDirectoriesIfNecessary, 0, -1);
    }
    /**
     * An url is < <protocol>>:// < <path with / separators>>/ <<file>>
     *
     * @param destinationFileName
     * @return
     */
    public static String parsePath(String url) {
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
    public static boolean makedir(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                logger.debug("Path [" + extractCanonicalName(file) + "] is already created.");
                return true;
            }
            if (file.isDirectory()) {
                logger.debug("Path [" + extractCanonicalName(file)
                        + "] can't be created, a file with the same name already exists.");
                return false;
            }
        }
        String pathString = extractCanonicalName(file);
        if (logger.isDebugEnabled()) {
            logger.debug("makedir(" + pathString + ")");
        }
        boolean result = file.mkdirs();
        if (logger.isDebugEnabled()) {
            logger.debug("makedir(" + pathString + ")=>" + result);
        }
        return result;
    }
    private static String extractCanonicalName(File file) {
        String pathString = null;
        try {
            pathString = file.getCanonicalPath();
        } catch (IOException e) {
            pathString = file.getAbsolutePath();
        }
        return pathString;
    }
    /**
     * On some JVMs there is a bug in delete method.
     */
    public static boolean delete(File file) throws IOException {
        return delete(file, 1000, 10, true);
    }
    /**
     * On some JVMs there is a bug in delete method.
     */
    public static boolean delete(String fileName) throws IOException {
        return delete(new File(fileName), 1000, 10, true);
    }
    public static boolean delete(String fileName, boolean errorIfNotExists) throws IOException {
        return delete(new File(fileName), 100, 10, errorIfNotExists);
    }
    /**
     * On some JVMs there is a bug in delete method.
     */
    static boolean delete(File file, int timeout, int tries, boolean errorIfNotExists)
            throws IOException {
        try {
            if (!file.exists()) {
                if (errorIfNotExists)
                    throw new IOException("File " + file.getAbsolutePath()
                            + " doesn't exist. Can't delete non existent files.");
                else {
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
                    + (result ? " was deleted." : " was NOT deleted. I tried " + tries
                            + " times x " + +pause + " miliseconds."));
            return result;
        } catch (InterruptedException e) {
            throw new IOException("The process of deleting file " + file.getAbsolutePath()
                    + " was interrupted.");
        }
    }
    /**
     * Checks if directory chosen exists and is writable
     */
    private static boolean checkTempLocation(String dir) {
        java.io.File test = new java.io.File(dir);
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
    public static String getTempFile(String id, String near) throws IOException {
        return getTempFile(id, new File(near)).getCanonicalPath();
    }
    /**
     * Create a uniquely named temporary file of the form XXXnnnnn.tmp.
     *
     * @param id
     *            a string prepended on the file generated. Should you fail to
     *            delete it later, the id will help identify where it came from.
     *            null and "" also allowed.
     * @param near
     *            Directory to create file in. Can also be a file, then
     *            temporary file is created in same directory. If null, one of
     *            these locations is used (sorted by preference; : is used as
     *            path.separator, / as file.separator ): 1) /tmp 2) /var/tmp 3)
     *            c:/temp 4) c:/windows/temp 5) / 6) current directory
     * @return a temporary File with a unique name of the form XXXnnnnn.tmp.
     */
    public static java.io.File getTempFile(String id, java.io.File near) throws java.io.IOException {
        String prepend = (id != null) ? id : "";
        // Find location for temp file
        String temp_loc = null;
        if (near != null) {
            if (near.isDirectory()) {
                temp_loc = near.getAbsolutePath();
            } else {
                java.io.File tmp = new java.io.File(near.getAbsolutePath());
                temp_loc = tmp.getParent();
            }
        }
        if ((near == null) || (near != null && checkTempLocation(temp_loc) == false)) {
            String pathSep = System.getProperty("path.separator");
            String fileSep = System.getProperty("file.separator");
            if (checkTempLocation(fileSep + "tmp") == true) {
                temp_loc = fileSep + "tmp";
            } else if (checkTempLocation(fileSep + "var" + fileSep + "tmp") == true) {
                temp_loc = fileSep + "var" + fileSep + "tmp";
            } else if (checkTempLocation("c" + pathSep + fileSep + "temp") == true) {
                temp_loc = "c" + pathSep + fileSep + "temp";
            } else if (checkTempLocation("c" + pathSep + fileSep + "windows" + fileSep + "temp") == true) {
                temp_loc = "c" + pathSep + fileSep + "windows" + fileSep + "temp";
            } else if (checkTempLocation(fileSep) == true) {
                temp_loc = fileSep;
            } else if (checkTempLocation(".") == true) {
                temp_loc = ".";
            } else {
                // give up
                throw new java.io.IOException("Could not find directory for temporary file");
            }
        }
        java.util.Random wheel = new java.util.Random(); // seeded from the
        // clock
        java.io.File tempFile = null;
        do {
            // generate random a number 10,000 .. 99,999
            int unique = ((wheel.nextInt() & Integer.MAX_VALUE) % 90000) + 10000;
            tempFile = new java.io.File(temp_loc, prepend + Integer.toString(unique) + ".tmp");
        } while (tempFile.exists());
        new java.io.FileOutputStream(tempFile).close();
        // debugging peek at the name generated.
        if (false) {
            System.out.println(tempFile.getCanonicalPath());
        }
        return tempFile;
    }
    public static void rename(File srcFile, File dstFile, boolean checkDestination,
            boolean deleteDestination) throws IOException {
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
                throw new IOException("Can't delete source file[" + srcFile.getAbsolutePath()
                        + "].");
            }
        }
    }
    private static boolean exists(File dstFile, boolean caseSensitive) throws IOException {
        boolean result = dstFile.exists();
        if (caseSensitive && result) {
            result = dstFile.getCanonicalPath().equals(dstFile.getAbsolutePath());
        }
        return result;
    }
    private static boolean renameFixed(File srcFile, File dstFile) {
        boolean result = true;
        //windows case sensitive rename
        if (SystemUtils.isWindows()
                && srcFile.getAbsolutePath().equalsIgnoreCase(dstFile.getAbsolutePath())) {
            File dstFile2 = new File(dstFile.getAbsolutePath() + "tempdfwaetw2134213rwefsda");
            result &= srcFile.renameTo(dstFile2);
            result &= dstFile2.renameTo(dstFile);
        } else {
            result = srcFile.renameTo(dstFile);
        }
        return result;
    }
    /**
     * Rename srcFile to dstFile. If can't rename because srcFile must be moved
     * copies srcFile to dstFile and then deletes srcFile.
     *
     * @param srcFile
     * @param dstFile
     * @throws IOException
     */
    public static void rename(File srcFile, File dstFile) throws IOException {
        rename(srcFile, dstFile, false, false);
    }
    public static byte[] readFile(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        in.read(buffer);
        in.close();
        return buffer;
    }
    public static byte[] readFile(String htmlFile) throws IOException {
        return readFile(new File(htmlFile));
    }
    public static String getCurrentDirectory() {
        String result = new File(".").getAbsolutePath();
        return result.substring(0, result.length() - 1);
    }
    public static void subfile(String sourceFileName, String destinationFileName,
            boolean createDirectoriesIfNecessary, long from, long to) throws IOException {
        if (createDirectoriesIfNecessary) {
            makedir(parsePath(destinationFileName));
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(sourceFileName));
            out = new BufferedOutputStream(new FileOutputStream(destinationFileName));
            byte str[] = new byte[1024];
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
    public static void rename(String temp, String fileName, boolean checkDestination,
            boolean deleteDestination) throws IOException {
        rename(new File(temp), new File(fileName), checkDestination, deleteDestination);
    }
    public static void rename(String temp, String fileName) throws IOException {
        rename(new File(temp), new File(fileName));
    }
    public static void create(String file, byte[] data) throws IOException {
        FileOutputStream f = new FileOutputStream(file);
        f.write(data);
        f.close();
    }
    public static String getRealPath(final String fileName) throws IOException {
        final String name = parseFileName(fileName);
        final String dir2 = parsePath(new File(fileName).getAbsolutePath()) + "\\";
        File dir = parseDirectory(new File(fileName));
        String[] result = dir.list(new FilenameFilter() {
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
        //the system is case sensitive
        for (int i = 0; i < result.length; i++) {
            if (result[i].equals(name)) {
                return dir2 + fileName;
            }
        }
        throw new IOException("File [" + fileName
                + "] doesn't exists. But some file with other case exists: ["
                + Arrays.asList(result) + "]");
    }
    private static String parseFileName(String url) {
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
    public static String parseDirectory(String fileName) {
        return parseDirectory(new File(fileName)).getAbsolutePath();
    }
    public static File parseDirectory(File file) {
        if (file.isDirectory()) {
            return file;
        }
        return file.getParentFile();
    }
    public static boolean create(String fileName) throws IOException {
        return new File(fileName).createNewFile();
    }
    public static File[] list(String path, String regexp) {
        final Pattern p = Pattern.compile(regexp);
        return new File(path).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return p.matcher(pathname.getAbsolutePath()).matches();
            }
        });
    }
    public static String getFileName(File file) {
        return file.getName().substring(0,file.getName().lastIndexOf('.'));
    }
}
