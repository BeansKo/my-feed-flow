package com.beans.my.feedflow.job.scheduled.auto;

import java.util.LinkedHashMap;

import com.beans.my.feedflow.base.annotation.Step;
import com.beans.my.feedflow.base.annotation.StepConfig;
import com.beans.my.feedflow.job.scheduled.JobContext;
import com.beans.my.feedflow.job.scheduled.JobStep;
import com.beans.my.feedflow.job.scheduled.JobConstants.ConfigName;
import com.beans.my.feedflow.base.enums.FormType;

@Step(value = "AUTO-读取本地配置" ,config = {
		@StepConfig(label = "Config File", name = ConfigName.AUTO_CONFIG_FILE, type = FormType.TEXT, required = true)
})
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
