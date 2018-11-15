package com.beans.my.feedflow.job.config;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.beans.my.feedflow.base.model.Job;
import com.beans.my.feedflow.job.scheduled.quartz.ScheduleJob;
import com.beans.my.feedflow.job.scheduled.quartz.ScheduleTask;
import com.beans.my.feedflow.job.service.JobService;
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
	
	Thread task = null;
	
	@Autowired
	ScheduleTask scheduleTask;
	@Autowired
	JobService jobService;
	
//	@PostConstruct
	public void start() {
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
		
		this.consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(topic));
		logger.info("Kafka consumer created successfully, configuration: {}" + props);
		this.task = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						ConsumerRecords<String, String> records = consumer.poll(10000);
						for (final ConsumerRecord<String, String> record : records) {
							String type = record.key().trim();
							if(type.indexOf(":") != -1){
								type = type.split(":")[1].trim();
							}
							String jobID = record.value();
							switch (type) {
							case "addTask":
								addTask(jobID);
								break;
							case "updateTask":
								updateTask(jobID);
								break;
							case "delTask":
								delTask(jobID);
								break;
							case "runOne":
								runOne(jobID);
								break;
							default:
								logger.error("can not find by type:[" + type + "]");
								break;
							}
						}
						consumer.commitAsync();
					}catch(org.apache.kafka.common.errors.InterruptException e){
						return;
					}catch (Exception e) {
						logger.error("kafka consumer error", e);
					}
				}
			}
		});
		this.task.start();
	}
	
	private void addTask(String jobID) throws Exception {
		Job job = jobService.get(jobID);
		if(job.isStatus()){
			logger.info("add job:" + job.getName());
			scheduleTask.addTask(new ScheduleJob(job.getId(), job.getScheduled()));
		}else{
			delTask(jobID);
		}
	}
	
	private void updateTask(String jobID) throws Exception {
		Job job = jobService.get(jobID);
		if(job.isStatus()){
			logger.info("update job:" + job.getName());
			scheduleTask.addTask(new ScheduleJob(job.getId(), job.getScheduled()));
		}else{
			delTask(jobID);
		}
	}
	
	private void delTask(String jobID) throws Exception {
		logger.info("delete job:" + jobID);
		scheduleTask.delTask(jobID);
	}
	
	private void runOne(String jobID) throws Exception {
		Job job = jobService.get(jobID);
		logger.info("runOnce job:" + job.getName());
		scheduleTask.runOne(new ScheduleJob(job.getId(), job.getScheduled()));
	}
	
	@Override
	@PreDestroy
	public void close() throws Exception {
		this.task.interrupt();
		consumer.close();
	}
}
