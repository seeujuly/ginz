package com.ginz.service.impl;

import java.util.HashMap;
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
	public PubEvent updateEvent(PubEvent event){
		return eventDao.update(event);
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
	
	//进入个人主页时显示个人发布的所有信息
	public HashMap<String, Object> listRelease(Long userId, int page, int rows){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select t.id,t.subject,t.picIds,3 releaseType, t.createTime ");
		sb.append("  from pub_activities t where t.userId = " + userId);
		sb.append(" UNION SELECT t1.id,t1.subject,t1.picIds,0 releaseType,");
		sb.append(" t1.createTime from pub_event t1 WHERE t1.userId = " + userId);
		sb.append(" ORDER BY createTime DESC ");	
		hm.put("list", eventDao.queryBySql(sb.toString()));
		//hm.put("list", eventDao.queryBySql(sb.toString(), (page-1)*rows+1, rows));
		return hm;
		
	}

}
