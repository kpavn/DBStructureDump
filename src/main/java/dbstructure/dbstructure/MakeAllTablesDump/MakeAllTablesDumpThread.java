package dbstructure.dbstructure.MakeAllTablesDump;

import java.sql.*;
import dbstructure.dbstructure.CommonAllTablesDump.dto.DbObjectDTO;

public class MakeAllTablesDumpThread extends Thread {
	MakeAllTablesDumpThread (MakeAllTablesDumpObject obj) {
		this.obj = obj;
	}

	public void run() {
		DbObjectDTO dbObjectDTO = null;
		Connection _con         = null;

		try {
			// Attempt to connect to a driver.
			_con = obj.getConnection();

			while ((dbObjectDTO = (DbObjectDTO)(obj.GetNextObject())) != null) {
				obj.DumpObject (
				 _con
				 ,dbObjectDTO);
			}

			_con.close();
			_con = null;
		} catch (SQLException e) {
			System.err.println (e.getMessage());
		}
		catch (ClassNotFoundException e) {
			System.err.println (e.getMessage());
		}
		finally {
			try {
				// Close the connection
				if (_con != null) {
					_con.close();
				}
			} catch (SQLException e) {
			}
		}

		obj.NotifyThreadDeath();
	}

	private MakeAllTablesDumpObject obj;
}
