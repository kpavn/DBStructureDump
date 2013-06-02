package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.apache.log4j.Logger;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class ProcedureDTO extends DbObjectDTO {
	public ProcedureDTO(
		 final DbObjectDTO   dbObjectDTO
		,final String        strProcedureText
		,int                 intTranmode
		,List<PermissionDTO> permissionList
	) {
		super(dbObjectDTO);

		this.strProcedureText = strProcedureText;
		this.intTranmode      = intTranmode;
		this.permissionList   = new ArrayList<PermissionDTO>();

		if (permissionList != null) {
			this.permissionList.addAll(permissionList);
		}
	}
	public ProcedureDTO (
		 final DbObjectDTO               dbObjectDTO
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null, 0, null);
		this.LOGGER = LOGGER;

		strProcedureText = catdo.RunDefnCopy (_con, this);
		permissionList.addAll(new PermissionListDTO(this, LOGGER, _con, catdo));

		processDatabase(_con, catdo);
	}

	/*
	 * Get
	 */
	public final String getProcedureText() {
		return strProcedureText;
	}
	public final String getProcedureExecMode() {
		String strTranmode = null;

		if ((intTranmode & 0x10) == 0x10) {
			strTranmode = "chained";
		} else if ((intTranmode & 0x20) == 0x20) {
			strTranmode = "anymode";
		} else {
			strTranmode = "unchained";
		}

		return strTranmode;
	}
	public final List<PermissionDTO> getPermissionList() {
		return Collections.unmodifiableList(permissionList);
	}

	private void processDatabase (Connection _con, final CommonAllTablesDumpObject catdo) {
		ResultSet rs            = null;
		Statement stmt          = null;
		String    str_sql_query = null;

		str_sql_query = catdo.getQueryText("GetProcedureExecutionMode", "dbObjectDTO", this);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			 "SELECT\n"
			 + "\to.sysstat2\n"
			 + "FROM\n"
			 + "\t" + dbObject.getDbName() + "..sysobjects o\n"
			 + "WHERE\n"
			 + "\to.type = \'" + dbObject.getObjectType() + "\'\n"
			 + "\tAND o.name = \'" + dbObject.getObjectName() + "\'";
		}
		else if (getRDBMSVersion() == "mssql200") {
			str_sql_query = null;
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query = null;
		}
*/

		if (str_sql_query != null) {
			// procedure execution mode
			try {
				stmt = _con.createStatement();

				rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					intTranmode = rs.getInt(1);
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private String              strProcedureText;
	private int                 intTranmode;
	private List<PermissionDTO> permissionList;
	private Logger              LOGGER;
}
