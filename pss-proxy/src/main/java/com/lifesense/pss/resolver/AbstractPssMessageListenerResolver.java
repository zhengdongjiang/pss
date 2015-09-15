package com.lifesense.pss.resolver;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;

import com.lifesense.pss.MessageContext;
import com.lifesense.pss.api.PssMessage;

/**
 * @author ZengFC
 *
 */
public abstract class AbstractPssMessageListenerResolver extends ConfigurableListenerResolver implements ListenerResolver{
	
	private float threadsTimes = 2;	//线程因数. 表示每个订阅的消息大概由几个线程去监听, 可以是小数
	
	

	public float getThreadsTimes() {
		return threadsTimes;
	}

	public void setThreadsTimes(float threadsTimes) {
		this.threadsTimes = threadsTimes;
	}

	public abstract <T extends PssMessage>void doListener(String topic, byte[] message, MessageContext context);

	@Override
	public abstract Map<String, List<KafkaStream<byte[], byte[]>>> buildConsumer();

	protected ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		String zookeeperUrl = getZookeeper().replaceAll("^zookeeper://", "");
		props.put("zookeeper.connect", zookeeperUrl);
		props.put("group.id", getAppId());
		props.put("zookeeper.session.timeout.ms", getSessionTimeout()+"");
		props.put("zookeeper.sync.time.ms", getSyncTimeout()+"");
		props.put("auto.commit.interval.ms", getAutoCommitInterval()+"");
		return new ConsumerConfig(props);
	}

	
	
}
