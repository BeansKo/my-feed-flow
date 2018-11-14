package com.beans.my.feedflow.base.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Job extends BaseDomain{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务名
	 */
	String name;
	
	/**
	 * 任务定时器
	 */
	String scheduled;
	
	/**
	 * 任务类型
	 */
	String jobType;
	
	/**
	 * 状态
	 */
	boolean status;
	
	/**
	 * 上次成功执行时间
	 */
	long lastSuccessTime;
	
	/**
	 * Job配置
	 */
	Map<String,String> config = new LinkedHashMap<String,String>();
	
	/**
	 * 异常邮件配置
	 */
	String logEmail;
	
	/**
	 * 运行日志级别
	 */
	String logLevel;

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getScheduled() {
		return scheduled;
	}


	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}


	public String getJobType() {
		return jobType;
	}


	public void setJobType(String jobType) {
		this.jobType = jobType;
	}


	public boolean isStatus() {
		return status;
	}


	public void setStatus(boolean status) {
		this.status = status;
	}


	public long getLastSuccessTime() {
		return lastSuccessTime;
	}


	public void setLastSuccessTime(long lastSuccessTime) {
		this.lastSuccessTime = lastSuccessTime;
	}


	public Map<String, String> getConfig() {
		return config;
	}


	public void setConfig(Map<String, String> config) {
		this.config = config;
	}


	public String getLogEmail() {
		return logEmail;
	}


	public void setLogEmail(String logEmail) {
		this.logEmail = logEmail;
	}


	public String getLogLevel() {
		return logLevel;
	}


	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public String toString() {
		return "{name : " + name + " , scheduled : " + scheduled + " , jobType : " + jobType + " , status : " + status + " }";
	}
}
