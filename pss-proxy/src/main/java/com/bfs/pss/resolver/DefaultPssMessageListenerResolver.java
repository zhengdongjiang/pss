package com.bfs.pss.resolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bfs.pss.MessageContext;
import com.bfs.pss.PssMessageTopicListener;
import com.bfs.pss.api.PssMessage;
import com.bfs.pss.encode.ObjectEncoder;

/**
 * @author ZengFC
 *
 */
public class DefaultPssMessageListenerResolver extends AbstractPssMessageListenerResolver implements ListenerResolver{
	private Map<String, PssMessageTopicListener<? extends PssMessage>> pssListeners;
	private Logger logger = LoggerFactory.getLogger(DefaultPssMessageListenerResolver.class);
	
	
	
	/** 添加新的事件监听器
	 * @param listener
	 * @return
	 */
	public DefaultPssMessageListenerResolver addListener(PssMessageTopicListener<? extends PssMessage> listener) {
		if (listener == null) {
			throw new NullPointerException("listener不能为null");
		}
		if (pssListeners == null) {
			pssListeners = new HashMap<String, PssMessageTopicListener<? extends PssMessage>>();
		}

		Type[] types = listener.getClass().getGenericInterfaces();
		@SuppressWarnings("unchecked")
		Class<? extends PssMessage> pssMessageType = (Class<? extends PssMessage>) ((ParameterizedType) (types[0])).getActualTypeArguments()[0];
		pssListeners.put(pssMessageType.getName(), listener);
		return this;
	}
	
	@Override
	public <T extends PssMessage>void doListener(String topic, byte[] message, MessageContext context) {
		if (pssListeners != null && pssListeners.containsKey(topic)){
			@SuppressWarnings("unchecked")
			PssMessageTopicListener<T> listener = (PssMessageTopicListener<T>) pssListeners.get(topic);
			@SuppressWarnings("unchecked")
			Class<T> messageType = (Class<T>) ((ParameterizedType)(listener.getClass().getGenericInterfaces()[0])).getActualTypeArguments()[0];
			T msg = null;
			try {
				msg = ObjectEncoder.mapper.readValue(message, messageType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new IllegalArgumentException(e);
			}
			listener.onMessage(msg, context);
		}
	}

	@Override
	public Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer() {
		setConsumer(kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig()));
		if (getThreads() < pssListeners.size()  * getPartitionsPerTopic()){
			setThreads((pssListeners.size()  * getPartitionsPerTopic()) + 1);
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		
		for (String pssTopic : pssListeners.keySet()) {
			topicCountMap.put(pssTopic, getPartitionsPerTopic());
		}
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = null;
		if (!topicCountMap.isEmpty()) {
			consumerMap = getConsumer().createMessageStreams(topicCountMap);
		}
		return consumerMap;
	}

}
