package com.lifesense.pss;

import com.lifesense.pss.api.PssMessage;


public interface PssMessageTopicListener<T extends PssMessage> {
	void onMessage(T message, MessageContext context);
}
