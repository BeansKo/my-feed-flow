package com.beans.my.feedflow.job.config;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.beans.my.feedflow.job.util.RandomString;

@Component
public class KafkaConsumerThread implements AutoCloseable{
	private static final Logger logger = Logger.getLogger(KafkaConsumerThread.class);
	KafkaConsumer<String, String> consumer;
	
	@Value("${${local}.kafka.host}")
	String consumerHost;
	
	@Value("${kafka.groupparent}")
	String group;
	
	@Value("${kafka.topic}")
	String topic;
	
	@PostConstruct
	public void start(){
		Properties props = new Properties();
		props.put("bootstrap.servers", consumerHost);
		props.put("group.id", group + "-" + RandomString.next(5));
		props.put("auto.commit.enable", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("enable.auto.commit", "true");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("rebalance.backoff.ms", "2000");
		props.put("rebalance.max.retries", "5");
		props.put("max.poll.records", "10");
	}
	
	@Override
	public void close() throws Exception {
		
	}
}
