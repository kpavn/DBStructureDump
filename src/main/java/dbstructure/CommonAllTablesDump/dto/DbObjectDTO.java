package dbstructure.CommonAllTablesDump.dto;

public class DbObjectDTO extends BaseDTO {
	public DbObjectDTO(
		 final DbObjectDTO dbParent
		,final String      strDbName
		,final String      strObjectType
		,final String      strObjectName
		,final String      strObjectOwner
		,final String      strObjectSchema
	) {
		super();

		this.dbParent        = dbParent;
		this.strDbName       = strDbName;
		this.strObjectType   = strObjectType;
		this.strObjectName   = strObjectName;
		this.strObjectOwner  = strObjectOwner;
		this.strObjectSchema = strObjectSchema;
	}
	public DbObjectDTO(
		 final DbObjectDTO dbParent
		,final DbObjectDTO dbObjectDTO
	) {
		this(
			 dbParent
			,dbObjectDTO.getDbName()
			,dbObjectDTO.getObjectType()
			,dbObjectDTO.getObjectName()
			,dbObjectDTO.getObjectOwner()
			,dbObjectDTO.getObjectSchema()
		);
	}
	public DbObjectDTO(
			final DbObjectDTO dbObjectDTO
	) {
		this(
			 dbObjectDTO.getParent()
			,dbObjectDTO.getDbName()
			,dbObjectDTO.getObjectType()
			,dbObjectDTO.getObjectName()
			,dbObjectDTO.getObjectOwner()
			,dbObjectDTO.getObjectSchema()
		);
	}

	/*
	 * Get
	 */
	public final DbObjectDTO getParent() {
		return dbParent;
	}
	public final String getDbName() {
		return strDbName;
	}
	public final String getObjectType() {
		return strObjectType;
	}
	public final String getObjectTypeText() {
		String strObjectTypeText = strObjectType;

		strObjectTypeText = strObjectTypeText == null ? null : strObjectTypeText.replace('\\', '_');
		strObjectTypeText = strObjectTypeText == null ? null : strObjectTypeText.replace(':', '_');
		strObjectTypeText = strObjectTypeText == null ? null : strObjectTypeText.replace(' ', '_');

		return strObjectTypeText;
	}
	public final String getObjectName() {
		return strObjectName;
	}
	public final String getObjectNameText() {
		String strObjectNameText = strObjectName;

		strObjectNameText = strObjectNameText == null ? null : strObjectNameText.replace('\\', '_');
		strObjectNameText = strObjectNameText == null ? null : strObjectNameText.replace(':', '_');
		strObjectNameText = strObjectNameText == null ? null : strObjectNameText.replace(' ', '_');

		return strObjectNameText;
	}
	public final String getObjectOwner() {
		return strObjectOwner;
	}
	public final String getObjectSchema() {
		return strObjectSchema;
	}

	/*
	 * Set
	 */
	public void setType(final DbObjectDTO dbParent) {
		this.dbParent = dbParent;
	}
	public void setDbName(final String strDbName) {
		this.strDbName = strDbName;
	}
	public void setObjectType(final String strObjectType) {
		this.strObjectType = strObjectType;
	}
	public void setObjectName(final String strObjectName) {
		this.strObjectName = strObjectName;
	}
	public void setObjectOwner(final String strObjectOwner) {
		this.strObjectName = strObjectOwner;
	}
	public void setObjectSchema(final String strObjectSchema) {
		this.strObjectName = strObjectSchema;
	}

	/*
	 * Class variables
	 */
	private DbObjectDTO dbParent;
	private String      strDbName;
	private String      strObjectType;
	private String      strObjectName;
	private String      strObjectOwner;
	private String      strObjectSchema;
}
