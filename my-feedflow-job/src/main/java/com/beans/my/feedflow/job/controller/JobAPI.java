package com.beans.my.feedflow.job.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beans.my.feedflow.base.controller.BaseAPI;
import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.base.model.JobType;
import com.beans.my.feedflow.base.model.Version;
import com.beans.my.feedflow.base.util.Response;
import com.beans.my.feedflow.job.config.KafkaProducerService;
import com.beans.my.feedflow.job.scheduled.ExecuteJob;
import com.beans.my.feedflow.job.service.JobService;
import com.beans.my.feedflow.job.service.JobTypeService;
import com.beans.my.feedflow.job.service.VersionService;

@Api(tags="任务配置管理")
@RestController
@RequestMapping("/api/job")
public class JobAPI extends BaseAPI{
	static final Logger LOGGER = Logger.getLogger(JobAPI.class);
	
	@Autowired
	JobService jobService;
	@Autowired
	JobTypeService jobTypeService;
	@Autowired
	VersionService versionService;
	@Autowired
	ExecuteJob executeJob;
	@Autowired
	KafkaProducerService kafkaService;
	
	@ApiOperation(value="根据ID查询", consumes="application/json", produces="application/json")
    @RequestMapping(value="/get/{id}", method = RequestMethod.GET)
	@ResponseBody
    public Response<Job> get(@PathVariable String id) throws Exception {
		return SUCCESS(jobService.get(id));
    }
	
	@ApiOperation(value="执行JOB", consumes="application/json", produces="application/json")
    @RequestMapping(value="/run/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Response<String> run(@PathVariable String id) throws Exception {
		try {
			Version version = versionService.getByJobId(id);
			if("running".equals(version.getStatus())) {
				return FAIL("Job is running now, please wait a minute.");
			}
			new Thread(() -> {
				try {
					executeJob.execute(id);
				} catch (Exception e) {
					LOGGER.error("execute job fail, cause by ", e);
				}
			}).start();
		} catch (Exception e) {
			LOGGER.error("execute job fail, cause by ", e);
			return FAIL("execute job fail, cause by " + e.getMessage(), e);
		}
		return SUCCESS("Job started");
	}
	
	@ApiOperation(value="检查任务时间配置", consumes="application/json", produces="application/json")
    @RequestMapping(value="/checkScheduled", method = RequestMethod.GET)
	@ResponseBody
	public Response<Boolean> CheckScheduled(@RequestParam String scheduled) throws Exception {
		return SUCCESS(CronExpression.isValidExpression(scheduled));
	}
	
	@ApiOperation(value="查询全部任务配置", consumes="application/json", produces="application/json")
    @RequestMapping(value="/list", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<Job>> list() throws Exception {
		List<Job> list = jobService.list();
		
		List<JobType> jobTypes = jobTypeService.list();
		Map<String, JobType> idToType = jobTypes.stream().collect(Collectors.toMap(JobType::getId, jobType -> {return jobType;}));
		for(Job job : list) {
			if (StringUtils.isNotEmpty(job.getJobType())) {
				job.setJobTypeValue(idToType.get(job.getJobType()));
			}
		}
		return SUCCESS(list);
	}
	
	@ApiOperation(value="保存任务配置", consumes="application/json", produces="application/json")
    @RequestMapping(value="/insert", method = RequestMethod.POST)
	@ResponseBody
	public Response<Job> insert(@RequestBody Job job) throws Exception{
		job = jobService.insert(job);
//		kafkaService.addTask(job.getId());
		return SUCCESS(job);
	}
	
	@ApiOperation(value="更新任务配置", consumes="application/json", produces="application/json")
    @RequestMapping(value="/update", method = RequestMethod.POST)
	@ResponseBody
    public Response<Job> update(@RequestBody Job job) throws Exception {
		job = jobService.update(job);
//		kafkaService.updateTask(job.getId());
		return SUCCESS(job);
    }
	
	@ApiOperation(value="删除指定的任务配置", consumes="application/json", produces="application/json")
	@RequestMapping(value="/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Response<Void> delete(@PathVariable String id) throws Exception{
		jobService.delete(id);
//		kafkaService.delTask(id);
//		versionService.deleteAllVersion(id);
		return SUCCESS();
	}
}
