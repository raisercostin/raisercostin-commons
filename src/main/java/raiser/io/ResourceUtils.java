package raiser.io;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ResourceUtils {
	public static void visit(File file, FileVisitor resourceVisitor)
			throws IOException {
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

	public static void visit(Resource resource,
			final ResourceVisitor resourceVisitor) throws IOException {
		visit(resource.getFile(), new FileVisitor() {
			public void visit(File file) throws IOException {
				resourceVisitor.visit(new FileSystemResource(file));
			}
		});
	}
}
