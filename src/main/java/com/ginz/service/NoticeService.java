package com.ginz.service;

import java.util.List;

import com.ginz.model.PubNotice;

public interface NoticeService {

	public PubNotice loadNotice(Long id);

	public void saveNotice(PubNotice notice);
	
	public void updateNotice(PubNotice notice);
	
	public void deleteNotice(Long id);
	
	public List<PubNotice> findNotice(String condition, int page, int rows);
	
}
