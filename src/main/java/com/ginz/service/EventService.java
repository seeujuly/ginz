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
	
	//进入个人主页时显示个人发布的所有信息
	public HashMap<String, Object> listRelease(Long userId, int page, int rows);
}
