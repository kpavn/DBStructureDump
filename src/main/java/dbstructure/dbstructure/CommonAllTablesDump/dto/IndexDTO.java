package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.util.ArrayList;
import java.util.List;

public final class IndexDTO {
	public IndexDTO(final String strIndexName, boolean boolIsPrimaryKey, boolean boolIsUnique, boolean boolIsClustered) {
		this.strIndexName     = strIndexName;
		this.boolIsPrimaryKey = boolIsPrimaryKey;
		this.boolIsUnique     = boolIsUnique;
		this.boolIsClustered  = boolIsClustered;
		this.columnList       = new ArrayList<IndexColumnDTO>();

		if (this.boolIsPrimaryKey) {
			this.boolIsUnique = true;
		}
	}

	/*
	 * Add
	 */
	public void addColumn(final IndexColumnDTO indexColumnDTO) {
		columnList.add(indexColumnDTO);
	}

	/*
	 * Get
	 */
	public String getIndexName() {
		return strIndexName;
	}
	public boolean isClustered() {
		return boolIsClustered;
	}
	public boolean isPrimaryKey() {
		return boolIsPrimaryKey;
	}
	public boolean isUnique() {
		return boolIsUnique;
	}
	public List<IndexColumnDTO> getColumnList() {
		return columnList;
	}

	private String               strIndexName;
	private boolean              boolIsPrimaryKey;
	private boolean              boolIsUnique;
	private boolean              boolIsClustered;
	private List<IndexColumnDTO> columnList;
}
