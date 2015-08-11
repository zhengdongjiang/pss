package com.lifesense.pss.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.lifesense.pss.PssPublisher;
import com.lifesense.pss.api.PssMessage;
import com.lifesense.pss.encode.ObjectEncoder;

public class PssPublisherProxy implements PssPublisher {
	private String kafkaBrokers;
	private int serverId = 0;
	private Producer<Map<String, String>, Object> producer;
	

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
		producer = new Producer<Map<String, String>, Object>(config);

	}
	
	public void destory(){
		if (this.producer != null) {
			producer.close();
		}
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	@Override
	public void publish(PssMessage message, Map<String, String> headers) {
		publish(message.getClass().getName(), message, headers);
	}
	
	private void publish(String topic, Object message, Map<String, String> headers){
		if (message == null){
			throw new NullPointerException("发布的消息不能为null");
		}
		if (topic == null || topic.trim().length() < 1){
			throw new NullPointerException("发布的消息的topic不能为null");
		}
		if (headers == null){
			headers = new HashMap<String, String>();
		}
		headers.put("serverId", serverId + "");
		
		// 如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
		KeyedMessage<Map<String, String>, Object> data = new KeyedMessage<Map<String, String>, Object>(topic, headers, message);
		producer.send(data);
	}

	@Override
	public void publish(PssMessage message) {
		this.publish(message, null);
	}

	@Override
	public void publish(String topic, String message, Map<String, String> headers) {
		this.publish(topic, message, headers);
	}

	@Override
	public void publish(String topic, String message) {
		this.publish(topic, message);
	}

}
