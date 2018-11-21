package com.beans.my.feedflow.job.scheduled;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.beans.my.feedflow.base.model.Email;
import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.base.model.JobType;
import com.beans.my.feedflow.base.model.Version;
import com.beans.my.feedflow.job.log4j.DetailFilter;
import com.beans.my.feedflow.job.log4j.DetailLevel;
import com.beans.my.feedflow.job.service.EmailService;
import com.beans.my.feedflow.job.service.JobService;
import com.beans.my.feedflow.job.service.JobTypeService;
import com.beans.my.feedflow.job.service.VersionService;
import com.beans.my.feedflow.job.util.DateUtil;

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
	@Value("${job.log.path:/opt/app/feedflow/logs}")
	private String logPath;
	@Value("${job.config.path:/opt/app/feedflow/config}")
	private String configPath;
	@Value("${job.data.path:/opt/app/feedflow/data}")
	private String dataPath;
	@Value("${job.log.email.smtp.server}")
	private String smtpServer;
	@Value("${job.log.email.smtp.port:-1}")
	private int smtpPort;
	
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
		
		String logPath = getFilePath(this.logPath);
		String configPath = getFilePath(this.configPath);
		String dataPath = getFilePath(this.dataPath + job.getName());
		
		jobExecutor.execute(job, jobType, configPath, dataPath, getJobNotice(job, logPath));
		LOGGER.info("====> end job:" + jobId);
	}
	
	private String getFilePath(String path){
		if(StringUtils.isEmpty(path)){
			return "";
		}
		if(!path.endsWith(File.separator)){
			path = path + File.separator;
		}
		return path;
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
				version.setRecordNumber(recordNumber);
				version.setFileSize(fileSize);
				saveVersion();
				
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

			/**
			 * 初始化Job运行过程中的日志对象
			 * @return 日志对象
			 */
			private Logger initLogger(){
				Logger logger = Logger.getLogger(job.getName());
				// email appender if exists
				Appender email = logger.getAppender("email");
				
				logger.removeAllAppenders();
				logger.setLevel(getLogLevel(job.getLogLevel()));
				logger.setAdditivity(false);
				// default: write log to hbase
				logger.addAppender(getDataBaseAppender(this.version));
				// write to log file
		        logger.addAppender(getFileAppender(logPath, job));
		        if(! "dev".equals(local)) {
		        	Email emailConfig;
					try {
						emailConfig = emailService.get(job.getLogEmail());
						if(emailConfig != null && StringUtils.isNotEmpty(smtpServer)) {
							String subject = "(info) [" + local + "] " + emailConfig.getSubject();
							logger.addAppender(getEmailAppender(smtpServer, smtpPort, emailConfig.getFrom(), emailConfig.getTo(), emailConfig.getCc(), subject));
						} else if(email != null) {
							logger.addAppender(email);
						}
					} catch (HBaseException e) {
						LOGGER.error("init email logger appender error", e);
					}

		        }
				
				return logger;
			}
			
			private Level getLogLevel(String logLevel){
				if("ERROR".equalsIgnoreCase(logLevel)) {
					return Level.ERROR;
				} else if ("WARN".equalsIgnoreCase(logLevel)) {
					return Level.WARN;
				} else if ("INFO".equalsIgnoreCase(logLevel)) {
					return Level.INFO;
				} else if ("DEBUG".equalsIgnoreCase(logLevel)) {
					return Level.DEBUG;
				} else if ("FEED_DETAIL".equalsIgnoreCase(logLevel)) {
					return DetailLevel.FEED_DETAIL;
				}
				return Level.INFO;
			}
			
			private Appender getDataBaseAppender(Version version){
				return new AppenderSkeleton() {

					@Override
					public void close() {}

					@Override
					public boolean requiresLayout() {return false;}

					@Override
					protected void append(LoggingEvent event) {
						if(event.getLevel() == DetailLevel.FEED_DETAIL){
							version.addFeedFileLine(String.valueOf(event.getMessage()));
						} else {
							StringBuffer buffer = new StringBuffer(128);
							// log message
							buffer.append("[")
								.append(DateUtil.getDateTime(new Date(event.getTimeStamp()))).append("] ")
								.append("[").append(event.getLevel().toString()).append("] ")
								.append(event.getRenderedMessage()).append(Layout.LINE_SEP);
							// exception
							String[] s = event.getThrowableStrRep();
							if (s != null) {
								int len = s.length;
								for (int i = 0; i < len; ++i) {
									buffer.append(s[i]).append(Layout.LINE_SEP);
								}
							}
							// write message to database
							version.addLog(buffer.toString());
						}
						try {
							versionService.update(version);
						} catch (HBaseException e) {
							LOGGER.error("update job version error", e);
						}
					}
				};
			}
			
			/**
			 * 初始化文件的log appender
			 */
			private Appender getFileAppender(String logPath, Job job) {
				RollingFileAppender fileAppender = new RollingFileAppender();
				fileAppender.addFilter(new DetailFilter());
				PatternLayout layoutFileAppender = new PatternLayout();
				String conversionPatternFileAppender = "%d %p [%c] - %m%n";
				layoutFileAppender.setConversionPattern(conversionPatternFileAppender);
				fileAppender.setLayout(layoutFileAppender);
				fileAppender.setFile(logPath + DateUtil.DateToString(new Date(), "yyyyMMdd") + File.separator + job.getName() + ".log");
				fileAppender.setEncoding("UTF-8");
				fileAppender.setMaxFileSize("1MB");
				fileAppender.setMaxBackupIndex(5);
				fileAppender.setAppend(true);
				fileAppender.activateOptions();
				return fileAppender;
			}

			
			/**
			 * 初始化Email的log appender
			 */
			private Appender getEmailAppender(String smtpServer, int port, String from, String to, String cc, String subject) {
				SMTPAppender emailAppender = new SMTPAppender();
				emailAppender.addFilter(new DetailFilter());
				emailAppender.setThreshold(Level.ERROR);
				PatternLayout layoutEmailAppender = new PatternLayout();
				String conversionPatternEmailAppender = "%d{yyyy/MM/dd HH:mm:ss,SSS} [%t] %-5p %c %x - %m%n";
				layoutEmailAppender.setConversionPattern(conversionPatternEmailAppender);
				emailAppender.setLayout(layoutEmailAppender);
				emailAppender.setSMTPHost(smtpServer);
				emailAppender.setSMTPPort(port);
				emailAppender.setFrom(from);
				emailAppender.setTo(to.replace(";", ","));
				emailAppender.setCc(StringUtils.isEmpty(cc) ? null : cc.replace(";", ","));
				emailAppender.setSubject(subject);
				emailAppender.activateOptions();
				return emailAppender;
			}
		};
	}
	

}
