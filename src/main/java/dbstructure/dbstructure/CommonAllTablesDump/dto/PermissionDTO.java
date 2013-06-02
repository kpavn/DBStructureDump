package dbstructure.dbstructure.CommonAllTablesDump.dto;

public final class PermissionDTO extends DbObjectDTO {
	public PermissionDTO(
		 final DbObjectDTO dbObjectDTO
		,int               intProtectType
		,final String      strRight
		,final String      strUser
	) {
		super(dbObjectDTO);

		this.intProtectType = intProtectType;
		this.strRight       = strRight;
		this.strUser        = strUser;
	}

	/*
	 * Get
	 */
	public int GetProtectType() {
		return intProtectType;
	}
	public final String GetRight() {
		return strRight;
	}
	public final String GetUser() {
		return strUser;
	}

	private int    intProtectType;
	private String strRight;
	private String strUser;
}
