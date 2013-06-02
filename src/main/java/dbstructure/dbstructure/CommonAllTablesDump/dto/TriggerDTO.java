package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

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
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);

		//this.LOGGER = LOGGER;
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
		//LOGGER.info(strTriggerText);
	}

	/*
	 * Class variables
	 */
	private String                    strTriggerText;
	//private Logger                    LOGGER;
	private CommonAllTablesDumpObject catdo;
}
