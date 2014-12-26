package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.PubEvent;
import com.ginz.service.EventService;

@Service("eventService")
public class EventServiceImpl implements EventService {

	private BaseDao<PubEvent> eventDao;
	
	public BaseDao<PubEvent> getEventDao() {
		return eventDao;
	}

	@Autowired
	public void setEventDao(BaseDao<PubEvent> eventDao) {
		this.eventDao = eventDao;
	}

	@Override
	public PubEvent loadEvent(Long id) {
		return eventDao.get(PubEvent.class, id);
	}

	@Override
	public PubEvent saveEvent(PubEvent personalStatus) {
		return eventDao.save(personalStatus);
	}

	@Override
	public void deleteEvent(Long id){
		PubEvent p = eventDao.get(PubEvent.class, id);
		if (p != null) {
			eventDao.delete(p);
		}
	}
	
	@Override
	public List<PubEvent> findEvent(String condition) {
		String hql = "from PubEvent where 1=1" + condition;
		return eventDao.find(hql);
	}
	
	@Override
	public List<PubEvent> findEvent(String condition, int page, int rows) {
		String hql = "from PubEvent where 1=1" + condition;
		return eventDao.find(hql, (page - 1) * rows + 1, rows);
	}

}
