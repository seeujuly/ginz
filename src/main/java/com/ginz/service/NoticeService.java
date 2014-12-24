package com.ginz.service;

import java.util.List;

import com.ginz.model.PubNotice;

public interface NoticeService {

	public PubNotice loadNotice(Long id);

	public PubNotice saveNotice(PubNotice notice);
	
	public PubNotice updateNotice(PubNotice notice);
	
	public void deleteNotice(Long id);
	
	public List<PubNotice> findNotice(String condition);
	
	public List<PubNotice> findNotice(String condition, int page, int rows);
	
}
