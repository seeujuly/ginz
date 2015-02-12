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
@Table(name = "ac_user_detail")
public class AcUserDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="userId")
	private String userId;
	
	@Column(name="gender")
	private String gender;	//性别
	
	@Column(name="isOpen_gender")
	private String isOpenGender;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="birthday")
	private Date birthday;	//生日
	
	@Column(name="isOpen_birthday")
	private String isOpenBirthday;

	@Column(name="age")
	private int age;	//年龄
	
	@Column(name="constellation")
	private String constellation;	//星座
	
	@Column(name="emotional_state")
	private String emotionalState;	//情感状态
	
	@Column(name="isOpen_emotional")
	private String isOpenEmotional;
	
	@Column(name="career")
	private String career;	//职业
	
	@Column(name="isOpen_career")
	private String isOpenCareer;
	
	@Column(name="company")
	private String company;	//公司
	
	@Column(name="isOpen_company")
	private String isOpenCompany;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="work_start")
	private Date workStart;	//工作时间-开始
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="work_end")
	private Date workEnd;	//工作时间-结束
	
	@Column(name="isOpen_workTime")
	private String isOpenWorkTime;
	
	@Column(name="school")
	private String school;	//学校
	
	@Column(name="isOpen_school")
	private String isOpenSchool;
	
	@Column(name="personal_tag")
	private String personalTag;		//个人标签：由用户自己添加
	
	@Column(name="system_tag")
	private String systemTag;		//系统标签：由系统根据用户参与的信息条分析其中使用最多的标签
	
	@Column(name="catering")
	private String catering;	//餐饮喜好
	
	@Column(name="social_contact")
	private String socialContact;	//社交喜好
	
	@Column(name="travel")
	private String travel;	//旅游喜好
	
	@Column(name="sports")
	private String sports;	//运动喜好
	
	@Column(name="music")
	private String music;	//音乐喜好
	
	@Column(name="others")
	private String others;	//其他喜好
	
	@Column(name="community_need")
	private String communityNeed;	//社区需要
	
	@Column(name="dislike")
	private String dislike;	//不喜欢
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createTime")
	private Date createTime;
	
	@Column(name="flag")
	private String flag;	//删除标识
	
	public AcUserDetail(){
		
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIsOpenGender() {
		return isOpenGender;
	}

	public void setIsOpenGender(String isOpenGender) {
		this.isOpenGender = isOpenGender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getIsOpenBirthday() {
		return isOpenBirthday;
	}

	public void setIsOpenBirthday(String isOpenBirthday) {
		this.isOpenBirthday = isOpenBirthday;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public String getEmotionalState() {
		return emotionalState;
	}

	public void setEmotionalState(String emotionalState) {
		this.emotionalState = emotionalState;
	}

	public String getIsOpenEmotional() {
		return isOpenEmotional;
	}

	public void setIsOpenEmotional(String isOpenEmotional) {
		this.isOpenEmotional = isOpenEmotional;
	}

	public String getCareer() {
		return career;
	}

	public void setCareer(String career) {
		this.career = career;
	}

	public String getIsOpenCareer() {
		return isOpenCareer;
	}

	public void setIsOpenCareer(String isOpenCareer) {
		this.isOpenCareer = isOpenCareer;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getIsOpenCompany() {
		return isOpenCompany;
	}

	public void setIsOpenCompany(String isOpenCompany) {
		this.isOpenCompany = isOpenCompany;
	}

	public Date getWorkStart() {
		return workStart;
	}

	public void setWorkStart(Date workStart) {
		this.workStart = workStart;
	}

	public Date getWorkEnd() {
		return workEnd;
	}

	public void setWorkEnd(Date workEnd) {
		this.workEnd = workEnd;
	}

	public String getIsOpenWorkTime() {
		return isOpenWorkTime;
	}

	public void setIsOpenWorkTime(String isOpenWorkTime) {
		this.isOpenWorkTime = isOpenWorkTime;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getIsOpenSchool() {
		return isOpenSchool;
	}

	public void setIsOpenSchool(String isOpenSchool) {
		this.isOpenSchool = isOpenSchool;
	}

	public String getPersonalTag() {
		return personalTag;
	}

	public void setPersonalTag(String personalTag) {
		this.personalTag = personalTag;
	}

	public String getSystemTag() {
		return systemTag;
	}

	public void setSystemTag(String systemTag) {
		this.systemTag = systemTag;
	}

	public String getCatering() {
		return catering;
	}

	public void setCatering(String catering) {
		this.catering = catering;
	}

	public String getSocialContact() {
		return socialContact;
	}

	public void setSocialContact(String socialContact) {
		this.socialContact = socialContact;
	}

	public String getTravel() {
		return travel;
	}

	public void setTravel(String travel) {
		this.travel = travel;
	}

	public String getSports() {
		return sports;
	}

	public void setSports(String sports) {
		this.sports = sports;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getCommunityNeed() {
		return communityNeed;
	}

	public void setCommunityNeed(String communityNeed) {
		this.communityNeed = communityNeed;
	}

	public String getDislike() {
		return dislike;
	}

	public void setDislike(String dislike) {
		this.dislike = dislike;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
