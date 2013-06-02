package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserDTO extends DbObjectDTO {
	public UserDTO (
		 final DbObjectDTO dbObjectDTO
		,final String      strLoginName
	) {
		super(dbObjectDTO);

		this.strLoginName   = strLoginName;
		this.listGroup      = new ArrayList<GroupDTO>();
		this.listPermission = new ArrayList<PermissionDTO>();
	}
	public UserDTO (
		 final DbObjectDTO               dbObjectDTO
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);

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
				LOGGER.error("Error while read users", e);
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
				LOGGER.error("Error while read user groups", e);
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
				LOGGER.error("Error while read permissions", e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private String                   strLoginName;
	private ArrayList<GroupDTO>      listGroup;
	private ArrayList<PermissionDTO> listPermission;
	private final Logger             LOGGER = LoggerFactory.getLogger(UserDTO.class);
}
