/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.*;
import java.io.File;
import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {
    public void testFileRename() throws IOException {
        String path = "temp\\";
        new File(path).mkdir();
        String oldFileName = "temptest.something";
        String newFileName = oldFileName.toUpperCase();
        FileUtils.delete(path+oldFileName,false);
        FileUtils.create(path+oldFileName);
        FileUtils.rename(path + oldFileName, path + newFileName);
        String allPath = FileUtils.parsePath(new File(path).getAbsolutePath());
        //assertEquals(allPath+"\\"+path+newFileName,FileUtils.getRealPath(path+oldFileName));
        //assertNotSame(allPath+"\\"+path+oldFileName,FileUtils.getRealPath(path+oldFileName));
        assertEquals(allPath + "\\" + path + oldFileName, new File(path + oldFileName)
                .getAbsolutePath());
    }

    public void testFileRename2() throws IOException {
        String path = "temp\\";
        new File(path).mkdir();
        new File("temp\\SOMETHING").createNewFile();
        assertEquals("SOMETHING",new File("temp\\something").getCanonicalFile().getName());
    }
}
