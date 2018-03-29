# kafka消息发布订阅系统
***
### 在spring中配置：
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
