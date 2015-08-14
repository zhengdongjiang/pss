package com.lifesense.pss;

import java.util.Map;

import com.lifesense.pss.api.PssMessage;

/**
 * @author ZengFC
 *
 */
public interface PssPublisher {
	
	
	/** 发布消息到消息中心, 所有订阅了该消息的系统都将收到此消息
	 * @param message 消息内容
	 * @param headers 该消息的附加信息 是一组key-value值, 可以为null
	 */
	void publish(PssMessage message, Map<String, String> attributes);
	
	
	/** 发布消息到消息中心, 所有订阅了该消息的系统都将收到此消息
	 * @param message 消息内容
	 */
	void publish(PssMessage message);
	
	
	/** 发布消息到消息中心, 所有订阅了该消息的系统都将收到此消息
	 * @param topic 消息的topic
	 * @param message 消息内容
	 * @param headers 该消息的附加信息, 是一组key-value值, 可以为null
	 */
	void publish(String topic, String message, Map<String, String> attributes);
	
	
	/** 发布消息到消息中心, 所有订阅了该消息的系统都将收到此消息
	 * @param topic 消息的topic
	 * @param message 消息内容
	 */
	void publish(String topic, String message);
}
