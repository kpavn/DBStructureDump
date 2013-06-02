package dbstructure.MakeAllTablesDump;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.apache.log4j.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.DateTool;

import org.w3c.dom.NodeList;

import dbstructure.CommonAllTablesDump.*;
import dbstructure.CommonAllTablesDump.dto.*;

/**
 * MakeAllTablesDumpObject
 * It make the dump of an database in the human readable form.
 */
public class MakeAllTablesDumpObject {
	/**
	 * MakeAllTablesDumpObject: only initialisation the variables
	 */
	public MakeAllTablesDumpObject (
	 final HashMap<String, String> mapMakeAllTablesDumpOptions,
	 VelocityEngine                myVelocity,
	 final NodeList                nodeListTemplate
	) {
		this.mapMakeAllTablesDumpOptions = mapMakeAllTablesDumpOptions;
		this.myVelocity                  = myVelocity;

		this.dateStartDateTime  = new Date();

		this.array_object       = new ArrayList<DbObjectDTO>();
		this.int_active_threads = 0;
		this.objectWait         = new Object();
		this.needToWait         = true;
		this.LOGGER             = Logger.getLogger (MakeAllTablesDumpObject.class);

		this.commonAllTablesDumpObject = new CommonAllTablesDumpObject (
			LOGGER,
			myVelocity,
			nodeListTemplate
		);
	}

	/*
	 * Get
	 */
	public final HashMap<String, String> getMapMakeAllTablesDumpOptions() {
		return mapMakeAllTablesDumpOptions;
	}
	public final String getServerText() {
		return mapMakeAllTablesDumpOptions.get("server").replaceAll(":", "_");
	}

	/**
	 * Get the net database object to dump them
	 */
	public synchronized Object GetNextObject() {
		if (array_object.isEmpty()) {
			return null;
		}

		return array_object.remove(0);
	}

	/**
	 * Notify, that the child thread is finished
	 */
	public synchronized void NotifyThreadDeath() {
		if ((--int_active_threads) == 0) {
			synchronized (this.objectWait) {
				this.needToWait = false;
				this.objectWait.notifyAll();
			}
		}
	}

