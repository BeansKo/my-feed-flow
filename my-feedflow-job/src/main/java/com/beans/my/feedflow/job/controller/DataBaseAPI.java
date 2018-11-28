package com.beans.my.feedflow.job.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beans.my.feedflow.base.controller.BaseAPI;
import com.beans.my.feedflow.base.model.Databases;
import com.beans.my.feedflow.base.util.Response;
import com.beans.my.feedflow.job.service.DatabaseService;

@Api(tags="数据库配置管理")
@RestController
@RequestMapping("/api/database")
public class DataBaseAPI extends BaseAPI {
	
	@Autowired
	private DatabaseService databaseService;
	
	@ApiOperation(value="根据ID查询", consumes="application/json", produces="application/json")
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	@ResponseBody
	public Response<Databases> get(@PathVariable String id) throws Exception{
		return SUCCESS(databaseService.get(id));
	}
	
	@ApiOperation(value="查询全部数据库配置", consumes="application/json", produces="application/json")
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public Response<List<Databases>> list() throws Exception{
		return SUCCESS(databaseService.list());
	}
	
	@ApiOperation(value="删除指定的数据库配置", consumes="application/json", produces="application/json")
	@RequestMapping(value="/delete/{id}",method = RequestMethod.POST)
	@ResponseBody
	public Response<Void> delete(@PathVariable String id) throws Exception{
		databaseService.delete(id);
		return SUCCESS();
	}
	
	@ApiOperation(value="保存数据库配置", consumes="application/json", produces="application/json")
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	@ResponseBody
	public Response<Databases> insert(@RequestBody Databases database) throws Exception{
		return SUCCESS(databaseService.insert(database));
	}
	
	@ApiOperation(value="更新数据库配置", consumes="application/json", produces="application/json")
	@RequestMapping(value="/update", method = RequestMethod.POST)
	@ResponseBody
	public Response<Databases> update(@RequestBody Databases database) throws Exception{
		return SUCCESS(databaseService.update(database));
	}
}
