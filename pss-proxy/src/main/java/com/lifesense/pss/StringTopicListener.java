package com.lifesense.pss;


/**
 * @author ZengFC
 *
 */
public interface StringTopicListener {
	void onMessage(String message, MessageContext context);
}
