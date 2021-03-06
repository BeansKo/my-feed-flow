package com.beans.my.feedflow.job.scheduled.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

	@Autowired
	Scheduler scheduler;
	
	/**
	 * <p>运行一次任务</p>
	 * @param job
	 * @throws SchedulerException
	 */
	public void runOne(ScheduleJob job) throws SchedulerException{
		runOne(job,QuartzJob.class);
	}
	
	/**
	 * <p>运行一次任务</p>
	 * @param job
	 * @param jobClazz
	 * @throws SchedulerException
	 */
	public void runOne(ScheduleJob job, Class<? extends Job> jobClazz)
			throws SchedulerException {
		JobKey jobKey = JobKey.jobKey("ones_" + job.getJobId(), "Group");

		JobDetail jobDetail = JobBuilder.newJob(jobClazz).withIdentity(jobKey)
				.build();
		
		jobDetail.getJobDataMap().put("scheduleJob", job);
		scheduler.addJob(jobDetail, true, true);
		scheduler.triggerJob(jobKey);
		scheduler.deleteJob(jobKey);
	}
	
	/**
	 * <p>添加定时任务</p>
	 * @param job
	 * @throws SchedulerException
	 */
	public void addTask(ScheduleJob job) throws SchedulerException{
		addTask(job,QuartzJob.class);
	}
	
	/**
	 * <p>添加定时任务</p>
	 * @param job
	 * @param jobClazz
	 * @throws SchedulerException
	 */
	public void addTask(ScheduleJob job,Class<? extends Job> jobClazz) throws SchedulerException{
		//将Job和Trigger注册到Scheduler时，可以为它们设置key，配置其身份属性。
		JobKey jobKey = JobKey.jobKey(job.getJobId(),"Group");
		
		//JobDetail对象是在将job加入scheduler时，由客户端程序（你的程序）创建的。
		//它包含job的各种属性设置，以及用于存储job实例状态信息的JobDataMap。
		JobDetail jobDetail = JobBuilder.newJob(jobClazz).withIdentity(jobKey).build();
		jobDetail.getJobDataMap().put("scheduleJob", job);
		
		//Trigger用于触发Job的执行。当你准备调度一个job时，你创建一个Trigger的实例，然后设置调度相关的属性。
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobId(),"Group");
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
		
		if(scheduler.checkExists(jobKey)){
			scheduler.deleteJob(jobKey);
		}
		if(scheduler.checkExists(triggerKey)){
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
		}
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	/**
	 * <p>删除定时任务</p>
	 * @param jobID
	 * @throws SchedulerException
	 */
	public void delTask(String jobID) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobID, "Group");
		JobKey jobKey = new JobKey(jobID, "Group");
		if (scheduler.checkExists(jobKey)) {
			scheduler.pauseJob(jobKey);
			scheduler.deleteJob(jobKey);
		}
		if (scheduler.checkExists(triggerKey)) {
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
		}
	}
}
