package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);
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
	private final Logger              LOGGER = LoggerFactory.getLogger(DefaultDTO.class);
	private CommonAllTablesDumpObject catdo;
}
