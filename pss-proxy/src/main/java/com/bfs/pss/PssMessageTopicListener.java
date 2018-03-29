package com.bfs.pss;

import com.bfs.pss.api.PssMessage;


/**
 * @author ZengFC
 *
 * @param <T>
 */
public interface PssMessageTopicListener<T extends PssMessage> {
	void onMessage(T message, MessageContext context);
}
