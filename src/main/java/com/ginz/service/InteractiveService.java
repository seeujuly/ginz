package com.ginz.service;

import java.util.HashMap;
import java.util.List;

import com.ginz.model.PubInteractive;

public interface InteractiveService {

	public PubInteractive loadInteractive(Long id);

	public PubInteractive saveInteractive(PubInteractive interactive);
	
	public PubInteractive updateInteractive(PubInteractive interactive);
	
	public void deleteInteractive(Long id);
	
	public List<PubInteractive> findInteractive(String condition);
	
	public List<PubInteractive> findInteractive(String condition, int page, int rows);
	
	//个人用户主动搜索————用输入的关键字，搜索相关连的信息（主题，内容，标签）
	public HashMap<String, Object> seachInteractive(String keyWord, int page, int rows);
	
	//个人用户查看活动页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	public HashMap<String, Object> seachInteractive(String in, String notIn, int page, int rows);
	
}
