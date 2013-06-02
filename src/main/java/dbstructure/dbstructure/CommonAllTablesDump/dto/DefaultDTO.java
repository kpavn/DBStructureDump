package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class DefaultDTO extends DbObjectDTO {
	public DefaultDTO (
		 final DbObjectDTO dbObjectDTO
		,final String      strDefaultText
	) {
		super(dbObjectDTO);

		this.strDefaultText = strDefaultText;
	}
	public DefaultDTO (
		 final DbObjectDTO               dbObjectDTO
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);

		this.LOGGER = LOGGER;
		this.catdo = catdo;

		processDatabase(_con);
	}

	/*
	 * Get
	 */
	public final String getDefaultText() {
		return strDefaultText;
	}

	private void processDatabase (Connection _con) {
		strDefaultText = catdo.RunDefnCopy(_con, this);
		LOGGER.info("41: DefaultText:" + strDefaultText);
	}

	/*
	 * Class variables
	 */
	private String                    strDefaultText;
	private Logger                    LOGGER;
	private CommonAllTablesDumpObject catdo;
}
