package dbstructure.CommonAllTablesDump.dto;

public final class IndexColumnDTO {
	public IndexColumnDTO(String strColumnName, boolean boolColumnDesc) {
		this.strColumnName  = strColumnName;
		this.boolColumnDesc = boolColumnDesc;
	}

	/*
	 * Get
	 */
	public String getColumnName() {
		return strColumnName;
	}
	public boolean isColumnDesc() {
		return boolColumnDesc;
	}

	private String  strColumnName;
	private boolean boolColumnDesc;
}
