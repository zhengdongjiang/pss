package com.lifesense.pss;

import com.lifesense.pss.api.PssMessage;


/**
 * @author ZengFC
 *
 * @param <T>
 */
public interface PssMessageTopicListener<T extends PssMessage> {
	void onMessage(T message, MessageContext context);
}
