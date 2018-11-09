package com.beans.my.feedflow.base.model;

public class Databases extends BaseDomain{
	private static final long serialVersionUID = 3643565753245162600L;
	
	private String name;
	
	private String server;
	
	private String user;
	
	private String password;
	
	private String type;
	
	private String database;
	

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getServer() {
		return server;
	}


	public void setServer(String server) {
		this.server = server;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDatabase() {
		return database;
	}


	public void setDatabase(String database) {
		this.database = database;
	}


	@Override
	public String toString() {
		return "{id : " + id + " , name : " + name + " , server : " + server + " , user : " + user + " , password : "
				+ password + " , type : " + type + " , database : " + database + " }";
	}
}
