package com.ginz.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.PubActivities;
import com.ginz.service.ActivitiesService;

@Service("activitiesService")
public class ActivitiesServiceImpl implements ActivitiesService {

private BaseDao<PubActivities> activitiesDao;
	
	public BaseDao<PubActivities> getActivitiesDao() {
		return activitiesDao;
	}

	@Autowired
	public void setActivitiesDao(BaseDao<PubActivities> activitiesDao) {
		this.activitiesDao = activitiesDao;
	}

	@Override
	public PubActivities loadActivities(Long id) {
		return activitiesDao.get(PubActivities.class, id);
	}

	@Override
	public PubActivities saveActivities(PubActivities activities) {
		return activitiesDao.save(activities);
	}

	@Override
	public PubActivities updateActivities(PubActivities activities) {
		return activitiesDao.update(activities);
	}

	@Override
	public void deleteActivities(Long id) {
		PubActivities i = activitiesDao.get(PubActivities.class, id);
		if (i != null) {
			activitiesDao.delete(i);
		}
	}

	@Override
	public List<PubActivities> findActivities(String condition) {
		String hql = "from PubActivities where 1=1" + condition;
		return activitiesDao.find(hql);
	}

	@Override
	public List<PubActivities> findActivities(String condition, int page,
			int rows) {
		String hql = "from PubActivities where 1=1" + condition;
		return activitiesDao.find(hql, (page - 1) * rows + 1, rows);
	}
	
	@Override
	public HashMap<String, Object> findActivitiesBySql(String condition){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select t.id,t.subject,t.createTime,t.picIds,t.userId from pub_activities t ");
		sb.append(" where 1=1 ");
		if(condition!=null&&!condition.equals("")){
			sb.append(condition);
		}
		hm.put("list", activitiesDao.queryBySql(sb.toString()));
		return hm;
	}
	
	//个人用户查看活动页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	@Override
	public HashMap<String, Object> searchActivities(String in, String notIn){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select t.id,t.subject,t.createTime,t.userId,t.picIds,t.label from pub_activities t ");
		sb.append(" where 1=1 ");
/*		if(in!=null&&!in.equals("")){
			sb.append(" and CONCAT(subject,',',label) REGEXP '" + in + "' ");
		}*/
		if(notIn!=null&&!notIn.equals("")){
			sb.append(" and CONCAT(subject,',',label) not REGEXP '" + notIn + "' ");
		}
		sb.append(" and status not REGEXP '1' order by createTime desc ");
		hm.put("list", activitiesDao.queryBySql(sb.toString()));
		return hm;
	}
	
	//获取所有的信息标签
	public HashMap<String, Object> getLabels(){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT GROUP_CONCAT(label SEPARATOR ',') as labels FROM pub_activities ");
		hm.put("list", activitiesDao.queryBySql(sb.toString()));
		return hm;
		
	}
	
}
