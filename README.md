# kafka消息发布订阅系统
***
### 在spring中XMl配置：
```java 
<!-- 发布配置 -->
<bean id="publisher" class="com.bfs.pss.proxy.PssPublisherProxy" init-method="init" destroy-method="destory">
	<property name="kafkaBrokers" value="kafka地址:端口" /> <!-- 配置kafka brokers地址-->
	<property name="appId" value="test-server" /> <!-- appId是每个子系统的唯一识别id, 一般为项目maven中定义的artifactId -->
</bean>
	
<!-- 订阅配置 -->
<bean id="listenerResolver" class="com.bfs.pss.resolver.SpringPssMessageListenerResolver">
	<property name="zookeeper" value="地址:端口" /> <!-- 注册中心地址 -->
	<property name="appId" value="test-server" />  <!-- appId是每个子系统的唯一识别id, 一般为项目maven中定义的artifactId -->
	<property name="threads" value="1" />
	<property name="sessionTimeout" value="5000" />
	<property name="ignoreSelfMessage" value="false" /> <!-- 是否忽略自己发布的消息,默认不接收自己发送的消息-->
</bean>

	
<bean id="subscriptionContext" class="com.bfs.pss.resolver.SubscriptionScheduler" init-method="start" destroy-method="destory">
	<constructor-arg ref="listenerResolver" />
</bean>

<task:executor id="lsExecutor" pool-size="2" />
<task:scheduler id="lsScheduler" pool-size="2" />
<task:annotation-driven executor="lsExecutor" scheduler="lsScheduler" />
```
### 在spring-boot中配置：
```java
/**
  * 配置发布
  * 
  * @return
  */
@Bean(initMethod = "init", destroyMethod = "destory")
public PssPublisherProxy getPssPublisherProxy() {
	PssPublisherProxy pssPublisherProxy = new PssPublisherProxy();
	pssPublisherProxy.setKafkaBrokers(...); //kafka地址
	pssPublisherProxy.setAppId(...);//应有id
	return pssPublisherProxy;
}

/**
  * 订阅
  * 
  * @return
  */
@Bean
public SpringPssMessageListenerResolver getSpringPssMessageListenerResolver() {
       SpringPssMessageListenerResolver springPssMessageListenerResolver = new SpringPssMessageListenerResolver();
       springPssMessageListenerResolver.setZookeeper(...); //zookeeper地址
       springPssMessageListenerResolver.setAppId(...); //应用id
       springPssMessageListenerResolver.setThreads(1);
       springPssMessageListenerResolver.setSessionTimeout(5000);
       springPssMessageListenerResolver.setIgnoreSelfMessage(false);
       return springPssMessageListenerResolver;
}

@Bean(initMethod = "start", destroyMethod = "destory")
public SubscriptionScheduler getSubscriptionScheduler() {
	SubscriptionScheduler subscriptionScheduler = new SubscriptionScheduler(getSpringPssMessageListenerResolver());
		return subscriptionScheduler;
}
```
### 发布消息
消息类需实现PssMessage接口：
```java
public class LogMessage implements PssMessage {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

public class KafkaTest {

	@Autowired
	private PssPublisher publisher;
	

	/**
	 *
	 *发布消息
	 */
	@Test
	public void pubMessage() {
		LogMessage logMessage = new LogMessage();
		logMessage.setName("test");
		publisher.publish(logMessage);
	}
}
```
### 订阅消息
实现PssMessageTopicListener接口泛型为订阅的消息类型
```java
public class LogListener implements  PssMessageTopicListener<LogMessage>{
	
	private final Logger logger = LoggerFactory.getLogger(LogListener.class);

	@Override
	public void onMessage(LogMessage message, MessageContext context) {
		logger.debug("===========================收到消息了========================");
	}

}
```
     

