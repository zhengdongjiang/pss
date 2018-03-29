package com.bfs.pss;

import java.util.Calendar;
import java.util.Map;

/** 消息上下文
 * @author ZengFC
 *
 */
public class MessageContext {
	private String appId;
	private Calendar publishTime;
	private Map<String, String> attributes;
	
	/** 该消息的发布者
	 * @return
	 */
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/** 该消息的产生时间
	 * @return
	 */
	public Calendar getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Calendar publishTime) {
		this.publishTime = publishTime;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
