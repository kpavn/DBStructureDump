package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class UserTableDTO extends DbObjectDTO {
	public UserTableDTO (
		 final DbObjectDTO dbObjectDTO
		,int               intTableRowCount
	) {
		super(dbObjectDTO);

		this.intTableRowCount = intTableRowCount;
		this.columnList       = new ArrayList<ColumnDTO>();
		this.indexList        = new ArrayList<IndexDTO>();
		this.foreignKeyList   = new ArrayList<ForeignKeyDTO>();
		this.permissionList   = new ArrayList<PermissionDTO>();
		this.constraintList   = new ArrayList<RuleDTO>();
		this.triggerList      = new ArrayList<TriggerDTO>();
	}
	public UserTableDTO (
		 final DbObjectDTO               dbObjectDTO
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, 0);

		this.LOGGER = LOGGER;
		this.catdo  = catdo;

		processDatabase(_con, catdo);
	}

	/*
	 * Get
	 */
	public final int getTableRowCount() {
		return intTableRowCount;
	}
	public final ArrayList<ColumnDTO> getColumnList() {
		return columnList;
	}
	public final ArrayList<ForeignKeyDTO> getForeignKeyList() {
		return foreignKeyList;
	}
	public final ArrayList<IndexDTO> getIndexList() {
		return indexList;
	}
	public final ArrayList<PermissionDTO> getPermissionList() {
		return permissionList;
	}
	public final ArrayList<RuleDTO> getConstraintList() {
		return constraintList;
	}
	public final ArrayList<TriggerDTO> getTriggerList() {
		return triggerList;
	}

	/*
	 * Add
	 */
	public void addColumn(final ColumnDTO column) {
		columnList.add(column);
	}
	public void addIndex(final IndexDTO index) {
		indexList.add(index);
	}
	public void addForeignKey(final ForeignKeyDTO fk) {
		foreignKeyList.add(fk);
	}
	public void addPermission(final PermissionDTO permissionDTO) {
		permissionList.add(permissionDTO);
	}
	public void addColumnList(final ArrayList<ColumnDTO> columnList) {
		this.columnList.addAll(columnList);
	}
	public void addIndexList(final ArrayList<IndexDTO> indexList) {
		this.indexList.addAll(indexList);
	}
	public void addForeignKeyList(final ArrayList<ForeignKeyDTO> fkList) {
		this.foreignKeyList.addAll(fkList);
	}
	public void addPermissionList(final ArrayList<PermissionDTO> permissionList) {
		this.permissionList.addAll(permissionList);
	}
	public void addConstraintList(final ArrayList<RuleDTO> constraintList) {
		if (constraintList != null) {
			this.constraintList.addAll(constraintList);
		}
	}

	private void processDatabase (Connection _con, final CommonAllTablesDumpObject catdo) {
		class TTableStructure {
			public String  str_column_name;
			public String  str_column_nameSpace;
			public String  str_column_type;
			public String  str_column_typeSpace;
			public String  str_column_type_ext;
			public String  str_column_default;
			public String  str_nullable;
			public String  str_column_rule;
			public String  str_column_constraint;
			public Boolean is_column_identity;

			public TTableStructure (
					 final String  str_column_name
					,final String  str_column_type
					,final String  str_column_type_ext
					,final String  str_column_default
					,final String  str_nullable
					,final String  str_column_rule
					,final String  str_column_constraint
					,final Boolean is_column_identity
				) {
				this.str_column_name         = str_column_name;
				this.str_column_nameSpace    = "";
				this.str_column_type         = str_column_type;
				this.str_column_typeSpace    = "";
				this.str_column_type_ext     = str_column_type_ext;
				this.str_column_default      = str_column_default;
				this.str_nullable            = str_nullable;
				this.str_column_rule         = str_column_rule;
				this.str_column_constraint   = str_column_constraint;
				this.is_column_identity      = is_column_identity;
			}
		}

		class TTableStructureSize {
			public int size_str_column_name;
			public int size_str_column_type;
			public int size_str_column_default;
			public int size_str_nullable;

			public TTableStructureSize() {
				size_str_column_name     = 0;
				size_str_column_type     = 0;
				size_str_column_default  = 0;
				size_str_nullable        = 0;
			}
		}

		ArrayList<TTableStructure> array_tbl_struct   = new ArrayList<TTableStructure>();
		ColumnDTO                  column             = null;
		ResultSet                  rs                 = null;
		RuleDTO                    constraintDTO      = null;
		RuleDTO                    ruleDTO            = null;
		Statement                  stmt               = null;
		String                     str_column_name    = null;
		String                     str_default_name   = null;
		String                     str_sql_query      = null;
		String                     str_nullable       = null;
		String                     str_type_name      = null;
		String                     str_type_name_ext  = null;
		TTableStructure            tbl_struct         = null;
		TTableStructureSize        size_tbl_struct    = new TTableStructureSize();
		int                        int_prec           = 0;
		int                        int_scale          = 0;
		int                        int_type_length    = 0;
		int                        int_user_type      = 0;
		int                        int_column_length  = 0;
		DefaultDTO                 defaultDTO         = null;

		str_sql_query = catdo.getQueryText("GetTableStructure", "dbObjectDTO", this);
/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			  "SELECT\n"
			+ "\t c.name   AS column_name\n"
			+ "\t,t.name   AS type_name\n"
			+ "\t,c.length AS type_length\n"
			+ "\t,c.prec   AS prec\n"
			+ "\t,c.scale  AS scale\n"
			+ "\t,CASE\n"
			+ "\t\tWHEN t.usertype <= 100 THEN 1 -- internal type\n"
			+ "\t\tELSE 0 -- user type\n"
			+ "\t END AS user_type\n"
			+ "\t,CASE\n"
			+ "\t\tWHEN c.status & 8 = 8 THEN 'NULL'\n"
			+ "\t\tELSE 'NOT NULL'\n"
			+ "\tEND AS nullable\n"
			+ "\t,o.sysstat2 & 57344 AS table_lock_type\n"
			+ "\t,CASE\n"
			+ "\t\tWHEN ISNULL(o2.name, '') <> '' AND c.cdefault <> t.tdefault THEN o2.name\n"
			+ "\t\tELSE ''\n"
			+ "\tEND AS default_name\n"
			+ "\t,CASE\n"
			+ "\t\tWHEN c.domain IS NOT NULL THEN (\n"
			+ "\t\t\tSELECT oRule.name FROM sysobjects oRule WHERE oRule.id = c.domain AND oRule.id NOT IN (\n"
			+ "\t\t\t\tSELECT co.constrid FROM sysconstraints co WHERE co.constrid = c.domain AND co.colid = c.colid))\n"
			+ "\t\tELSE NULL\n"
			+ "\tEND AS rule_name\n"
			+ "\t,CASE\n"
			+ "\t\tWHEN c.domain IS NOT NULL THEN (\n"
			+ "\t\t\tSELECT oConstraint.name FROM sysobjects oConstraint, sysconstraints co WHERE oConstraint.id = c.domain AND oConstraint.id = co.constrid AND co.colid = c.colid)\n"
			+ "\t\tELSE NULL\n"
			+ "\tEND AS constraint_name\n"
			 + "\t,0 AS is_column_identity\n"
			+ "FROM\n"
			+ "\t " + dbObject.getDbName() + "..sysobjects o\n"
			+ "\t," + dbObject.getDbName() + "..syscolumns c\n"
			+ "\t," + dbObject.getDbName() + "..systypes t\n"
			+ "\t," + dbObject.getDbName() + "..sysobjects o2\n"
			+ "WHERE\n"
			+ "\to.name         = '" + dbObject.getObjectName() + "'\n"
			+ "\tAND o.type     = '" + dbObject.getObjectType() + "'\n"
			+ "\tAND c.id       = o.id\n"
			+ "\tAND t.usertype = c.usertype\n"
			+ "\tAND o2.id      =* c.cdefault\n"
			+ "ORDER BY\n"
			+ "\tc.colid ASC";
		}
		else if (getRDBMSVersion() == "mssql2000") {
			str_sql_query =
			   "SELECT\n"
			 + "\t c.[name]   AS [column_name]\n"
			 + "\t,t.[name]   AS [type_name]\n"
			 + "\t,c.[length] AS [type_length]\n"
			 + "\t,c.[prec]   AS [prec]\n"
			 + "\t,c.[scale]  AS [scale]\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN (t.[usertype] <= 100) THEN\n"
			 + "\t\t\t1 -- internal type\n"
			 + "\t\tELSE\n"
			 + "\t\t\t0 -- user type\n"
			 + "\tEND         AS [user_type]\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN (ISNULL(c.[isnullable], 0) = 0) THEN\n"
			 + "\t\t\t'NOT NULL'\n"
			 + "\t\tELSE\n"
			 + "\t\t\t'NULL'\n"
			 + "\tEND       AS [nullable]\n"
			 + "\t,NULL     AS [default_name]\n"
			 + "\t,NULL     AS [rule_name]\n"
			 + "\t,NULL     AS [constraint_name]\n"
			 + "\t,0        AS [is_column_identity]\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[dbo].[sysobjects] o\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[dbo].[sysusers] u ON\n"
			 + "\t\tu.[uid] = o.[uid]\n"
			 + "\t\tAND u.[name] = '" + dbObject.getObjectOwner() + "'\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[dbo].[syscolumns] c ON\n"
			 + "\t\tc.[id] = o.[id]\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[dbo].[systypes] t ON\n"
			 + "\t\tt.[xusertype] = c.[xusertype]\n"
			 + "WHERE\n"
			 + "\to.[name]         = '" + dbObject.getObjectName() + "'\n"
			 + "\tAND o.[type]     = '" + dbObject.getObjectType() + "'\n"
			 + "ORDER BY\n"
			 + "\tc.[colid] ASC";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t c.[name]        AS [column_name]\n"
			 + "\t,t.[name]        AS [type_name]\n"
			 + "\t,c.[max_length]  AS [type_length]\n"
			 + "\t,c.[precision]   AS [prec]\n"
			 + "\t,c.[scale]       AS [scale]\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN (ISNULL(t.[is_user_defined], 0) = 0) THEN\n"
			 + "\t\t\t1\n"
			 + "\t\tELSE\n"
			 + "\t\t\t0\n"
			 + "\tEND              AS [user_type]\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN (ISNULL(c.[is_nullable], 0) = 0) THEN\n"
			 + "\t\t\t'NOT NULL'\n"
			 + "\t\tELSE\n"
			 + "\t\t\t'NULL'\n"
			 + "\tEND              AS [nullable]\n"
			 + "\t,NULL            AS [default_name]\n"
			 + "\t,NULL            AS [rule_name]\n"
			 + "\t,NULL            AS [constraint_name]\n"
			 + "\t,c.[is_identity] AS [is_column_identity]\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[sys].[objects] o\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] s ON\n"
			 + "\t\ts.[schema_id] = o.[schema_id]\n"
			 + "\t\tAND s.[name] = '" + dbObject.getObjectSchema() + "'\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[sys].[columns] c ON\n"
			 + "\t\tc.[object_id] = o.[object_id]\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[sys].[types] t ON\n"
			 + "\t\tt.[user_type_id] = c.[user_type_id]\n"
			 + "WHERE\n"
			 + "\to.[name] = '" + dbObject.getObjectName() + "'\n"
			 + "\tAND o.[type] = '" + dbObject.getObjectType() + "'\n"
			 + "ORDER BY\n"
			 + "\tc.[column_id] ASC";
		}
