package com.beans.my.feedflow.job.scheduled.auto;

import java.util.LinkedHashMap;

import com.beans.my.feedflow.base.annotation.Step;
import com.beans.my.feedflow.job.scheduled.JobContext;
import com.beans.my.feedflow.job.scheduled.JobStep;

@Step(value = "AUTO-读取本地配置")
public class ReadConfigFile extends JobStep{

	@Override
	public JobStep onInit(JobContext context,
			LinkedHashMap<String, String> config, JobStep next)
			throws Exception {
		context.logger.info("b");
		return super.onInit(context, config, next);
	}

	@Override
	public void execute(LinkedHashMap<String, String> data) throws Exception {
		super.execute(data);
	}

}
