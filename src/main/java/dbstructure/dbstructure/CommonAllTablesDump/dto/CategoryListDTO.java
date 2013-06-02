package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class CategoryListDTO extends ArrayList<CategoryDTO> {
	public CategoryListDTO() {
		super();
	}
	public CategoryListDTO (
		 Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this();

		this.LOGGER = LOGGER;
		this.catdo  = catdo;

		processDatabase(_con);
	}

	private void processDatabase (Connection _con) {
		String str_sql_query = null;

		str_sql_query = catdo.getQueryText("ServerCategories", null, null);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query = null;
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t c.[name] AS [category_name]\n"
			 + "\t,N'JOB'   AS [category_class]\n"
			 + "FROM\n"
			 + "\t[msdb].[dbo].[sysjobs] j\n"
			 + "\tINNER JOIN [msdb].[dbo].[syscategories] c ON\n"
			 + "\t\tc.[category_id] = j.[category_id]\n"
			 + "GROUP BY\n"
			 + "\tc.[name]";
		}
*/
		if (str_sql_query != null) {
			try {
				Statement stmt = _con.createStatement();

				ResultSet rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					// category
					add(new CategoryDTO (
						rs.getObject(1) == null ? "" : rs.getString(1).trim()
					));
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	private Logger                    LOGGER;
	private CommonAllTablesDumpObject catdo;
	private static final long         serialVersionUID = 2947720036928883508L;
}
