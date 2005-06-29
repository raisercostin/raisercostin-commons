package raiser.db;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

//one Database per application
//one ConnectionPool per Database
public class Database
{

	ConnectionPool cp = null;
    boolean detailedExceptionMessages; 

    public Database(ConnectionPool cp)
    {
        this.cp = cp;
        this.detailedExceptionMessages = true;
    }
    public Database(HashMap params)
    {
        cp = new ConnectionPool(params);
        this.detailedExceptionMessages = true;
    }
    public Database(HashMap params, boolean detailedExceptionMessages)
    {
        cp = new ConnectionPool(params);
        this.detailedExceptionMessages = detailedExceptionMessages;
    }

	public ConnectionPool getConnectionPool()
	{
		return cp;
	}

	public synchronized ResultSet get(String sid, String rsname)
	{
		return ResultSetCache.get(sid, rsname);
	}

	public synchronized void set(String sid, ResultSet rs)
	{
		ResultSetCache.set(sid, rs);
	}

	public synchronized static void remove(String sid)
	{
		ResultSetCache.remove(sid);
	}

	public synchronized void remove(String sid, String rsname)
	{
		//System.out.println("Removed " + sid + "-" + rsname );
		ResultSetCache.remove(sid, rsname);
	}

	public String getString(String sql, String field)
	{
		try
		{
			ResultSet rs = select(sql);
			if (rs.next())
				return rs.getString(field);

		}
		catch (Exception e)
		{
            System.out.println("Sql exception getString(sql=<" + sql+">,field=<"+field+">):"+e.getMessage());            
			e.printStackTrace();
		}
		return null;
	}

	public long getLong(String sql, String field)
	{
		try
		{
			ResultSet rs = select(sql);
			if (rs.next())
				return rs.getLong(field);
		}
		catch (Exception e)
		{
            System.out.println("Sql exception getLong(sql=<" + sql+">,field=<"+field+">):"+e.getMessage());            
			e.printStackTrace();
		}
		return -1;
	}

