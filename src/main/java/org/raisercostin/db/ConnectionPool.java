package org.raisercostin.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Stack;

public class ConnectionPool {

	private int STACK_SIZE = 20;

	private String DB_URL = "";

	private String DB_DRIVER = "";

	private String DB_USER = "";

	private String DB_PASSWD = "";

	private final String DB_TEST = "select 'sample' as sample";

	public static final int RECYCLE_OK = 1;

	public static final int RECYCLE_EX = 2;

	private static final long RETRY_TIME = 10000L;

	private Stack<Connection> available = null;

	public final static Object lock = new Object();

	public ConnectionPool(final String databaseUrl, final String driver,
			final String user, final String password, final int stackSize) {
		STACK_SIZE = stackSize;
		DB_URL = databaseUrl;
		DB_DRIVER = driver;
		DB_USER = user;
		DB_PASSWD = password;
		try {
			// init connections
			final Driver drv = (Driver) (Class.forName(DB_DRIVER).newInstance());

			DriverManager.registerDriver(drv);

			available = new Stack<Connection>();

		} catch (final Exception e) {
			System.out.println(" FATAL ERROR :unable to register driver :"
					+ DB_DRIVER + "\nreason :" + e.getMessage());

			e.printStackTrace();

			return;
		}

		for (int i = 0; i < STACK_SIZE; i++) {
			new ThreadConResolver(this).start();
		}
	}

	public ConnectionPool(final HashMap<String, String> params) {
		this(params.get("/db_password"), params.get("/db_url"), params
				.get("/db_driver"), params.get("/db_user"), Integer
				.parseInt(params.get("/connections")));
	}

	private class ThreadConResolver extends Thread {
		ConnectionPool cp = null;

		public ThreadConResolver(final ConnectionPool cp) {
			super();
			this.cp = cp;
		}

		@Override
		public synchronized void start() {
			super.start();
		}

		@Override
		public void run() {
			while (true) {
				try {
					final Connection con = DriverManager.getConnection(DB_URL,
							DB_USER, DB_PASSWD);

					/*
					 * connection aquired ok
					 */

					cp.recycle(con, RECYCLE_OK);

					/*
					 * exit from while loop
					 */
					break;

				} catch (final Exception e) {
					System.out
							.println("Unable to acquire db connection. Reason "
									+ e.getMessage() + "\nRetry in "
									+ RETRY_TIME + " ms");

					try {
						sleep(RETRY_TIME);
					} catch (final InterruptedException ie) {
					}
				}
			}
		}
	}

	/*
	 * public synchronized static ConnectionPool getInstance() { return
	 * instance; }
	 */

	public void recycle(final Connection con, final int recType) {
		if (con == null) {
			return;
		}

		/*
		 * check for connection ok
		 */
		if (recType == RECYCLE_EX) {
			if (!verifyConnection(con)) {
				System.out.println("Starting connection recovery ...");
				try {
					/*
					 * connection is bad .. close it to free resources
					 * 
					 */
					con.close();
				} catch (final Exception e) {
				}

				/*
				 * create new one instead
				 */
				new ThreadConResolver(this).start();

				return;
			}
		}

		synchronized (available) {
			available.push(con);
			/*
			 * notify all that a new connection is free and ready to be used
			 */
			available.notifyAll();
		}
	}

	private boolean verifyConnection(final Connection con) {
		try {
			con.createStatement().execute(DB_TEST);
			return true;

		} catch (final Exception ex) {
			return false;
		}
	}

	public Connection getConnection() {
		if (available == null) {
			return null;
		}

		Connection con = null;

		synchronized (available) {
			while (available.isEmpty()) {
				try {
					available.wait();
				} catch (final Exception e) {
					// we just be notified
				}
			}
			con = available.pop();
		}
		return con;
	}

	public String info() {
		return "Cnx left:" + String.valueOf(available.size());
	}
}