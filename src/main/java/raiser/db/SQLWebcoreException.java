/*
 * Created on Jan 17, 2004
 */
package raiser.db;

import java.sql.SQLException;

/**
 * @author raiser
 */
public class SQLWebcoreException extends SQLException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8145890352768127704L;

	SQLException e;

	public SQLWebcoreException(SQLException e) {
		this.e = e;
	}

	public SQLWebcoreException() {
		e = new SQLException();
	}

	public SQLWebcoreException(String reason) {
		e = new SQLException(reason);
	}

	public SQLWebcoreException(String reason, String SQLState) {
		e = new SQLException(reason, SQLState);
	}

	public SQLWebcoreException(String reason, String SQLState, int vendorCode) {
		e = new SQLException(reason, SQLState, vendorCode);
	}

	private static void addMessage(StringBuffer result, int pos, String message) {
		result.append(Integer.toString(pos));
		result.append(".\t");
		result.append(message);
		result.append("\n");
	}

	public String getDetailedMessage() {
		StringBuffer result = new StringBuffer("\n");
		int i = 0;
		SQLException ex = e;
		while (ex != null) {
			addMessage(result, i, ex.getMessage());
			ex = ex.getNextException();
			i++;
		}
		return result.toString();
	}

	@Override
	public String getMessage() {
		return getDetailedMessage();
	}

	@Override
	public int getErrorCode() {
		return e.getErrorCode();
	}

	@Override
	public SQLException getNextException() {
		return e.getNextException();
	}

	@Override
	public String getSQLState() {
		return e.getSQLState();
	}

	@Override
	public synchronized void setNextException(SQLException ex) {
		e.setNextException(ex);
	}

}
