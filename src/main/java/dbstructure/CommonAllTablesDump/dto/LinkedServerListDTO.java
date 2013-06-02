package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class LinkedServerListDTO extends ArrayList<LinkedServerDTO> {
	public LinkedServerListDTO () {
		super();
	}
	public LinkedServerListDTO (
		 Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this();
		this.LOGGER = LOGGER;

		processDatabase(_con, catdo);
	}

	private void processDatabase (Connection _con, final CommonAllTablesDumpObject catdo) {
		String            str_sql_query                      = null;
		String            str_sql_query_linked_server_logins = null;
		Statement         stmt                               = null;
		PreparedStatement pstmt_logins                       = null;

		str_sql_query = catdo.getQueryText("ServerLinkedServers", null, null);
		str_sql_query_linked_server_logins = catdo.getQueryText("ServerLinkedServerLogins", null, null);

		if (str_sql_query != null) {
			try {
				stmt = _con.createStatement();

				if (str_sql_query_linked_server_logins != null) {
					pstmt_logins = _con.prepareStatement(str_sql_query_linked_server_logins);
				} else {
					pstmt_logins = null;
				}

				ResultSet rs = stmt.executeQuery (str_sql_query);
				while (rs.next()) {
					LinkedServerDTO linkedServerDTO = new LinkedServerDTO (
						 rs.getString(1) // strLinkedServerName
						,rs.getString(2) // strLinkedServerProduct
						,rs.getString(3) // strLinkedServerProvider
						,rs.getString(4) // strLinkedServerDataSource
						,rs.getString(5) // strLinkedServerLocation
						,rs.getString(6) // strLinkedServerProviderString
						,rs.getString(7) // strLinkedServerCatalog
					);

					if (pstmt_logins != null) {
						pstmt_logins.setString(1, linkedServerDTO.getObjectName());

						ResultSet rs1 = pstmt_logins.executeQuery();
						while (rs1.next()) {
							linkedServerDTO.addLinkedServerLogin(rs1.getString(1));
						}
						rs1.close();
					}

					add(linkedServerDTO);
				}
				rs.close();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	private Logger            LOGGER;
	private static final long serialVersionUID = -918223727891828885L;
}
