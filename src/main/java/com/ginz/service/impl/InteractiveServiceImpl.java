package com.ginz.service.impl;

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
	public void saveInteractive(PubInteractive interactive) {
		interactiveDao.save(interactive);
	}

	@Override
	public void updateInteractive(PubInteractive interactive) {
		interactiveDao.update(interactive);
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

}
