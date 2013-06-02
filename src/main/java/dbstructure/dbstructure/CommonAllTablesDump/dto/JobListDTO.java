package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dbstructure.dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;

public final class JobListDTO extends ArrayList<JobDTO> {
	public JobListDTO() {
		super();
	}
	public JobListDTO (
		 Logger                          LOGGER
		,Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this();

		this.LOGGER = LOGGER;
		this.catdo  = catdo;

		processDatabase(_con);
	}

	private void processDatabase (Connection _con) {
		String str_sql_query              = null;
		String str_sql_query_job_step     = null;
		String str_sql_query_job_schedule = null;

		str_sql_query = catdo.getQueryText("ServerJobs", null, null);
/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query = null;
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query =
			   "SELECT\n"
			 + "\t j.[name]        AS [job_name]\n"
			 + "\t,j.[description] AS [job_description]\n"
			 + "\t,j.[enabled]     AS [job_enabled]\n"
			 + "\t,c.[name]        AS [job_category]\n"
			 + "FROM\n"
			 + "\t[msdb].[dbo].[sysjobs] j\n"
			 + "\tINNER JOIN [msdb].[dbo].[syscategories] c ON\n"
			 + "\t\tc.[category_id] = j.[category_id]\n"
			 + "ORDER BY\n"
			 + "\tj.[name] ASC";
		}
*/
		str_sql_query_job_step = catdo.getQueryText("ServerJobSteps", null, null);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query_job_step = null;
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query_job_step =
			   "SELECT\n"
			 + "\t js.[step_name]     AS [step_name]\n"
			 + "\t,js.[step_id]       AS [step_id]\n"
			 + "\t,js.[command]       AS [step_command]\n"
			 + "\t,js.[server]        AS [step_server]\n"
			 + "\t,js.[database_name] AS [database_name]\n"
			 + "FROM\n"
			 + "\t[msdb].[dbo].[sysjobs] j\n"
			 + "\tINNER JOIN [msdb].[dbo].[sysjobsteps] js ON\n"
			 + "\t\tjs.[job_id] = j.[job_id]\n"
			 + "WHERE\n"
			 + "\tj.[name] = ?\n"
			 + "ORDER BY\n"
			 + "\tjs.[step_id] ASC";
		}
*/

		str_sql_query_job_schedule = catdo.getQueryText("ServerJobSchedules", null, null);

/*
		if (getRDBMSVersion() == "sybase") {
			str_sql_query_job_step = null;
		}
		else if ((getRDBMSVersion() == "mssql2005") || (getRDBMSVersion() == "mssql2008")) {
			str_sql_query_job_schedule =
			   "SELECT\n"
			 + "\t sc.[name]                   AS [schedule_name]\n"
			 + "\t,sc.[enabled]                AS [schedule_enabled]\n"
			 + "\t,sc.[freq_type]              AS [schedule_freq_type]\n"
			 + "\t,sc.[freq_interval]          AS [schedule_freq_interval]\n"
			 + "\t,sc.[freq_subday_type]       AS [schedule_freq_subday_type]\n"
			 + "\t,sc.[freq_subday_interval]   AS [schedule_freq_subday_interval]\n"
			 + "\t,sc.[freq_relative_interval] AS [schedule_freq_relative_interval]\n"
			 + "\t,sc.[freq_recurrence_factor] AS [schedule_freq_recurrence_factor]\n"
			 + "\t,sc.[active_start_date]      AS [schedule_active_start_date]\n"
			 + "\t,sc.[active_end_date]        AS [schedule_active_end_date]\n"
			 + "\t,sc.[active_start_time]      AS [schedule_active_start_time]\n"
			 + "\t,sc.[active_end_time]        AS [schedule_active_end_time]\n"
			 + "FROM\n"
			 + "\t[msdb].[dbo].[sysjobs] j\n"
			 + "\tINNER JOIN [msdb].[dbo].[sysjobschedules] jsc ON\n"
			 + "\t\tjsc.[job_id] = j.[job_id]\n"
			 + "\tINNER JOIN [msdb].[dbo].[sysschedules] sc ON\n"
			 + "\t\tsc.[schedule_id] = jsc.[schedule_id]\n"
			 + "WHERE\n"
			 + "\tj.[name] = ?\n"
			 + "ORDER BY\n"
			 + "\tjsc.[schedule_id] ASC";
		}
*/
		if (str_sql_query != null) {
			try {
				Statement         stmt               = _con.createStatement();
				PreparedStatement pstmt_job_step     = null;
				PreparedStatement pstmt_job_schedule = null;

				if (str_sql_query_job_step != null) {
					pstmt_job_step = _con.prepareStatement(str_sql_query_job_step);
				}

				if (str_sql_query_job_schedule != null) {
					pstmt_job_schedule = _con.prepareStatement(str_sql_query_job_schedule);
				}

				ResultSet rs = stmt.executeQuery(str_sql_query);
				while (rs.next()) {
					// job
					JobDTO jobDTO = new JobDTO (
						 rs.getObject(1) == null ? ""  : rs.getString(1).trim() // strJobName
						,rs.getObject(2) == null ? ""  : rs.getString(2).trim() // strJobDescription
						,rs.getObject(3) == null ? 0   : rs.getInt(3)           // intIsEnabled
						,rs.getObject(4) == null ? ""  : rs.getString(4).trim() // strJobCategory
						,rs.getObject(5) == null ? "?" : rs.getString(5).trim() // strJobOwnerLoginName
					);

					// job steps
					if (pstmt_job_step != null) {
						pstmt_job_step.setString(1, jobDTO.getObjectName());

						ResultSet rs1 = pstmt_job_step.executeQuery();
						while (rs1.next()) {
							jobDTO.addJobStep(new JobStepDTO (
								 rs1.getString(1) // strJobStepName
								,rs1.getInt(2)    // intStepId
								,rs1.getString(3) // strJobStepCommand
								,rs1.getString(4) // strJobStepServer
								,rs1.getString(5) // strJobStepDatabaseName
								,rs1.getString(6) // strJobSubsystem
							));
						}
						rs1.close();
					}

					// job schedule
					if (str_sql_query_job_schedule != null) {
						pstmt_job_schedule.setString(1, jobDTO.getObjectName());

						ResultSet rs1 = pstmt_job_schedule.executeQuery();
						while (rs1.next()) {
							jobDTO.addJobSchedule(new JobScheduleDTO (
								 rs1.getString(1)
								,rs1.getInt(2)
								,rs1.getInt(3)
								,rs1.getInt(4)
								,rs1.getInt(5)
								,rs1.getInt(6)
								,rs1.getInt(7)
								,rs1.getInt(8)
								,rs1.getInt(9)
								,rs1.getInt(10)
								,rs1.getInt(11)
								,rs1.getInt(12)
							));
						}
						rs1.close();
					}

					add (jobDTO);
				}
				rs.close();

				if (pstmt_job_schedule != null) {
					pstmt_job_schedule.close();
				}
				if (pstmt_job_step != null) {
					pstmt_job_step.close();
				}
				stmt.close();
			} catch (SQLException e) {
				LOGGER.error(e, e);
			}
		}
	}

	private Logger                    LOGGER;
	private CommonAllTablesDumpObject catdo;
	private static final long         serialVersionUID = 1343817251152018369L;
}
