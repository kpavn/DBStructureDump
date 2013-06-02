package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CategoryListDTO extends ArrayList<CategoryDTO> {
	public CategoryListDTO() {
		super();
	}
	public CategoryListDTO (
		 Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this();
		this.catdo  = catdo;

		processDatabase(_con);
	}

	private void processDatabase (Connection _con) {
		String str_sql_query = null;

		str_sql_query = catdo.getQueryText("ServerCategories", null, null);

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
				LOGGER.error("Server categories", e);
			}
		}
	}

	private final Logger              LOGGER = LoggerFactory.getLogger(CategoryListDTO.class);
	private CommonAllTablesDumpObject catdo;
	private static final long         serialVersionUID = 2947720036928883508L;
}
