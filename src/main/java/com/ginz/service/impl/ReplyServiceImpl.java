package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.PubComments;
import com.ginz.model.PubPraise;
import com.ginz.service.ReplyService;

@Service("replyService")
public class ReplyServiceImpl implements ReplyService {

	private BaseDao<PubComments> commentsDao;
	private BaseDao<PubPraise> praiseDao;
	
	public BaseDao<PubComments> getCommentsDao() {
		return commentsDao;
	}

	@Autowired
	public void setCommentsDao(BaseDao<PubComments> commentsDao) {
		this.commentsDao = commentsDao;
	}

	public BaseDao<PubPraise> getPraiseDao() {
		return praiseDao;
	}

	@Autowired
	public void setPraiseDao(BaseDao<PubPraise> praiseDao) {
		this.praiseDao = praiseDao;
	}

	//评论
	@Override
	public PubComments loadComments(Long id) {
		return commentsDao.get(PubComments.class, id);
	}

	@Override
	public void saveComments(PubComments comments) {
		commentsDao.save(comments);
	}

	@Override
	public void updateComments(PubComments comments) {
		commentsDao.update(comments);
	}

	@Override
	public void deleteComments(Long id) {
		PubComments c = commentsDao.get(PubComments.class, id);
		if (c != null) {
			commentsDao.delete(c);
		}
	}

	@Override
	public List<PubComments> findComments(String condition) {
		String hql = "from PubComments where 1=1" + condition;
		return commentsDao.find(hql);
	}
	
	@Override
	public List<PubComments> findComments(String condition, int page, int rows){
		String hql = "from PubComments where 1=1" + condition;
		return commentsDao.find(hql, (page - 1) * rows + 1, rows);
	}
	
	//点赞
	@Override
	public PubPraise loadPraise(Long id) {
		return praiseDao.get(PubPraise.class, id);
	}

	@Override
	public void savePraise(PubPraise comments) {
		praiseDao.save(comments);
	}

	@Override
	public void updatePraise(PubPraise comments) {
		praiseDao.update(comments);
	}

	@Override
	public void deletePraise(Long id) {
		PubPraise p = praiseDao.get(PubPraise.class, id);
		if (p != null) {
			praiseDao.delete(p);
		}
	}

	@Override
	public List<PubPraise> findPraise(String condition) {
		String hql = "from PubComments where 1=1" + condition;
		return praiseDao.find(hql);
	}
	
	@Override
	public List<PubPraise> findPraise(String condition, int page, int rows){
		String hql = "from PubPraise where 1=1" + condition;
		return praiseDao.find(hql, (page - 1) * rows + 1, rows);
	}

}
