package com.ginz.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "msg_message_info")
public class MsgMessageInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="userId")
	private String userId;
	
	@Column(name="target_userId")
	private String targetUserId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createTime")
	private Date createTime;
	
	@Column(name="subject")
	private String subject;
	
	@Column(name="content")
	private String content;
	
	@Column(name="picIds")
	private String picIds;
	
	@Column(name="releaseId")
	private Long releaseId;
	
	@Column(name="releaseType")
	private String releaseType;
	
	@Column(name="message_type")
	private String messageType;
	
	@Column(name="flag")
	private String flag;
	
	public MsgMessageInfo(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicIds() {
		return picIds;
	}

	public void setPicIds(String picIds) {
		this.picIds = picIds;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}

	public String getReleaseType() {
		return releaseType;
	}

	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}

}
