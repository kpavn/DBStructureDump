package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.util.ArrayList;

public final class LoginDTO extends DbObjectDTO {
	public LoginDTO (
		 final String strLoginName
		,final String strDefaultDbName
		,final String strLanguage
		,final String strFullName
		,int          intIsNtName
		,final String strSID
		,int          intIsDenyLogin
	) {
		super(
			 null         // dbParent
			,null         // strDbName
			,"login"      // strObjectType
			,strLoginName // strObjectName
			,null         // strObjectOwner
			,null         //strObjectSchema
		);

		this.strDefaultDbName = strDefaultDbName;
		this.strLanguage      = strLanguage;
		this.strFullName      = strFullName;
		this.intIsNtName      = intIsNtName;
		this.strSID           = strSID;
		this.intIsDenyLogin   = intIsDenyLogin;

		strListRole = new ArrayList<String>();
	}

	/*
	 * Get
	 */
	public final String getDefaultDbName() {
		return strDefaultDbName;
	}
	public final String getLanguage() {
		return strLanguage;
	}
	public final String getFullName() {
		return strFullName;
	}
	public int getIsNtName() {
		return intIsNtName;
	}
	public final String getSID() {
		return strSID;
	}
	public int getIsDenyLogin() {
		return intIsDenyLogin;
	}
	public final ArrayList<String> getListRole() {
		return strListRole;
	}

	/*
	 * Add
	 */
	public void addRole(final String strRole) {
		strListRole.add(strRole);
	}

	/*
	 * Class variables
	 */
	private String            strDefaultDbName;
	private String            strLanguage;
	private String            strFullName;
	private int               intIsNtName;
	private String            strSID;
	private int               intIsDenyLogin;
	private ArrayList<String> strListRole;
}
