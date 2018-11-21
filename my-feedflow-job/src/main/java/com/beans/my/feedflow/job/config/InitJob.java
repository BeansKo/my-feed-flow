package com.beans.my.feedflow.job.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.job.scheduled.quartz.ScheduleJob;
import com.beans.my.feedflow.job.scheduled.quartz.ScheduleTask;
import com.beans.my.feedflow.job.service.JobService;

@Component
public class InitJob {
	static final Logger logger = Logger.getLogger(InitJob.class);
	
	@Autowired
	JobService jobService;
	
	@Autowired
	ScheduleTask scheduleTask;
	
	@PostConstruct
	public void start() throws Exception{
		List<Job> jobs = jobService.list();
		for(Job job:jobs){
			if(job.isStatus()){
				logger.info("add job:" + job.getName());
				scheduleTask.addTask(new ScheduleJob(job.getId(), job.getScheduled()));
			}else{
				scheduleTask.delTask(job.getId());
			}
		}
	}
}
