package com.beans.my.feedflow.base.model;

import com.beans.my.feedflow.base.annotation.StepConfig;
import com.beans.my.feedflow.base.enums.FormType;

public class StepConfigResponse {
	private String label;
	private String name;
	private FormType type;
	private boolean required;
	private String defaultValue = "";
	
	public StepConfigResponse(){}
	
	public StepConfigResponse(StepConfig config) {
		this(config.label(), config.name(), config.type(), config.required(), config.defaultValue());
	}

	/**
	 * Create Step Config Definition
	 * @param label 页面表单Label
	 * @param name 参数名
	 * @param type 类型
	 */
	public StepConfigResponse(String label, String name, FormType type) {
		this(label, name, type, false, "");
	}

	/**
	 * Create Step Config Definition
	 * @param label 页面表单Label
	 * @param name 参数名
	 * @param type 类型
	 * @param required 是否必须
	 */
	public StepConfigResponse(String label, String name, FormType type, boolean required) {
		this(label, name, type, required, "");
	}

	/**
	 * Create Step Config Definition
	 * @param label 页面表单Label
	 * @param name 参数名
	 * @param type 类型
	 * @param required 是否必须
	 * @param defaultValue 默认值
	 */
	public StepConfigResponse(String label, String name, FormType type, boolean required, String defaultValue) {
		this.label = label;
		this.name = name;
		this.type = type;
		this.required = required;
		this.defaultValue = defaultValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FormType getType() {
		return type;
	}

	public void setType(FormType type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
