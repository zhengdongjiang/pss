package com.lifesense.pss.proxy;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.lifesense.pss.MessageContext;
import com.lifesense.pss.PssPublisher;
import com.lifesense.pss.api.PssMessage;
import com.lifesense.pss.encode.ObjectEncoder;

public class PssPublisherProxy implements PssPublisher {
	private String kafkaBrokers;
	private String appId;
	private Producer<MessageContext, Object> producer;
	

	public String getKafkaBrokers() {
		return kafkaBrokers;
	}

	public void setKafkaBrokers(String kafkaBrokers) {
		this.kafkaBrokers = kafkaBrokers;
	}

	public void init() {
		// 设置配置属性
		Properties props = new Properties();
		props.put("metadata.broker.list", this.getKafkaBrokers());
		props.put("serializer.class", ObjectEncoder.class.getName());
		props.put("key.serializer.class", ObjectEncoder.class.getName());
		//props.put("key.serializer.class", "kafka.serializer.StringEncoder");
		// http://kafka.apache.org/08/configuration.html
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);

		// 创建producer
		producer = new Producer<MessageContext, Object>(config);

	}
	
	public void destory(){
		if (this.producer != null) {
			producer.close();
		}
	}


	@Override
	public void publish(PssMessage message, Map<String, String> attributes) {
		publish(message.getClass().getName(), message, attributes);
	}
	
	private void publish(String topic, Object message, Map<String, String> attributes){
		if (message == null){
			throw new NullPointerException("发布的消息不能为null");
		}
		if (topic == null || topic.trim().length() < 1){
			throw new NullPointerException("发布的消息的topic不能为null");
		}
		if (appId == null || topic.trim().length() < 1){
			throw new NullPointerException("尚未设置当前系统的appId");
		}
		if (attributes == null){
			attributes = new HashMap<String, String>();
		}
		
		MessageContext context = new MessageContext();
		context.setAppId(appId);
		context.setAttributes(attributes);
		context.setPublishTime(Calendar.getInstance());
		
		// 如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
		KeyedMessage<MessageContext, Object> data = new KeyedMessage<MessageContext, Object>(topic, context, message);
		producer.send(data);
	}

	@Override
	public void publish(PssMessage message) {
		this.publish(message, null);
	}

	@Override
	public void publish(String topic, String message, Map<String, String> attributes) {
		this.publish(topic, message, attributes);
	}

	@Override
	public void publish(String topic, String message) {
		this.publish(topic, message);
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}
