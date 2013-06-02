package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PermissionListDTO extends ArrayList<PermissionDTO> {
	public PermissionListDTO (
		final DbObjectDTO dbObjectDTO
	) {
		super();

		this.dbObjectDTO = dbObjectDTO;
	}
	public PermissionListDTO (
		 final DbObjectDTO               dbObjectDTO
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO);

		processDatabase(_con, catdo);
	}

	/*
	 * Get
	 */
	public final DbObjectDTO GetDbObjectDTO() {
		return dbObjectDTO;
	}

	private void processDatabase (Connection _con, final CommonAllTablesDumpObject catdo) {
		String str_sql_query = null;

		str_sql_query = catdo.getQueryText("GetObjectPermissions", "dbObjectDTO", GetDbObjectDTO());

		if (str_sql_query != null) {
			try {
				Statement stmt = _con.createStatement();

				ResultSet rs = stmt.executeQuery (str_sql_query);
				while (rs.next()) {
					PermissionDTO permissionDTO = new PermissionDTO (
						 GetDbObjectDTO()
						,rs.getInt(3)
						,rs.getString(2)
						,rs.getString(1)
					);

					add(permissionDTO);
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error("Process permissions", e);
			}
		}
	}

	private DbObjectDTO       dbObjectDTO;
	private final Logger      LOGGER = LoggerFactory.getLogger(PermissionListDTO.class);
	private static final long serialVersionUID = -2786233175785417511L;

}
