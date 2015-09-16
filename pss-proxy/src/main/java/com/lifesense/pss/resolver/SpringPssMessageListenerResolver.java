package com.lifesense.pss.resolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.lifesense.pss.MessageContext;
import com.lifesense.pss.PssMessageTopicListener;
import com.lifesense.pss.api.PssMessage;
import com.lifesense.pss.encode.ObjectEncoder;

/** 适用于Spring环境的Resolver
 * @author ZengFC
 *
 */
public class SpringPssMessageListenerResolver extends AbstractPssMessageListenerResolver implements ListenerResolver, ApplicationContextAware{
	private Logger logger = LoggerFactory.getLogger(SpringPssMessageListenerResolver.class);
	private Map<String, Class<? extends PssMessage>> topicMsgTypeMap = new HashMap<String, Class<? extends PssMessage>>();
	private Map<String, String> topicBeanNameMap = new HashMap<String, String>();
	
	private ApplicationContext applicationContext;

	@Override
	public <T extends PssMessage> void doListener(String topic, byte[] message, MessageContext context) {
		@SuppressWarnings("unchecked")
		Class<T> messageType = (Class<T>) topicMsgTypeMap.get(topic);
		String beanName = topicBeanNameMap.get(topic);
		@SuppressWarnings("unchecked")
		PssMessageTopicListener<T> listener = (PssMessageTopicListener<T>) applicationContext.getBean(beanName);
		if (listener == null) {
			throw new NullPointerException("找不到话题" + topic + "的处理bean, 请检查是否有该话题对应的Listener");
		}
		T msg = null;
		try {
			msg = ObjectEncoder.mapper.readValue(message, messageType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
		listener.onMessage(msg, context);
	}

	@Override
	public Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer() {
		setConsumer(kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig()));
		@SuppressWarnings("rawtypes")
		Map<String,PssMessageTopicListener> maps = applicationContext.getBeansOfType(PssMessageTopicListener.class);
		if (getThreads() < maps.size()  * getPartitionsPerTopic()){
			setThreads((maps.size()  * getPartitionsPerTopic()) + 1);
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

		for (String beanName : maps.keySet()) {
			PssMessageTopicListener<?> listener = maps.get(beanName);
			Class<?> target = null;
			if (listener instanceof Advised){
				Advised advised = (Advised) listener;
				target = advised.getTargetClass();
			}
			else {
				target = listener.getClass();
			}
			Type[] types  = ((ParameterizedType) target.getGenericInterfaces()[0]).getActualTypeArguments();
			
			@SuppressWarnings("unchecked")
			Class<? extends PssMessage> messageType = (Class<? extends PssMessage>) types[0];
			String topic = messageType.getName();
			
			topicCountMap.put(topic, getPartitionsPerTopic());
			topicMsgTypeMap.put(topic, messageType);
			topicBeanNameMap.put(topic, beanName);
		}
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = null;
		if (!topicCountMap.isEmpty()) {
			consumerMap = getConsumer().createMessageStreams(topicCountMap);
		}
		return consumerMap;
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}


}
