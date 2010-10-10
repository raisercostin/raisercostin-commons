package org.raisercostin.utils.hibernate.dialect;

import org.hibernate.dialect.MySQL5InnoDBDialect;

public class MySQL5InnoDBUtf8Dialect extends MySQL5InnoDBDialect {

	@Override
	public String getTableTypeString() {
		return " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin";
	}
}
