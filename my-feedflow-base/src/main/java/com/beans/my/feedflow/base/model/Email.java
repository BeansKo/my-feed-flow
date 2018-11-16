package com.beans.my.feedflow.base.model;

public class Email extends BaseDomain{
	private static final long serialVersionUID = 1L;
	/**
	 * 邮件配置名
	 */
	String name;
	/**
	 * From
	 */
	String from;
	/**
	 * To, 多个邮件地址用","分隔
	 */
	String to;
	/**
	 * CC, 多个邮件地址用","分隔
	 */
	String cc;
	/**
	 * 邮件主题
	 */
	String subject;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Override
	public String toString() {
		return "Email [name=" + name + ", from=" + from + ", to=" + to + ", cc=" + cc + ", subject=" + subject + "]";
	}

}
