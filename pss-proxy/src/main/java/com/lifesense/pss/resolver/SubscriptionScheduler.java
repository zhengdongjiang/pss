package com.lifesense.pss.resolver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ZengFC
 *
 */
public class SubscriptionScheduler {
	private ThreadPoolExecutor executor;
	private Logger logger = LoggerFactory.getLogger(SubscriptionScheduler.class);
	private ListenerResolver listenerResolver;

	public void destory() {
		if (listenerResolver.getConsumer() != null)
			listenerResolver.getConsumer().shutdown();
		if (executor != null)
			executor.shutdown();
		try {
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				logger.error("Tied out waiting for consumer threads to shut down, exiting uncleanly");
			}
		} catch (InterruptedException e) {
			logger.error("Interrupted during shutdown, exiting uncleanly");
		}
	}


	public SubscriptionScheduler(ListenerResolver listenerResolver) {
		super();
		this.listenerResolver = listenerResolver;
	}


	public void start() {
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = this.listenerResolver.buildConsumer();

		//executor = Executors.newFixedThreadPool(this.listenerResolver.getThreads());
		int threads = this.listenerResolver.getThreads();
		int maxQueueSize = this.listenerResolver.getMaxQueueSize();
		executor = new ThreadPoolExecutor(threads, threads*2, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(maxQueueSize));

		if (consumerMap != null) {
			//循环提交监听
			for (String topic : consumerMap.keySet()) {
				List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
				for (final KafkaStream<byte[], byte[]> stream : streams) {
					ListenerExecutor listenerExecutor = new ListenerExecutor(stream.iterator(), executor, topic, listenerResolver);
					listenerExecutor.submitCaller(listenerExecutor);
				}
			}
		}
	}


	public ListenerResolver getListenerResolver() {
		return listenerResolver;
	}

	public void setListenerResolver(ListenerResolver listenerResolver) {
		this.listenerResolver = listenerResolver;
	}

}
