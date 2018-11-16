package com.beans.my.feedflow.job.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.base.model.JobType;
import com.beans.my.feedflow.base.model.Version;

@Service
public class VersionService extends BaseService<Version>{
	
	@Autowired
	JobService jobService;
	@Autowired
	JobTypeService jobTypeService;
	
	@Override
	public Version insert(Version version) throws HBaseException {
		version.setJob(null);
		return super.insert(version);
	}
	
	@Override
	public Version update(Version version) throws HBaseException{
		version.setJob(null);
		return super.update(version);
	}
	
	@Override
	public Version get(String id) throws HBaseException{
		Version v = super.get(id);
		if(v != null){
			v.setJob(jobService.get(v.getJobId()));
		}
		return v;
	}
	
	@Override
	public List<Version> list() throws HBaseException{
		List<Version> list = this.listByRowPrefix("^V\\:[0-9a-fA-F\\-]{36}\\:0$");
		
		List<JobType> jobTypes = jobTypeService.list();
		Map<String,JobType> idToType = jobTypes.stream().collect(
				Collectors.toMap(JobType::getId, jobType -> {return jobType;}));
		for(Version v : list){
			Job job = jobService.get(v.getJobId());
			job.setJobTypeValue(idToType.get(job.getJobType()));
			v.setJob(job);
		};
		return list;
	}
	
	public List<Version> listByJobId(String jobId) throws HBaseException {
		Assert.notNull(jobId, "jobId can not be null");
		List<Version> list = this.listByRowPrefix("V:" + jobId);

		List<JobType> jobTypes = jobTypeService.list();
		Map<String, JobType> idToType = jobTypes.stream().collect(
			Collectors.toMap(JobType::getId, jobType -> {return jobType;}));

		Iterator<Version> it = list.iterator();
		while (it.hasNext()) {
			Version v = it.next();
			if(("V:" + jobId + ":0").equals(v.getId())) {
				it.remove();
			} else {
				Job job = jobService.get(v.getJobId());
				job.setJobTypeValue(idToType.get(job.getJobType()));
				v.setJob(job);
			}
		}
		return list;
	}

	public Version getByJobId(String jobId) throws HBaseException {
		Assert.notNull(jobId, "jobId can not be null");
		List<Version> list = this.listByRowPrefix("V:" + jobId);
		Iterator<Version> it = list.iterator();
		while (it.hasNext()) {
			Version v = it.next();
			if(("V:" + jobId + ":0").equals(v.getId())) {
				return v;
			}
		}
		return new Version();
	}

	public void deleteOldVersion(String jobId, long version) throws HBaseException {
		Assert.notNull(jobId, "jobId can not be null");
		Assert.isTrue(version > 0, "version must greater than 0");
		List<Version> list = this.listByRowPrefix("V:" + jobId);
		HashSet<String> ids = new HashSet<String>();
		if(list != null && list.size() > 0) {
			list.forEach(v -> {
				if(!v.getId().endsWith(":0") && v.getVersion() <= version ) {
					ids.add(v.getId());
				}
			});
			if(ids.size() > 0) { this.delete(ids); }
		}
	}

	public void deleteAllVersion(String jobId) throws HBaseException {
		Assert.notNull(jobId, "jobId can not be null");
		List<Version> list = this.listByRowPrefix("V:" + jobId);
		this.delete(list.stream().map(Version::getId).collect(Collectors.toSet()));
	}

}