	public ResultSet select(String sql) throws SQLException
	{
		return select(
			sql,
			ResultSet.TYPE_SCROLL_SENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
	}

	public ResultSet selectForUpdate(String sql) throws SQLException
	{
		return select(
			sql,
			ResultSet.TYPE_SCROLL_SENSITIVE,
			ResultSet.CONCUR_UPDATABLE);
	}

	private ResultSet select(String sql, int type, int fetch)
		throws SQLException
	{
		java.sql.Connection con = getConnectionPool().getConnection();

		try
		{
			Statement stmt = con.createStatement(type, fetch);

			stmt.execute(sql);

			ResultSet rs = stmt.getResultSet();

			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_OK);
			return rs;

		}
		catch (SQLException sqle)
		{
			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_EX);
            sqle.setNextException(new SQLException("Sql exception select(sql=<" + sql+">,type=<"+type+">,fetch=<"+fetch+">)"));
            if(detailedExceptionMessages)
                throw new SQLWebcoreException(sqle);
            else
                throw sqle;                
		}
	}

	public int update(String sql) throws SQLException
	{
		java.sql.Connection con = getConnectionPool().getConnection();
		try
		{
			Statement stmt = con.createStatement();

			int ret = stmt.executeUpdate(sql);

			stmt.close();

			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_OK);
			return ret;

		}
		catch (SQLException sqle)
		{
			//System.out.println( "Sql exception: " + sql );
			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_EX);
            sqle.setNextException(new SQLException("Sql exception update(sql=<" + sql+">)"));
            if(detailedExceptionMessages)
                throw new SQLWebcoreException(sqle);
            else
                throw sqle;                
		}
	}
	
	public Object insertAndRetrieveIdentityColumn(String sql) throws SQLException
	{
		return insertAndRetrieveIdentityColumnForSQLServer(sql);
	}
	
	public Object insertAndRetrieveIdentityColumnForSQLServer(String sql)
		throws SQLException
	{
		java.sql.Connection con = getConnectionPool().getConnection();
		try
		{
			Statement stmt = con.createStatement();

			sql += ";SELECT SCOPE_IDENTITY();";

			stmt.execute(sql);
			Object result = null;
			while (true)
			{
				ResultSet rs = stmt.getResultSet();
				if (rs != null)
				{
					rs.next();
					result = rs.getObject(1);
				}
				if (!stmt.getMoreResults())
				{
					break;
				}
			}
			stmt.close();

			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_OK);

			return result;
		}
		catch (SQLException sqle)
		{
			System.out.println("Sql exception: " + sql);
			getConnectionPool().recycle(con, ConnectionPool.RECYCLE_EX);
            sqle.setNextException(new SQLException("Sql exception insertAndRetrieveIdentityColumnForSQLServer(sql=<" + sql+">)"));
            if(detailedExceptionMessages)
                throw new SQLWebcoreException(sqle);
            else
                throw sqle;                
		}
	}
    public static String toString(SQLException e)
    {
        StringBuffer result = new StringBuffer("java.sql.SQLException[\n");
        int i = 0;
        while(e != null)
        {
            addMessage(result,i,e.getErrorCode(),e.getMessage());
            e = e.getNextException();
            i++;
        }
        result.append(']');
        return result.toString();
    }

    private static void addMessage(StringBuffer result, int pos, int errorCode, String message)
    {
        result.append(Integer.toString(pos));
        result.append(".\t#");
        result.append(errorCode);
        result.append(".\t");
        result.append(message);
        result.append("\n");
    }
    /**
     * Make a quoted string, for using with a database.
     * For example:
     * - getQuotedString(null) => null
     * - getQuotedString("car") => 'car'
     * - getQuotedString("nike's car") => 'nike''s car'
     * @param value the string
     * @return quoted string, always a string not a null object.
     */
    public static String getQuotedString(String value)
    {
        if(value == null)
            return "null";
        value = value.replaceAll("'", "''");
        return "'"+value+"'";
    }
    /**
     * Make a quoted date, for using with a database. 
     * In the default formating suported by database. 
     * For example:
     * - getQuotedDate(null) => null
     * - getQuotedDate(Date("24.10.2003")) => '10.24.2003'
     * @param value the date
     * @return quoted string, always a string not a null object.
     */
    public static String getQuotedDate(java.util.Date value)
    {
        if(value == null)
            return "null";
        String result = getDate(value);
        result = result.replaceAll("'", "''");
        return "'"+result+"'";
    }
    /**
     * Make a quoted date, for using with a database. 
     * In the default formating suported by database. 
     * For example:
     * - getQuotedDate(null) => null
     * - getQuotedDate(Date("24.10.2003")) => '10.24.2003'
     * @param value the date
     * @return quoted string, always a string not a null object.
     */
    public static String getQuotedDate(java.sql.Date value)
    {
        if(value == null)
            return "null";
        String result = getDate(value);
        result = result.replaceAll("'", "''");
        return "'"+result+"'";
    }
    /**
     * Make a quoted date, for using with a database. 
     * In the default formating suported by database. 
     * For example:
     * - getQuotedDate(null) => null
     * - getQuotedDate(Date("24.10.2003")) => '10.24.2003'
     * @param value the date
     * @return quoted string, always a string not a null object.
     */
    public static String getQuotedDate(Calendar value)
    {
        if(value == null)
            return "null";
        String result = getDate(value);
        result = result.replaceAll("'", "''");
        return "'"+result+"'";
    }
    /**
     * The default formating of date, suported by database. 
     * For example:
     * - getDate(null) => null
     * - getDate(Date("24.10.2003")) => 10.24.2003
     * @param value the date
     * @return quoted string, or null if date is null. 
     */
    public static String getDate(java.sql.Date d)
    {
        if(d==null)
            return null;
        return sdf.format(d);
    }   
    /**
     * The default formating of date, suported by database. 
     * For example:
     * - getDate(null) => null
     * - getDate(Date("24.10.2003")) => 10.24.2003
     * @param value the date
     * @return quoted string, or null if date is null. 
     */
    public static String getDate(java.util.Date d)
    {
        if(d==null)
            return null;
        return sdf.format(d);
    }   
    /**
     * The default formating of date, suported by database. 
     * For example:
     * - getDate(null) => null
     * - getDate(Date("24.10.2003")) => 10.24.2003
     * @param value the date
     * @return quoted string, or null if date is null. 
     */
    public static String getDate(Calendar d)
    {
        if(d==null)
            return null;
        return sdf.format(d);
    }  
    
    public static int exist( ResultSet rs , String colname , String valname )
    {
		if (rs != null && colname != null)
		{
			int oldRow = -1;
			try
			{
				oldRow = rs.getRow();
				
				rs.beforeFirst();
				
				while (rs.next())
				{
					String cval = rs.getString(colname);
					if (cval != null && cval.equals(valname))
						return rs.getRow();
				}
			}
			catch (SQLException sqle)
			{
                System.out.println(sqle.getMessage());
			}
			finally
			{
				// move back on the old row
				try
				{
					if (oldRow != -1)
					{
						if( oldRow==0 )
							rs.beforeFirst();
						else
							rs.absolute(oldRow);
							
					}
				}
				catch (SQLException s)
				{
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
    public static String getSafeWhere(String where)
    {
        //TODO make a safe where
        return where;
    }
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (!(object instanceof Database)) {
            return false;
        }
        Database rhs = (Database) object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(this.cp, rhs.cp)
                .append(this.detailedExceptionMessages, rhs.detailedExceptionMessages).isEquals();
    }
}
