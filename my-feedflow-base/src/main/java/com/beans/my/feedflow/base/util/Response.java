package com.beans.my.feedflow.base.util;

/**
 * 返回请求封装
 */
public class Response<T> {

	private boolean status;
	
	private T data;
	
	private String message;
	
	private Exception error;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}
}
