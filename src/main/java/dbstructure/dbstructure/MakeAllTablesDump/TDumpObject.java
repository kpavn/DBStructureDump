package dbstructure.dbstructure.MakeAllTablesDump;

/**
 * TDumpObject
 * Structure with information about elementary dump object, such as
 * table, view, procedure, trigger...
 */
public class TDumpObject {
	public TDumpParams DumpParams;
	public String      str_db;
	public String      str_name;
	public String      str_type;
	public int         int_status;

	/**
	 * TDumpObject
	 */
	public TDumpObject (
	  final TDumpParams DumpParams,
	  final String      str_db,
	  final String      str_name,
	  final String      str_type)
	{
		this.DumpParams = DumpParams;
		this.str_db     = str_db;
		this.str_name   = str_name;
		this.str_type   = str_type;
		int_status      = 0;
	}
}
