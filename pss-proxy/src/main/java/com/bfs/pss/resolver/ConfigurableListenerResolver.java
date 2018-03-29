package com.bfs.pss.resolver;

import kafka.javaapi.consumer.ConsumerConnector;

public abstract class ConfigurableListenerResolver implements ListenerResolver{
	private String zookeeper;	//zookpeer地址. 如果是zookeer集群, 各个地址间以英文逗号隔开
	private String appId;	//当前应用名称,只能是以英文字母开头, 英文字母or数字or下划线or减号的组合
	private int sessionTimeout = 1000;
	private int syncTimeout = 500;
	private int autoCommitInterval = 1000;
	private ConsumerConnector consumer;
	private int threads = 0;
	private boolean ignoreSelfMessage = true;
	private int maxQueueSize = 1000;
	private int partitionsPerTopic = 1;
	
	
	public String getZookeeper() {
		return zookeeper;
	}
	/** zookeeper地址
	 * @param zookeeper
	 */
	public void setZookeeper(String zookeeper) {
		this.zookeeper = zookeeper;
	}
	public String getAppId() {
		return appId;
	}
	
	/** 当前应用名称,只能是以英文字母开头, 英文字母or数字or下划线or减号的组合, 必填  <br>
	 *  当前应用若为多个实例的集群时, 统一的serverName可以保证统一topic只被集群中的一台接收
	 * @param serverName
	 */
	public void setAppId(String appId) {
		this.appId = appId;
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
	public ConsumerConnector getConsumer() {
		return consumer;
	}
	public void setConsumer(ConsumerConnector consumer) {
		this.consumer = consumer;
	}
	public int getThreads() {
		return threads;
	}
	public void setThreads(int threads) {
		this.threads = threads;
	}
	public boolean isIgnoreSelfMessage() {
		return ignoreSelfMessage;
	}
	public void setIgnoreSelfMessage(boolean ignoreSelfMessage) {
		this.ignoreSelfMessage = ignoreSelfMessage;
	}
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	public int getPartitionsPerTopic() {
		return partitionsPerTopic;
	}
	public void setPartitionsPerTopic(int partitionsPerTopic) {
		this.partitionsPerTopic = partitionsPerTopic;
	}
}
