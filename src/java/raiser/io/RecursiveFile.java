/*
 * Created on Jan 16, 2004
 */
package raiser.io;

import java.io.*;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.List;
import raiser.util.JobController;

/**
 * @author raiser
 */
public class RecursiveFile extends File
{
    private JobController controller;

    /**
     * @param pathname
     */
    public RecursiveFile(String pathname)
    {
        super(pathname);
    }
    public RecursiveFile(String pathname, JobController controller)
    {
        super(pathname);
        this.controller = controller;
    }

    /**
     * @param uri
     */
    public RecursiveFile(URI uri)
    {
        super(uri);
    }

    /**
     * @param parent
     * @param child
     */
    public RecursiveFile(File parent, String child)
    {
        super(parent, child);
    }

    /**
     * @param parent
     * @param child
     */
    public RecursiveFile(String parent, String child)
    {
        super(parent, child);
    }

    private void list(List v, File directory)
    {
        v.add(directory);
        File[] files = directory.listFiles();
        if (files == null)
        {
            return;
        }
        for (int i = 0; i < files.length; i++)
        {
            if(!canContinueRunning())
            {
                break;
            }
            if (files[i].isFile())
            {
                v.add(files[i]);
            }
            else
            {
                list(v, files[i]);
            }
        }
    }
    private boolean canContinueRunning() {
        return controller==null  || controller.canContinueRunning();
    }

    public String[] listRecursively()
    {
        List ss = new Vector();
        list(ss, this);
        String[] result = new String[ss.size()];
        for (int i = 0; i < result.length; i++)
        {
            if(!canContinueRunning())
            {
                break;
            }
            try
            {
                result[i] = ((File) ss.get(i)).getCanonicalPath();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }
    public String[] listRecursively(FilenameFilter filter)
    {
        List ss = new Vector();
        list(ss, this);
        ArrayList v = new ArrayList();
        for (int i = 0; i < ss.size(); i++)
        {
            if(!canContinueRunning())
            {
                break;
            }
            if ((filter == null)
                || filter.accept(this, ((File) ss.get(i)).getName()))
            {
                try
                {
                    v.add(((File) ss.get(i)).getCanonicalPath());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return (String[]) (v.toArray(new String[0]));
    }
    public File[] listFilesRecursively()
    {
        List ss = new Vector();
        list(ss, this);
        return (File[]) (ss.toArray(new File[0]));
    }
    public File[] listFilesRecursively(FileFilter filter)
    {
        List ss = new Vector();
        list(ss, this);
        ArrayList v = new ArrayList();
        for (int i = 0; i < ss.size(); i++)
        {
            if(!canContinueRunning())
            {
                break;
            }
            if ((filter == null) || filter.accept((File) ss.get(i)))
            {
                v.add(ss.get(i));
            }
        }
        return (File[]) (v.toArray(new File[0]));
    }
    public File[] listFilesRecursively(FilenameFilter filter)
    {
        List ss = new Vector();
        list(ss, this);
        ArrayList v = new ArrayList();
        for (int i = 0; i < ss.size(); i++)
        {
            if(canContinueRunning())
            {
                break;
            }
            if ((filter == null)
                || filter.accept(this, ((File) ss.get(i)).getName()))
            {
                v.add(ss.get(i));
            }
        }
        return (File[]) (v.toArray(new File[0]));
    }
    /**@deprecated Use FileUtils.getFileSeparator() instead.*/
    public static String getFileSeparator()
    {
        return System.getProperty("file.separator");
    }
    /**@deprecated Use FileUtils.getPathSeparator() instead.*/
    public static String getPathSeparator()
    {
        return System.getProperty("path.separator");
    }
    /**@deprecated Use FileUtils.getLineSeparator() instead.*/
    public static String getLineSeparator()
    {
        return System.getProperty("line.separator");
    }

    /**
     * @deprecated Use FileUtils.getPath() instead.
     * @return
     */
    public static String getPath(String path)
    {
        return FileUtils.getPath(path);
    }

}
