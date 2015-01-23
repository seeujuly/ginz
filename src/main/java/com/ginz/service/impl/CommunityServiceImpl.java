package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.Community;
import com.ginz.service.CommunityService;

@Service("communityService")
public class CommunityServiceImpl implements CommunityService {

	private BaseDao<Community> communityDao;
	
	public BaseDao<Community> getCommunityDao() {
		return communityDao;
	}

	@Autowired
	public void setCommunityDao(BaseDao<Community> communityDao) {
		this.communityDao = communityDao;
	}
	
	public Community load(Long id) {
		return communityDao.get(Community.class, id);
	}

	public void save(Community community) {
		communityDao.save(community);
	}

	public void update(Community community) {
		communityDao.update(community);
	}

	public void delete(Long id) {
		Community c = communityDao.get(Community.class, id);
		if (c != null) {
			communityDao.delete(c);
		}
	}

	public List<Community> find(String condition) {
		String hql = "from Community where 1=1" + condition;
		return communityDao.find(hql);
	}

}
