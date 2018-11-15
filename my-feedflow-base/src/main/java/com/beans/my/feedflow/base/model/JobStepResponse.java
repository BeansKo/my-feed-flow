package com.beans.my.feedflow.base.model;

import java.util.LinkedList;

public class JobStepResponse {
	String label;
	String value;
	LinkedList<StepConfigResponse> config;
	
	public JobStepResponse(String value, String label, LinkedList<StepConfigResponse> config){
		this.value = value;
		this.label = label;
		this.config = config;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public LinkedList<StepConfigResponse> getConfig() {
		return config;
	}
	public void setConfig(LinkedList<StepConfigResponse> config) {
		this.config = config;
	}
}
