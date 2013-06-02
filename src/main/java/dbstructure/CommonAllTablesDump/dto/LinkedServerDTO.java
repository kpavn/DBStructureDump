package dbstructure.CommonAllTablesDump.dto;

import java.util.ArrayList;

public final class LinkedServerDTO extends DbObjectDTO {
	public LinkedServerDTO (
		 final String strLinkedServerName
		,final String strLinkedServerProduct
		,final String strLinkedServerProvider
		,final String strLinkedServerDataSource
		,final String strLinkedServerLocation
		,final String strLinkedServerProviderString
		,final String strLinkedServerCatalog
	) {
		super(
			 null                // dbParent
			,null                //strDbName
			,"linkedserver"      // strObjectType
			,strLinkedServerName // strObjectName
			,null                // strObjectOwner
			,null                // strObjectSchema
		);

		this.strLinkedServerProduct        = strLinkedServerProduct;
		this.strLinkedServerProvider       = strLinkedServerProvider;
		this.strLinkedServerDataSource     = strLinkedServerDataSource;
		this.strLinkedServerLocation       = strLinkedServerLocation;
		this.strLinkedServerProviderString = strLinkedServerProviderString;
		this.strLinkedServerCatalog        = strLinkedServerCatalog;

		this.listLinkedServerLogin         = new ArrayList<String>();
	}

	/*
	 * Get
	 */
	public final String getLinkedServerProduct() {
		return strLinkedServerProduct;
	}
	public final String getLinkedServerProvider() {
		return strLinkedServerProvider;
	}
	public final String getLinkedServerDataSource() {
		return strLinkedServerDataSource;
	}
	public final String getLinkedServerLocation() {
		return strLinkedServerLocation;
	}
	public final String getLinkedServerProviderString() {
		return strLinkedServerProviderString;
	}
	public final String getLinkedServerCatalog() {
		return strLinkedServerCatalog;
	}
	public final ArrayList<String> getLinkedServerLogins() {
		return listLinkedServerLogin;
	}

	/*
	 * Add
	 */
	public void addLinkedServerLogin(final String strLinkedServerLogin) {
		listLinkedServerLogin.add (strLinkedServerLogin);
	}

	/*
	 * Class variables
	 */
	private String            strLinkedServerProduct;
	private String            strLinkedServerProvider;
	private String            strLinkedServerDataSource;
	private String            strLinkedServerLocation;
	private String            strLinkedServerProviderString;
	private String            strLinkedServerCatalog;
	private ArrayList<String> listLinkedServerLogin;
}