	/**
	 * It makes dump in the text form for the databases
	 */
	public void DumpDatabase(
		TDumpParams DumpParams
	) throws SQLException, ClassNotFoundException {
		Connection                         _con                = null;
		ArrayList<MakeAllTablesDumpThread> array_thread        = new ArrayList<MakeAllTablesDumpThread>();
		ArrayList<DatabaseDTO>             arrayInstanceObject = new ArrayList<DatabaseDTO>();
		ResultSet                          rs_sysdatabases     = null;
		VelocityContext                    ctx                 = new VelocityContext();
		DatabaseMetaData                   dbMetaData          = null;
		int                                intMaxLengthDbName  = 0;
		int                                intThreads          = 0;
		ArrayList<DbObjectDTO>             arrayDbObject       = new ArrayList<DbObjectDTO>();

		ctx.put("number", new NumberTool());

		_con = getConnection();

		dbMetaData = _con.getMetaData();

		LOGGER.info("getDatabaseProductName:" + dbMetaData.getDatabaseProductName());
		LOGGER.info("getDatabaseProductVersion:" + dbMetaData.getDatabaseProductVersion());
		LOGGER.info("getDatabaseMajorVersion:" + Integer.toString(dbMetaData.getDatabaseMajorVersion()));
		LOGGER.info("getDatabaseMinorVersion:" + Integer.toString(dbMetaData.getDatabaseMinorVersion()));
		LOGGER.info("getCatalogSeparator:" + dbMetaData.getCatalogSeparator());
		LOGGER.info("getCatalogTerm:" + dbMetaData.getCatalogTerm());
		LOGGER.info("getSchemaTerm:" + dbMetaData.getSchemaTerm());

		// set RDBMS version. Currently supported RDBMS systems:
		//  Sybase ASE 11.x - 'sybase'
		//  MS SQL 2000     - 'mssql2000'
		//  MS SQL 2005     - 'mssql2005'
		//  MS SQL 2008     - 'mssql2008'
		commonAllTablesDumpObject.setRDBMSVersion(_con, mapMakeAllTablesDumpOptions);

		/*
		 * logins
		 */
		if (mapMakeAllTablesDumpOptions.get("logins").compareTo("true") == 0) {
			DbObjectDTO dbObjectLogin = new DbObjectDTO(
				 null    // dbParent
				,null    // strDbName
				,"login" // strObjectType
				,null    // strObjectName
				,null    // strObjectOwner
				,null    // strObjectSchema
			);
			array_object.add(dbObjectLogin);
		}

		/*
		 * categories
		 */
		if (mapMakeAllTablesDumpOptions.get("categories").compareTo("true") == 0) {
			DbObjectDTO dbObjectCategory = new DbObjectDTO(
				 null       // dbParent
				,null       // strDbName
				,"category" // strObjectType
				,null       // strObjectName
				,null       // strObjectOwner
				,null       // strObjectSchema
			);
			array_object.add(dbObjectCategory);
		}

		/*
		 * jobs
		 */
		if (mapMakeAllTablesDumpOptions.get("jobs").compareTo("true") == 0) {
			DbObjectDTO dbObjectJob = new DbObjectDTO(
				 null  // dbParent
				,null  // strDbName
				,"job" // strObjectType
				,null  // strObjectName
				,null  // strObjectOwner
				,null  // strObjectSchema
			);
			array_object.add(dbObjectJob);
		}

		/*
		 * linked servers
		 */
		if (mapMakeAllTablesDumpOptions.get("linkedservers").compareTo("true") == 0) {
			DbObjectDTO dbObjectLinkedServer = new DbObjectDTO(
				 null           // dbParent
				,null           // strDbName
				,"linkedServer" // strObjectType
				,null           // strObjectName
				,null           // strObjectOwner
				,null           // strObjectSchema
			);
			array_object.add(dbObjectLinkedServer);
		}

		arrayInstanceObject.clear();

		rs_sysdatabases = dbMetaData.getCatalogs();
		while(rs_sysdatabases.next()) {
			String str_db = rs_sysdatabases.getObject(1) == null ? null : rs_sysdatabases.getString(1).trim();

			if ((str_db == null) || (!DumpParams.isDbExists (str_db))) {
				continue;
			}

			arrayInstanceObject.add(new DatabaseDTO(str_db, LOGGER, _con, commonAllTablesDumpObject));
		}
		rs_sysdatabases.close();

		intMaxLengthDbName = 0;
		for (Iterator<DatabaseDTO> it = arrayInstanceObject.iterator(); it.hasNext();) {
			DatabaseDTO databaseDTO = (DatabaseDTO) it.next();

			if (databaseDTO.getDbName().length() > intMaxLengthDbName) {
				intMaxLengthDbName = databaseDTO.getDbName().length();
			}
		}

		for (Iterator<DatabaseDTO> it = arrayInstanceObject.iterator(); it.hasNext();) {
			DatabaseDTO databaseDTO    = (DatabaseDTO)it.next();
			String      strDbNameSpace = "";

			for (int intIndex = 0; intIndex < intMaxLengthDbName - databaseDTO.getDbName().length(); intIndex++) {
				strDbNameSpace = strDbNameSpace + ' ';
			}

			databaseDTO.setDbNameSpace(strDbNameSpace);
		}

		for (Iterator<DatabaseDTO> it = arrayInstanceObject.iterator(); it.hasNext();) {
			DatabaseDTO databaseDTO = (DatabaseDTO)it.next();

			arrayDbObject.clear();
			arrayDbObject.addAll(commonAllTablesDumpObject.getAllDbObjects(_con, databaseDTO));

			for (Iterator<DbObjectDTO> itObjectDTO = arrayDbObject.iterator(); itObjectDTO.hasNext();) {
				DbObjectDTO dbObjectDTO = itObjectDTO.next();

				if (DumpParams.getDbOptionValue(dbObjectDTO, "text").compareTo("true") == 0) {
					array_object.add(dbObjectDTO);
				}
				if (DumpParams.getDbOptionValue(dbObjectDTO, "bcp", "run").compareTo("true") == 0) {
					make_bcp (dbObjectDTO, DumpParams);
				}
				if (DumpParams.getDbOptionValue(dbObjectDTO, "count").compareTo("true") == 0) {
					writeRowCountObject (dbObjectDTO, _con);
				}
			}

			// index list
			if (mapMakeAllTablesDumpOptions.get("datatypes").compareTo("true") == 0) {
				ctx.put("DatabaseDTO", databaseDTO);
				ctx.put("DbObjectsInfo", arrayDbObject);
				writeVelocityContext (databaseDTO, databaseDTO.getDbName(), null, "indexDatabase", null, null, ctx);
			}
		}

		// index list (list of databases in instance)
		ctx.put("InstanceObjectsInfo", arrayInstanceObject);
		writeVelocityContext (null, null, null, "indexInstance", null, null, ctx);

		if (mapMakeAllTablesDumpOptions.containsKey("threads")) {
			String strThreads = mapMakeAllTablesDumpOptions.get("threads");

			try {
				intThreads = Integer.valueOf(strThreads);
			} catch (NumberFormatException e) {
				LOGGER.error(e, e);
			}
		}

		for (int int_index = 0; int_index < intThreads; int_index++) {
			synchronized (objectWait) {
				int_active_threads++;
			}

			MakeAllTablesDumpThread t = new MakeAllTablesDumpThread (this);
			t.start();
			array_thread.add (t);
		}

		try {
			synchronized (this.objectWait) {
				while (this.needToWait) {
					this.objectWait.wait();
				}
			}
		} catch (InterruptedException e) {
			LOGGER.error(e, e);
		}

		LOGGER.info("all threads finished");
	}

