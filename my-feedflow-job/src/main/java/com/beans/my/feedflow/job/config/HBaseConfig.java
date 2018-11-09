package com.beans.my.feedflow.job.config;

import java.io.IOException;

import org.apache.hadoop.hbase.exceptions.HBaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.beans.my.feedflow.job.dao.HBaseDao;

@Configuration
public class HBaseConfig {
	
	@Value("${${local}.hbase.zookeeper.quorum}")
	private String zookeeper;
	
	@Value("${${local}.hbase.zookeeper.port:2181}")
	private int port=2181;
	
	@Value("${${local}.hbase.databases}")
	private String databases;
	
	@Value("${${local}.hbase.family:i}")
	private String family = "i";
	
	@Bean
	public HBaseDao getHBaseDao() throws HBaseException, IOException{
		HBaseDao dao = new HBaseDao(zookeeper,port,databases,family);
		dao.check();
		return dao;
	}
}
