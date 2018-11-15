package com.beans.my.feedflow.job.scheduled;

import org.apache.log4j.Logger;

import com.beans.my.feedflow.base.model.Job;

public class JobContext {
	
	public JobContext(Job job, Logger logger,JobNotice notice){
		this.job = job;
		this.logger = logger;
		this.notice = notice;
	}
	
	public Job job;
	public Logger logger;
	public JobNotice notice;
}
