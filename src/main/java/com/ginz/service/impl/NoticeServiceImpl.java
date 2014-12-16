package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.PubNotice;
import com.ginz.service.NoticeService;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

	private BaseDao<PubNotice> noticeDao;
	
	public BaseDao<PubNotice> getNoticeDao() {
		return noticeDao;
	}

	@Autowired
	public void setNoticeDao(BaseDao<PubNotice> noticeDao) {
		this.noticeDao = noticeDao;
	}

	@Override
	public PubNotice loadNotice(Long id) {
		return noticeDao.get(PubNotice.class, id);
	}

	@Override
	public void saveNotice(PubNotice notice) {
		noticeDao.save(notice);
	}

	@Override
	public void updateNotice(PubNotice notice) {
		noticeDao.update(notice);
	}

	@Override
	public void deleteNotice(Long id) {
		PubNotice u = noticeDao.get(PubNotice.class, id);
		if (u != null) {
			noticeDao.delete(u);
		}
	}
	
	@Override
	public List<PubNotice> findNotice(String condition) {
		String hql = "from PubNotice where 1=1" + condition;
		return noticeDao.find(hql);
	}

	@Override
	public List<PubNotice> findNotice(String condition, int page, int rows) {
		String hql = "from PubNotice where 1=1" + condition;
		return noticeDao.find(hql, (page - 1) * rows + 1, rows);
	}

}
