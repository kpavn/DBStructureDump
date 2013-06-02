package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null, 0, null);

		strProcedureText = catdo.RunDefnCopy (_con, this);
		permissionList.addAll(new PermissionListDTO(this, _con, catdo));

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
				LOGGER.error("Get proc exec mode", e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private String              strProcedureText;
	private int                 intTranmode;
	private List<PermissionDTO> permissionList;
	private final Logger        LOGGER = LoggerFactory.getLogger(ProcedureDTO.class);
}
