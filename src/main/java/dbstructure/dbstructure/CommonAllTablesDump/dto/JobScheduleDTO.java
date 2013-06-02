package dbstructure.dbstructure.CommonAllTablesDump.dto;

public final class JobScheduleDTO {
	public JobScheduleDTO (
		 final String strJobScheduleName
		,int          intJobScheduleEnabled
		,int          intJobScheduleFreqType
		,int          intJobScheduleFreqInterval
		,int          intJobScheduleFreqSubdayType
		,int          intJobScheduleFreqSubdayInterval
		,int          intJobScheduleFreqRelativeInterval
		,int          intJobScheduleFreqRecurrenceFactor
		,int          intJobScheduleActiveStartDate
		,int          intJobScheduleActiveEndDate
		,int          intJobScheduleActiveStartTime
		,int          intJobScheduleActiveEndTime
	) {
		this.strJobScheduleName                 = strJobScheduleName;
		this.intJobScheduleEnabled              = intJobScheduleEnabled;
		this.intJobScheduleFreqType             = intJobScheduleFreqType;
		this.intJobScheduleFreqInterval         = intJobScheduleFreqInterval;
		this.intJobScheduleFreqSubdayType       = intJobScheduleFreqSubdayType;
		this.intJobScheduleFreqSubdayInterval   = intJobScheduleFreqSubdayInterval;
		this.intJobScheduleFreqRelativeInterval = intJobScheduleFreqRelativeInterval;
		this.intJobScheduleFreqRecurrenceFactor = intJobScheduleFreqRecurrenceFactor;
		this.intJobScheduleActiveStartDate      = intJobScheduleActiveStartDate;
		this.intJobScheduleActiveEndDate        = intJobScheduleActiveEndDate;
		this.intJobScheduleActiveStartTime      = intJobScheduleActiveStartTime;
		this.intJobScheduleActiveEndTime        = intJobScheduleActiveEndTime;
	}

	/*
	 * Get
	 */
	public final String getJobScheduleName() {
		return strJobScheduleName;
	}
	public int getJobScheduleEnabled() {
		return intJobScheduleEnabled;
	}
	public int getJobScheduleFreqType() {
		return intJobScheduleFreqType;
	}
	public int getJobScheduleFreqInterval() {
		return intJobScheduleFreqInterval;
	}
	public int getJobScheduleFreqSubdayType() {
		return intJobScheduleFreqSubdayType;
	}
	public int getJobScheduleFreqSubdayInterval() {
		return intJobScheduleFreqSubdayInterval;
	}
	public int getJobScheduleFreqRelativeInterval() {
		return intJobScheduleFreqRelativeInterval;
	}
	public int getJobScheduleFreqRecurrenceFactor() {
		return intJobScheduleFreqRecurrenceFactor;
	}
	public int getJobScheduleActiveStartDate() {
		return intJobScheduleActiveStartDate;
	}
	public int getJobScheduleActiveEndDate() {
		return intJobScheduleActiveEndDate;
	}
	public int getJobScheduleActiveStartTime() {
		return intJobScheduleActiveStartTime;
	}
	public int getJobScheduleActiveEndTime() {
		return intJobScheduleActiveEndTime;
	}

	/*
	 * Class variables
	 */
	private String strJobScheduleName;
	private int    intJobScheduleEnabled;
	private int    intJobScheduleFreqType;
	private int    intJobScheduleFreqInterval;
	private int    intJobScheduleFreqSubdayType;
	private int    intJobScheduleFreqSubdayInterval;
	private int    intJobScheduleFreqRelativeInterval;
	private int    intJobScheduleFreqRecurrenceFactor;
	private int    intJobScheduleActiveStartDate;
	private int    intJobScheduleActiveEndDate;
	private int    intJobScheduleActiveStartTime;
	private int    intJobScheduleActiveEndTime;
}