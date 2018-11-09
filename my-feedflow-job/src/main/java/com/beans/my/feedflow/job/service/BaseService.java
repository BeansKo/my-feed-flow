package com.beans.my.feedflow.job.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.beans.my.feedflow.base.model.BaseDomain;
import com.beans.my.feedflow.job.dao.HBaseDao;

@Service
public abstract class BaseService<T extends BaseDomain> {
	
	@Autowired
	HBaseDao baseDao;
	
	Class<T> clazz = null;
	
	@SuppressWarnings("unchecked")
	public BaseService(){
		ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
		this.clazz = (Class<T>)type.getActualTypeArguments()[0];
	}
	
	public String getDBType(){
		return this.clazz.getSimpleName().toUpperCase();
	}
	
	public T get(String id) throws HBaseException{
		Assert.notNull(id,"id can not be null");
		T t = baseDao.get(id,this.clazz);
		if( t == null ){ return null; }
		if(this.getDBType().equals(t.getClass_type())){
			return t;
		}
		return null;
	}
	
	public List<T> list() throws HBaseException{
		return baseDao.listByType(getDBType(), this.clazz);
	}
	
	public T insert(T domain) throws HBaseException{
		Assert.notNull(domain,"can not update null object");
		domain.setId(UUID.randomUUID().toString());
		domain.setClass_type(getDBType());
		baseDao.put(domain.getId(),domain);
		return domain;
	}
}
