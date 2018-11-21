package com.beans.my.feedflow.job.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beans.my.feedflow.base.controller.BaseAPI;
import com.beans.my.feedflow.base.model.Email;
import com.beans.my.feedflow.base.util.Response;
import com.beans.my.feedflow.job.service.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailAPI extends BaseAPI{
	@Autowired
	EmailService service;
	
    @RequestMapping(value="/get/{id}", method = RequestMethod.GET)
	@ResponseBody
    public Response<Email> get(@PathVariable String id) throws Exception {
		return SUCCESS(service.get(id));
    }
	
    @RequestMapping(value="/list", method = RequestMethod.GET)
	@ResponseBody
    public Response<List<Email>> list() throws Exception {
		return SUCCESS(service.list());
    }
    
    @RequestMapping(value="/insert", method = RequestMethod.POST)
	@ResponseBody
    public Response<Email> insert(@RequestBody Email email) throws Exception {
		return SUCCESS(service.insert(email));
    }
    
    @RequestMapping(value="/update", method = RequestMethod.POST)
	@ResponseBody
    public Response<Email> update(@RequestBody Email email) throws Exception {
		return SUCCESS(service.update(email));
    }
    
    @RequestMapping(value="/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
    public Response<Void> delete(@PathVariable String id) throws Exception {
		service.delete(id);
		return SUCCESS();
    }
}
