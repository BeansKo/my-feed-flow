# my-feed-flow
1.基于SpringBoot的分布式JOB服务。
2.SpringBoot启动后，通过@PostConstruct注解初始化JOB。
3.初始化JOB,将所有的JOB存入Quartz Schedule,通过Quartz进行调度管理。
4.对JOB的维护，通过KAFKA消息进行分布式通知，比如增加一个JOB，该JOB的会通过发布KAFKA消息，所有的部署机器都会消费该消息，得到新增JOB存入Quartz的Schedule。
5.通过Zookeeper的分布式共享锁来协调各部署机器的JOB，避免JOB重复执行。
