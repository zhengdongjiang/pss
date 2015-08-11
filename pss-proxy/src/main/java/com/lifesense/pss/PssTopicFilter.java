package com.lifesense.pss;

import kafka.consumer.TopicFilter;

public class PssTopicFilter extends TopicFilter{

	public PssTopicFilter(String rawRegex) {
		super(rawRegex);
	}

	@Override
	public boolean isTopicAllowed(String topic, boolean excludeInternalTopics) {
		// TODO Auto-generated method stub
		return false;
	}

}
