package dbstructure.dbstructure.CommonAllTablesDump.dto;

/*
 * UDT - User Define Type
 */
public final class UDTDTO extends DbObjectDTO {
	public UDTDTO (
		 final DbObjectDTO dbObjectDTO
		,final String      strUDTType
		,boolean           boolIsNullable
		,final String      strDefaultName
		,final String      strDefaultText
	) {
		super(dbObjectDTO);

		this.strUDTType     = strUDTType;
		this.boolIsNullable = boolIsNullable;
		this.strDefaultName = strDefaultName;
		this.strDefaultText = strDefaultText;
	}

	/*
	 * Get
	 */
	public final String getUDTType() {
		return strUDTType;
	}
	public boolean isNullable() {
		return boolIsNullable;
	}
	public final String getDefaultName() {
		return strDefaultName;
	}
	public final String getDefaultText() {
		return strDefaultText;
	}

	/*
	 * Class variables
	 */
	private String  strUDTType;
	private boolean boolIsNullable;
	private String  strDefaultName;
	private String  strDefaultText;
}
