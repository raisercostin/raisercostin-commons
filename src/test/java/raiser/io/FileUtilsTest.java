/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {
	public void testFileRename() throws IOException {
		final String path = "target/temp/";
		new File(path).mkdir();
		final String oldFileName = "temptest.something";
		final String newFileName = oldFileName.toUpperCase();
		FileUtils.delete(path + oldFileName, false);
		FileUtils.create(path + oldFileName);
		FileUtils.rename(path + oldFileName, path + newFileName);
		final String allPath = FileUtils.parsePath(new File(path)
				.getAbsolutePath());
		// assertEquals(allPath+"/"+path+newFileName,FileUtils.getRealPath(path+oldFileName));
		// assertNotSame(allPath+"/"+path+oldFileName,FileUtils.getRealPath(path+oldFileName));
		assertEquals(allPath + "\\temp\\" +oldFileName, new File(path+"/"
				+ oldFileName).getAbsolutePath());
	}

	public void testFileRename2() throws IOException {
		final String path = "target/temp/";
		new File(path).mkdir();
		new File("target/temp/SOMETHING").createNewFile();
		assertEquals("SOMETHING", new File("target/temp/something")
				.getCanonicalFile().getName());
	}
}
