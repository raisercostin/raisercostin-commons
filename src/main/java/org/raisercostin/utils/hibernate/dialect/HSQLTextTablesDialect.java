package org.raisercostin.utils.hibernate.dialect;

import org.hibernate.dialect.HSQLDialect;

public class HSQLTextTablesDialect extends HSQLDialect{
	@Override
	public String getCreateTableString() {
		return "create text table";
	}
}
