package com.beans.my.feedflow.job.config;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService implements AutoCloseable{
	Producer<String,String> producer = null;
	
	@Value("${${local}.kafka.host}")
	String producerHost;
	
	@Value("${kafka.topic}")
	String topic;
	
	@PostConstruct
	public void start(){
		Properties prop = new Properties();
		prop.put("bootstrap.servers", producerHost);
		prop.put("acks", "1");
		prop.put("retries", 0);
		prop.put("batch.size", 500);
		prop.put("linger.ms", 10);
		prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String,String>(prop);
	}
	
	public void addTask(String jobID){
		producer.send(new ProducerRecord<String,String>(topic,RandomUtils.nextInt(1000) + ":addTask",jobID));
	}
	
	public void updateTask(String jobID) {
		producer.send(new ProducerRecord<String, String>(topic, RandomUtils.nextInt(1000) + ":updateTask", jobID));
	}
	
	public void delTask(String jobID) {
		producer.send(new ProducerRecord<String, String>(topic, RandomUtils.nextInt(1000) + ":delTask", jobID));
	}
	
	public void runOne(String jobID){
		producer.send(new ProducerRecord<String, String>(topic, RandomUtils.nextInt(1000) + ":runOne", jobID));
	}
	
	@Override
	public void close() throws Exception {
		producer.close();
	}

}
