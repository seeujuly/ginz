package com.ginz.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
	
	public HashMap<String, Object> findEventBySql(String condition){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select t.id,t.subject,t.createTime,t.picIds,t.userId from pub_event t ");
		sb.append(" where 1=1 ");
		if(condition!=null&&!condition.equals("")){
			sb.append(condition);
		}
		hm.put("list", eventDao.queryBySql(sb.toString()));
		return hm;
	}
	
	//个人用户查看社区生活页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	@Override
	public HashMap<String, Object> seachEvents(String in, String notIn) {
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select t.id,t.subject,t.createTime,t.userId,t.picIds,t.label from pub_event t ");
		sb.append(" where 1=1 ");
/*		if(in!=null&&!in.equals("")){
			sb.append(" and CONCAT(subject,',',label) REGEXP '" + in + "' ");
		}*/
		if(notIn!=null&&!notIn.equals("")){
			sb.append(" and CONCAT(subject,',',label) not REGEXP '" + notIn + "' ");
		}
		sb.append(" and status not REGEXP '1' order by createTime desc ");
		hm.put("list", eventDao.queryBySql(sb.toString()));
		return hm;
		
	}
	
	//进入个人主页时显示个人发布的所有信息
	public HashMap<String, Object> listRelease(String userId){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		
		if(StringUtils.equals(userId.substring(0, 1), "u")){
			sb.append(" select t.id,t.subject,t.picIds,3 releaseType, t.createTime ");
			sb.append(" from pub_activities t where t.userId = '" + userId + "'");
			sb.append(" UNION SELECT t1.id,t1.subject,t1.picIds,2 releaseType,");
			sb.append(" t1.createTime from pub_event t1 WHERE t1.userId = '" + userId + "'");
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			sb.append(" select t.id,t.subject,t.picIds,1 releaseType, t.createTime ");
			sb.append(" from pub_notice t where t.property_id = '" + userId + "'");
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			
		}
		
		
		sb.append(" ORDER BY createTime DESC ");	
		hm.put("list", eventDao.queryBySql(sb.toString()));
		hm.put("cnt", eventDao.queryBySql(sb.toString()).size());
		return hm;
		
	}
	
	//获取所有的社区生活信息标签
	public HashMap<String, Object> getLabels(){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT GROUP_CONCAT(label SEPARATOR ',') as labels FROM pub_event ");
		//SELECT GROUP_CONCAT(personal_tag SEPARATOR ',') as tab FROM ac_user_detail where personal_tag LIKE '%逗%'
		hm.put("list", eventDao.queryBySql(sb.toString()));
		return hm;
		
	}

	//系统设置中用于查询用户赞过/评论过的所有信息
	@Override
	public HashMap<String, Object> listAllRelease(String noticeIds, String eventIds, String activityIds) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT '3' as releaseType, t.id, t.subject, t.createTime, t.userId, t.picIds");
		sb.append(" from pub_activities t where 1=1 ");
		if(StringUtils.isNotEmpty(activityIds)){
			sb.append(" and t.id in (" + activityIds + ") ");
		}
		sb.append(" UNION select '2' as releaseType, t1.id, t1.subject, t1.createTime, t1.userId, t1.picIds");
		sb.append(" from pub_event t1 where 1=1 ");
		if(StringUtils.isNotEmpty(eventIds)){
			sb.append(" and t1.id in (" + eventIds + ") ");
		}
		sb.append(" UNION SELECT '1' as releaseType, t2.id, t2.subject, t2.createTime, t2.property_id as userId, t2.picIds");
		sb.append(" from pub_notice t2 where 1=1 ");
		if(StringUtils.isNotEmpty(noticeIds)){
			sb.append(" and t12.id in (" + noticeIds + ") ");
		}
		sb.append(" ORDER BY createTime ");
		hm.put("list", eventDao.queryBySql(sb.toString()));
		return hm;
	}
	
}
