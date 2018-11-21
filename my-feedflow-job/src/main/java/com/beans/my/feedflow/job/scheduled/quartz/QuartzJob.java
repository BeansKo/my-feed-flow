package com.beans.my.feedflow.job.scheduled.quartz;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.beans.my.feedflow.job.scheduled.ExecuteJob;

@Component
@Scope("prototype")
public class QuartzJob extends QuartzJobBean{
	private static final Logger LOGGER = Logger.getLogger(QuartzJob.class);
	
	@Autowired
	CuratorFramework zookeeper;
	
	@Autowired
	ExecuteJob executeJob;

	//当Job的一个trigger被触发，executeInternal（）方法被调度程序的一个工作线程调用。
	//传递给executeInternal()方法的JobExecutionContext对象中保存着该job运行时的一些信息。
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		final ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
		this.run(scheduleJob);
		
	}
	
	public void run(ScheduleJob scheduleJob) throws JobExecutionException{
		//全局共享锁
		InterProcessMutex lock = null;
		try {
			lock = new InterProcessMutex(zookeeper, "/examples/lock/" + scheduleJob.getJobId());
			//锁是否被获取到
			if(lock.acquire(5, TimeUnit.SECONDS)){
				executeJob.execute(scheduleJob.getJobId());
			}
		} catch (Exception e){
			LOGGER.error("execute job error",e);
			throw new JobExecutionException("execute job error", e);
		} finally {
			try {
				//判断是否持有锁，进而进行师傅释放锁的操作
				if(lock != null && lock.isAcquiredInThisProcess()){
					//释放锁
					lock.release();
				}
			} catch (Exception e){
				LOGGER.error("release lock error",e);
			}
		}
	}
}