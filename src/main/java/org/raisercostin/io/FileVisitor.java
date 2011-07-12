package org.raisercostin.io;

import java.io.File;
import java.io.IOException;

public interface FileVisitor {
	void visit(File file) throws IOException;
}
