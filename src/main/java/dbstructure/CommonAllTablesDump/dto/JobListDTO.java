package dbstructure.CommonAllTablesDump.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dbstructure.CommonAllTablesDump.CommonAllTablesDumpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JobListDTO extends ArrayList<JobDTO> {
	public JobListDTO() {
		super();
	}
	public JobListDTO (
         Connection                      _con
		,final CommonAllTablesDumpObject catdo
	) {
		this();

		this.catdo  = catdo;

		processDatabase(_con);
	}

	private void processDatabase (Connection _con) {
		String str_sql_query              = null;
		String str_sql_query_job_step     = null;
		String str_sql_query_job_schedule = null;

		str_sql_query = catdo.getQueryText("ServerJobs", null, null);
		str_sql_query_job_step = catdo.getQueryText("ServerJobSteps", null, null);
		str_sql_query_job_schedule = catdo.getQueryText("ServerJobSchedules", null, null);
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
				LOGGER.error("Error while processing database", e);
			}
		}
	}

	private final Logger LOGGER = LoggerFactory.getLogger(JobListDTO.class);
	private CommonAllTablesDumpObject catdo;
	private static final long         serialVersionUID = 1343817251152018369L;
}
