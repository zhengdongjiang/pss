package com.lifesense.pss.resolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;

import com.lifesense.pss.MessageContext;
import com.lifesense.pss.StringTopicListener;
import com.lifesense.pss.api.PssMessage;

/**
 * @author ZengFC
 *
 */
public class DefaultPatternMessageListenerResolver extends ConfigurableListenerResolver  implements ListenerResolver{
	private String topicPattern;
	private StringTopicListener listener;
	
	public void setListener(String topicPattern, StringTopicListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener不能为null");
		}
		this.topicPattern = topicPattern;
		this.listener = listener;
	}
	



	@Override
	public <T extends PssMessage>void doListener(String topic, byte[] message, MessageContext context) {
			listener.onMessage(new String(message), context);
	}

	@Override
	public Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer() {
		setConsumer(kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig()));
		
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = new HashMap<String, List<KafkaStream<byte[],byte[]>>>();
		List<KafkaStream<byte[], byte[]>> streams = getConsumer().createMessageStreamsByFilter(new Whitelist(topicPattern), getThreads());
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

	
}
