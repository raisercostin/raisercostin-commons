package raiser.io;

import java.io.File;

public class ResourceUtils {
	public static void visit(File file, ResourceVisitor resourceVisitor) {
		if (file.isDirectory()) {
			for (File file2 : file.listFiles()) {
				if (file.isDirectory()) {
					visit(file2, resourceVisitor);
				} else {
					resourceVisitor.visit(file2);
				}
			}
		} else if (file.isFile()) {
			resourceVisitor.visit(file);
		} else {
			throw new RuntimeException("The resource ["
					+ file.getAbsoluteFile()
					+ "] is nor a directory nor a file.");
		}
	}
}
