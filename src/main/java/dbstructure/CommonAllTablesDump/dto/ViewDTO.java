package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.util.ArrayList;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ViewDTO extends DbObjectDTO {
	public ViewDTO(
	 final DbObjectDTO              dbObjectDTO
	,final String                   strViewText
	,final ArrayList<PermissionDTO> permissionList
	) {
		super(dbObjectDTO);

		this.strViewText    = strViewText;
		this.permissionList = new ArrayList<PermissionDTO>();

		if (permissionList != null) {
			this.permissionList.addAll(permissionList);
		}
	}
	public ViewDTO (
		 final DbObjectDTO               dbObjectDTO
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, (String) null, null);
		this.catdo  = catdo;

		processDatabase(_con);
	}

	private void processDatabase (Connection _con) {
		strViewText = catdo.RunDefnCopy(_con, this);
		permissionList.addAll(new PermissionListDTO(this, _con, catdo));
	}

	/*
	 * Get
	 */
	public final String getViewText() {
		return strViewText;
	}
	public final ArrayList<PermissionDTO> getPermissionList() {
		return permissionList;
	}

	/*
	 * Class variables
	 */
	private String                    strViewText;
	private ArrayList<PermissionDTO>  permissionList;
	private final Logger              LOGGER = LoggerFactory.getLogger(ViewDTO.class);
	private CommonAllTablesDumpObject catdo;
}
