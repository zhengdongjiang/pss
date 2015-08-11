package com.lifesense.pss.resolver;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import kafka.consumer.ConsumerIterator;
import kafka.message.MessageAndMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lifesense.pss.encode.ObjectEncoder;

public class ListenerExecutor implements Runnable {
	private ConsumerIterator<byte[], byte[]> consumer;
	private ExecutorService executor;
	private ListenerResolver listenerResolver;
	private String topic;
	private Logger logger = LoggerFactory.getLogger(ListenerExecutor.class);

	public ListenerExecutor(ConsumerIterator<byte[], byte[]> consumer, ExecutorService executor, String topic, ListenerResolver listenerResolver) {
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
			submitCaller(new ListenerExecutor(consumer, executor, topic, listenerResolver));

			TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};

			HashMap<String, String> headers;
			try {
				headers = ObjectEncoder.mapper.readValue(m.key(), typeRef);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new IllegalArgumentException(e);
			}
			listenerResolver.doListener(topic, m.message(), headers);
		}
	}

	protected void submitCaller(ListenerExecutor runner) {
		runner.executor.submit(runner);

	}

}
