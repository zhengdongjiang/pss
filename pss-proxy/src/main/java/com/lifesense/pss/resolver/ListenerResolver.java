package com.lifesense.pss.resolver;

import java.util.List;
import java.util.Map;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import com.lifesense.pss.MessageContext;
import com.lifesense.pss.api.PssMessage;

/** Topic监听器解析的实现方式
 * @author ZengFC
 *
 */
public interface ListenerResolver {
	
	/** zookeeper地址
	 * @return
	 */
	String getZookeeper();

	/** 当前应用名称,只能是以英文字母开头, 英文字母or数字or下划线or减号的组合 <br>
	 *  当前应用若为多个实例的集群时, 统一的appId可以保证统一topic只被集群中的一台接收
	 * @return
	 */
	String getAppId();
	
	
	boolean isIgnoreSelfMessage();

	int getSessionTimeout();

	int getSyncTimeout();

	int getAutoCommitInterval();
	
	Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer();

	<T extends PssMessage>void doListener(String topic, byte[] message, MessageContext context);
	
	ConsumerConnector getConsumer();
	
	int getThreads();
}
