package raiser.db;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;

public class ResultSetCache {
	public static HashMap<String, HashMap<String, ResultSet>> sessions = new HashMap<String, HashMap<String, ResultSet>>();

	public synchronized static ResultSet get(final String sid,
			final String rsname) {
		if ((sid == null) || (rsname == null)) {
			return null;
		}

		final HashMap<String,ResultSet> h = sessions.get(sid);
		if (h != null) {
			return h.get(rsname);
		}

		return null;
	}

	public synchronized static void remove(final String sid, final String rsname) {
		if ((sid == null) || (rsname == null)) {
			return;
		}

		final HashMap<String,ResultSet> h = sessions.get(sid);
		if (h != null) {
			// System.out.println("ResultSet " + rsname + " removed from session
			// " + sid);

			h.remove(rsname);
			/*
			 * if (h.remove(rsname) != null) SimpleLogger.log(rsname + " removed
			 * ok from Database cache"); else SimpleLogger.log(rsname + " remove
			 * failure from Database cache");
			 */
		}
	}

	public synchronized static void set(final String sid, final ResultSet rs) {
		if ((rs == null) || (sid == null)) {
			return;
		}

		HashMap<String, ResultSet> h = sessions.get(sid);
		if (h == null) {
			// System.out.println("ResultSet cache created for session " + sid);
			h = new HashMap<String, ResultSet>();
			sessions.put(sid, h);
		}

		// System.out.println("ResultSet " + rs.toString() + " added in session
		// " + sid);
		h.put(rs.toString(), rs);
	}

	public synchronized static void remove(final String sid) {
		try {
			if (sid != null) {
				System.out.println("Session destroyed " + sid);

				final HashMap<String,ResultSet> s = sessions.remove(sid);

				if (s != null) {
					final Iterator<String> si = s.keySet().iterator();
					while (si.hasNext()) {
						final ResultSet rs = s.get(si.next());
						if (rs != null) {
							try {
								rs.close();
							} catch (final Exception e) {
								e.printStackTrace();
							}

							si.remove();
						}
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
