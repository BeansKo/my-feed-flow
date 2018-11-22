package com.beans.my.feedflow.job.scheduled;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.beans.my.feedflow.base.annotation.Step;

@Step(value="默认类型")
public abstract class JobStep implements AutoCloseable{
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	protected JobContext context;
	protected LinkedHashMap<String,String> config;
	private JobStep next;
	
	public JobStep onInit(JobContext context,LinkedHashMap<String,String> config,JobStep next) throws Exception {
		this.context = context;
		this.config = config;
		this.next = next;
		return this;
	}
	
	public void execute(LinkedHashMap<String,String> data) throws Exception{ next(data);}
	
	protected void next(LinkedHashMap<String,String> data) throws Exception{
		if(this.next != null){next.execute(data);}
	}
	
	@Override
	public void close() throws Exception {}

}
