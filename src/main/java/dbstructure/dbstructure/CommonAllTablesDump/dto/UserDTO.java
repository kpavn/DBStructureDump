package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class UserDTO extends DbObjectDTO {
	public UserDTO (
		 final DbObjectDTO dbObjectDTO
		,final String      strLoginName
	) {
		super(dbObjectDTO);

		this.LOGGER         = null;
		this.strLoginName   = strLoginName;
		this.listGroup      = new ArrayList<GroupDTO>();
		this.listPermission = new ArrayList<PermissionDTO>();
	}
	public UserDTO (
		 final DbObjectDTO               dbObjectDTO
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);
		this.LOGGER = LOGGER;

		processDatabase(_con, catdo);
	}

	/*
	 * Get
	 */
	public final String getLoginName() {
		return strLoginName;
	}
	public ArrayList<GroupDTO> getGroupList() {
		return listGroup;
	}
	public ArrayList<PermissionDTO> getPermissionList() {
		return listPermission;
	}

	/*
	 * Add
	 */
	public void addGroup(final GroupDTO groupDTO) {
		listGroup.add(groupDTO);
	}
	public void addPermission(final PermissionDTO permissionDTO) {
		listPermission.add(permissionDTO);
	}

	/*
	 * Set
	 */
	public void setLoginName(final String strLoginName) {
		this.strLoginName = strLoginName;
	}

	private void processDatabase (Connection _con, final CommonAllTablesDumpObject catdo) {
		String str_sql_query                  = null;
		String str_sql_query_user_groups      = null;
		String str_sql_query_user_permissions = null;

		str_sql_query = catdo.getQueryText("DatabaseUser", "dbObjectDTO", this);
		str_sql_query_user_groups = catdo.getQueryText("DatabaseUserGroups", "dbObjectDTO", this);
		str_sql_query_user_permissions = catdo.getQueryText("UserPermissions", "dbObjectDTO", this);
/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			 "SELECT\n"
			 + "\t u.name AS user_name\n"
			 + "\t,sl.name AS login_name\n"
			 + "\t,ISNULL(u2.name, '') AS group_name\n"
			 + "FROM\n"
			 + "\t" + str_db + "..sysusers u\n"
			 + "\tINNER JOIN master..syslogins sl\n"
			 + "\t\tON u.suid = sl.suid\n"
			 + "\tLEFT JOIN " + str_db + "..sysusers u2\n"
			 + "\t\tON u.gid = u2.uid\n"
			 + "\t\tAND u2.gid = u2.uid\n"
			 + "\t\tAND u2.suid = -2\n"
			 + "WHERE\n"
			 + "\tu.suid NOT IN (-2, 1)\n"
			 + "\tAND u.uid <> u.gid\n"
			 + "ORDER BY\n"
			 + "\tu.uid";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			 "SELECT\n"
			 + "\t u.[name]  AS [user_name]\n"
			 + "\t,sl.[name] AS [login_name]\n"
			 + "\t,NULL      AS [group_name]\n"
			 + "FROM\n"
			 + "\t[" + str_db + "].[sys].[sysusers] u\n"
			 + "\tLEFT OUTER JOIN [master].[sys].[syslogins] sl ON\n"
			 + "\t\tsl.[sid] = u.[sid]\n"
			 + "ORDER BY\n"
			 + "\tu.[sid]";
		}
*/
		if (str_sql_query != null) {
			try {
				PreparedStatement pstmt_user = _con.prepareStatement(str_sql_query);
				pstmt_user.setString(1, getObjectName());

				ResultSet rs = pstmt_user.executeQuery();
				while (rs.next()) {
					setLoginName(rs.getObject(1) == null ? null : rs.getString(1).trim());
				}
				rs.close();

				pstmt_user.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}

		if (str_sql_query_user_groups != null) {
			try {
				PreparedStatement pstmt_user_groups = _con.prepareStatement(str_sql_query_user_groups);
				pstmt_user_groups.setString(1, getObjectName());

				ResultSet rs = pstmt_user_groups.executeQuery();
				while (rs.next()) {
					addGroup( new GroupDTO(new DbObjectDTO(
						 this
						,getDbName()
						,"Group"
						,rs.getObject(1) == null ? null : rs.getString(1).trim()
						,null
						,null
					)));
				}
				rs.close();

				pstmt_user_groups.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}

		if (str_sql_query_user_permissions != null) {
			try {
				PreparedStatement pstmt_user_permissions = _con.prepareStatement(str_sql_query_user_permissions);
				pstmt_user_permissions.setString(1, getObjectName());

				ResultSet rs = pstmt_user_permissions.executeQuery();
				while (rs.next()) {
					addPermission(new PermissionDTO(
						new DbObjectDTO(
							 this
							,getDbName()                                             // strDbName
							,rs.getObject(1) == null ? null : rs.getString(1).trim() // strObjectType
							,rs.getObject(2) == null ? null : rs.getString(2).trim() // strObjectName
							,rs.getObject(3) == null ? null : rs.getString(3).trim() // strObjectOwner
							,rs.getObject(4) == null ? null : rs.getString(4).trim() // strObjectSchema
						)
						,rs.getInt(5)                                            // intProtectType
						,rs.getObject(6) == null ? null : rs.getString(6).trim() // strRight
						,null                                                    // strUser
					));
				}
				rs.close();

				pstmt_user_permissions.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private String                   strLoginName;
	private ArrayList<GroupDTO>      listGroup;
	private ArrayList<PermissionDTO> listPermission;
	private Logger                   LOGGER;
}
