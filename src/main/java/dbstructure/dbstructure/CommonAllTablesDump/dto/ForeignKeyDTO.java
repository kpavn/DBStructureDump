package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ForeignKeyDTO extends DbObjectDTO {
	public ForeignKeyDTO (
		 final DbObjectDTO dbObjectDTO
		,final String      strForeignKeyName
		,final DbObjectDTO dbObjectDTOReference
	) {
		super(dbObjectDTO);

		this.strForeignKeyName    = strForeignKeyName;
		this.dbObjectDTOReference = dbObjectDTOReference;

		this.columnList           = new ArrayList<String>();
		this.referenceColumnList  = new ArrayList<String>();
	}

	/*
	 * Get
	 */
	public final String getForeignKeyName() {
		return strForeignKeyName;
	}
	public final DbObjectDTO getReferenceObjectDTO() {
		return dbObjectDTOReference;
	}
	public final List<String> getColumnList() {
		return Collections.unmodifiableList(columnList);
	}
	public final List<String> getReferenceColumnList() {
		return Collections.unmodifiableList(referenceColumnList);
	}
	public final String getReferenceRelativePath() {
		int idx = dbObjectDTOReference.getObjectName().indexOf("..");

		if (idx > 0) {
			return "..\\" + dbObjectDTOReference.getObjectName().substring(idx) + "\\"
			 + dbObjectDTOReference.getObjectName().substring(idx + 2, dbObjectDTOReference.getObjectName().length() - idx + 2);
		} else {
			return dbObjectDTOReference.getObjectName();
		}
	}

	/*
	 * Add
	 */
	public void addColumn(final String strColumn, final String strReferenceColumn) {
		this.columnList.add(strColumn);
		this.referenceColumnList.add(strReferenceColumn);
	}
	public void addColumnList(final List<String> columnList) {
		this.columnList.addAll(columnList);
	}
	public void addReferenceColumnList(final List<String> columnList) {
		this.referenceColumnList.addAll(columnList);
	}

	/*
	 * Class variables
	 */
	private String       strForeignKeyName;
	private DbObjectDTO  dbObjectDTOReference;
	private List<String> columnList;
	private List<String> referenceColumnList;
}
