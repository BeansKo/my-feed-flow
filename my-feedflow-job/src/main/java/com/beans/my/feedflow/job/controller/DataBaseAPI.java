package com.beans.my.feedflow.job.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beans.my.feedflow.base.controller.BaseAPI;
import com.beans.my.feedflow.base.model.Databases;
import com.beans.my.feedflow.base.util.Response;
import com.beans.my.feedflow.job.service.DatabaseService;

@RestController
@RequestMapping("/api/database")
public class DataBaseAPI extends BaseAPI {
	
	@Autowired
	private DatabaseService databaseService;
	
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	@ResponseBody
	public Response<Databases> get(@PathVariable String id) throws Exception{
		return SUCCESS(databaseService.get(id));
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public Response<List<Databases>> list() throws Exception{
		return SUCCESS(databaseService.list());
	}
}
