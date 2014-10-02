package org.raisercostin.util;

import java.util.List;

import org.raisercostin.util.db.DatabaseConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class DBUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DBUtils.class);

	public static void createSchemaIfNotExists(DatabaseConfig db) {
		SingleConnectionDataSource parentDataSource = createDataSource(db);
		try {
			JdbcTemplate t = new JdbcTemplate(parentDataSource);
			boolean schemaExists = schemaExists(db, t);
			if (!schemaExists) {
				createSchema(db, t);
			} else {
				LOG.info("create database [" + db.getSchema() + "] ... already exists");
			}
		} finally {
			parentDataSource.destroy();
		}
	}

	public static void createSchema(DatabaseConfig db) {
		SingleConnectionDataSource parentDataSource = createDataSource(db);
		JdbcTemplate t = new JdbcTemplate(parentDataSource);
		createSchema(db, t);
	}

	public static boolean exists(DatabaseConfig db) {
		SingleConnectionDataSource parentDataSource = createDataSource(db);
		JdbcTemplate t = new JdbcTemplate(parentDataSource);
		return db.getDdlDialect().exists(db, t);
	}

	private static void createSchema(DatabaseConfig db, JdbcTemplate t) {
		LOG.info("create database [" + db.getSchema() + "] ...");
		db.getDdlDialect().createSchema(t, db);
		LOG.info("create database [" + db.getSchema() + "] done.");
	}

	public static SingleConnectionDataSource createDataSource(DatabaseConfig db) {
		return new SingleConnectionDataSource(db.getDbaUrl(), db.getUsername(), db.getPassword(), true);
	}

	private static boolean schemaExists(DatabaseConfig db, JdbcTemplate t) {
		LOG.trace("Check if schema exists " + db);
		return db.getDdlDialect().checkIfExists(t, db.getSchema());
	}

	public static void dropDatabases(DatabaseConfig db, String schemaLike) {
		LOG.info("drop databases like [" + schemaLike + "] ...");
		db.getDdlDialect().dropSchemas(db, schemaLike);
		LOG.info("drop databases like [" + schemaLike + "] done.");
	}

	public static <T> T uniqueResult(List<T> result) {
		if (result.size() == 0) {
			return null;
		}
		if (result.size() > 1) {
			throw new RuntimeException("List has " + result.size() + " and not 0 or 1 :" + result);
		}
		return result.get(0);
	}
	//
	// public <T> T findFirstResult(String queryString, Object... values) {
	// List<T> result = ht.find(queryString, values);
	// if (result.size() == 0) {
	// return null;
	// }
	// return result.get(0);
	// }

	public static void createDb(DatabaseConfig db) {
		createSchemaIfNotExists(db);
	}

	public static List<String> showSchemas(DatabaseConfig db, String schemaLike) {
		return db.getDdlDialect().showSchemas(db, schemaLike);
	}

	public static List<String> showTables(DatabaseConfig db, String tableLike) {
		return db.getDdlDialect().showTables(db, tableLike);
	}

	public static void dropDatabase(DatabaseConfig db) {
		dropDatabases(db, db.getSchema());
	}
}
