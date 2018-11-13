package com.beans.my.feedflow.job.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;
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
	
	public T insert(T domain) throws HBaseException {
		Assert.notNull(domain, "can not update null object");
		domain.setId(UUID.randomUUID().toString());
		domain.setClass_type(getDBType());
		baseDao.put(domain.getId(), domain);
		return domain;
	}
	
	public T update(T domain) throws HBaseException {
		Assert.notNull(domain, "can not update null object");
		Assert.notNull(domain.getId(), "id can not be null");
		domain.setClass_type(getDBType());
		baseDao.put(domain.getId(), domain);
		return domain;
	}
	
	public T get(String id) throws HBaseException{
		Assert.notNull(id, "id can not be null");
		T t = baseDao.get(id, this.clazz);
		if(t == null) { return null; }
		if(this.getDBType().equals(t.getClass_type())){
			return t;
		}
		return null;
	}
	
	public void delete(String id) throws HBaseException {
		Assert.notNull(id, "id can not be null");
		baseDao.delete(id);
	}

	public void delete(Set<String> ids) throws HBaseException {
		Assert.notNull(ids, "ID's can't be null");
		if(ids.isEmpty()){ return; }
		baseDao.delete(ids);
	}

	public List<T> list() throws HBaseException {
		return baseDao.listByType(getDBType(), this.clazz);
	}

	public List<T> listByRowPrefix(String rowKeyPrefix) throws HBaseException {
		Assert.notNull(rowKeyPrefix, "row key prefix can not be null");
		return baseDao.listByType(rowKeyPrefix, getDBType(), this.clazz);
	}

	public List<T> listByRowRegEx(String rowKeyRegEx) throws HBaseException {
		Assert.notNull(rowKeyRegEx, "row key regular expression can not be null");
		return baseDao.listByType(null, rowKeyRegEx, getDBType(), this.clazz);
	}
}
