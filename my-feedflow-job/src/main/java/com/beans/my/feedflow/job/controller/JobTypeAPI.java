package com.beans.my.feedflow.job.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beans.my.feedflow.base.annotation.Step;
import com.beans.my.feedflow.base.annotation.StepConfig;
import com.beans.my.feedflow.base.controller.BaseAPI;
import com.beans.my.feedflow.base.model.JobType;
import com.beans.my.feedflow.base.model.StepConfigResponse;
import com.beans.my.feedflow.base.util.Response;
import com.beans.my.feedflow.job.service.JobTypeService;

@RestController
@RequestMapping("/api/jobType")
public class JobTypeAPI extends BaseAPI{
	@Autowired
	JobTypeService service;
	
    @RequestMapping(value="/get/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Response<JobType> get(@PathVariable String id) throws Exception{
		return SUCCESS(service.get(id));
	}
    
    @RequestMapping(value="/get/{id}/config", method = RequestMethod.GET)
	@ResponseBody
    public Response<List<StepConfigResponse>> getConfig(@PathVariable String id) throws Exception{
		JobType jobType = service.get(id);
		LinkedList<StepConfigResponse> config = new LinkedList<StepConfigResponse>();
		for (String clazzName : jobType.getJobStep()) {
			Class<?> clazz = Class.forName(clazzName, false, this.getClass().getClassLoader());
			Step step = clazz.getAnnotation(Step.class);
			if(step != null) {
				StepConfig[] configValues = step.config();
				for(StepConfig configValue : configValues) {
					config.add(new StepConfigResponse(configValue));
				}
			}
		}

		return SUCCESS(config);
    }
    
    @RequestMapping(value="/list", method = RequestMethod.GET)
	@ResponseBody
    public Response<List<JobType>> list() throws Exception {
		List<JobType> list = service.list();
		Collections.sort(list, (o1, o2) -> {return o1.getName().compareTo(o2.getName());});
		return SUCCESS(list);
    }
    
    @RequestMapping(value="/insert", method = RequestMethod.POST)
    @ResponseBody
    public Response<JobType> insert(@RequestBody JobType jobType) throws Exception{
    	if(jobType.getJobStep() != null) {
    		List<String> data = new ArrayList<String>();
    		List<String> all = jobType.getJobStep();
    		for (String name : all){
    			try {
					Class.forName(name);
					data.add(name);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    		jobType.setJobStep(data);
    	}
    	jobType = service.insert(jobType);
    	return SUCCESS(jobType);
    }
    
    @RequestMapping(value="/update", method = RequestMethod.POST)
	@ResponseBody
    public Response<JobType> update(@RequestBody JobType jobType) throws Exception {
		if(jobType.getJobStep() != null){
			List<String> data = new ArrayList<String>();
			List<String> all = jobType.getJobStep();
			for (String name : all) {
				try {
					Class.forName(name);
					data.add(name);
				} catch (Exception e) {
				}
			}
			jobType.setJobStep(data);
		}
		jobType = service.update(jobType);
		return SUCCESS(jobType);
    }
    
    @RequestMapping(value="/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
    public Response<Void> delete(@PathVariable String id) throws Exception {
		service.delete(id);
		return SUCCESS();
    }
}
