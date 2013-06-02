package dbstructure.dbstructure.CommonAllTablesDump.dto;

public final class JobStepDTO {
	public JobStepDTO (
		 final String strJobStepName
		,int          intStepId
		,final String strJobStepCommand
		,final String strJobStepServer
		,final String strJobStepDatabaseName
		,final String strJobStepSubsystem
	) {
		this.strJobStepName         = strJobStepName;
		this.intStepId              = intStepId;
		this.strJobStepCommand      = strJobStepCommand;
		this.strJobStepServer       = strJobStepServer;
		this.strJobStepDatabaseName = strJobStepDatabaseName;
		this.strJobStepSubsystem    = strJobStepSubsystem;
	}

	/*
	 * Get
	 */
	public final String getJobStepName() {
		return strJobStepName;
	}
	public int getJobStepId() {
		return intStepId;
	}
	public final String getJobStepCommand() {
		return strJobStepCommand;
	}
	public final String getJobStepServer() {
		return strJobStepServer;
	}
	public final String getJobStepDatabaseName() {
		return strJobStepDatabaseName;
	}
	public final String getJobStepSubsystem() {
		return strJobStepSubsystem;
	}

	/*
	 * Class variables
	 */
	private String strJobStepName;
	private int    intStepId;
	private String strJobStepCommand;
	private String strJobStepServer;
	private String strJobStepDatabaseName;
	private String strJobStepSubsystem;
}