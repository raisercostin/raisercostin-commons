package org.raisercostin.util.db;

import javax.xml.bind.annotation.*;

import org.raisercostin.utils.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseConfig {
	private final String hostname;
	private final int port;
	private final String username;
	private final String password;
	private final String schema;
	private final String ddlDialectName;
	@XmlTransient
	private DdlDialect ddlDialect;

	public DatabaseConfig() {
		this.hostname = null;
		this.port = 0;
		this.username = null;
		this.password = null;
		this.schema = null;
		this.ddlDialectName = null;// = new PostgresDdlDialect()
	}
	public DatabaseConfig(String hostname, String port, String username, String password, String schema,
			String ddlDialectName) {
		this(hostname, Integer.parseInt(port), username, password, schema, ddlDialectName);
	}
	public DatabaseConfig(String hostname, String port, String username, String password, String schema,
			Class<? extends DdlDialect> ddlDialectClass) {
		this(hostname, Integer.parseInt(port), username, password, schema, ddlDialectClass.getName());
	}
	public DatabaseConfig(String hostname, int port, String username, String password, String schema,
			Class<? extends DdlDialect> ddlDialectClass) {
		this(hostname, port, username, password, schema, ddlDialectClass.getName());
	}
	public DatabaseConfig(String hostname, int port, String username, String password, String schema,
			String ddlDialectName) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.schema = schema;
		this.ddlDialectName = ddlDialectName;
	}
	@Override
	public String toString() {
		return ObjectUtils.toString(this, "password");
	}

	@JsonIgnore
	public String getId() {
		return "jdbc:postgresql://" + username + "@" + hostname + ":" + port + "/" + schema;
	}
	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSchema() {
		return schema;
	}
	@JsonIgnore
	// "com.p6spy.engine.spy.P6SpyDriver"
	public String getDriverName() {
		return getDdlDialect().getDriverName();
	}
	@JsonIgnore
	public String getUrl(String schema) {
		return "jdbc:postgresql://" + getHostname() + ":" + getPort() + "/" + schema;
	}
	@JsonIgnore
	public String getConnectionUrl() {
		return getDdlDialect().getUrl(this);
	}
	@JsonIgnore
	public String getDbaUrl() {
		return getDdlDialect().getDbaUrl(this);
	}
	@JsonIgnore
	public DdlDialect getDdlDialect() {
		if (ddlDialect == null) {
			if (ddlDialectName != null) {
				try {
					ddlDialect = (DdlDialect) Class.forName(ddlDialectName).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return ddlDialect;
	}

	public String getDdlDialectName() {
		return ddlDialectName;
	}

	@JsonIgnore
	public String getDialect() {
		return getDdlDialect().getDialect();
	}
	@JsonIgnore
	public String getUrl() {
		return getDdlDialect().getUrl(this);
	}
}
