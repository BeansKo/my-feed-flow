package com.beans.my.feedflow.base.controller;

import com.beans.my.feedflow.base.util.Response;

public class BaseAPI {
	public Response<Void> SUCCESS(){
		Response<Void> response = new Response<Void>();
		response.setStatus(true);
		return response;
	}
	
	public <T> Response<T> SUCCESS(T data){
		Response<T> response = new Response<T>();
		response.setStatus(true);
		response.setData(data);
		return response;
	}
}
