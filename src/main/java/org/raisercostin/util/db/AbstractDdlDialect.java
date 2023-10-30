package org.raisercostin.util.db;

import java.util.List;

import org.raisercostin.util.DBUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public abstract class AbstractDdlDialect implements DdlDialect {
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractDdlDialect.class);

  public AbstractDdlDialect(String driverName) {
    try {
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Can't find driver " + driverName, e);
    }
  }

  @Override
  public final List<String> showSchemas(DatabaseConfig db, String schemaLike) {
    SingleConnectionDataSource parentDataSource = DBUtils.createDataSource(db);
    JdbcTemplate t = new JdbcTemplate(parentDataSource);
    List<String> databases = showSchemas(t, schemaLike);
    return databases;
  }

  protected abstract List<String> showSchemas(JdbcTemplate t, String schema);

  @Override
  public void createSchema(DatabaseConfig db) {
    LOG.info("create schema [" + db.getSchema() + "]");
    SingleConnectionDataSource parentDataSource = DBUtils.createDataSource(db);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(parentDataSource);
    createSchema(jdbcTemplate, db);
  }
}
