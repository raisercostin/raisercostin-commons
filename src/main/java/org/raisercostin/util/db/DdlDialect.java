package org.raisercostin.util.db;

import java.util.List;

import javax.xml.bind.annotation.*;

import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@XmlSeeAlso({ H2DdlDialect.class, PostgresDdlDialect.class })
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public interface DdlDialect {
	String getDriverName();
	boolean checkIfExists(JdbcTemplate jdbcTemplate, String schema);
	String getDbaUrl(DatabaseConfig databaseConfig);
	void createSchema(JdbcTemplate jdbcTemplate, DatabaseConfig db);
	boolean exists(DatabaseConfig db, JdbcTemplate t);
	void dropSchemas(DatabaseConfig db, String schemaLike);
	List<String> showSchemas(DatabaseConfig db, String schemaLike);
	String getDialect();
	String getUrl(DatabaseConfig databaseConfig);
	List<String> showTables(DatabaseConfig db, String tableLike);
	void createSchema(DatabaseConfig db);
}
