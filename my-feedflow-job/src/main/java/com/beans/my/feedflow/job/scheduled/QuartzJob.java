package com.beans.my.feedflow.job.scheduled;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QuartzJob extends QuartzJobBean{

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
	}

}