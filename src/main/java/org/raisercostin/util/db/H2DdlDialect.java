package org.raisercostin.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.raisercostin.util.DBUtils;
import org.raisercostin.util.PropertyUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;

/** @see http://www.h2database.com/html/grammar.html - for h2 grammar. */
public class H2DdlDialect extends AbstractDdlDialect implements DdlDialect {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(H2DdlDialect.class);
	private static final Dialect dialect = new H2Dialect();
	private static final String EESSI_DB_H2_DIR = configure();

	private static String configure() {
		return PropertyUtils.configure("EESSI_DB_H2_DIR", "~/_default-db/", new Function<String, String>() {
			@Override
			@Nullable
			public String apply(@Nullable String value) {
				return value;
			}
		}, "");
	}

	public H2DdlDialect() {
		super("org.h2.Driver");
	}

	@Override
	@JsonIgnore
	public String getDialect() {
		return "org.hibernate.dialect.H2Dialect";
	}

	@Override
	@JsonIgnore
	public String getDriverName() {
		return "org.h2.Driver";
	}

	@Override
	public String getUrl(DatabaseConfig databaseConfig) {
		return "jdbc:h2:" + EESSI_DB_H2_DIR + databaseConfig.getSchema();
	}

	@Override
	public String getDbaUrl(DatabaseConfig databaseConfig) {
		return "jdbc:h2:" + EESSI_DB_H2_DIR + databaseConfig.getSchema();
	}

	@Override
	public void createSchema(JdbcTemplate jdbcTemplate, DatabaseConfig db) {
		String owner = db.getUsername();
		// CREATE SCHEMA IF NOT EXISTS TEST_SCHEMA AUTHORIZATION SA
		jdbcTemplate.update("CREATE SCHEMA IF NOT EXISTS " + quote(db.getSchema()) + " AUTHORIZATION " + quote(owner));
	}
	private String quote(String name) {
		return dialect.quote(name);
	}

	@Override
	public boolean checkIfExists(JdbcTemplate t, String schema) {
		String name = DBUtils.uniqueResult(showSchemas(t, schema));
		return name != null;
	}

	@Override
	protected List<String> showSchemas(JdbcTemplate t, String schema) {
		return t.query("select schema_name from information_schema.schemata where schema_name like ?",
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				}, schema.toUpperCase());
	}
	@Override
	public boolean exists(DatabaseConfig db, JdbcTemplate t) {
		return checkIfExists(t, db.getSchema());
	}
	@Override
	public void dropSchemas(DatabaseConfig db, String schemaLike) {
		if (schemaLike.toLowerCase().equals("information_schema")) {
			throw new RuntimeException("You can't delete schema [information_schema].");
		}
		SingleConnectionDataSource parentDataSource = DBUtils.createDataSource(db);
		try {
			JdbcTemplate t = new JdbcTemplate(parentDataSource);
			List<String> databases = showSchemas(t, schemaLike);
			for (String database : databases) {
				LOG.info("drop schema " + database);
				t.update("drop schema " + database);
			}
		} finally {
			parentDataSource.destroy();
		}
	}

	@Override
	public List<String> showTables(DatabaseConfig db, String tableLike) {
		SingleConnectionDataSource parentDataSource = DBUtils.createDataSource(db);
		JdbcTemplate t = new JdbcTemplate(parentDataSource);
		return t.query("select table_name from information_schema.tables where table_schema=? and table_name like ?",
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				}, "PUBLIC" // db.getSchema().toUpperCase()
				, "%" + tableLike.toUpperCase() + "%");
	}
}
