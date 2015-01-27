package com.ginz.service;

import java.util.HashMap;
import java.util.List;

import com.ginz.model.PubActivities;

public interface ActivitiesService {

	public PubActivities loadActivities(Long id);

	public PubActivities saveActivities(PubActivities activities);
	
	public PubActivities updateActivities(PubActivities activities);
	
	public void deleteActivities(Long id);
	
	public List<PubActivities> findActivities(String condition);
	
	public List<PubActivities> findActivities(String condition, int page, int rows);
	
	//个人用户查看活动页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	public HashMap<String, Object> searchActivities(String in, String notIn, int page, int rows);
	
	//用户发布信息之后搜索相关的个人用户群体,推送消息给他们(以发布信息的主题,内容,标签为关键字,搜索目标为个人的喜好)
	public HashMap<String,Object> searchUsers();
	
}
