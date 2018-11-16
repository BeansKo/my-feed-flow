package com.beans.my.feedflow.job.scheduled;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.base.model.JobType;
import com.beans.my.feedflow.job.scheduled.JobConstants.ConfigName;

/**
 * JOB的运行器，专注负责执行JOB
 * @author fl76
 *
 */
@Service
public class JobExecutor {
	protected ApplicationContext applicationContext;
	
	@Autowired
	JobExecutor jobExecutor;
	
	protected <T extends JobStep> T createBean(String clazz) throws Exception{
		@SuppressWarnings("unchecked")
		T bean = (T) Class.forName(clazz).newInstance();
		applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
		return bean;
	}
	
	/**
	 * 开始执行具体的JOB
	 * @param job
	 * @param jobType
	 * @param configPath
	 * @param dataPath
	 * @param notice
	 */
	public void execute(Job job,JobType jobType,String configPath,String dataPath,JobNotice notice){
		List<String> list = jobType.getJobStep();
		Assert.notNull(list, "can not find jobStep");
		Assert.notEmpty(list, "jobStep can not empty");
		
		LinkedHashMap<String,String> config = new LinkedHashMap<String,String>();
		
		//数据路径
		config.put(ConfigName.BASE_DATA_PATH, dataPath);
		//本地配置路径
		config.put(ConfigName.BASE_CONFIG_PATH, configPath);
		config.putAll(job.getConfig());
		
		//根据JOB获取响应写日志实例
		Logger logger = notice.init();
		JobContext context = new JobContext(job, logger, notice);
		logger.info("====>Start job:" + job.getId());
		long startTime = System.currentTimeMillis();
		notice.onStart(startTime);
		
		List<JobStep> stepBeans = new ArrayList<JobStep>();
		try {
			//创建第一个step
			stepBeans.add(this.createBean(list.get(0)));
			//创建其他step
			JobStep next = null;
			int index = 0;
			while(index < list.size()-1) {
				next = this.createBean(list.get(index + 1));
				stepBeans.add(next);
				stepBeans.get(index).onInit(context, config, next);
				index++;
			}
			//创建最后一个step
			stepBeans.get(list.size() - 1).onInit(context, config, null);
			//开始按顺序执行JOB的step
			stepBeans.get(0).execute(new LinkedHashMap<String, String>());
			//从后向前开始关闭JOB的step资源
			for(int i=stepBeans.size()-1; i>=0; i--){
				stepBeans.get(i).close();
			}
			notice.onSuccess();
		} catch (Exception e) {
			notice.onFail();
			logger.error("Job execute failed: " + e.getMessage(), e);
		}
		long endTime = System.currentTimeMillis();
		logger.info("====>End job:" + job.getId() + ", use time: " + (endTime-startTime) + "ms");
		notice.onEnd(endTime);
	}
}
