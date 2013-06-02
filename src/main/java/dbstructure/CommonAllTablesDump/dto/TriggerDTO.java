package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class TriggerDTO extends DbObjectDTO {
	public TriggerDTO(
		 final DbObjectDTO dbObjectDTO
		,final String      strTriggerText
	) {
		super(dbObjectDTO);

		this.strTriggerText = strTriggerText;
	}
	public TriggerDTO (
		 final DbObjectDTO               dbObjectDTO
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);
		this.catdo  = catdo;

		processDatabase(_con);
	}

	/*
	 * Get
	 */
	public final String getTriggerText() {
		return strTriggerText;
	}

	private void processDatabase (Connection _con) {
		strTriggerText = catdo.RunDefnCopy(_con, this);
	}

	/*
	 * Class variables
	 */
	private String                    strTriggerText;
	private CommonAllTablesDumpObject catdo;
}
