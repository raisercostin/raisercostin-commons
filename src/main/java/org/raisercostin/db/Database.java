package org.raisercostin.db;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

//one Database per application
//one ConnectionPool per Database
public class Database {

	ConnectionPool cp = null;

	boolean detailedExceptionMessages;

	public Database(final ConnectionPool cp) {
		this.cp = cp;
		detailedExceptionMessages = true;
	}

	public Database(final HashMap<String, String> params) {
		cp = new ConnectionPool(params);
		detailedExceptionMessages = true;
	}

	public Database(final HashMap<String, String> params,
			final boolean detailedExceptionMessages) {
		cp = new ConnectionPool(params);
		this.detailedExceptionMessages = detailedExceptionMessages;
	}

	public ConnectionPool getConnectionPool() {
		return cp;
	}

	public synchronized ResultSet get(final String sid, final String rsname) {
		return ResultSetCache.get(sid, rsname);
	}

	public synchronized void set(final String sid, final ResultSet rs) {
		ResultSetCache.set(sid, rs);
	}

	public synchronized static void remove(final String sid) {
		ResultSetCache.remove(sid);
	}

	public synchronized void remove(final String sid, final String rsname) {
		// System.out.println("Removed " + sid + "-" + rsname );
		ResultSetCache.remove(sid, rsname);
	}

