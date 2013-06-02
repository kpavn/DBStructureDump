package dbstructure.CommonAllTablesDump;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import dbstructure.CommonAllTablesDump.dto.DatabaseDTO;
import dbstructure.CommonAllTablesDump.dto.DbObjectDTO;
import dbstructure.CommonAllTablesDump.dto.DefaultDTO;

/**
 * CommonAllTablesDumpObject
 * The collection of common methods in order to use them in other projects
 */
public class CommonAllTablesDumpObject {
	/**
	 * CommonAllTablesDumpObject
	 * constructor: only initialisation the variables and memory allocation
	 */
	public CommonAllTablesDumpObject (
		 Logger         LOGGER
		,VelocityEngine myVelocity
		,final NodeList nodeListTemplate
	) {
		this.LOGGER              = LOGGER;
		this.myVelocity          = myVelocity;
		this.nodeListTemplate    = nodeListTemplate;
		this.strRDBMSVersion     = null;
		this.query               = new TQuery();
	}

	/**
	 * define RDBMS system version
	 */
	public void setRDBMSVersion (
		 Connection              _con
		,HashMap<String, String> mapMakeAllTablesDumpOptions
	) {
		DatabaseMetaData dbMetaData              = null;
		String           strDatabaseProductName  = null;
		int              intDatabaseMajorVersion = 0;
		int              intDatabaseMinorVersion = 0;
		String           strQueryConfig          = null;
		boolean          bool_is_found           = false;

		try {
			dbMetaData = _con.getMetaData();

			if (dbMetaData != null) {
				strDatabaseProductName = dbMetaData.getDatabaseProductName();
				intDatabaseMajorVersion = dbMetaData.getDatabaseMajorVersion();
				intDatabaseMinorVersion = dbMetaData.getDatabaseMinorVersion();
			} else {
				LOGGER.info("Metadata are not supported.");
				return;
			}
		} catch (SQLException e) {
			LOGGER.error(e, e);
			return;
		}

		for (int int_index_template = 0; int_index_template < nodeListTemplate.getLength() && !bool_is_found; int_index_template++) {
			NodeList nodeListDatabaseProduct = ((Element)nodeListTemplate.item(int_index_template)).getElementsByTagName ("database_product");

			for (int int_index_DatabaseProduct = 0; int_index_DatabaseProduct < nodeListDatabaseProduct.getLength() && !bool_is_found; int_index_DatabaseProduct++) {
				Element element = (Element)(nodeListDatabaseProduct.item(int_index_DatabaseProduct));

				if (strDatabaseProductName.matches(element.getAttribute("name")) 
					&& Integer.toString(intDatabaseMajorVersion).matches(element.getAttribute("major_version"))
					&& Integer.toString(intDatabaseMinorVersion).matches(element.getAttribute("minor_version"))
				) {
					LOGGER.info("find configuration:" + element.getAttribute("name"));

					bool_is_found = true;

					NamedNodeMap namedNodeMapParameter = element.getAttributes();

					for (int int_index = 0; int_index < namedNodeMapParameter.getLength(); int_index++) {
						Node node = namedNodeMapParameter.item(int_index);
						mapMakeAllTablesDumpOptions.put(node.getNodeName(), node.getNodeValue());
					}

					strQueryConfig = element.getAttribute("query_config");

					try {
						DocumentBuilder documentBuilderQuery = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document        documentQuery        = documentBuilderQuery.parse (new File (strQueryConfig));

						NodeList nodeQueryList = documentQuery.getElementsByTagName ("MakeAllTablesDumpQueries");
						for (int int_index_query = 0; int_index_query < nodeQueryList.getLength(); int_index_query++) {
							NodeList nodeQList = ((Element)nodeQueryList.item(int_index_query)).getElementsByTagName ("query");

							for (int int_q_index = 0; int_q_index < nodeQList.getLength(); int_q_index++) {
								Element elementQ = (Element)(nodeQList.item(int_q_index));

								query.putQuery(
									 elementQ.getAttribute("name")
									,elementQ.getTextContent()
								);
							}
						}
					} catch (IOException e) {
						LOGGER.debug (e, e);
					} catch (Exception e) {
						LOGGER.debug (e, e);
					}

					break;
				}
			}
		}

		if (strDatabaseProductName.matches("Microsoft SQL Server")) {
			if (intDatabaseMajorVersion == 10) {
				strRDBMSVersion = "mssql2008";
			}
			else if (intDatabaseMajorVersion == 9) {
				strRDBMSVersion = "mssql2005";
			}
			else if (intDatabaseMajorVersion == 8) {
				strRDBMSVersion = "mssql2000";
			}
			else {
				strRDBMSVersion = "mssql";
			}
		}
		else if (strDatabaseProductName.matches("Adaptive Server Enterprise")) {
			strRDBMSVersion = "sybase";
		}
		else if (strDatabaseProductName.matches("PostgreSQL")) {
			strRDBMSVersion = "postgresql";
		}
		else {
			strRDBMSVersion = null;
		}
	}