	/**
	 * DumpObject
	 * It makes dump in the text form for an object
	 */
	public void DumpObject (Connection _con, final DbObjectDTO dbObjectDTO) {
		try {
			if (dbObjectDTO.getObjectType().equals("U") || dbObjectDTO.getObjectType().equals("S")) {
				processTable (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("P")) {
				processProcedure (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("V")) {
				processView (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("TR")) {
				processTrigger (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("R")) {
				processRule (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("D")) {
				processDefault (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("User")) {
				processUser (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("Group")) {
				processGroup (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("linkedServer")) {
				processLinkedServer (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("job")) {
				processJob (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("category")) {
				processCategory (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("login")) {
				processLogin (_con, dbObjectDTO);
			} else if (dbObjectDTO.getObjectType().equals("udt")) {
				processUdt (_con, dbObjectDTO);
			} else {
				LOGGER.error("unknown object type:" + dbObjectDTO.getObjectType() + ";Object Name:" + dbObjectDTO.getObjectName());
			}
		} catch (SQLException e) {
			LOGGER.error(e, e);
		}
	}

	/**
	 * getConnection
	 * generate new database connection
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Properties _props              = null;
		String     str_jdbc_connection = null;

		// Load the driver
		Class.forName(mapMakeAllTablesDumpOptions.get("driver"));

		_props = new Properties();
		_props.put ("user",     mapMakeAllTablesDumpOptions.get("login_name"));
		_props.put ("password", mapMakeAllTablesDumpOptions.get("login_password"));

		if ((mapMakeAllTablesDumpOptions.get("instance") != null) && (mapMakeAllTablesDumpOptions.get("instance").length() > 0)) {
			str_jdbc_connection = mapMakeAllTablesDumpOptions.get("connection") + mapMakeAllTablesDumpOptions.get("server") + ";instance=" + mapMakeAllTablesDumpOptions.get("instance") + mapMakeAllTablesDumpOptions.get("connection_property");
		} else {
			str_jdbc_connection = mapMakeAllTablesDumpOptions.get("connection") + mapMakeAllTablesDumpOptions.get("server") + mapMakeAllTablesDumpOptions.get("connection_property");
		}
		LOGGER.info("str_jdbc_connection:" + str_jdbc_connection);

		// Attempt to connect to a driver.
		return DriverManager.getConnection (str_jdbc_connection, _props);
	}

	/**
	 * writeObjectInfo
	 * str_db             - database name (we split objects by directories by DB name)
	 * str_type           - object type (U,V,P,TR,..) we create file extension, based on the type
	 * str_name           - object name we create file name as the object name
	 * str_info           - what need to written in file
	 * bool_append        - add to existing file or create a new one
	 * str_template       - (dbstructure,html,tex,...) the new top level directory will be created
	 * str_post_extension - (html,tex,...) the string for postfix extension
	 */
	private void writeObjectInfo (
		 final DbObjectDTO dbObject
		,final String      str_info
		,boolean           bool_append
		,final String      str_template
	) {
		VelocityContext ctx           = new VelocityContext();
		StringWriter    sw            = new StringWriter();
		String          str_file_name = null;

		ctx.put("date", new DateTool());
		ctx.put("dateStart", dateStartDateTime);
		ctx.put("dbObjectDTO", dbObject);
		ctx.put("objectFileExtension", "dbstructure");
		ctx.put("Parameters", this);

		myVelocity.evaluate(ctx, sw, "writeObjectInfo", mapMakeAllTablesDumpOptions.get("output_file_name"));
		str_file_name = sw.toString();

		try {
			// create file
			File file_out_name = new File(str_file_name);

			// create parent directory(ies)
			File dir_out_name = new File(file_out_name.getParent());
			if (!dir_out_name.isDirectory()) {
				dir_out_name.mkdirs();
			}

			DataOutputStream out =
				new DataOutputStream (
					new BufferedOutputStream (
						new FileOutputStream (file_out_name, bool_append)));

			if (str_info != null) {
				out.writeBytes (str_info);
			}

			out.close();
		} catch (IOException e) {
			LOGGER.error(e, e);
		}
	}

	/**
	 * processTable
	 */
	private void processTable (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		VelocityContext ctx = new VelocityContext();
		ctx.put("tableInfo", new UserTableDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext (dbObject, ctx);
	}

	/**
	 * processProcedure
	 */
	private void processProcedure (Connection _con, final DbObjectDTO dbObject) {
		VelocityContext ctx = new VelocityContext();
		ctx.put("procedureInfo", new ProcedureDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext (dbObject, ctx);
	}

	/**
	 * processView
	 */
	private void processView (Connection _con, final DbObjectDTO dbObject) {
		VelocityContext ctx = new VelocityContext();
		ctx.put("viewInfo", new ViewDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext(dbObject, ctx);
	}

	/**
	 * processTrigger
	 */
	private void processTrigger (Connection _con, final DbObjectDTO dbObject) {
		VelocityContext ctx = new VelocityContext();
		ctx.put("triggerInfo", new TriggerDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext(dbObject, ctx);
	}

	/**
	 * processRule
	 */
	private void processRule (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		VelocityContext ctx = new VelocityContext();
		ctx.put("ruleInfo", new RuleDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext(dbObject, ctx);
	}

	/**
	 * processDefault
	 */
	private void processDefault (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		VelocityContext ctx = new VelocityContext();
		ctx.put("defaultInfo", commonAllTablesDumpObject.processDefault(_con, dbObject));
		writeVelocityContext (dbObject, ctx);
	}

	/**
	 * processUser
	 */
	private void processUser (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		VelocityContext ctx = new VelocityContext();
		ctx.put("userInfo", new UserDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext (dbObject, ctx);
	}

	/**
	 * processGroup
	 */
	private void processGroup (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		VelocityContext ctx = new VelocityContext();
		ctx.put("groupInfo", new GroupDTO(dbObject, LOGGER, _con, commonAllTablesDumpObject));
		writeVelocityContext (dbObject, ctx);
	}

	/**
	 * processLinkedServer
	 */
	private void processLinkedServer (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		if (mapMakeAllTablesDumpOptions.containsKey("linkedservers")) {
			if (mapMakeAllTablesDumpOptions.get("linkedservers").compareTo("true") == 0) {
				LinkedServerListDTO linkedServerListDTO = new LinkedServerListDTO (
					 LOGGER
					,_con
					,commonAllTablesDumpObject
				);
	
				for (Iterator<LinkedServerDTO> it = linkedServerListDTO.iterator(); it.hasNext();) {
					LinkedServerDTO     linkedServerDTO = (LinkedServerDTO) it.next();
					VelocityContext ctx = new VelocityContext();

					ctx.put("linkedServerInfo", linkedServerDTO);

					writeVelocityContext (
						 linkedServerDTO
						,ctx
					);
				}
			}
		}
	}

	/**
	 * processJob
	 */
	private void processJob (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		if (mapMakeAllTablesDumpOptions.containsKey("jobs")) {
			if (mapMakeAllTablesDumpOptions.get("jobs").compareTo("true") == 0) {
				JobListDTO jobListDTO = new JobListDTO (
					 LOGGER
					,_con
					,commonAllTablesDumpObject
				);

				for (Iterator<JobDTO> it = jobListDTO.iterator(); it.hasNext();) {
					JobDTO          jobDTO = (JobDTO) it.next();
					VelocityContext ctx    = new VelocityContext();

					ctx.put("jobInfo", jobDTO);

					writeVelocityContext (
						 jobDTO
						,ctx
					);
				}
			}
		}
	}

	/**
	 * processCategory
	 */
	private void processCategory (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		if ((mapMakeAllTablesDumpOptions.containsKey("categories")) && (mapMakeAllTablesDumpOptions.get("categories").compareTo("true") == 0)) {
			CategoryListDTO categoryListDTO = new CategoryListDTO (
				 LOGGER
				,_con
				,commonAllTablesDumpObject
			);

			for (Iterator<CategoryDTO> it = categoryListDTO.iterator(); it.hasNext();) {
				CategoryDTO     categoryDTO = (CategoryDTO) it.next();
				VelocityContext ctx         = new VelocityContext();

				ctx.put("categoryInfo", categoryDTO);

				writeVelocityContext (
					 categoryDTO
					,ctx
				);
			}
		}
	}

	/**
	 * processLogin
	 */
	private void processLogin (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		if ((mapMakeAllTablesDumpOptions.containsKey("logins")) && (mapMakeAllTablesDumpOptions.get("logins").compareTo("true") == 0)) {
			LoginListDTO loginListDTO = new LoginListDTO (
				 LOGGER
				,_con
				,commonAllTablesDumpObject
			);

			for (Iterator<LoginDTO> it = loginListDTO.iterator(); it.hasNext();) {
				LoginDTO        loginDTO = (LoginDTO) it.next();
				VelocityContext ctx      = new VelocityContext();

				ctx.put("loginInfo", loginDTO);

				writeVelocityContext (
					 loginDTO
					,ctx
				);
			}
		}
	}

	/**
	 * processUdt
	 */
	private void processUdt (Connection _con, final DbObjectDTO dbObject) throws SQLException {
		UDTListDTO udtListDTO = new UDTListDTO (
			 LOGGER
			,_con
			,commonAllTablesDumpObject
			,dbObject
		);

		for (Iterator<UDTDTO> it = udtListDTO.iterator(); it.hasNext();) {
			UDTDTO          udtDTO = (UDTDTO) it.next();
			VelocityContext ctx    = new VelocityContext();

			ctx.put("udtInfo", udtDTO);

			writeVelocityContext (
				 udtDTO
				,ctx
			);
		}
	}

	private void make_bcp (final DbObjectDTO dbObject, final TDumpParams DumpParams) {
		File            file_bcp                    = null;
		File            file_bcp_sorted             = null;
		String          str_bcp_parameters          = null;
		String          str_cmd                     = null;
		long            long_file_bcp_length        = 0L;
		long            long_file_bcp_sorted_length = 0L;
		VelocityContext ctx                         = new VelocityContext();
		StringWriter    sw                          = new StringWriter();
		String          str_file_name               = null;

		ctx.put("date", new DateTool());
		ctx.put("dateStart", dateStartDateTime);
		ctx.put("dbObjectDTO", dbObject);
		ctx.put("objectFileExtension", "bcp");

		myVelocity.evaluate(ctx, sw, "make_bcp", mapMakeAllTablesDumpOptions.get("output_file_name"));
		str_file_name = sw.toString();

		// create file
		file_bcp = new File(str_file_name);

		// create parent directory(ies)
		File dir_out_name = new File(file_bcp.getParent());
		if (!dir_out_name.isDirectory()) {
			dir_out_name.mkdirs();
		}

		str_cmd =
		   "bcp"
		 + ' ' + dbObject.getDbName() + ".." + dbObject.getObjectName()
		 + " out"
		 + ' ' + file_bcp.getPath()
		 + " -S " + mapMakeAllTablesDumpOptions.get("server")
		 + " -U " + mapMakeAllTablesDumpOptions.get("login_name")
		 + " -P " + mapMakeAllTablesDumpOptions.get("login_password");

		str_bcp_parameters = DumpParams.getDbOptionValue(dbObject, "bcp", "parameters");

		if (str_bcp_parameters.length() > 0) {
			str_cmd = str_cmd + ' ' + str_bcp_parameters;
		}

		exec_cmd (dbObject, str_cmd, file_bcp.getParent());

		/**
		 * only if the "bcp_sort" flag is set on "true", we have to sort a bcp file
		 */
		if (DumpParams.getDbOptionValue(dbObject, "bcp", "sort").compareTo("true") == 0) {
			long_file_bcp_length = file_bcp.length();
			file_bcp_sorted = new File (file_bcp.getPath() + ".sorted");

			str_cmd =
			   "sort"
			 + " /LOCALE C"
			 + " /RECORD_MAXIMUM 65535"
			 + ' ' + file_bcp.getPath()
			 + " /OUTPUT"
			 + ' ' + file_bcp_sorted.getPath();

			exec_cmd (dbObject, str_cmd, file_bcp.getParent());

			long_file_bcp_sorted_length = file_bcp_sorted.length();

			if (long_file_bcp_sorted_length == long_file_bcp_length) {
				file_bcp.delete();
				file_bcp_sorted.renameTo (file_bcp);
			} else {
				LOGGER.error("Error by sorting " + file_bcp.getPath());
				file_bcp_sorted.delete();
			}
		}
	}

	private void writeRowCountObject (final DbObjectDTO dbObjectDTO, Connection _con) {
		VelocityContext ctx = new VelocityContext();

		ctx.put("tableInfo", dbObjectDTO);
		ctx.put("countInfo", commonAllTablesDumpObject.getRowCountObject(_con, dbObjectDTO));

		dbObjectDTO.setObjectType(dbObjectDTO.getObjectType() + ".count");
		writeVelocityContext(dbObjectDTO, ctx);
	}

	/**
	 * writeVelocityContext
	 */
	private void writeVelocityContext (
	 final DbObjectDTO     dbParent,
	 final String          strDbName,
	 final String          strObjectType,
	 final String          strObjectName,
	 final String          strObjectOwner,
	 final String          strObjectSchema,
	 final VelocityContext velocityContext
	) {
		writeVelocityContext (new DbObjectDTO(
				 dbParent
				,strDbName
				,strObjectType
				,strObjectName
				,strObjectOwner
				,strObjectSchema
			)
			,velocityContext
		);
	}

	private void writeVelocityContext (
	 final DbObjectDTO     dbObjectDTO,
	 final VelocityContext velocityContext
	) {
		Template     tpl                 = null;
		String       strTemplateFileName = null;
		StringWriter sw                  = new StringWriter();

		if (mapMakeAllTablesDumpOptions.containsKey("template")) {
			if (dbObjectDTO.getObjectType() != null) {
				strTemplateFileName = "templates" + File.separator + mapMakeAllTablesDumpOptions.get("template") +  File.separator + dbObjectDTO.getObjectTypeText() + ".vm";
			} else {
				strTemplateFileName = "templates" + File.separator + mapMakeAllTablesDumpOptions.get("template") +  File.separator + dbObjectDTO.getObjectNameText() + ".vm";
			}

			try {
				if (myVelocity.resourceExists(strTemplateFileName)) {
					tpl = myVelocity.getTemplate(strTemplateFileName);
					tpl.merge (velocityContext, sw);
					writeObjectInfo (dbObjectDTO, sw.toString(), true, mapMakeAllTablesDumpOptions.get("template"));
				} else {
					LOGGER.error("template '" + strTemplateFileName + "' is not exists");
				}
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.error("writeVelocityContext:error\nstr_template is not defined;" + "\nstr_type:" + dbObjectDTO.getObjectType());
		}
	}

	class ThreadStreamRead extends Thread {
		public ThreadStreamRead (
		 final InputStream in,
		 final File        dirOut,
		 final String      strFileNameOut
		) {
			this.in             = new BufferedReader(new InputStreamReader(in));
			this.dirOut         = dirOut;
			this.strFileNameOut = strFileNameOut;
		}

		public void run() {
			String           strLine       = "";
			File             file_out_name = null;
			DataOutputStream out           = null;

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

	private void exec_cmd (final DbObjectDTO dbObject, final String strCmd, final String strDirectory) {
		File dirTypeDirectory = new File(strDirectory);

		try {
			Process p = Runtime.getRuntime().exec(strCmd);
			new ThreadStreamRead (p.getInputStream(), dirTypeDirectory, dbObject.getObjectName() + ".out_log").start();
			new ThreadStreamRead (p.getErrorStream(), dirTypeDirectory, dbObject.getObjectName() + ".err_log").start();
			p.waitFor();
		} catch (IOException e) {
			LOGGER.error(e, e);
		} catch (InterruptedException e) {
			LOGGER.error(e, e);
		}
	}

	private HashMap<String, String>   mapMakeAllTablesDumpOptions;
	private VelocityEngine            myVelocity;

	private ArrayList<DbObjectDTO>    array_object;
	private int                       int_active_threads;
	private boolean                   needToWait;
	private final Object              objectWait;
	private final Logger              LOGGER;
	private CommonAllTablesDumpObject commonAllTablesDumpObject;

	private Date                      dateStartDateTime;
}
