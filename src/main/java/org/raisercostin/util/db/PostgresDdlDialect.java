package org.raisercostin.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.raisercostin.util.DBUtils;
import org.raisercostin.util.PlayUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PostgresDdlDialect extends AbstractDdlDialect implements DdlDialect {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PostgresDdlDialect.class);

	@Override
	@JsonIgnore
	public String getDialect() {
		return "org.hibernate.dialect.PostgreSQLDialect";
	}

	public PostgresDdlDialect() {
		super("org.postgresql.Driver");
	}

	@Override
	@JsonIgnore
	public String getDriverName() {
		return "org.postgresql.Driver";
	}
	@JsonIgnore
	public String getDbaSchema() {
		return "postgres";
	}
	@Override
	public String getDbaUrl(DatabaseConfig databaseConfig) {
		return getUrl(databaseConfig, getDbaSchema());
	}
	public String getUrl(DatabaseConfig databaseConfig, String schema) {
		return "jdbc:postgresql://" + databaseConfig.getHostname() + ":" + databaseConfig.getPort() + "/" + schema;
	}

	@Override
	public String getUrl(DatabaseConfig databaseConfig) {
		return getUrl(databaseConfig, databaseConfig.getSchema());
	}
	@Override
	public void createSchema(JdbcTemplate jdbcTemplate, DatabaseConfig db) {
		String owner = db.getUsername();
		jdbcTemplate.update("CREATE DATABASE " + db.getSchema() + " WITH OWNER=" + owner
				+ " ENCODING='UTF8' CONNECTION LIMIT=-1");
	}
	@Override
	public boolean checkIfExists(JdbcTemplate t, String schema) {
		PlayUtils.dumpClassloader();
		String name = DBUtils.uniqueResult(t.query("select datname from pg_database where datname=?",
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				}, schema));
		return name != null;
	}
	@Override
	public boolean exists(DatabaseConfig db, JdbcTemplate t) {
		return checkIfExists(t, db.getSchema());
	}
	@Override
	public void dropSchemas(DatabaseConfig db, String schemaLike) {
		if (schemaLike.contains("postgres")) {
			throw new RuntimeException();
		}
		SingleConnectionDataSource parentDataSource = DBUtils.createDataSource(db);
		try {
			JdbcTemplate t = new JdbcTemplate(parentDataSource);
			List<String> databases = showSchemas(t, schemaLike);
			for (String database : databases) {
				LOG.info("drop database " + database);
				t.update("drop database " + database);
			}
		} finally {
			parentDataSource.destroy();
		}
	}
	@Override
	protected List<String> showSchemas(JdbcTemplate t, String schemaLike) {
		return t.query("select datname from pg_database where datname like ?", new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		}, schemaLike);
	}

	@Override
	public List<String> showTables(DatabaseConfig db, String tableLike) {
		throw new NotImplementedException("");
	}
}
