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
	
	public HashMap<String, Object> findActivitiesBySql(String condition);
	
	//个人用户查看活动页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	public HashMap<String, Object> searchActivities(String in, String notIn, int page, int rows);
	
	//获取所有的互动交易信息标签
	public HashMap<String, Object> getLabels();
	
}
