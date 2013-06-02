package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(dbObjectDTO, 0);
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
				LOGGER.error("Process table", e);
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
			new PermissionListDTO(this, _con, catdo)
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
				LOGGER.error("Process indexes", e);
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
				LOGGER.error("Process foreign keys", e);
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
							,_con
							,catdo
						));
					}
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error("Process constraints", e);
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
							,_con
							,catdo
						));
					}
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error("Process triggers", e);
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
	private final Logger              LOGGER = LoggerFactory.getLogger(UserTableDTO.class);
	private CommonAllTablesDumpObject catdo;
}
