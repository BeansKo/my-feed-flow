# my-feed-flow
1.基于SpringBoot的分布式JOB服务。</br>
2.SpringBoot启动后，通过@PostConstruct注解初始化JOB。
3.初始化JOB,将所有的JOB存入Quartz Schedule,通过Quartz进行调度管理。
4.对JOB的维护，通过KAFKA消息进行分布式通知，比如增加一个JOB，该JOB的会通过发布KAFKA消息，所有的部署机器都会消费该消息，得到新增JOB存入Quartz的Schedule。
5.通过Zookeeper的分布式共享锁来协调各部署机器的JOB，避免JOB重复执行。
6.job执行器，获得当前job的所有step,通过bean管理器控制step的执行。具体业务功能区实现step。
7.job通过注解的方式，实现自动配置。portal通过jobstep注解获取job的step,实现前台自动加载step。
8.自定义日志，为每个运行job自动加载log appender。
