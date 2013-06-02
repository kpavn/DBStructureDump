package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class DatabaseDTO extends DbObjectDTO {
	public DatabaseDTO (
		final String strDbName
	) {
		super(
			 null
			,strDbName
			,"Database"
			,null
			,null
			,null
		);

		this.strDbNameSpace            = null;
		this.intDbId                   = 0;
		this.intSuId                   = 0;
		this.strOwner                  = null;
		this.intStatus                 = 0;
		this.intVersion                = 0;
		this.intLogPtr                 = 0;
		this.dateCrDate                = null;
		this.dateDumpTrDate            = null;
		this.intStatus2                = 0;
		this.intAudFlags               = 0;
		this.intDeftabaud              = 0;
		this.intDefvwaud               = 0;
		this.intDefpraud               = 0;
		this.intDefRemoteType          = 0;
		this.strDefRemoteLoc           = null;
		this.intStatus3                = 0;
		this.intStatus4                = 0;
		this.intSnapshotIsolationState = 0;
		this.listDbObject              = new ArrayList<DbObjectDTO>();
		this.listDbDevice              = new ArrayList<DatabaseDeviceDTO>();
	}
	public DatabaseDTO (
		 final String                    strDbName
		,Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this(strDbName);
		this.LOGGER = LOGGER;

		processDatabase(_con, catdo);
	}

	/*
	 * Get
	 */
	public final String getDbNameSpace() {
		return strDbNameSpace;
	}
	public long getDbSize() {
		long lintDbSize = 0L;

		for (Iterator<DatabaseDeviceDTO> it = listDbDevice.iterator(); it.hasNext();) {
			DatabaseDeviceDTO databaseDeviceDTO = (DatabaseDeviceDTO)it.next();
			lintDbSize += databaseDeviceDTO.getSize();
		}

		return lintDbSize;
	}
	public long getDbDataSize() {
		long lintDbSize = 0L;

		for (Iterator<DatabaseDeviceDTO> it = listDbDevice.iterator(); it.hasNext();) {
			DatabaseDeviceDTO databaseDeviceDTO = (DatabaseDeviceDTO)it.next();

			if (databaseDeviceDTO.isDataDevice()) {
				lintDbSize += databaseDeviceDTO.getSize();
			}
		}

		return lintDbSize;
	}
	public long getDbLogSize() {
		long lintDbSize = 0L;

		for (Iterator<DatabaseDeviceDTO> it = listDbDevice.iterator(); it.hasNext();) {
			DatabaseDeviceDTO databaseDeviceDTO = (DatabaseDeviceDTO)it.next();

			if (databaseDeviceDTO.isLogDevice()) {
				lintDbSize += databaseDeviceDTO.getSize();
			}
		}

		return lintDbSize;
	}
	public long getDbSizeKb() {
		return getDbDataSize() / 1024L;
	}
	public long getDbSizeMb() {
		return getDbDataSize() / (1024L * 1024L);
	}
	public final String getStringDbSize() {
		return String.valueOf(getDbSize());
	}
	public final String getStringDbSize(final String strFormatString) {
		return String.format(strFormatString, getDbSize());
	}
	public final String getStringDbSizeKb() {
		return String.format("%,12d", getDbSizeKb());
	}
	public final String getStringDbSizeMb() {
		return String.format("%,12d", getDbSizeMb());
	}
	public int getDbId() {
		return intDbId;
	}
	public int getSuId() {
		return intSuId;
	}
	public String getOwner() {
		return strOwner;
	}
	public int getStatus() {
		return intStatus;
	}
	public String getTextStatus() {
		String strStatus = "";

		/*
		 * status
		*/
		if ((intStatus & 4) > 0) {
			strStatus += "select into/bulkcopy/pllsort;";
		}
		if ((intStatus & 8) > 0) {
			strStatus += "trunc log on chkpt;";
		}
		if ((intStatus & 16) > 0) {
			strStatus += "no chkpt on recovery;";
		}
		if ((intStatus & 32) > 0) {
			strStatus += "for load;";
		}
		if ((intStatus & 256) > 0) {
			strStatus += "not recovered;";
		}
		if ((intStatus & 512) > 0) {
			strStatus += "ddl in tran;";
		}
		if ((intStatus & 1024) > 0) {
			strStatus += "read only;";
		}
		if ((intStatus & 2048) > 0) {
			strStatus += "dbo use only;";
		}
		if ((intStatus & 4096) > 0) {
			strStatus += "single user;";
		}
		if ((intStatus & 8192) > 0) {
			strStatus += "allow nulls by default;";
		}

		/*
		 * status2
		*/
		if ((intStatus2 & 1) > 0) {
			strStatus += "abort tran on log full;";
		}
		if ((intStatus2 & 2) > 0) {
			strStatus += "no free space acctg;";
		}
		if ((intStatus2 & 4) > 0) {
			strStatus += "auto identity;";
		}
		if ((intStatus2 & 8) > 0) {
			strStatus += "identity in nonunique index;";
		}
		if ((intStatus2 & 16) > 0) {
			strStatus += "db in auto regime;";
		}
		if ((intStatus2 & 32) > 0) {
			strStatus += "db currently in auto regime until restoration ended;";
		}
		if ((intStatus2 & 64) > 0) {
			strStatus += "db is restoring;";
		}
		if ((intStatus2 & 128) > 0) {
			strStatus += "db has strange pages;";
		}
		if ((intStatus2 & 256) > 0) {
			strStatus += "db structure has been written on disk;";
		}
		if ((intStatus2 & 512) > 0) {
			strStatus += "db under upgrade;";
		}
		if ((intStatus2 & 1024) > 0) {
			strStatus += "db in operative regime;";
		}
		if ((intStatus2 & 0xFFFF8000) > 0) {
			strStatus += "some part of db journal is not on the special device;";
		}

		return strStatus;
	}
	public int getVersion() {
		return intVersion;
	}
	public int getLogPtr() {
		return intLogPtr;
	}
	public Date getCrDate() {
		return dateCrDate;
	}
	public Date getDumpTrDate() {
		return dateDumpTrDate;
	}
	public int getStatus2() {
		return intStatus2;
	}
	public int getAudFlags() {
		return intAudFlags;
	}
	public int getDeftabaud() {
		return intDeftabaud;
	}
	public int getDefvwaud() {
		return intDefvwaud;
	}
	public int getDefpraud() {
		return intDefpraud;
	}
	public int getDefRemoteType() {
		return intDefRemoteType;
	}
	public String getDefRemoteLoc() {
		return strDefRemoteLoc == null ? "" : strDefRemoteLoc;
	}
	public int getStatus3() {
		return intStatus3;
	}
	public int getStatus4() {
		return intStatus4;
	}
	public int getSnapshotIsolationState() {
		return intSnapshotIsolationState;
	}
	public final List<DbObjectDTO> getListDbObject() {
		return Collections.unmodifiableList(listDbObject);
	}
	public final List<DatabaseDeviceDTO> getListDbDevice() {
		return Collections.unmodifiableList(listDbDevice);
	}

	/*
	 * Set
	 */
	public void setDbNameSpace (final String strDbNameSpace) {
		this.strDbNameSpace = strDbNameSpace;
	}
	public void setDbId (int intDbId) {
		this.intDbId = intDbId;
	}
	public void setSuId (int intSuId) {
		this.intSuId = intSuId;
	}
	public void setOwner (final String strOwner) {
		this.strOwner = strOwner;
	}
	public void setStatus (int intStatus) {
		this.intStatus = intStatus;
	}
	public void setVersion (int intVersion) {
		this.intVersion = intVersion;
	}
	public void setLogPtr (int intLogPtr) {
		this.intLogPtr = intLogPtr;
	}
	public void setCrDate (final Date dateCrDate) {
		this.dateCrDate = dateCrDate;
	}
	public void setDumpTrDate (final Date dateDumpTrDate) {
		this.dateDumpTrDate = dateDumpTrDate;
	}
	public void setStatus2 (int intStatus2) {
		this.intStatus2 = intStatus2;
	}
	public void setAudFlags (int intAudFlags) {
		this.intAudFlags = intAudFlags;
	}
	public void setDeftabaud (int intDeftabaud) {
		this.intDeftabaud = intDeftabaud;
	}
	public void setDefvwaud (int intDefvwaud) {
		this.intDefvwaud = intDefvwaud;
	}
	public void setDefpraud (int intDefpraud) {
		this.intDefpraud = intDefpraud;
	}
	public void setDefRemoteType (int intDefRemoteType) {
		this.intDefRemoteType = intDefRemoteType;
	}
	public void setDefRemoteLoc (final String strDefRemoteLoc) {
		this.strDefRemoteLoc = strDefRemoteLoc;
	}
	public void setStatus3 (int intStatus3) {
		this.intStatus3 = intStatus3;
	}
	public void setStatus4 (int intStatus4) {
		this.intStatus4 = intStatus4;
	}
	public void setSnapshotIsolationState(int intSnapshotIsolationState) {
		this.intSnapshotIsolationState = intSnapshotIsolationState;
	}

	/*
	 * Add
	 */
	public void addDbObject (final DbObjectDTO dbObject) {
		listDbObject.add(dbObject);
	}
	public void addDbDevice (final DatabaseDeviceDTO databaseDeviceDTO) {
		listDbDevice.add(databaseDeviceDTO);
	}

	/**
	 * get information about database parameters
	 */
	private void processDatabase (Connection _con, final CommonAllTablesDumpObject catdo) {
		ResultSet rs_db_device           = null;
		ResultSet rs_db_prm              = null;
		ResultSet rs_login               = null;
		Statement stmt                   = null;
		String    str_sql_query          = null;
		String    str_sql_query_dbdevice = null;
		String    str_sql_query_dbowner  = null;

		str_sql_query = catdo.getQueryText("DatabaseParameters", "databaseDTO", this);
/*
		// str_sql_query
		if (getRDBMSVersion() == "sybase") {
			str_sql_query =
			   "SELECT\n"
			 + "\t d.dbid\n"
			 + "\t,d.suid\n"
			 + "\t,d.status\n"
			 + "\t,d.version\n"
			 + "\t,d.logptr\n"
			 + "\t,d.crdate\n"
			 + "\t,d.dumptrdate\n"
			 + "\t,d.status2\n"
			 + "\t,d.audflags\n"
			 + "\t,d.deftabaud\n"
			 + "\t,d.defvwaud\n"
			 + "\t,d.defpraud\n"
			 + "\t,d.def_remote_type\n"
			 + "\t,d.def_remote_loc\n"
			 + "\t,d.status3\n"
			 + "\t,d.status4\n"
			 + "\t,0\n"
			 + "FROM\n"
			 + "\tmaster..sysdatabases d\n"
			 + "WHERE\n"
			 + "\td.name = \'" + databaseDTO.getDbName() + "\'";
		}
		else if (getRDBMSVersion() == "mssql2000") {
			str_sql_query =
			   "SELECT\n"
			 + "\t d.[dbid]    AS [dbid]\n"
			 + "\t,0           AS [suid]\n"
			 + "\t,d.[status]  AS [status]\n"
			 + "\t,d.[version] AS [version]\n"
			 + "\t,0           AS [logptr]\n"
			 + "\t,d.[crdate]  AS [crdate]\n"
			 + "\t,d.[crdate]  AS [dumptrdate]\n"
			 + "\t,d.[status2] AS [status2]\n"
			 + "\t,0           AS [audflags]\n"
			 + "\t,0           AS [deftabaud]\n"
			 + "\t,0           AS [defvwaud]\n"
			 + "\t,0           AS [defpraud]\n"
			 + "\t,0           AS [def_remote_type]\n"
			 + "\t,'?'         AS [def_remote_loc]\n"
			 + "\t,0           AS [status3]\n"
			 + "\t,0           AS [status4]\n"
			 + "\t,0           AS [snapshot_isolation_state]\n"
			 + "FROM\n"
			 + "\t[master]..[sysdatabases] d\n"
			 + "WHERE\n"
			 + "\td.[name] = '" + databaseDTO.getDbName() + "'";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			  "SELECT\n"
			+ "\t d.[database_id]              AS [dbid]\n"
			+ "\t,0                            AS [suid]\n"
			+ "\t,d.[state]                    AS [status]\n"
			+ "\t,d.[compatibility_level]      AS [version]\n"
			+ "\t,0                            AS [logptr]\n"
			+ "\t,d.[create_date]              AS [crdate]\n"
			+ "\t,bs.[last_db_backup_date]     AS [dumptrdate]\n"
			+ "\t,0                            AS [status2]\n"
			+ "\t,0                            AS [audflags]\n"
			+ "\t,0                            AS [deftabaud]\n"
			+ "\t,0                            AS [defvwaud]\n"
			+ "\t,0                            AS [defpraud]\n"
			+ "\t,0                            AS [def_remote_type]\n"
			+ "\t,'?'                          AS [def_remote_loc]\n"
			+ "\t,0                            AS [status3]\n"
			+ "\t,0                            AS [status4]\n"
			+ "\t,d.[snapshot_isolation_state] AS [snapshot_isolation_state]\n"
			+ "FROM\n"
			+ "\t[master].[sys].[databases] d\n"
			+ "\tLEFT OUTER JOIN (\n"
			+ "\t\tSELECT\n"
			+ "\t\t\t bs.[database_name]          AS [database_name]\n"
			+ "\t\t\t,bs.[type]                    AS [type]\n"
			+ "\t\t\t,MAX(bs.[backup_finish_date]) AS [last_db_backup_date]\n"
			+ "\t\tFROM\n"
			+ "\t\t\t[msdb].[dbo].[backupset] bs\n"
			+ "\t\tWHERE\n"
			+ "\t\t\tbs.[server_name] = @@SERVERNAME\n"
			+ "\t\tGROUP BY\n"
			+ "\t\t\t bs.[database_name]\n"
			+ "\t\t\t,bs.[type]\n"
			+ "\t\t) bs ON\n"
			+ "\t\t\tbs.[database_name] = d.[name]\n"
			+ "\t\t\tAND bs.[type] = 'D'\n"
			+ "WHERE\n"
			+ "\td.[name] = '" + databaseDTO.getDbName() + "'";
		}
*/
		str_sql_query_dbdevice = catdo.getQueryText("DatabaseDevices", "databaseDTO", this);
/*
		// str_sql_query_dbdevice
		if (getRDBMSVersion() == "sybase") {
			str_sql_query_dbdevice =
			   "SELECT\n"
			 + "\t dv.name\n"
			 + "\t,dv.phyname\n"
			 + "\t,us.size * 2048\n"
			 + "\t,us.segmap\n"
			 + "\t,us.crdate\n"
			 + "\t,CASE WHEN (us.segmap & 4) = 0) THEN 1 ELSE 0 END AS isDataDevice\n"
			 + "\t,CASE WHEN (us.segmap & 4) > 0) THEN 1 ELSE 0 END AS isLogDevice\n"
			 + "FROM\n"
			 + "\t master..sysdatabases d\n"
			 + "\t,master..sysdevices dv\n"
			 + "\t,master..sysusages us\n"
			 + "WHERE\n"
			 + "\td.name = \'" + databaseDTO.getDbName() + "\'"
			 + "\tAND us.dbid = d.dbid\n"
			 + "\tAND us.vstart BETWEEN dv.low AND dv.high";
		}
		else if (getRDBMSVersion() == "mssql2000") {
			str_sql_query_dbdevice =
			   "SELECT\n"
			 + "\t t.[name]                                              AS [name]\n"
			 + "\t,t.[filename]                                          AS [phyname]\n"
			 + "\t,CONVERT(BIGINT, t.[size]) * CONVERT(BIGINT, 8192)     AS [size]\n"
			 + "\t,NULL                                                  AS [segmap]\n"
			 + "\t,NULL                                                  AS [crdate]\n"
			 + "\t,CASE WHEN ((t.[status] & 0x40) = 0) THEN 1 ELSE 0 END AS [isDataDevice]\n"
			 + "\t,CASE WHEN ((t.[status] & 0x40) > 0) THEN 1 ELSE 0 END AS [isLogDevice]\n"
			 + "FROM\n"
			 + "\t[" + databaseDTO.getDbName() + "].[dbo].[sysfiles] t\n"
			 + "ORDER BY\n"
			 + "\tt.fileid";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query_dbdevice =
			   "SELECT\n"
			 + "\t t.[name]                                          AS [name]\n"
			 + "\t,t.[physical_name]                                 AS [phyname]\n"
			 + "\t,CONVERT(BIGINT, t.[size]) * CONVERT(BIGINT, 8192) AS [size]\n"
			 + "\t,NULL                                              AS [segmap]\n"
			 + "\t,NULL                                              AS [crdate]\n"
			 + "\t,CASE WHEN (t.[type] = 0) THEN 1 ELSE 0 END        AS [isDataDevice]\n"
			 + "\t,CASE WHEN (t.[type] = 1) THEN 1 ELSE 0 END        AS [isLogDevice]\n"
			 + "FROM\n"
			 + "\t[" + databaseDTO.getDbName() + "].[sys].[database_files] t\n"
			 + "ORDER BY\n"
			 + "\tt.file_id";
		}
*/
		str_sql_query_dbowner  = catdo.getQueryText("DatabaseOwner", "databaseDTO", this);
/*
		// str_sql_query_dbowner
		if (getRDBMSVersion() == "sybase") {
			str_sql_query_dbowner =
			   "SELECT\n"
			 + "\tl.name\n"
			 + "FROM\n"
			 + "\tmaster..syslogins l\n"
			 + "\tINNER JOIN master..sysdatabases d ON\n"
			 + "\t\td.sid = l.sid\n"
			 + "WHERE\n"
			 + "\td.name = \'" + databaseDTO.getDbName() + "\'";
		}
		else if (getRDBMSVersion() == "mssql2000") {
			str_sql_query_dbowner =
			   "SELECT\n"
			 + "\tSUSER_SNAME(d.[sid])\n"
			 + "FROM\n"
			 + "\t[master].[dbo].[sysdatabases] d\n"
			 + "WHERE\n"
			 + "\td.[name] = \'" + databaseDTO.getDbName() + "\'";
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query_dbowner =
			   "SELECT\n"
			 + "\tl.[name]\n"
			 + "FROM\n"
			 + "\t[master].[sys].[databases] d\n"
			 + "\tLEFT OUTER JOIN [master].[sys].[syslogins] l ON\n"
			 + "\t\tl.[sid] = d.[owner_sid]\n"
			 + "WHERE\n"
			 + "\td.[name] = \'" + databaseDTO.getDbName() + "\'";
		}
*/
		if (str_sql_query != null) {
			try {
				stmt = _con.createStatement();
	
				
				rs_db_prm = stmt.executeQuery(str_sql_query);
	
				while (rs_db_prm.next()) {
					setDbId                   (rs_db_prm.getInt(1));
					setSuId                   (rs_db_prm.getInt(2));
					setStatus                 (rs_db_prm.getInt(3));
					setVersion                (rs_db_prm.getInt(4));
					setLogPtr                 (rs_db_prm.getInt(5));
					setCrDate                 (rs_db_prm.getDate(6));
					setDumpTrDate             (rs_db_prm.getDate(7));
					setStatus2                (rs_db_prm.getInt(8));
					setAudFlags               (rs_db_prm.getInt(9));
					setDeftabaud              (rs_db_prm.getInt(10));
					setDefvwaud               (rs_db_prm.getInt(11));
					setDefpraud               (rs_db_prm.getInt(12));
					setDefRemoteType          (rs_db_prm.getInt(13));
					setDefRemoteLoc           (rs_db_prm.getString(14));
					setStatus3                (rs_db_prm.getInt(15));
					setStatus4                (rs_db_prm.getInt(16));
					setSnapshotIsolationState (rs_db_prm.getInt(17));
				}
				rs_db_prm.close();
	
				/*
				 * database owner
				 */
				rs_login = stmt.executeQuery(str_sql_query_dbowner);
	
				while (rs_login.next()) {
					setOwner(rs_login.getObject(1) == null ? "?" : rs_login.getString(1).trim());
				}
				rs_login.close();
	
				// database devices
				rs_db_device = stmt.executeQuery (str_sql_query_dbdevice);
	
				while (rs_db_device.next()) {
					addDbDevice(new DatabaseDeviceDTO(
					 rs_db_device.getObject(1) == null ? "?" : rs_db_device.getString(1).trim(),
					 rs_db_device.getObject(2) == null ? "?" : rs_db_device.getString(2).trim(),
					 rs_db_device.getLong(3),
					 rs_db_device.getInt(4),
					 rs_db_device.getDate(5),
					 rs_db_device.getBoolean(6),
					 rs_db_device.getBoolean(7)
					));
				}
				rs_db_device.close();
	
				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	/*
	 * Class variables
	 */
	private String                  strDbNameSpace;
	private int                     intDbId;
	private int                     intSuId;
	private String                  strOwner;
	private int                     intStatus;
	private int                     intVersion;
	private int                     intLogPtr;
	private Date                    dateCrDate;
	private Date                    dateDumpTrDate;
	private int                     intStatus2;
	private int                     intAudFlags;
	private int                     intDeftabaud;
	private int                     intDefvwaud;
	private int                     intDefpraud;
	private int                     intDefRemoteType;
	private String                  strDefRemoteLoc;
	private int                     intStatus3;
	private int                     intStatus4;
	private int                     intSnapshotIsolationState;
	private List<DbObjectDTO>       listDbObject;
	private List<DatabaseDeviceDTO> listDbDevice;
	private Logger                  LOGGER;
}