	public String getString(final String sql, final String field) {
		try {
			final ResultSet rs = select(sql);
			if (rs.next()) {
				return rs.getString(field);
			}

		} catch (final Exception e) {
			System.out.println("Sql exception getString(sql=<" + sql
					+ ">,field=<" + field + ">):" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public long getLong(final String sql, final String field) {
		try {
			final ResultSet rs = select(sql);
			if (rs.next()) {
				return rs.getLong(field);
			}
		} catch (final Exception e) {
			System.out.println("Sql exception getLong(sql=<" + sql
					+ ">,field=<" + field + ">):" + e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}

	public ResultSet select(final String sql) throws SQLException {
		return select(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
	}

	public ResultSet selectForUpdate(final String sql) throws SQLException {
		return select(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
	}

	private ResultSet select(final String sql, final int type, final int fetch)
			throws SQLException {
		final java.sql.Connection con = getConnectionPool().getConnection();

		try {
			final Statement stmt = con.createStatement(type, fetch);

			stmt.execute(sql);

			final ResultSet rs = stmt.getResultSet();

			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_OK);
			return rs;

		} catch (final SQLException sqle) {
			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_EX);
			sqle.setNextException(new SQLException("Sql exception select(sql=<"
					+ sql + ">,type=<" + type + ">,fetch=<" + fetch + ">)"));
			if (detailedExceptionMessages) {
				throw new SQLWebcoreException(sqle);
			} else {
				throw sqle;
			}
		}
	}

	public int update(final String sql) throws SQLException {
		final java.sql.Connection con = getConnectionPool().getConnection();
		try {
			final Statement stmt = con.createStatement();

			final int ret = stmt.executeUpdate(sql);

			stmt.close();

			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_OK);
			return ret;

		} catch (final SQLException sqle) {
			// System.out.println( "Sql exception: " + sql );
			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_EX);
			sqle.setNextException(new SQLException("Sql exception update(sql=<"
					+ sql + ">)"));
			if (detailedExceptionMessages) {
				throw new SQLWebcoreException(sqle);
			} else {
				throw sqle;
			}
		}
	}

	public Object insertAndRetrieveIdentityColumn(final String sql)
			throws SQLException {
		return insertAndRetrieveIdentityColumnForSQLServer(sql);
	}

	public Object insertAndRetrieveIdentityColumnForSQLServer(String sql)
			throws SQLException {
		final java.sql.Connection con = getConnectionPool().getConnection();
		try {
			final Statement stmt = con.createStatement();

			sql += ";SELECT SCOPE_IDENTITY();";

			stmt.execute(sql);
			Object result = null;
			while (true) {
				final ResultSet rs = stmt.getResultSet();
				if (rs != null) {
					rs.next();
					result = rs.getObject(1);
				}
				if (!stmt.getMoreResults()) {
					break;
				}
			}
			stmt.close();

			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_OK);

			return result;
		} catch (final SQLException sqle) {
			System.out.println("Sql exception: " + sql);
			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_EX);
			sqle.setNextException(new SQLException(
					"Sql exception insertAndRetrieveIdentityColumnForSQLServer(sql=<"
							+ sql + ">)"));
			if (detailedExceptionMessages) {
				throw new SQLWebcoreException(sqle);
			} else {
				throw sqle;
			}
		}
	}

	public static String toString(SQLException e) {
		final StringBuffer result = new StringBuffer("java.sql.SQLException[\n");
		int i = 0;
		while (e != null) {
			addMessage(result, i, e.getErrorCode(), e.getMessage());
			e = e.getNextException();
			i++;
		}
		result.append(']');
		return result.toString();
	}

	private static void addMessage(final StringBuffer result, final int pos,
			final int errorCode, final String message) {
		result.append(Integer.toString(pos));
		result.append(".\t#");
		result.append(errorCode);
		result.append(".\t");
		result.append(message);
		result.append("\n");
	}

	/**
	 * Make a quoted string, for using with a database. For example: -
	 * getQuotedString(null) => null - getQuotedString("car") => 'car' -
	 * getQuotedString("nike's car") => 'nike''s car'
	 * 
	 * @param value
	 *            the string
	 * @return quoted string, always a string not a null object.
	 */
	public static String getQuotedString(String value) {
		if (value == null) {
			return "null";
		}
		value = value.replaceAll("'", "''");
		return "'" + value + "'";
	}

	/**
	 * Make a quoted date, for using with a database. In the default formating
	 * suported by database. For example: - getQuotedDate(null) => null -
	 * getQuotedDate(Date("24.10.2003")) => '10.24.2003'
	 * 
	 * @param value
	 *            the date
	 * @return quoted string, always a string not a null object.
	 */
	public static String getQuotedDate(final java.util.Date value) {
		if (value == null) {
			return "null";
		}
		String result = getDate(value);
		result = result.replaceAll("'", "''");
		return "'" + result + "'";
	}

	/**
	 * Make a quoted date, for using with a database. In the default formating
	 * suported by database. For example: - getQuotedDate(null) => null -
	 * getQuotedDate(Date("24.10.2003")) => '10.24.2003'
	 * 
	 * @param value
	 *            the date
	 * @return quoted string, always a string not a null object.
	 */
	public static String getQuotedDate(final java.sql.Date value) {
		if (value == null) {
			return "null";
		}
		String result = getDate(value);
		result = result.replaceAll("'", "''");
		return "'" + result + "'";
	}

	/**
	 * Make a quoted date, for using with a database. In the default formating
	 * suported by database. For example: - getQuotedDate(null) => null -
	 * getQuotedDate(Date("24.10.2003")) => '10.24.2003'
	 * 
	 * @param value
	 *            the date
	 * @return quoted string, always a string not a null object.
	 */
	public static String getQuotedDate(final Calendar value) {
		if (value == null) {
			return "null";
		}
		String result = getDate(value);
		result = result.replaceAll("'", "''");
		return "'" + result + "'";
	}

	/**
	 * The default formating of date, suported by database. For example: -
	 * getDate(null) => null - getDate(Date("24.10.2003")) => 10.24.2003
	 * 
	 * @param value
	 *            the date
	 * @return quoted string, or null if date is null.
	 */
	public static String getDate(final java.sql.Date d) {
		if (d == null) {
			return null;
		}
		return sdf.format(d);
	}

	/**
	 * The default formating of date, suported by database. For example: -
	 * getDate(null) => null - getDate(Date("24.10.2003")) => 10.24.2003
	 * 
	 * @param value
	 *            the date
	 * @return quoted string, or null if date is null.
	 */
	public static String getDate(final java.util.Date d) {
		if (d == null) {
			return null;
		}
		return sdf.format(d);
	}

	/**
	 * The default formating of date, suported by database. For example: -
	 * getDate(null) => null - getDate(Date("24.10.2003")) => 10.24.2003
	 * 
	 * @param value
	 *            the date
	 * @return quoted string, or null if date is null.
	 */
	public static String getDate(final Calendar d) {
		if (d == null) {
			return null;
		}
		return sdf.format(d);
	}

	public static int exist(final ResultSet rs, final String colname,
			final String valname) {
		if ((rs != null) && (colname != null)) {
			int oldRow = -1;
			try {
				oldRow = rs.getRow();

				rs.beforeFirst();

				while (rs.next()) {
					final String cval = rs.getString(colname);
					if ((cval != null) && cval.equals(valname)) {
						return rs.getRow();
					}
				}
			} catch (final SQLException sqle) {
				System.out.println(sqle.getMessage());
			} finally {
				// move back on the old row
				try {
					if (oldRow != -1) {
						if (oldRow == 0) {
							rs.beforeFirst();
						} else {
							rs.absolute(oldRow);
						}

					}
				} catch (final SQLException s) {
					System.out.println(s.getMessage());
				}
			}
		}

		// 0 index of an row that doesn't have info
		return 0;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");

	/**
	 * @param where
	 * @return
	 */
	public static String getSafeWhere(final String where) {
		// TODO make a safe where
		return where;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cp == null) ? 0 : cp.hashCode());
		result = prime * result + (detailedExceptionMessages ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Database other = (Database) obj;
		if (cp == null) {
			if (other.cp != null)
				return false;
		} else if (!cp.equals(other.cp))
			return false;
		if (detailedExceptionMessages != other.detailedExceptionMessages)
			return false;
		return true;
	}
}
