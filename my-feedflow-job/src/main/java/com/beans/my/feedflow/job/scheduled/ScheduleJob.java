package com.beans.my.feedflow.job.scheduled;

/**
 * 任务数据
 */
public class ScheduleJob {
	
	public ScheduleJob(String jobId, String cronExpression) {
		super();
		this.jobId = jobId;
		this.cronExpression = cronExpression;
	}

	/** 任务ID*/
	private String jobId;
	
	/** 任务Cron表达式*/
	private String cronExpression;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
