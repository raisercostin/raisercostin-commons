package org.raisercostin.io;

import java.io.IOException;

public interface ResourceVisitor {
	void visit(org.springframework.core.io.Resource file) throws IOException;
}
