package com.beans.my.feedflow.job.scheduled;

import java.util.Date;

import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.base.model.JobType;
import com.beans.my.feedflow.base.model.Version;
import com.beans.my.feedflow.job.service.EmailService;
import com.beans.my.feedflow.job.service.JobService;
import com.beans.my.feedflow.job.service.JobTypeService;
import com.beans.my.feedflow.job.service.VersionService;

@Service
public class ExecuteJob {
	private static final Logger LOGGER = Logger.getLogger(ExecuteJob.class);
	
	@Autowired
	JobService jobService;
	@Autowired
	JobTypeService jobTypeService;
	@Autowired
	VersionService versionService;
	@Autowired
	EmailService emailService;
	@Autowired
	JobExecutor jobExecutor;
	
	@Value("${local}")
	private String local;
	@Value("${job.max.version.number:-1}")
	private int maxVersionNumber = -1;
	
	/**
	 * 开始执行Job
	 * @param jobId
	 * @throws Exception
	 */
	public void execute(String jobId) throws Exception{
		LOGGER.info("====> start job:" + jobId);
		Job job = jobService.get(jobId);
		Assert.notNull(job,"can not find job:" + jobId);
		JobType jobType = jobTypeService.get(job.getJobType());
		Assert.notNull(jobType, "can not find jobType:" + job.getJobType());
		job.setJobTypeValue(jobType);
		
		String logPath = "";
		String configPath = "";
		String dataPath = "";
		
		jobExecutor.execute(job, jobType, configPath, dataPath, getJobNotice(job, logPath));
	}
	
	private JobNotice getJobNotice(final Job job,final String logPath){
		return new JobNotice() {

			private Long startTime;
			private Version version;
			
			@Override
			public Logger init() {
				this.version = initVersion();
				return initLogger();
			}

			@Override
			public void onStart(long startTime) {
				this.startTime = startTime;
				version.setStartTime(startTime);
				version.setStatus("running");
				saveVersion();
			}

			@Override
			public void onFileComplete(long recordNumber, long fileSize) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				version.setStatus("success");
				Job current = new Job();
				try {
					current = jobService.get(job.getId());
					current.setLastSuccessTime(new Date().getTime());
					jobService.update(current);
				} catch (HBaseException e) {
					LOGGER.error("update lastsuccessTime error: "+current.getName(),e);
				}
				saveVersion();
			}

			@Override
			public void onFail() {
				version.setStatus("fail");
				saveVersion();
			}

			@Override
			public void onEnd(long endTime) {
				//当前日志保存到rowkey为0的记录上，始终保持0记录的为正在运行的log
				version.setCountTime(endTime-this.startTime);
				saveVersion();
				//当前日志保存到历史版本中
				version.setId("V:" + job.getId() + ":" + version.getVersion());
				saveVersion();
				long maxOldVersion = version.getVersion() - maxVersionNumber;
				if(maxVersionNumber >= 0 && maxOldVersion > 0){
					try {
						versionService.deleteOldVersion(job.getId(), maxOldVersion);
					} catch (HBaseException e) {
						LOGGER.error("delete old version error[jobId=" + job.getId() + "]", e);
					}
				}
				
			}

			@Override
			public Version currentVersion() {
				return this.version;
			}

			@Override
			public Version lastVersion() {
				try{
					Long version = this.version.getVersion();
					if(version == null || version ==1) {
						return this.version;
					} else {
						if("running".equals(this.version.getStatus())) {
							version = version -1;
						}
						return versionService.get("V:" + job.getId() + ":" + version);
					}
				} catch (HBaseException e){
					LOGGER.error("get job last version error[jobId=" + job.getId() + "]", e);
				}
				return null;
			}
			
			/**
			 * 保存版本信息
			 */
			private void saveVersion() {
				try {
					versionService.update(this.version);
				} catch (HBaseException e) {
					LOGGER.error("update job version error[jobId=" + this.version.getJobId() + "]", e);
				}
			}
			
			/**
			 * 初始化Job运行版本
			 * @return 本次Job运行版本
			 */
			private Version initVersion() {
				String versionId = "V:" + job.getId() + ":0";
				Version version = null;
				try {
					version = versionService.get(versionId);
				} catch (HBaseException e) {
					LOGGER.error("get job version error[jobId=" + job.getId() + "]", e);
				}
				if(version == null) {
					version = new Version();
					version.setId(versionId);
					version.setJob(job);
					version.setJobId(job.getId());
					version.setVersion(1L);
				} else {
					version.setVersion(version.getVersion() + 1);
					version.setCountTime(null);
					version.setStartTime(null);
					version.clearLog();
					version.clearFeedFileLines();
				}
				return version;
			}
			
			private Logger initLogger(){
				Logger logger = Logger.getLogger(job.getName());
				
				return logger;
			}
			
		};
	}
	

}
