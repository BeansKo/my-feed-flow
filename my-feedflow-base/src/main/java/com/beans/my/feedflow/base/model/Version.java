package com.beans.my.feedflow.base.model;

import java.util.ArrayList;
import java.util.List;

public class Version extends BaseDomain{

	private static final long serialVersionUID = 1L;
	
	/** job id */
	String jobId;
	/** 版本号 */
	Long version;
	/** 开始时间 */
	Long startTime;
	/** job执行耗时 */
	Long countTime;
	/** 本次job执行日志 */
	List<String> logs = new ArrayList<String>();
	/** feed文件每行数据 **/
	List<String> feedFileLines = new ArrayList<>();
	/** 生成feed记录数(如果job会生成feed文件) */
	Long recordNumber;
	/** 生成feed文件大小(如果job会生成feed文件) */
	Long fileSize;
	/**
	 * 任务状态
	 * running
	 * fail
	 * success
	 */
	String status;
	/** job id对应的job数据 */
	Job job;
	
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getCountTime() {
		return countTime;
	}
	public void setCountTime(Long countTime) {
		this.countTime = countTime;
	}
	public void setLogs(List<String> logs) {
		this.logs = logs;
	}
	public List<String> getFeedFileLines() {
		return feedFileLines;
	}
	public void setFeedFileLines(List<String> feedFileLines) {
		this.feedFileLines = feedFileLines;
	}
	public Long getRecordNumber() {
		return recordNumber;
	}
	public void setRecordNumber(Long recordNumber) {
		this.recordNumber = recordNumber;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	
	public List<String> getLogs() {
		if(logs == null){
			logs = new ArrayList<String>();
		}
		return logs;
	}

	public void addLog(String log){
		if(logs == null){
			this.logs = new ArrayList<String>();
		}
		this.logs.add(log);
	}

	public void clearLog(){
		if(logs != null){
			this.logs.clear();
		}
	}
	
	public void addFeedFileLine(String line) {
		this.feedFileLines.add(line);
	}
	
	public void clearFeedFileLines() {
		this.feedFileLines.clear();
	}
	
	@Override
	public String toString() {
		return "Version [jobId=" + jobId + ", version=" + version
				+ ", startTime=" + startTime + ", countTime=" + countTime
				+ ", logs=" + logs + ", feedFileLines=" + feedFileLines
				+ ", recordNumber=" + recordNumber + ", fileSize=" + fileSize
				+ ", status=" + status + ", job=" + job + "]";
	}
}
