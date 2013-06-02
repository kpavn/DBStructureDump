package dbstructure.dbstructure.CommonAllTablesDump.dto;

public final class ColumnDTO extends BaseDTO {
	public ColumnDTO(
		 final String     strColumnName
		,final String     strColumnNameSpace
		,final String     strColumnType
		,final String     strColumnTypeSpace
		,final String     strColumnTypeExt
		,final DefaultDTO columnDefault
		,boolean          columnNullable
		,final RuleDTO    columnRule
		,final RuleDTO    columnConstraint
		,boolean          is_column_identity
	) {
		super();

		this.strColumnName         = strColumnName;
		this.strColumnNameSpace    = strColumnNameSpace;
		this.strColumnType         = strColumnType;
		this.strColumnTypeSpace    = strColumnTypeSpace;
		this.strColumnTypeExt      = strColumnTypeExt;
		this.columnDefault         = columnDefault;
		this.columnNullable        = columnNullable;
		this.columnRule            = columnRule;
		this.columnConstraint      = columnConstraint;
		this.is_column_identity    = is_column_identity;
	}

	/*
	 * get
	 */
	public final String getColumnName() {
		return strColumnName;
	}
	public final String getColumnNameSpace() {
		return strColumnNameSpace;
	}
	public String getColumnType() {
		return strColumnType;
	}
	public String getColumnTypeSpace() {
		return strColumnTypeSpace;
	}
	public String getColumnTypeExt() {
		return strColumnTypeExt;
	}
	public final DefaultDTO getColumnDefault() {
		return columnDefault;
	}
	public final String getColumnDefaultSpace() {
		return strColumnDefaultSpace;
	}
	public boolean isColumnNullable() {
		return columnNullable;
	}
	public final RuleDTO getColumnRule() {
		return columnRule;
	}
	public boolean isColumnRule() {
		return columnRule != null;
	}
	public final RuleDTO getColumnConstraint() {
		return columnConstraint;
	}
	public boolean isColumnConstraint() {
		return columnConstraint != null;
	}
	public boolean isColumnidentity() {
		return is_column_identity;
	}

	/*
	 * class members, keep them private as 'get' functions are available 
	 */
	private String     strColumnName;
	private String     strColumnNameSpace;
	private String     strColumnType;
	private String     strColumnTypeSpace;
	private String     strColumnTypeExt;
	private DefaultDTO columnDefault;
	private String     strColumnDefaultSpace;
	private boolean    columnNullable;
	private RuleDTO    columnRule;
	private RuleDTO    columnConstraint;
	private boolean    is_column_identity;
}
