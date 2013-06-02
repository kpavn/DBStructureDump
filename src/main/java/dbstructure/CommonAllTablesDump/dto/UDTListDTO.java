package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UDTListDTO extends ArrayList<UDTDTO> {
	public UDTListDTO() {
		super();
	}
	public UDTListDTO (
		 Connection                      _con
		,final CommonAllTablesDumpObject catdo
		,final DbObjectDTO               dbObjectDTO
	) {
		this();

		this.catdo       = catdo;
		this.dbObjectDTO = dbObjectDTO;

		processDatabase(_con);
	}

	private void processDatabase (Connection _con) {
		ResultSet         rs                = null;
		Statement         stmt              = null;
		String            str_base_type     = null;
		String            str_default_descr = null;
		String            str_default_name  = null;
		String            str_sql_query     = null;
		String            str_udt_type      = null;
		int               int_prec          = 0;
		int               int_scale         = 0;
		int               int_type_length   = 0;
		String            strObjectOwner    = null;
		String            strObjectSchema   = null;
		String            strObjectType     = null;

		str_sql_query = catdo.getQueryText("DatabaseUDT", "strDatabaseName", dbObjectDTO.getDbName());
		if (str_sql_query != null) {
			try {
				stmt = _con.createStatement();
	
				rs = stmt.executeQuery(str_sql_query);
	
				while (rs.next()) {
					str_base_type    = rs.getString(2) == null ? "" : rs.getString(2).trim();
					int_type_length  = rs.getInt(3);
					int_prec         = rs.getInt(4);
					int_scale        = rs.getInt(5);
					str_default_name = rs.getString(8) == null ? "" : rs.getString(8).trim();
					strObjectOwner   = rs.getString(9) == null ? "" : rs.getString(9).trim();
					strObjectSchema  = rs.getString(10) == null ? "" : rs.getString(10).trim();
					strObjectType    = rs.getString(11) == null ? "" : rs.getString(11).trim();
	
					if (str_base_type.compareTo ("char") == 0 ||
					    str_base_type.compareTo ("varchar") == 0 ||
					    str_base_type.compareTo ("unichar") == 0 ||
					    str_base_type.compareTo ("univarchar") == 0 ||
					    str_base_type.compareTo ("nchar") == 0 ||
					    str_base_type.compareTo ("nvarchar") == 0 ||
					    str_base_type.compareTo ("binary") == 0 ||
					    str_base_type.compareTo ("varbinary") == 0) {
						str_udt_type = str_base_type + "(" + Integer.toString(int_type_length) + ")";
					} else if (str_base_type.compareTo ("float") == 0) {
						/* Important (Sybase SQL ASE 12.5.3 server feature):
						 * for float(8) actually type - double precision
						 * for float(16) - float
						 * for double precision - float
						 */
						if (int_type_length == 8) {
							str_udt_type = "double precision";
						} else {
							str_udt_type = str_base_type;
						}
					} else if (str_base_type.compareTo ("real") == 0) {
						str_udt_type = str_base_type;
					} else if (str_base_type.compareTo ("numeric") == 0 ||
						       str_base_type.compareTo ("decimal") == 0) {
						str_udt_type = str_base_type + "(" + Integer.toString(int_prec) + "," + Integer.toString(int_scale) + ")";
					} else {
						str_udt_type = str_base_type;
					}
	
					if (str_default_name != null && str_default_name.length() > 0) {
						str_default_descr = catdo.RunDefnCopy (_con, new DbObjectDTO(
							 dbObjectDTO.getParent()
							,dbObjectDTO.getDbName()
							,strObjectType
							,str_default_name
							,strObjectOwner
							,strObjectSchema
						));
	
						str_default_descr = str_default_descr.trim();
	
						/*
						 * It is not possible to use something like if
						 * object_id(default_name) IS NOT NULL CREATE DEFAULT...
						 * due to the limitations of ASE:
						 * CREATE DEFAULT must be the first command in a query batch.
						 */
					} else {
						str_default_descr = null;
					}
	
					add(new UDTDTO(
						new DbObjectDTO(
							 dbObjectDTO.getParent()
							,dbObjectDTO.getDbName()
							,"udt"
							,rs.getObject(1) == null ? "" : rs.getString(1).trim()
							,strObjectOwner
							,strObjectSchema
						)
						,str_udt_type
						,rs.getObject(6) == null ? true : (rs.getInt(6) == 1)
						,str_default_name
						,str_default_descr
					));
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error("Process UDT", e);
			}
		}
	}

	private final Logger              LOGGER = LoggerFactory.getLogger(UDTListDTO.class);
	private CommonAllTablesDumpObject catdo;
	private DbObjectDTO               dbObjectDTO;
	private static final long         serialVersionUID = 8066304646158630387L;
}
