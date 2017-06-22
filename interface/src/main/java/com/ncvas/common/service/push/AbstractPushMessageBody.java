package com.ncvas.common.service.push;

import java.util.Date;

/**
 * 抽象的推送消息体
 * @author twy
 *
 */
public abstract class AbstractPushMessageBody implements Pushable, PushMessageBody {

	/**
	 * 消息ID
	 */
	protected String messageId;
	/**
	 * 消息描述
	 */
	protected String description;
	/**
	 * 消息有效开始时间
	 */
	protected Date startTime;
	/**
	 * 消息有效结束时间
	 */
	protected Date expireTime;
	/**
	 * 构造函数
	 * @param messageId 消息ID
	 * @param description 消息描述
	 * @param startTime 消息有效开始时间
	 * @param expireTime 消息有效结束时间
	 */
	public AbstractPushMessageBody(String messageId, String description,
			Date startTime, Date expireTime) {
		this.messageId = messageId;
		this.description = description;
		this.startTime = startTime;
		this.expireTime = expireTime;
	}

	@Override
	public PushMessageBody getPushMessageBody() {
		return this;
	}

	@Override
	public String getMessageId() {
		return messageId;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	@Override
	public Date getExpireTime() {
		return expireTime;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
	
}
