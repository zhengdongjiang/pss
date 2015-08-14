package com.lifesense.pss;


public interface StringTopicListener {
	void onMessage(String message, MessageContext context);
}
