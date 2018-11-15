package com.beans.my.feedflow.job.scheduled;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beans.my.feedflow.job.service.JobService;

@Service
public class ExecuteJob {
	private static final Logger LOGGER = Logger.getLogger(ExecuteJob.class);
	
	@Autowired
	JobService jobService;
	/**
	 * 开始执行Job
	 * @param jobId
	 * @throws Exception
	 */
	public void execute(String jobId) throws Exception{
		LOGGER.info("====> start job:" + jobId);
	}

}
