/*
 * Created on Nov 10, 2003
 */
package raiser.util;

import java.io.File;
import java.net.*;

import javax.swing.JApplet;

/**
 * @author raiser
 */
public class Resources extends JApplet
{
    /**
     *  Get an url for a relativePath. If the current application is an applet
     * tries to resolve path in the current jar, or using codeBase.
     *  If the application is stand-alone then the relativePath is from current
     * directory.
     * @param relativePath
     * @return
     */
    public static URL getResource(String path)
    {
        return getBaseDir(path);
    }
    private static URL getBaseDir(String path)
    {
        URL baseDir = null;
        if (baseDir == null)
        {
            baseDir = getBaseDirFromSystemResources(path);
        }
        if (baseDir == null)
        {
            baseDir = getBaseDirFromLocalDirectory(path);
        }
        return baseDir;
    }
    private static URL getBaseDirFromSystemResources(String path)
    {
        if(!path.startsWith("/"))
        {
            path = "/"+path;
        }
        return Resources.class.getResource(path);
    }
    private static URL getBaseDirFromLocalDirectory(String path)
    {
        URL baseDir = null;
        try
        {
            if (path.startsWith("/"))
            {
                path = path.substring(1);
            }
            baseDir = new URL("file:/" + new File(path).getAbsolutePath());
        }
        catch (java.security.AccessControlException e)
        {
        }
        catch (MalformedURLException e)
        {
        }
        return baseDir;
    }
    public static void main(String[] args)
    {
    }
    public void init()
    {
    }
}
