package com.beans.my.feedflow.job.scheduled.auto;

import java.util.LinkedHashMap;

import com.beans.my.feedflow.base.annotation.Step;
import com.beans.my.feedflow.base.annotation.StepConfig;
import com.beans.my.feedflow.base.enums.FormType;
import com.beans.my.feedflow.job.scheduled.JobConstants.ConfigName;
import com.beans.my.feedflow.job.scheduled.JobContext;
import com.beans.my.feedflow.job.scheduled.JobStep;

@Step(value = "AUTO-读取SQL配置", config = {
		@StepConfig(label = "Database", name = ConfigName.AUTO_DATABASE, type = FormType.DATABASE, required = true)
})
public class ReadConfigDatabase  extends JobStep{

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
