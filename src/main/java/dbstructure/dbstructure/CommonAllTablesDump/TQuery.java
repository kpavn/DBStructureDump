package dbstructure.dbstructure.CommonAllTablesDump;

import java.util.*;

/**
 * TDumpParams
 * parameters for dump the database instance (database specific)
 */
public class TQuery {
	/**
	 * TQuery
	 */
	public TQuery() {
		this.hashmap_queries = new HashMap<String, String>();
	}

	public final String getQuery(final String strQueryName) {
		return hashmap_queries.get(strQueryName);
	}

	public void putQuery(final String strQueryName, final String strQueryText) {
		hashmap_queries.put(strQueryName, strQueryText);
	}

	private HashMap<String, String> hashmap_queries;
}