*/
		if (str_sql_query != null) {
			try {
				// create statement with source database instance
				stmt = _con.createStatement();
	
				rs = stmt.executeQuery (str_sql_query);
				while (rs.next()) {
					str_column_name     = rs.getString(1) == null ? "" :  rs.getString(1).trim();
					str_type_name       = rs.getString(2) == null ? "" :  rs.getString(2).trim();
					str_type_name_ext   = "";
					int_type_length     = rs.getInt(3);
					int_prec            = rs.getInt(4);
					int_scale           = rs.getInt(5);
					int_user_type       = rs.getInt(6);
					str_nullable        = rs.getString(7) == null ? "" :  rs.getString(7).trim();
					str_default_name    = rs.getString(8) == null ? "" :  rs.getString(8).trim();
	
					if (int_user_type == 1) {
						if (
						    str_type_name.compareTo ("binary")    == 0
						 || str_type_name.compareTo ("varbinary") == 0
						) {
							str_type_name_ext = "(" + Integer.toString(int_prec) + ")";
						} else if (
							    str_type_name.compareTo ("char")    == 0
							 || str_type_name.compareTo ("varchar") == 0
							) {
								str_type_name_ext = "(" + Integer.toString(int_type_length) + ")";
						} else if (
						    str_type_name.compareTo ("nchar") == 0
						 || str_type_name.compareTo ("nvarchar") == 0
						) {
							str_type_name_ext = "(" + Integer.toString(int_type_length / 2) + ")";
						} else if (str_type_name.compareTo ("float") == 0) {
							if ((catdo.getRDBMSVersion() == "sybase") && (int_type_length == 8)) {
								str_type_name = "double precision";
							} else {
								str_type_name_ext = "(" + Integer.toString(int_type_length) + ")";
							}
						} else if (
							str_type_name.compareTo ("numeric") == 0 ||
							str_type_name.compareTo ("decimal") == 0) {
							str_type_name_ext = "(" + Integer.toString(int_prec) + "," + Integer.toString(int_scale) + ")";
						}
					}
	
					tbl_struct = new TTableStructure (
					  str_column_name    // str_column_name
					 ,str_type_name      // str_column_type
					 ,str_type_name_ext  // str_column_type_ext
					 ,str_default_name   // str_column_default
					 ,str_nullable       // str_nullable
					 ,rs.getString(9)    // str_column_rule
					 ,rs.getString(10)   // str_column_constraint
					 ,rs.getBoolean(11)  // is_column_identity
					);
	
					array_tbl_struct.add (tbl_struct);
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}

		for (Iterator<TTableStructure> it = array_tbl_struct.iterator(); it.hasNext();) {
			tbl_struct = (TTableStructure)it.next();

			if (tbl_struct.str_column_name != null) {
				if (tbl_struct.str_column_name.length() > size_tbl_struct.size_str_column_name) {
					size_tbl_struct.size_str_column_name = tbl_struct.str_column_name.length();
				}
			}
			if ((tbl_struct.str_column_type != null) && (tbl_struct.str_column_type_ext != null)) {
				if ((tbl_struct.str_column_type.length() + tbl_struct.str_column_type_ext.length()) > size_tbl_struct.size_str_column_type) {
					size_tbl_struct.size_str_column_type = tbl_struct.str_column_type.length() + tbl_struct.str_column_type_ext.length();
				}
			}
			if (tbl_struct.str_column_default != null) {
				if (tbl_struct.str_column_default.length() > size_tbl_struct.size_str_column_default) {
					size_tbl_struct.size_str_column_default = tbl_struct.str_column_default.length();
				}
			}
			if (tbl_struct.str_nullable != null) {
				if (tbl_struct.str_nullable.length() > size_tbl_struct.size_str_nullable) {
					size_tbl_struct.size_str_nullable = tbl_struct.str_nullable.length();
				}
			}
		}

		for (Iterator<TTableStructure> it = array_tbl_struct.iterator(); it.hasNext();) {
			tbl_struct = (TTableStructure)it.next();

			int_column_length = tbl_struct.str_column_name == null ? 0 : tbl_struct.str_column_name.length();
			for (int intIndex = 0; intIndex < size_tbl_struct.size_str_column_name - int_column_length; intIndex ++) {
				tbl_struct.str_column_nameSpace = tbl_struct.str_column_nameSpace + ' ';
			}

			int_column_length = (tbl_struct.str_column_type == null ? 0 : tbl_struct.str_column_type.length()) + (tbl_struct.str_column_type_ext == null ? 0 : tbl_struct.str_column_type_ext.length());
			for (int intIndex = 0; intIndex < size_tbl_struct.size_str_column_type - int_column_length; intIndex ++) {
				tbl_struct.str_column_typeSpace = tbl_struct.str_column_typeSpace + ' ';
			}
		}

		for (int i = 0; i < array_tbl_struct.size(); ++i) {
			tbl_struct = (TTableStructure) array_tbl_struct.get(i);

			if ((tbl_struct.str_column_rule != null) && (tbl_struct.str_column_rule.trim().length() > 0)) {
				ruleDTO = new RuleDTO(
					new DbObjectDTO(
						 this
						,getDbName()
						,"R"
						,tbl_struct.str_column_rule
						,getObjectOwner()
						,getObjectSchema()
					)
					,LOGGER
					,_con
					,catdo
				);
			} else {
				ruleDTO = null;
			}

			if ((tbl_struct.str_column_constraint != null) && (tbl_struct.str_column_constraint.trim().length() > 0)) {
				constraintDTO = new RuleDTO(
					new DbObjectDTO(
						 this
						,getDbName()
						,"R"
						,tbl_struct.str_column_constraint
						,getObjectOwner()
						,getObjectSchema()
					)
					,LOGGER
					,_con
					,catdo
				);
			} else {
				constraintDTO = null;
			}

			if ((tbl_struct.str_column_default != null) && (tbl_struct.str_column_default.trim().length() > 0)) {
				defaultDTO = new DefaultDTO(
					new DbObjectDTO(
						 this
						,getDbName()
						,"D"
						,tbl_struct.str_column_default
						,getObjectOwner()
						,getObjectSchema()
					)
					,LOGGER
					,_con
					,catdo
				);
			} else {
				defaultDTO = null;
			}

			column = new ColumnDTO (
			 tbl_struct.str_column_name,
			 tbl_struct.str_column_nameSpace,
			 tbl_struct.str_column_type,
			 tbl_struct.str_column_typeSpace,
			 tbl_struct.str_column_type_ext,
			 defaultDTO,
			 "NULL".compareTo(tbl_struct.str_nullable.trim()) == 0,
			 ruleDTO,
			 constraintDTO,
			 tbl_struct.is_column_identity
			);

			addColumn(column);
		}

		// indexes
		processTableIndex(_con);
		// foreign keys
		processTableReference(_con);
		// constraints
		processTableConstraints(_con);
		// triggers
		processTableTriggers(_con);
		// permissions
		addPermissionList(
			new PermissionListDTO(this, LOGGER, _con, catdo)
		);
	}

	private void processTableIndex (Connection _con) {
		IndexDTO  idx                 = null;
		ResultSet rs                  = null;
		String    str_sql_query       = null;
		Statement stmt                = null;
		String    str_idx_field       = null;
		String    str_idx_name        = null;
		String    str_idx_name_prev   = null;
		int       int_idx_clustered   = 0;
		int       int_idx_field_order = 0;
		int       int_idx_pk          = 0;
		int       int_idx_unique      = 0;

		str_sql_query = catdo.getQueryText("IndexParameters", "dbObjectDTO", this);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			   "SELECT\n"
			 + "\t i.name\n"
			 + "\t,0\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN i.indid = 1 THEN\n"
			 + "\t\t\t1\n"
			 + "\t\tELSE\n"
			 + "\t\t\t0\n"
			 + "\tEND\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN i.status & 2048 = 2048 THEN 1\n"
			 + "\t\tELSE 0\n"
			 + "\tEND\n"
			 + "\t,ik.keyno\n"
			 + "\t,c.name\n"
			 + "\t,0\n"
			 + "\t,0\n"
			 + "FROM\n"
			 + "\tsysobjects o\n"
			 + "\tINNER JOIN sysindexes i ON\n"
			 + "\t\ti.id = o.id\n"
			 + "\tINNER JOIN sysindexkeys ik ON\n"
			 + "\t\tik.id = i.id\n"
			 + "\t\tAND ik.indid = i.indid\n"
			 + "\tINNER JOIN syscolumns c ON\n"
			 + "\t\tc.id = ik.id\n"
			 + "\t\tAND c.colid = ik.colid\n"
			 + "WHERE\n"
			 + "\ti.indid > 0\n"
			 + "\tAND i.indid < 255\n"
			 + "\tAND i.minlen > 0\n"
			 + "\tAND o.name = '" + dbObject.getObjectName() + "'";
		}
		else if (getRDBMSVersion() == "mssql200") {
			str_sql_query = null;
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t i.[name]\n"
			 + "\t,i.[is_unique]\n"
			 + "\t,CASE\n"
			 + "\t\tWHEN i.[type] = 1 THEN\n"
			 + "\t\t\t1\n"
			 + "\t\tELSE\n"
			 + "\t\t\t0\n"
			 + "\tEND\n"
			 + "\t,i.[is_primary_key]\n"
			 + "\t,ic.[index_column_id]\n"
			 + "\t,c.[name]\n"
			 + "\t,0\n"
			 + "\t,0\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[sys].[objects] o\n"
			 + "\tLEFT OUTER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] s ON\n"
			 + "\t\ts.[schema_id] = o.[schema_id]\n"
			 + "\t\tAND s.[name] = '" + dbObject.getObjectSchema() + "'\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[indexes] i ON\n"
			 + "\t\ti.[object_id] = o.[object_id]\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[index_columns] ic ON\n"
			 + "\t\tic.[object_id] = i.[object_id]\n"
			 + "\t\tAND ic.[index_id] = i.[index_id]\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[columns] c ON\n"
			 + "\t\tc.[object_id] = o.[object_id]\n"
			 + "\t\tAND c.[column_id] = ic.[column_id]\n"
			 + "WHERE\n"
			 + "\to.[name] = '" + dbObject.getObjectName() + "'"
			 + "ORDER BY\n"
			 + "\tic.[index_column_id]\n";
		}
*/

		if (str_sql_query != null) {
			str_idx_name_prev = "";

			try {
				stmt = _con.createStatement();

				rs = stmt.executeQuery (str_sql_query);
				while (rs.next()) {
					str_idx_name         = rs.getString(1) == null ? "" : rs.getString(1).trim();
					int_idx_unique       = rs.getInt(2);
					int_idx_clustered    = rs.getInt(3);
					int_idx_pk           = rs.getInt(4);
					str_idx_field        = rs.getString(6) == null ? "" : rs.getString(6).trim();
					int_idx_field_order  = rs.getInt(7);

					if (str_idx_name.compareTo (str_idx_name_prev) != 0) {
						idx = new IndexDTO(
						 str_idx_name,           // strIndexName
						 (int_idx_pk > 0),       // boolIsPrimaryKey
						 (int_idx_unique > 0),   // boolIsUnique
						 (int_idx_clustered > 0) // boolIsClustered
						);

						indexList.add(idx);

						str_idx_name_prev = str_idx_name;
					}

					idx.addColumn(new IndexColumnDTO(str_idx_field, (int_idx_field_order > 0)));
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/**
	 * processTableReference
	 * create SQL statements for creation table constraints
	 */
	private void processTableReference (Connection _con) {
		Statement     stmt                     = null;
		Statement     stmt1                    = null;
		ForeignKeyDTO foreignKeyDTO            = null;
		ResultSet     rs                       = null;
		ResultSet     rs_fkc                   = null;
		String        str_constraint_name      = null;
		String        str_reference_table_name = null;
		String        str_sql_query            = null;
		String        str_sql_query_fk_columns = null;
		int           int_index                = 0;

		str_sql_query = catdo.getQueryText("ObjectReferenceKeys", "dbObjectDTO", this);
/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			   "SELECT\n"
			 + "\t o.name AS constraint_name\n"
			 + "\t,r.frgndbid\n"
			 + "\t,r.pmrydbid\n"
			 + "\t,d.name AS ref_db_name\n"
			 + "\t,r.reftabid\n"
			 + "\t,object_name(r.reftabid, r.pmrydbid)\n"
			 + "\t,NULL   reftabid_schema\n"
			 + "\t,col_name(r.tableid, fokey1)\n"
			 + "\t,col_name(r.tableid, fokey2)\n"
			 + "\t,col_name(r.tableid, fokey3)\n"
			 + "\t,col_name(r.tableid, fokey4)\n"
			 + "\t,col_name(r.tableid, fokey5)\n"
			 + "\t,col_name(r.tableid, fokey6)\n"
			 + "\t,col_name(r.tableid, fokey7)\n"
			 + "\t,col_name(r.tableid, fokey8)\n"
			 + "\t,col_name(r.tableid, fokey9)\n"
			 + "\t,col_name(r.tableid, fokey10)\n"
			 + "\t,col_name(r.tableid, fokey11)\n"
			 + "\t,col_name(r.tableid, fokey12)\n"
			 + "\t,col_name(r.tableid, fokey13)\n"
			 + "\t,col_name(r.tableid, fokey14)\n"
			 + "\t,col_name(r.tableid, fokey15)\n"
			 + "\t,col_name(r.tableid, fokey16)\n"
			 + "\t,col_name(r.reftabid, refkey1, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey2, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey3, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey4, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey5, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey6, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey7, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey8, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey9, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey10, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey11, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey12, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey13, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey14, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey15, r.pmrydbid)\n"
			 + "\t,col_name(r.reftabid, refkey16, r.pmrydbid)\n"
			 + "FROM\n"
			 + "\t " + dbObject.getDbName() + "..sysreferences r\n"
			 + "\t," + dbObject.getDbName() + "..sysobjects o\n"
			 + "\t," + dbObject.getDbName() + "..sysobjects o1\n"
			 + "\t,master..sysdatabases d\n"
			 + "WHERE\n"
			 + "\tr.tableid = o1.id\n"
			 + "\tAND o1.name = '" + dbObject.getObjectName() + "'\n"
			 + "\tAND o.id = r.constrid\n"
			 + "\tAND d.dbid = r.pmrydbid\n"
			 + "ORDER BY\n"
			 + "\to.name";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t fk.[name]    AS [constraint_name]\n"
			 + "\t,0            AS [frgndbid]\n"
			 + "\t,0            AS [pmrydbid]\n"
			 + "\t,'?'          AS [ref_db_name]\n"
			 + "\t,0            AS [reftabid]\n"
			 + "\t,o_ref.[name] AS [reftabid_name]\n"
			 + "\t,s_ref.[name] AS [reftabid_schema]\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[sys].[foreign_keys] fk\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[objects] o ON\n"
			 + "\t\to.[object_id] = fk.[parent_object_id]\n"
			 + "\t\tAND o.[name] = '" + dbObject.getObjectName() + "'\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] s ON\n"
			 + "\t\ts.[schema_id] = o.[schema_id]\n"
			 + "\t\tAND s.[name] = '" + dbObject.getObjectSchema() + "'\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[objects] o_ref ON\n"
			 + "\t\to_ref.[object_id] = fk.[referenced_object_id]\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] s_ref ON\n"
			 + "\t\ts_ref.[schema_id] = o_ref.[schema_id]\n"
			 + "WHERE\n"
			 + "\tfk.[type] = 'F'\n"
			 + "\tAND fk.[is_disabled] = 0\n";
		}
*/
		if (str_sql_query != null) {
			try {
				stmt = _con.createStatement();

				rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					str_constraint_name = rs.getObject(1) == null ? "" : rs.getString(1).trim();

					if (rs.getString(2).trim().compareTo (rs.getString(3).trim()) != 0) {
						str_reference_table_name = rs.getString(4).trim() + "..";
					} else {
						str_reference_table_name = "";
					}

					str_reference_table_name += rs.getString(6);

					foreignKeyDTO = new ForeignKeyDTO(
						 this
						,str_constraint_name
						,new DbObjectDTO (
							 this.getParent()
							,getDbName()
							,"U"
							,str_reference_table_name
							,rs.getString(7) // owner
							,rs.getString(8) // schema
						)
					);

					if (catdo.getRDBMSVersion() == "sybase") {
						for (int_index = 0; int_index < 16; int_index++) {
							foreignKeyDTO.addColumn(rs.getString(8 + int_index), rs.getString (24 + int_index));
						}
					}
					else if ((catdo.getRDBMSVersion() == "mssql2000") || (catdo.getRDBMSVersion() == "mssql2005") || (catdo.getRDBMSVersion() == "mssql2008")) {
						str_sql_query_fk_columns = catdo.getQueryText("ObjectReferenceKeyParameters", "dbObjectDTO", foreignKeyDTO);

						/*
						str_sql_query_fk_columns =
						   "SELECT\n"
						 + "\t c.[name]     AS [parent_column_name]\n"
						 + "\t,c_ref.[name] AS [referenced_column_name]\n"
						 + "FROM\n"
						 + "\t[" + dbObject.getDbName() + "].[sys].[foreign_keys] fk\n"
						 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[objects] o ON\n"
						 + "\t\to.[object_id] = fk.[parent_object_id]\n"
						 + "\t\tAND o.[name] = '" + dbObject.getObjectName() + "'\n"
						 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] s ON\n"
						 + "\t\ts.[schema_id] = o.[schema_id]\n"
						 + "\t\tAND s.[name] = '" + dbObject.getObjectSchema() + "'\n"
						 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[objects] o_ref ON\n"
						 + "\t\to_ref.[object_id] = fk.[referenced_object_id]\n"
						 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[foreign_key_columns] fkc ON\n"
						 + "\t\tfkc.[constraint_object_id] = fk.[object_id]\n"
						 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[columns] c ON\n"
						 + "\t\tc.[object_id] = o.[object_id]\n"
						 + "\t\tAND c.[column_id] = fkc.[parent_column_id]\n"
						 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[columns] c_ref ON\n"
						 + "\t\tc_ref.[object_id] = o_ref.[object_id]\n"
						 + "\t\tAND c_ref.[column_id] = fkc.[constraint_column_id]\n"
						 + "WHERE\n"
						 + "\tfk.[type] = 'F'\n"
						 + "\tAND fk.[is_disabled] = 0\n"
						 + "\tAND fk.[name] = '" + str_constraint_name + "'\n";
						*/

						stmt1 = _con.createStatement();

						rs_fkc = stmt1.executeQuery(str_sql_query_fk_columns);
						while (rs_fkc.next()) {
							foreignKeyDTO.addColumn(rs_fkc.getString(1), rs_fkc.getString(2));
						}
						rs_fkc.close();

						stmt1.close();
					}

					foreignKeyList.add(foreignKeyDTO);
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/**
	 * processTableConstraint
	 * create SQL statements for creation table constraints
	 */
	private void processTableConstraints (Connection _con) {
		String str_constraint_name = null;
		String str_sql_query       = null;

		str_sql_query = catdo.getQueryText("ObjectConstraints", "dbObjectDTO", this);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			   "SELECT\n"
			 + "o.name\n"
			 + "FROM\n"
			 + "\t " + strDbName + "..sysconstraints c\n"
			 + "\t," + strDbName + "..sysobjects o\n"
			 + "\t," + strDbName + "..sysobjects o1\n"
			 + "WHERE\n"
			 + "\to.id = c.constrid\n"
			 + "\tAND o.type = \'R\'\n"
			 + "\tAND c.colid = 0\n"
			 + "\tAND o1.id = c.tableid\n"
			 + "\tAND o1.name = \'" + strObjectName + "\'"
			 + "ORDER BY\n"
			 + "\to.id ASC";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query = null;
		}
*/
		if (str_sql_query != null) {
			try {
				Statement stmt = _con.createStatement();

				ResultSet rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					str_constraint_name = rs.getObject(1) == null ? null : rs.getString(1).trim();

					if (str_constraint_name != null) {
						constraintList.add(new RuleDTO(
							new DbObjectDTO(
								 this
								,getDbName()
								,"R"
								,str_constraint_name
								,getObjectOwner()
								,getObjectSchema()
							)
							,LOGGER
							,_con
							,catdo
						));
					}
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/**
	 * processTableConstraint
	 * create SQL statements for creation table constraints
	 */
	private void processTableTriggers (Connection _con) {
		String str_sql_query      = null;
		String str_trigger_type   = null;
		String str_trigger_name   = null;
		String str_trigger_owner  = null;
		String str_trigger_schema = null;

		str_sql_query = catdo.getQueryText("ObjectTriggers", "dbObjectDTO", this);

		if (str_sql_query != null) {
			try {
				Statement stmt = _con.createStatement();

				ResultSet rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					str_trigger_type   = rs.getObject(1) == null ? null : rs.getString(1).trim();
					str_trigger_name   = rs.getObject(2) == null ? null : rs.getString(2).trim();
					str_trigger_owner  = rs.getObject(3) == null ? null : rs.getString(3).trim();
					str_trigger_schema = rs.getObject(4) == null ? null : rs.getString(4).trim();

					if (str_trigger_name != null) {
						triggerList.add(new TriggerDTO(new DbObjectDTO(
								 this
								,getDbName()
								,str_trigger_type
								,str_trigger_name
								,str_trigger_owner
								,str_trigger_schema
							)
							,LOGGER
							,_con
							,catdo
						));
					}
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private int                       intTableRowCount;
	private ArrayList<ColumnDTO>      columnList;
	private ArrayList<IndexDTO>       indexList;
	private ArrayList<ForeignKeyDTO>  foreignKeyList;
	private ArrayList<PermissionDTO>  permissionList;
	private ArrayList<RuleDTO>        constraintList;
	private ArrayList<TriggerDTO>     triggerList;
	private Logger                    LOGGER;
	private CommonAllTablesDumpObject catdo;
}
