package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;

import org.apache.log4j.Logger;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class RuleDTO extends DbObjectDTO {
	public RuleDTO(
		 final DbObjectDTO dbObjectDTO
		,final String      strRuleText
	) {
		super(dbObjectDTO);

		this.strRuleText = strRuleText;
	}
	public RuleDTO (
		 final DbObjectDTO               dbObjectDTO
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, null);

		//this.LOGGER = LOGGER;
		this.catdo = catdo;

		processDatabase(_con);
	}

	/*
	 * Get
	 */
	public final String getRuleText() {
		return strRuleText;
	}

	private void processDatabase (Connection _con) {
		strRuleText = catdo.RunDefnCopy(_con, this);
		//LOGGER.info(strTriggerText);
	}

	/*
	 * Class variables
	 */
	private String                    strRuleText;
	//private Logger                    LOGGER;
	private CommonAllTablesDumpObject catdo;
}
