package com.ginz.service;

import java.util.HashMap;
import java.util.List;

import com.ginz.model.PubEvent;

public interface EventService {

	//个人动态信息
	public PubEvent loadEvent(Long id);

	public PubEvent saveEvent(PubEvent event);
	
	public PubEvent updateEvent(PubEvent event);
	
	public void deleteEvent(Long id);
	
	public List<PubEvent> findEvent(String condition);
	
	public List<PubEvent> findEvent(String condition, int page, int rows);
	
	public HashMap<String, Object> findEventBySql(String condition);

	//个人用户查看社区生活页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	public HashMap<String, Object> seachEvents(String in, String notIn, int page, int rows);
	
	//进入个人主页时显示个人发布的所有信息
	public HashMap<String, Object> listRelease(Long userId);
	
	//获取所有的社区生活信息标签
	public HashMap<String, Object> getLabels();
	
	
	
}
