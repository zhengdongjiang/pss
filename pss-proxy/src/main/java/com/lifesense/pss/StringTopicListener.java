package com.lifesense.pss;

import java.util.Map;

public interface StringTopicListener {
	void onMessage(String message, Map<String, String> headers);
}
