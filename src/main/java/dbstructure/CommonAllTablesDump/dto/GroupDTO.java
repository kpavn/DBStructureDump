package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GroupDTO extends DbObjectDTO {
	public GroupDTO(
		final DbObjectDTO dbObjectDTO
	) {
		super(dbObjectDTO);

		this.listUser       = new ArrayList<UserDTO>();
		this.listPermission = new ArrayList<PermissionDTO>();
	}
	public GroupDTO (
		 final DbObjectDTO               dbObjectDTO
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO);

		this.catdo  = catdo;

		processDatabase(_con);
	}

	/*
	 * Get
	 */
	public ArrayList<UserDTO> getUserList() {
		return listUser;
	}
	public ArrayList<PermissionDTO> getPermissionList() {
		return listPermission;
	}

	/*
	 * Add
	 */
	public void addUser(final UserDTO userDTO) {
		listUser.add(userDTO);
	}
	public void addPermission(final PermissionDTO permissionDTO) {
		listPermission.add(permissionDTO);
	}

	private void processDatabase (Connection _con) {
		String str_sql_query_group_users       = null;
		String str_sql_query_group_permissions = null;

		str_sql_query_group_users = catdo.getQueryText("GroupUsers", "dbObjectDTO", this);
		str_sql_query_group_permissions = catdo.getQueryText("GroupPermissions", "dbObjectDTO", this);

		if (str_sql_query_group_users != null) {
			try {
				PreparedStatement pstmt_group_users = _con.prepareStatement(str_sql_query_group_users);

				pstmt_group_users.setString(1, getObjectName());
				ResultSet rs = pstmt_group_users.executeQuery();
				while (rs.next()) {

					addUser(new UserDTO(
						new DbObjectDTO(
							 this
							,getDbName()
							,"User"
							,rs.getObject(1) == null ? null : rs.getString(1).trim()
							,null
							,null
						)
						,null
					));
				}
				rs.close();

				pstmt_group_users.close();
			} catch (SQLException e) {
				LOGGER.error("Error while get groups", e);
			}
		}

		if (str_sql_query_group_permissions != null) {
			try {
				PreparedStatement pstmt_group_permissions = _con.prepareStatement(str_sql_query_group_permissions);
	
				pstmt_group_permissions.setString(1, getObjectName());
				ResultSet rs = pstmt_group_permissions.executeQuery();
				while (rs.next()) {
	
					addPermission(new PermissionDTO(
						new DbObjectDTO(
							 this                                                    // dbParent
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

				pstmt_group_permissions.close();
			} catch (SQLException e) {
				LOGGER.error("Error while get permissions", e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private ArrayList<UserDTO>        listUser;
	private ArrayList<PermissionDTO>  listPermission;
	private final Logger              LOGGER = LoggerFactory.getLogger(GroupDTO.class);
	private CommonAllTablesDumpObject catdo;
}
