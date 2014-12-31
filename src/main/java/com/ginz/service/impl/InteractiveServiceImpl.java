package com.ginz.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.PubInteractive;
import com.ginz.service.InteractiveService;

@Service("interactiveService")
public class InteractiveServiceImpl implements InteractiveService {

	private BaseDao<PubInteractive> interactiveDao;
	
	public BaseDao<PubInteractive> getInteractiveDao() {
		return interactiveDao;
	}

	@Autowired
	public void setInteractiveDao(BaseDao<PubInteractive> interactiveDao) {
		this.interactiveDao = interactiveDao;
	}

	@Override
	public PubInteractive loadInteractive(Long id) {
		return interactiveDao.get(PubInteractive.class, id);
	}

	@Override
	public PubInteractive saveInteractive(PubInteractive interactive) {
		return interactiveDao.save(interactive);
	}

	@Override
	public PubInteractive updateInteractive(PubInteractive interactive) {
		return interactiveDao.update(interactive);
	}

	@Override
	public void deleteInteractive(Long id) {
		PubInteractive i = interactiveDao.get(PubInteractive.class, id);
		if (i != null) {
			interactiveDao.delete(i);
		}
	}

	@Override
	public List<PubInteractive> findInteractive(String condition) {
		String hql = "from PubInteractive where 1=1" + condition;
		return interactiveDao.find(hql);
	}

	@Override
	public List<PubInteractive> findInteractive(String condition, int page,
			int rows) {
		String hql = "from PubInteractive where 1=1" + condition;
		return interactiveDao.find(hql, (page - 1) * rows + 1, rows);
	}

	//个人用户主动搜索————用输入的关键字，搜索相关连的信息（主题，内容，标签）
	@Override
	public HashMap<String, Object> seachInteractive(String keyWord, int page, int rows){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("  ");
		hm.put("list", interactiveDao.queryBySql(sb.toString(), (page-1)*rows+1, rows));
		return hm;
	}
	
	//个人用户查看活动页面————使用用户的个人兴趣爱好作为关键字，搜索相关连的信息（主题，内容，标签）
	@Override
	public HashMap<String, Object> seachInteractive(String in, String notIn, int page, int rows){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" select t.id,t.subject,t.createTime,t.userId,t.picIds from pub_interactive t ");
		sb.append(" where 1=1 ");
		if(in!=null&&!in.equals("")){
			sb.append(" and (subject REGEXP '" + in + "' OR content REGEXP '" + in + "' OR label REGEXP '" + in + "') ");
		}
		if(notIn!=null&&!notIn.equals("")){
			sb.append(" and subject not REGEXP '" + notIn + "' and content not REGEXP '" + notIn + "' and label not REGEXP '" + notIn + "' ");
		}
		sb.append(" and status not REGEXP '2' order by createTime desc ");
		hm.put("list", interactiveDao.queryBySql(sb.toString(), (page-1)*rows+1, rows));
		return hm;
	}
	
}
