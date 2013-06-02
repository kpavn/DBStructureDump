package dbstructure.dbstructure.CommonAllTablesDump.dto;

public final class CategoryDTO extends DbObjectDTO {
	public CategoryDTO (
		final String strCategoryName
	) {
		super(
			 null            // dbParent
			,null            // strDbName
			,"category"      // strObjectType
			,strCategoryName // strObjectName
			,null            // strObjectOwner
			,null            //strObjectSchema
		);
	}
}
