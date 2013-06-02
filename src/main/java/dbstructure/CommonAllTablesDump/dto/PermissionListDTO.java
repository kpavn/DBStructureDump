package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class PermissionListDTO extends ArrayList<PermissionDTO> {
	public PermissionListDTO (
		final DbObjectDTO dbObjectDTO
	) {
		super();

		this.dbObjectDTO = dbObjectDTO;
	}
	public PermissionListDTO (
		 final DbObjectDTO               dbObjectDTO
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO);
		this.LOGGER = LOGGER;

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

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			 "SELECT\n"
			 + "\t u.name\n"
			 + "\t,v.name\n"
			 + "\t,p.protecttype\n"
			 + "FROM\n"
			 + "\t " + dbObject.getDbName() + "..sysprotects p\n"
			 + "\t," + dbObject.getDbName() + "..sysusers u\n"
			 + "\t," + dbObject.getDbName() + "..sysobjects o\n"
			 + "\t," + "master"  + "..spt_values v\n"
			 + "WHERE\n"
			 + "\to.name = '" + dbObject.getObjectName() + "'\n"
			 + "\tAND p.id = o.id\n"
			 + "\tAND p.uid = u.uid\n"
			 + "\tAND v.number = p.action\n"
			 + "\tAND v.type = 'T'\n"
			 + "ORDER BY\n"
			 + "\t p.protecttype ASC\n"
			 + "\t,u.name ASC\n"
			 + "\t,v.name ASC\n";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t u.[name]\n"
			 + "\t,v.[name]\n"
			 + "\t,p.[protecttype]\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[sys].[objects] o\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] sch ON\n"
			 + "\t\tsch.[schema_id] = o.[schema_id]\n"
			 + "\t\tAND sch.[name] = '" + dbObject.getObjectSchema() + "'\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[sysprotects] p ON\n"
			 + "\t\tp.[id] = o.[object_id]\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[sysusers] u ON\n"
			 + "\t\tu.[uid] = p.[uid]\n"
			 + "\tINNER JOIN [master].[dbo].[spt_values] v ON\n"
			 + "\t\tv.[number] = p.[action]\n"
			 + "\t\tAND v.[type] = 'T'\n"
			 + "WHERE\n"
			 + "\to.[name] = '" + dbObject.getObjectName() + "'\n"
			 + "ORDER BY\n"
			 + "\t p.[protecttype] ASC\n"
			 + "\t,u.[name] ASC\n"
			 + "\t,v.[name] ASC\n";
		}
*/

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
				LOGGER.error(e, e);
			}
		}
	}

	private DbObjectDTO       dbObjectDTO;
	private Logger            LOGGER;
	private static final long serialVersionUID = -2786233175785417511L;

}
