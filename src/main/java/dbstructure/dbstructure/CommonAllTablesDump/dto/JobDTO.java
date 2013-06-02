package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.util.ArrayList;

public final class JobDTO extends DbObjectDTO {
	public JobDTO (
		 final String strJobName
		,final String strJobDescription
		,int          intIsEnabled
		,final String strJobCategory
		,final String strJobOwnerLoginName
	) {
		super(
			 null       // dbParent
			,null       // strDbName
			,"job"      // strObjectType
			,strJobName // strObjectName
			,null       // strObjectOwner
			,null       //strObjectSchema
		);

		this.strJobDescription    = strJobDescription;
		this.intIsEnabled         = intIsEnabled;
		this.strJobCategory       = strJobCategory;
		this.strJobOwnerLoginName = strJobOwnerLoginName;

		this.listJobStep          = new ArrayList<JobStepDTO>();
		this.listJobSchedule      = new ArrayList<JobScheduleDTO>();
	}

	/*
	 * Get
	 */
	public final String getJobDescription() {
		return strJobDescription;
	}
	public int getIsEnabled() {
		return intIsEnabled;
	}
	public final String getJobCategory() {
		return strJobCategory;
	}
	public final String getJobOwnerLoginName() {
		return strJobOwnerLoginName;
	}
	public final ArrayList<JobStepDTO> getListJobStep() {
		return listJobStep;
	}
	public final ArrayList<JobScheduleDTO> getListJobSchedule() {
		return listJobSchedule;
	}

	/*
	 * Add
	 */
	public void addJobStep(final JobStepDTO jobStep) {
		listJobStep.add(jobStep);
	}
	public void addJobSchedule(final JobScheduleDTO jobSchedule) {
		listJobSchedule.add(jobSchedule);
	}

	/*
	 * Class variables
	 */
	private String                    strJobDescription;
	private int                       intIsEnabled;
	private String                    strJobCategory;
	private String                    strJobOwnerLoginName;
	private ArrayList<JobStepDTO>     listJobStep;
	private ArrayList<JobScheduleDTO> listJobSchedule;
}
