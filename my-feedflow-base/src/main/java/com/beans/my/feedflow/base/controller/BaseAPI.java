package com.beans.my.feedflow.base.controller;

import com.beans.my.feedflow.base.util.Response;

public class BaseAPI {
	public Response<Void> SUCCESS(){
		Response<Void> response = new Response<Void>();
		response.setStatus(true);
		return response;
	}
	
	public <T> Response<T> SUCCESS(T data) {
		Response<T> response = new Response<T>();
		response.setStatus(true);
		response.setData(data);
		return response;
	}
	
	public <T> Response<T> SUCCESS(T data, String message) {
		Response<T> response = new Response<T>();
		response.setStatus(true);
		response.setMessage(message);
		response.setData(data);
		return response;
	}
	
	public <T> Response<T> FAIL(String message) {
		Response<T> response = new Response<T>();
		response.setStatus(false);
		response.setMessage(message);
		return response;
	}
	
	public <T> Response<T> FAIL(Exception e) {
		Response<T> response = new Response<T>();
		response.setStatus(false);
		response.setError(e);
		return response;
	}
	
	public <T> Response<T> FAIL(String message, Exception e) {
		Response<T> response = new Response<T>();
		response.setStatus(false);
		response.setMessage(message);
		response.setError(e);
		return response;
	}
}
