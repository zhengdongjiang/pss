package com.lifesense.pss.resolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;

import com.lifesense.pss.MessageContext;
import com.lifesense.pss.StringTopicListener;
import com.lifesense.pss.api.PssMessage;

/**
 * @author ZengFC
 *
 */
public class DefaultPatternMessageListenerResolver implements ListenerResolver{
	private String topicPattern;
	private StringTopicListener listener;
	private String zookeeper;
	private String appId;
	private int sessionTimeout = 400;
	private int syncTimeout = 200;
	private int autoCommitInterval = 1000;
	private ConsumerConnector consumer;
	private int threads = 3;
	private boolean ignoreSelfMessage = true;
	
	public void setListener(String topicPattern, StringTopicListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener不能为null");
		}
		this.topicPattern = topicPattern;
		this.listener = listener;
	}
	
	public String getZookeeper() {
		return zookeeper;
	}

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



	@Override
	public <T extends PssMessage>void doListener(String topic, byte[] message, MessageContext context) {
			listener.onMessage(new String(message), context);
	}

	@Override
	public Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer() {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
		
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = new HashMap<String, List<KafkaStream<byte[],byte[]>>>();
		List<KafkaStream<byte[], byte[]>> streams = consumer.createMessageStreamsByFilter(new Whitelist(topicPattern), threads);
		consumerMap.put(topicPattern, streams);
		return consumerMap;
		
	}

	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", getZookeeper());
		props.put("group.id", getAppId());
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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public boolean isIgnoreSelfMessage() {
		return ignoreSelfMessage;
	}

	public void setIgnoreSelfMessage(boolean ignoreSelfMessage) {
		this.ignoreSelfMessage = ignoreSelfMessage;
	}
	
}
