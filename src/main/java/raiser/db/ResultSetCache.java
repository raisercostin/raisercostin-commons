package raiser.db;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;

public class ResultSetCache {
	public static HashMap<String, HashMap<String, ResultSet>> sessions = new HashMap<String, HashMap<String, ResultSet>>();

	public synchronized static ResultSet get(String sid, String rsname) {
		if ((sid == null) || (rsname == null)) {
			return null;
		}

		HashMap h = (HashMap) sessions.get(sid);
		if (h != null) {
			return (ResultSet) h.get(rsname);
		}

		return null;
	}

	public synchronized static void remove(String sid, String rsname) {
		if ((sid == null) || (rsname == null)) {
			return;
		}

		HashMap h = (HashMap) sessions.get(sid);
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

	public synchronized static void set(String sid, ResultSet rs) {
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

	public synchronized static void remove(String sid) {
		try {
			if (sid != null) {
				System.out.println("Session destroyed " + sid);

				HashMap s = (HashMap) sessions.remove(sid);

				if (s != null) {
					Iterator si = s.keySet().iterator();
					while (si.hasNext()) {
						ResultSet rs = (ResultSet) s.get(si.next());
						if (rs != null) {
							try {
								rs.close();
							} catch (Exception e) {
								e.printStackTrace();
							}

							si.remove();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
