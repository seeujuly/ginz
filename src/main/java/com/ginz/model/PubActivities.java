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
@Table(name = "pub_activities")
public class PubActivities implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="userId")
	private String userId;
	
	@Column(name="subject")
	private String subject;
	
	@Column(name="label")
	private String label;	//标签，最多10个标签，每个标签长度不超过5个汉字
	
	@Column(name="place")
	private String place;
	
	@Column(name="cost")
	private String cost;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createTime")
	private Date createTime;
	
	@Column(name="picIds")
	private String picIds;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="startTime")
	private Date startTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="endTime")
	private Date endTime;
	
	@Column(name="signUp")
	private String signUp;	//报名用户
	
	@Column(name="joinIn")
	private String joinIn;	//参加用户
	
	@Column(name="status")
	private String status;	//信息状态
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="closeTime")
	private Date closeTime;	//关闭时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updateTime")
	private Date updateTime;
	
	@Column(name="flag")
	private String flag;
	
	public PubActivities(){
		
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPicIds() {
		return picIds;
	}

	public void setPicIds(String picIds) {
		this.picIds = picIds;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getSignUp() {
		return signUp;
	}

	public void setSignUp(String signUp) {
		this.signUp = signUp;
	}

	public String getJoinIn() {
		return joinIn;
	}

	public void setJoinIn(String joinIn) {
		this.joinIn = joinIn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
