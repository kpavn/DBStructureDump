package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class LoginListDTO extends ArrayList<LoginDTO> {
	public LoginListDTO() {
		super();
	}
	public LoginListDTO (
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
		String              str_sql_query             = null;
		String              str_sql_query_login_roles = null;

		str_sql_query = catdo.getQueryText("ServerLogins", null, null);
		str_sql_query_login_roles = catdo.getQueryText("ServerLoginRoles", null, null);
/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			 "SELECT\n"
			 + "\t sl.name     AS name\n"
			 + "\t,sl.dbname   AS dbname\n"
			 + "\t,sl.language AS language\n"
			 + "\t,sl.fullname AS fullname\n"
			 + "\t,0           AS isntname\n"
			 + "FROM\n"
			 + "\tmaster..syslogins sl\n"
			 + "WHERE\n"
			 + "\tsl.name NOT IN ('sa')\n"
			 + "ORDER BY\n"
			 + "\tsl.suid ASC\n";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			 "SELECT\n"
			 + "\t sl.[name]                                  AS [name]\n"
			 + "\t,sl.[dbname]                                AS [dbname]\n"
			 + "\t,sl.[language]                              AS [language]\n"
			 + "\t,sl.[loginname]                             AS [fullname]\n"
			 + "\t,sl.[isntname]                              AS [isntname]\n"
			 + "\t,[master].[dbo].[fn_varbintohexstr](sl.sid) AS [sid]\n"
			 + "FROM\n"
			 + "\t[master].[sys].[syslogins] sl\n"
			 + "WHERE\n"
			 + "\tsl.[name] NOT IN ('sa')\n"
			 + "ORDER BY\n"
			 + "\tsl.[sid] ASC";
		}
*/

		if (str_sql_query != null) {
			try {
				Statement         stmt              = _con.createStatement();
				PreparedStatement pstmt_login_roles = null;

				if (str_sql_query_login_roles != null) {
					pstmt_login_roles = _con.prepareStatement(str_sql_query_login_roles);
				}

				ResultSet rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					LoginDTO loginDTO = new LoginDTO(
						 rs.getObject(1) == null ? ""       : rs.getString(1).trim(), // name 
						 rs.getObject(2) == null ? "master" : rs.getString(2).trim(), // dbname
						 rs.getObject(3) == null ? ""       : rs.getString(3).trim(), // language
						 rs.getObject(4) == null ? ""       : rs.getString(4).trim(), // fullname
						 rs.getObject(5) == null ? 0        : rs.getInt(5),           // isntname
						 rs.getObject(6) == null ? ""       : rs.getString(6).trim(), // sid
						 rs.getObject(7) == null ? 0        : rs.getInt(7)            // denylogin
					);

					if (pstmt_login_roles != null) {
						pstmt_login_roles.setString(1, loginDTO.getObjectName());

						ResultSet rs1 = pstmt_login_roles.executeQuery();
						while (rs1.next()) {
							loginDTO.addRole(rs1.getString(1));
						}
						rs1.close();
					}

					add(loginDTO);
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
	private static final long         serialVersionUID = -8948693548004568332L;
}
