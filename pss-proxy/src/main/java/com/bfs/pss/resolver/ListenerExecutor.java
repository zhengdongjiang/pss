package com.bfs.pss.resolver;

import java.util.concurrent.ThreadPoolExecutor;

import kafka.consumer.ConsumerIterator;
import kafka.message.MessageAndMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bfs.pss.MessageContext;
import com.bfs.pss.encode.ObjectEncoder;

/**
 * @author ZengFC
 *
 */
public class ListenerExecutor implements Runnable {
	private ConsumerIterator<byte[], byte[]> consumer;
	private ThreadPoolExecutor executor;
	private ListenerResolver listenerResolver;
	private String topic;
	private Logger logger = LoggerFactory.getLogger(ListenerExecutor.class);

	public ListenerExecutor(ConsumerIterator<byte[], byte[]> consumer, ThreadPoolExecutor executor, String topic, ListenerResolver listenerResolver) {
		super();
		this.consumer = consumer;
		this.executor = executor;
		this.topic = topic;
		this.listenerResolver = listenerResolver;
	}

	@Override
	public void run() {
		if (consumer.hasNext()) {
			MessageAndMetadata<byte[], byte[]> m = consumer.next();
			//取出消息后继续监听
			submitCaller(new ListenerExecutor(consumer, executor, topic, listenerResolver));
			
			logger.debug("received message of topic {}: {} |context: {}", topic, new String(m.message()), new String(m.key()));

			MessageContext context = null;
			try {
				context = ObjectEncoder.mapper.readValue(m.key(), MessageContext.class);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new IllegalArgumentException(e);
			}
			String appId = listenerResolver.getAppId();
			if (!(listenerResolver.isIgnoreSelfMessage() && appId.equals(context.getAppId()))) {
				listenerResolver.doListener(topic, m.message(), context);
			}
		}
	}

	protected void submitCaller(ListenerExecutor runner) {
		ThreadPoolExecutor currentExecutor = runner.executor;
		currentExecutor.submit(runner);
		logger.debug("submit runner, current active threads: {}, current pool threads: {} ", currentExecutor.getActiveCount(), currentExecutor.getPoolSize());

	}

}
