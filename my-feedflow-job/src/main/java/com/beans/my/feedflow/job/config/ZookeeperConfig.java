package com.beans.my.feedflow.job.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {
	
	@Value("${${local}.zookeeper.host}")
	String zookeeper_host;
	@Value("${zookeeper.namespaces:FeedFlow}")
	String zookeeper_namespaces;

	@Bean
	public CuratorFramework getCuratorFramework(){
		CuratorFramework framework = CuratorFrameworkFactory.builder()
				.connectString(zookeeper_host)
				.namespace(zookeeper_namespaces)
				.retryPolicy(new ExponentialBackoffRetry(500,10)).build();
		framework.start();
		return framework;
	}
}
