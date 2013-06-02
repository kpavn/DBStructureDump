package dbstructure.CommonAllTablesDump;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.xml.sax.SAXException;

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
         VelocityEngine myVelocity
		,final NodeList nodeListTemplate
	) {
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
			LOGGER.error("Error while determnine db version", e);
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
					} catch (IOException | ParserConfigurationException | SAXException e) {
						LOGGER.debug ("Error while setup config for db version", e);
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

		try {
			Statement stmt = _con.createStatement();

			rs = stmt.executeQuery (str_sql_query);
			while (rs.next()) {
				int_row_count = rs.getInt(1);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Error while get # of rows", e);
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
				LOGGER.error("Error while get object text", e);
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
				LOGGER.error("Error while get list of database objects", e);
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
				LOGGER.error("Error in stdin read", e);
			}
		}

		private BufferedReader in;
		private File           dirOut;
		private String         strFileNameOut;
	}

	private final Logger   LOGGER = LoggerFactory.getLogger(CommonAllTablesDumpObject.class);
	private String         strRDBMSVersion;
	private TQuery         query;
	private VelocityEngine myVelocity;
	private NodeList       nodeListTemplate;
}
