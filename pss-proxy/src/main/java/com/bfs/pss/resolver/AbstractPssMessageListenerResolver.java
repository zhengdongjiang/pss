package com.bfs.pss.resolver;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;

import com.bfs.pss.MessageContext;
import com.bfs.pss.api.PssMessage;

/**
 * @author ZengFC
 *
 */
public abstract class AbstractPssMessageListenerResolver extends ConfigurableListenerResolver implements ListenerResolver{
	

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
