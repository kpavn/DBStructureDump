package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;

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
	public final String getRuleText() {
		return strRuleText;
	}

	private void processDatabase (Connection _con) {
		strRuleText = catdo.RunDefnCopy(_con, this);
	}

	/*
	 * Class variables
	 */
	private String                    strRuleText;
	private CommonAllTablesDumpObject catdo;
}
