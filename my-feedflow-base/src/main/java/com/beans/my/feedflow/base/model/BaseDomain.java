package com.beans.my.feedflow.base.model;

import java.io.Serializable;

/**
 * 
 * Cloneable的使用目的是？
 *
 */
public abstract class BaseDomain implements Serializable,Cloneable{

	private static final long serialVersionUID = 1L;
	
	String id;
	String class_type;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClass_type() {
		return class_type;
	}

	public void setClass_type(String class_type) {
		this.class_type = class_type;
	}

	@Override
	protected Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public abstract String toString();
}
