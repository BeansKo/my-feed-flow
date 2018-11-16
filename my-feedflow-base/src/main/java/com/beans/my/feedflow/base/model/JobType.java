package com.beans.my.feedflow.base.model;

import java.util.List;

public class JobType extends BaseDomain{
	private static final long serialVersionUID = 1L;
	
	String name;
	
	List<String> jobStep;

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<String> getJobStep() {
		return jobStep;
	}


	public void setJobStep(List<String> jobStep) {
		this.jobStep = jobStep;
	}


	@Override
	public String toString() {
		return "JobType [name=" + name + ", jobStep=" + jobStep + "]";
	}
}
