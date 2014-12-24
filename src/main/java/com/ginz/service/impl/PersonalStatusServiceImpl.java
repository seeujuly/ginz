package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.PubPersonalStatus;
import com.ginz.service.PersonalStatusService;

@Service("personalStatusService")
public class PersonalStatusServiceImpl implements PersonalStatusService {

	private BaseDao<PubPersonalStatus> personalStatusDao;
	
	public BaseDao<PubPersonalStatus> getPersonalStatusDao() {
		return personalStatusDao;
	}

	@Autowired
	public void setPersonalStatusDao(BaseDao<PubPersonalStatus> personalStatusDao) {
		this.personalStatusDao = personalStatusDao;
	}

	@Override
	public PubPersonalStatus loadPersonalStatus(Long id) {
		return personalStatusDao.get(PubPersonalStatus.class, id);
	}

	@Override
	public void savePersonalStatus(PubPersonalStatus personalStatus) {
		personalStatusDao.save(personalStatus);
	}

	@Override
	public void deletePersonalStatus(Long id){
		PubPersonalStatus p = personalStatusDao.get(PubPersonalStatus.class, id);
		if (p != null) {
			personalStatusDao.delete(p);
		}
	}
	
	@Override
	public List<PubPersonalStatus> findPersonalStatus(String condition) {
		String hql = "from PubPersonalStatus where 1=1" + condition;
		return personalStatusDao.find(hql);
	}
	
	@Override
	public List<PubPersonalStatus> findPersonalStatus(String condition, int page, int rows) {
		String hql = "from PubPersonalStatus where 1=1" + condition;
		return personalStatusDao.find(hql, (page - 1) * rows + 1, rows);
	}

}