	/**
	 * return currently connected RDBMS system version
	 */
	public final String getRDBMSVersion() {
		return strRDBMSVersion;
	}

	/**
	 * getRowCountObject
	 */
	public int getRowCountObject (Connection _con, final DbObjectDTO dbObject) {
		ResultSet rs            = null;
		String    str_sql_query = null;
		int       int_row_count = 0;

		str_sql_query = getQueryText("CountOfAllRows", "dbObjectDTO", dbObject);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			   "SELECT\n"
			 + "\tCOUNT(*)\n"
			 + "FROM\n"
			 + "\t" + dbObject.getDbName() + ".." + dbObject.getObjectName() + '\n';
		}
		else if (getRDBMSVersion() == "mssql2000") {
			str_sql_query =
			   "SELECT\n"
			 + "\tCOUNT(*)\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[" + dbObject.getObjectOwner() + "].[" + dbObject.getObjectName() + "]\n";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\tCOUNT(*)\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[" + dbObject.getObjectSchema() + "].[" + dbObject.getObjectName() + "]\n";
		}
*/
		try {
			Statement stmt = _con.createStatement();

			rs = stmt.executeQuery (str_sql_query);
			while (rs.next()) {
				int_row_count = rs.getInt(1);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error(e, e);
		}

		return int_row_count;
	}

