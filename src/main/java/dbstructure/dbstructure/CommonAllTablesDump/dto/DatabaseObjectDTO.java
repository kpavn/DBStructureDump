package dbstructure.dbstructure.CommonAllTablesDump.dto;

public final class DatabaseObjectDTO extends BaseDTO {
	public DatabaseObjectDTO(String strDbName) {
		super();

		this.strDbName = strDbName;
	}

	/*
	 * Get
	 */
	public final String getDbName() {
		return strDbName;
	}

	/*
	 * Class variables
	 */
	private String strDbName;
}
