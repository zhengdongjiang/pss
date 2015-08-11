package com.lifesense.pss;

import java.util.Map;

import com.lifesense.pss.api.PssMessage;


public interface PssMessageTopicListener<T extends PssMessage> {
	void onMessage(T message, Map<String, String> headers);
}