	/**
	 * RunDefnCopy
	 * it call defncopy Sybase(R) utility to get the text of some database
	 * stored objects, such as: procedure, trigger, view, default...
	 */
	public String RunDefnCopy (Connection _con, final DbObjectDTO dbObject) {
		ResultSet    rs            = null;
		Statement    stmt          = null;
		String       str_sql_query = null;
		StringBuffer sb            = new StringBuffer();

		str_sql_query = getQueryText("GetObjectText", "dbObjectDTO", dbObject);
/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			 "SELECT\n"
			 + "\ts.text\n"
			 + "FROM\n"
			 + "\t " + dbObject.getDbName() + "..syscomments s\n"
			 + "\t," + dbObject.getDbName() + "..sysobjects o\n"
			 + "WHERE\n"
			 + "\ts.id = o.id\n"
			 + "\tAND o.name = '" + dbObject.getObjectName() + "'\n"
			 + "ORDER BY\n"
			 + "\t s.number ASC\n"
			 + "\t,s.colid2 ASC\n"
			 + "\t,s.colid ASC";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\tsc.[text]\n"
			 + "FROM\n"
			 + "\t[" + dbObject.getDbName() + "].[sys].[syscomments] sc\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[objects] o ON\n"
			 + "\t\to.[object_id] = sc.[id]\n"
			 + "\t\tAND o.[name] = '" + dbObject.getObjectName() + "'\n"
			 + "\tINNER JOIN [" + dbObject.getDbName() + "].[sys].[schemas] sch ON\n"
			 + "\t\tsch.[schema_id] = o.[schema_id]\n"
			 + "\t\tAND sch.[name] = '" + dbObject.getObjectSchema() + "'\n"
			 + "ORDER BY\n"
			 + "\t sc.[number] ASC\n"
			 + "\t,sc.[colid] ASC";
		}
*/
		if (str_sql_query != null) {
			try {
				stmt = _con.createStatement();
				stmt.execute(str_sql_query);

				rs = stmt.getResultSet();
				while (rs.next()) {
					sb.append(rs.getString(1));
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}

		return sb.toString().trim();
	}

	/**
	 * processDefault
	 * create SQL statements for creation default
	 */
	public DefaultDTO processDefault (Connection _con, final DbObjectDTO dbObject) {
		return (new DefaultDTO(dbObject, RunDefnCopy(_con, dbObject)));
	}

	/**
	 * getAllDbObjects
	 */
	public List<DbObjectDTO> getAllDbObjects (Connection _con, final DatabaseDTO databaseDTO) {
		ArrayList<DbObjectDTO> arrayDbObject   = new ArrayList<DbObjectDTO>();
		ResultSet              rs_sysobjects   = null;
		Statement              stmt_sysobjects = null;
		String                 strObjectName   = null;
		String                 strObjectOwner  = null;
		String                 strObjectSchema = null;
		String                 strObjectType   = null;
		String                 str_sql_query   = null;

		str_sql_query = getQueryText("GetAllDatabaseObjects", "strDatabaseName", databaseDTO.getDbName());

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			   "SELECT\n"
			 + "\t o.type\n"
			 + "\t,o.name\n"
			 + "\t,NULL\n"
			 + "\t,NULL\n"
			 + "FROM\n"
			 + "\t " + strDbName + "..sysobjects o LEFT JOIN " + strDbName + "..systabstats s ON o.id = s.id AND s.indid = 0\n"
			 + "WHERE\n"
			 + "\to.type IN ('V', 'U', 'S')\n"

			 + "UNION ALL\n"
			 + "SELECT\n"
			 + "\t o.type\n"
			 + "\t,o.name\n"
			 + "\t,NULL\n"
			 + "\t,NULL\n"
			 + "FROM\n"
			 + "\t" + strDbName + "..sysobjects o\n"
			 + "WHERE\n"
			 + "\to.type IN ('P', 'TR')\n"

			 + "UNION ALL\n"
			 + "SELECT\n"
			 + "\t o.type\n"
			 + "\t,o.name\n"
			 + "\t,NULL\n"
			 + "\t,NULL\n"
			 + "FROM\n"
			 + "\t " + strDbName + "..sysobjects o\n"
			 + "\t," + strDbName + "..systypes t\n"
			 + "WHERE\n"
			 + "\tt.usertype > 100\n"
			 + "\tAND t.tdefault > 0\n"
			 + "\tAND o.id = t.tdefault\n"
			 + "\tAND o.type IN ('D')\n"

			 + "UNION ALL\n"
			 + "SELECT\n"
			 + "\t o.type\n"
			 + "\t,o.name\n"
			 + "\t,NULL\n"
			 + "\t,NULL\n"
			 + "FROM\n"
			 + "\t " + strDbName + "..sysobjects o\n"
			 + "WHERE\n"
			 + "\to.type IN ('R')\n"
			 + "\tAND o.id NOT IN (\n"
			 + "\t\tSELECT\n"
			 + "\t\t\tc.constrid\n"
			 + "\t\t\tFROM\n"
			 + "\t\t\t\t" + strDbName + "..sysconstraints c\n"
			 + "\t)";
		}
		else if (getRDBMSVersion() == "mssql2000") {
			str_sql_query =
			   "SELECT\n"
			 + "\t o.type AS [type]\n"
			 + "\t,o.name AS [name]\n"
			 + "\t,u.name AS [owner]\n"
			 + "\t,u.name AS [schema]\n"
			 + "FROM\n"
			 + "\t" + strDbName + "..sysobjects o\n"
			 + "\tLEFT OUTER JOIN " + strDbName + "..sysusers u ON\n"
			 + "\t\tu.uid = o.uid\n"
			 + "WHERE\n"
			 + "\to.type IN ('V', 'U', 'S')\n"

			 + "UNION ALL\n"

			 + "SELECT\n"
			 + "\t o.type AS [type]\n"
			 + "\t,o.name AS [name]\n"
			 + "\t,u.name AS [owner]\n"
			 + "\t,u.name AS [schema]\n"
			 + "FROM\n"
			 + "\t" + strDbName + "..sysobjects o\n"
			 + "\tLEFT OUTER JOIN " + strDbName + "..sysusers u ON\n"
			 + "\t\tu.uid = o.uid\n"
			 + "WHERE\n"
			 + "\to.type IN ('P', 'TR')\n"

			 + "UNION ALL\n"

			 + "SELECT\n"
			 + "\t o.type AS [type]\n"
			 + "\t,o.name AS [name]\n"
			 + "\t,u.name AS [owner]\n"
			 + "\t,u.name AS [schema]\n"
			 + "FROM\n"
			 + "\t" + strDbName + "..sysobjects o\n"
			 + "\tLEFT OUTER JOIN " + strDbName + "..sysusers u ON\n"
			 + "\t\tu.uid = o.uid\n"
			 + "WHERE\n"
			 + "\to.type IN ('D')\n"

			 + "UNION ALL\n"

			 + "SELECT\n"
			 + "\t o.[type] AS [type]\n"
			 + "\t,o.[name] AS [name]\n"
			 + "\t,u.[name] AS [owner]\n"
			 + "\t,u.[name] AS [schema]\n"
			 + "FROM\n"
			 + "\t[" + strDbName + "]..[sysobjects] o\n"
			 + "\tLEFT OUTER JOIN [" + strDbName + "]..[sysusers] u ON\n"
			 + "\t\tu.[uid] = o.[uid]\n"
			 + "WHERE\n"
			 + "\to.[type] IN ('R')\n";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t o.[type] AS [type]\n"
			 + "\t,o.[name] AS [name]\n"
			 + "\t,s.[name] AS [owner]\n"
			 + "\t,s.[name] AS [schema]\n"
			 + "FROM\n"
			 + "\t[" + strDbName + "].[sys].[objects] o\n"
			 + "\tLEFT OUTER JOIN [" + strDbName + "].[sys].[schemas] s ON\n"
			 + "\t\ts.[schema_id] = o.[schema_id]\n"
			 + "WHERE\n"
			 + "\to.[type] IN ('V', 'U', 'S', 'P', 'TR', 'D', 'R')";
		}
*/

		arrayDbObject.clear();

		if (str_sql_query != null) {
			try {
				stmt_sysobjects = _con.createStatement();;
				rs_sysobjects = stmt_sysobjects.executeQuery (str_sql_query);
	
				while (rs_sysobjects.next()) {
					strObjectType   = rs_sysobjects.getObject(1) == null ? null : rs_sysobjects.getString(1).trim();
					strObjectName   = rs_sysobjects.getObject(2) == null ? null : rs_sysobjects.getString(2).trim();
					strObjectOwner  = rs_sysobjects.getObject(3) == null ? null : rs_sysobjects.getString(3).trim();
					strObjectSchema = rs_sysobjects.getObject(4) == null ? null : rs_sysobjects.getString(4).trim();
	
					arrayDbObject.add(new DbObjectDTO(
						 databaseDTO
						,databaseDTO.getDbName()
						,strObjectType
						,strObjectName
						,strObjectOwner
						,strObjectSchema
					));
				}
				rs_sysobjects.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}

		return arrayDbObject;
	}

	/**
	 * get SQL query
	 */
	protected final String getQueryText (final String strQueryId, VelocityContext ctx) {
		StringWriter sw           = new StringWriter();
		String       strQueryText = null;

		strQueryText = query.getQuery(strQueryId);

		if ((ctx != null) && (strQueryText != null)) {
			myVelocity.evaluate(ctx, sw, "strQueryText", strQueryText);
			strQueryText = sw.toString();
		}

		return strQueryText;
	}

	public final String getQueryText (final String strQueryId, final String strObjectId, final Object Object) {
		VelocityContext ctx = null;

		if ((strObjectId != null) && (Object != null)) {
			ctx = new VelocityContext();
			ctx.put(strObjectId, Object);
		}

		return getQueryText (strQueryId, ctx);
	}

	class ThreadStreamRead extends Thread {
		public ThreadStreamRead (final InputStream in, final File dirOut, String strFileNameOut) {
			this.in             = new BufferedReader(new InputStreamReader(in));
			this.dirOut         = dirOut;
			this.strFileNameOut = strFileNameOut;
		}

		public void run() {
			DataOutputStream out           = null;
			File             file_out_name = null;
			String           strLine       = null;

			try {
				while (true) {
					if ((strLine = this.in.readLine()) == null) {
						break;
					}
					if (out == null) {
						file_out_name = new File (dirOut, strFileNameOut);
						out = new DataOutputStream (
							new BufferedOutputStream (
								new FileOutputStream (file_out_name, true)));
					}
					out.writeBytes (strLine + '\n');
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				LOGGER.error(e, e);
			}
		}

		private BufferedReader in;
		private File           dirOut;
		private String         strFileNameOut;
	}

	private Logger         LOGGER;
	private String         strRDBMSVersion;
	private TQuery         query;
	private VelocityEngine myVelocity;
	private NodeList       nodeListTemplate;
}
