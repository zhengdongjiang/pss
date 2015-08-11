package com.lifesense.pss.resolver;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import com.lifesense.pss.api.PssMessage;

/**
 * @author ZengFC
 *
 */
public abstract class AbstractPssMessageListenerResolver implements ListenerResolver{
	private String zookeeper;	//zookpeer地址. 如果是zookeer集群, 各个地址间以英文逗号隔开
	private String serverName;	//当前应用名称,只能是以英文字母开头, 英文字母or数字or下划线or减号的组合
	private int sessionTimeout = 400;
	private int syncTimeout = 200;
	private int autoCommitInterval = 1000;
	private float threadsTimes = 2;	//线程因数. 表示每个订阅的消息大概由几个线程去监听, 可以是小数
	private ConsumerConnector consumer;
	private int threads;
	
	public String getZookeeper() {
		return zookeeper;
	}

	/** zookpeer地址. 如果是zookeer集群, 各个地址间以英文逗号隔开
	 * @param zookeeper
	 */
	public void setZookeeper(String zookeeper) {
		this.zookeeper = zookeeper;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getSyncTimeout() {
		return syncTimeout;
	}

	public void setSyncTimeout(int syncTimeout) {
		this.syncTimeout = syncTimeout;
	}

	public int getAutoCommitInterval() {
		return autoCommitInterval;
	}

	public void setAutoCommitInterval(int autoCommitInterval) {
		this.autoCommitInterval = autoCommitInterval;
	}

	public String getServerName() {
		return serverName;
	}

	/** 当前应用名称,只能是以英文字母开头, 英文字母or数字or下划线or减号的组合, 必填  <br>
	 *  当前应用若为多个实例的集群时, 统一的serverName可以保证统一topic只被集群中的一台接收
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public float getThreadsTimes() {
		return threadsTimes;
	}

	public void setThreadsTimes(float threadsTimes) {
		this.threadsTimes = threadsTimes;
	}

	public abstract <T extends PssMessage>void doListener(String topic, byte[] message, Map<String, String> headers);

	@Override
	public abstract Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer();

	protected ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", getZookeeper());
		props.put("group.id", getServerName());
		props.put("zookeeper.session.timeout.ms", getSessionTimeout()+"");
		props.put("zookeeper.sync.time.ms", getSyncTimeout()+"");
		props.put("auto.commit.interval.ms", getAutoCommitInterval()+"");
		return new ConsumerConfig(props);
	}

	public int getThreads() {
		return threads;
	}

	public ConsumerConnector getConsumer() {
		return consumer;
	}

	protected void setConsumer(ConsumerConnector consumer) {
		this.consumer = consumer;
	}

	protected void setThreads(int threads) {
		this.threads = threads;
	}
	
}
