package com.ginz.service.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.AcMerchant;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.AcUserDetail;
import com.ginz.service.AccountService;
import com.ginz.util.base.Encrypt;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private BaseDao<AcUser> userDao;
	private BaseDao<AcUserDetail> userDetailDao;
	private BaseDao<AcProperty> propertyDao;
	private BaseDao<AcMerchant> merchantDao;
	
	public BaseDao<AcUser> getUserDao() {
		return userDao;
	}

	@Autowired
	public void setUserDao(BaseDao<AcUser> userDao) {
		this.userDao = userDao;
	}
	
	public BaseDao<AcUserDetail> getUserDetailDao() {
		return userDetailDao;
	}

	@Autowired
	public void setUserDetailDao(BaseDao<AcUserDetail> userDetailDao) {
		this.userDetailDao = userDetailDao;
	}

	public BaseDao<AcProperty> getPropertyDao() {
		return propertyDao;
	}

	@Autowired
	public void setPropertyDao(BaseDao<AcProperty> propertyDao) {
		this.propertyDao = propertyDao;
	}

	public BaseDao<AcMerchant> getMerchantDao() {
		return merchantDao;
	}

	@Autowired
	public void setMerchantDao(BaseDao<AcMerchant> merchantDao) {
		this.merchantDao = merchantDao;
	}
	
	//个人用户部分
	@Override
	public AcUser loginUser(AcUser user) {
		final String HQL_FIND = " from AcUser t where 1=1 {0} ";
		String condition = "";
		if (StringUtils.isNotEmpty(user.getEmail())) {
			condition += " and t.email = '" + user.getEmail() + "'";
		}
		if (StringUtils.isNotEmpty(user.getMobile())) {
			condition += " and t.mobile = '" + user.getMobile() + "'";
		}
		if (StringUtils.isNotEmpty(user.getPassword())) {
			condition += " and t.password = '" + Encrypt.e(user.getPassword()) + "'";
		}
		try {
			List<AcUser> list = userDao.find(MessageFormat.format(HQL_FIND, condition));
			if(list.size()>0){
				AcUser t = list.get(0);
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AcUser loadUser(Long id) {
		return userDao.get(AcUser.class, id);
	}

	@Override
	public void saveUser(AcUser user) {
		AcUser u = new AcUser();
		BeanUtils.copyProperties(user, u, new String[] { "password" });
		if (user.getCreateTime() == null) {
			u.setCreateTime(new Date());
		}
		u.setPassword(Encrypt.e(user.getPassword()));
		userDao.save(u);
	}

	@Override
	public void updateUser(AcUser user) {
		userDao.update(user);
	}

	@Override
	public void deleteUser(Long id) {
		AcUser u = userDao.get(AcUser.class, id);
		if (u != null) {
			userDao.delete(u);
		}
	}
	
	@Override
	public List<AcUser> findUser(String condition){
		String hql = "from AcUser where 1=1" + condition;
		return userDao.find(hql);
	}
	
	@Override
	public AcUserDetail loadUserDetail(Long id) {
		return userDetailDao.get(AcUserDetail.class, id);
	}

	@Override
	public void saveUserDetail(AcUserDetail userDetail) {
		userDetailDao.save(userDetail);
	}

	@Override
	public void updateUserDetail(AcUserDetail userDetail) {
		userDetailDao.saveOrUpdate(userDetail);
	}

	@Override
	public void deleteUserDetail(Long id) {
		AcUserDetail u = userDetailDao.get(AcUserDetail.class, id);
		if (u != null) {
			userDetailDao.delete(u);
		}
	}

	@Override
	public List<AcUserDetail> findUserDetail(String condition) {
		String hql = "from AcUserDetail where 1=1" + condition;
		return userDetailDao.find(hql);
	}

	//社区用户部分
	@Override
	public AcProperty loginProperty(AcProperty property) {
		final String HQL_FIND = " from AcProperty t where 1=1 {0} ";
		String condition = "";
		if (StringUtils.isNotEmpty(property.getEmail())) {
			condition += " and t.email = '" + property.getEmail() + "'";
		}
		if (StringUtils.isNotEmpty(property.getMobile())) {
			condition += " and t.mobile = '" + property.getMobile() + "'";
		}
		if (StringUtils.isNotEmpty(property.getPassword())) {
			condition += " and t.password = '" + Encrypt.e(property.getPassword()) + "'";
		}
		try {
			List<AcProperty> list = propertyDao.find(MessageFormat.format(HQL_FIND, condition));
			if(list.size()>0){
				AcProperty t = list.get(0);
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AcProperty loadProperty(Long id) {
		return propertyDao.get(AcProperty.class, id);
	}

	@Override
	public void saveProperty(AcProperty property) {
		AcProperty p = new AcProperty();
		BeanUtils.copyProperties(property, p, new String[] { "password" });
		if (property.getCreateTime() == null) {
			p.setCreateTime(new Date());
		}
		p.setPassword(Encrypt.e(property.getPassword()));
		propertyDao.save(p);
	}

	@Override
	public void updateProperty(AcProperty property) {
		propertyDao.update(property);
	}

	@Override
	public void deleteProperty(Long id) {
		AcProperty p = propertyDao.get(AcProperty.class, id);
		if (p != null) {
			propertyDao.delete(p);
		}
	}

	@Override
	public List<AcProperty> findProperty(String condition) {
		String hql = "from AcProperty where 1=1" + condition;
		return propertyDao.find(hql);
	}

	//商户部分
	@Override
	public AcMerchant loginMerchant(AcMerchant merchant) {
		final String HQL_FIND = " from AcMerchant t where 1=1 {0} ";
		String condition = "";
		if (StringUtils.isNotEmpty(merchant.getEmail())) {
			condition += " and t.email = '" + merchant.getEmail() + "'";
		}
		if (StringUtils.isNotEmpty(merchant.getMobile())) {
			condition += " and t.mobile = '" + merchant.getMobile() + "'";
		}
		if (StringUtils.isNotEmpty(merchant.getPassword())) {
			condition += " and t.password = '" + Encrypt.e(merchant.getPassword()) + "'";
		}
		try {
			List<AcMerchant> list = merchantDao.find(MessageFormat.format(HQL_FIND, condition));
			if(list.size()>0){
				AcMerchant t = list.get(0);
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AcMerchant loadMerchant(Long id) {
		return merchantDao.get(AcMerchant.class, id);
	}

	@Override
	public void saveMerchant(AcMerchant merchant) {
		AcMerchant m = new AcMerchant();
		BeanUtils.copyProperties(merchant, m, new String[] { "password" });
		if (merchant.getCreateTime() == null) {
			m.setCreateTime(new Date());
		}
		
		m.setPassword(Encrypt.e(merchant.getPassword()));
		merchantDao.save(m);
	}

	@Override
	public void updateMerchant(AcMerchant merchant) {
		merchantDao.update(merchant);
	}

	@Override
	public void deleteMerchant(Long id) {
		AcMerchant m = merchantDao.get(AcMerchant.class, id);
		if (m != null) {
			merchantDao.delete(m);
		}
	}

	@Override
	public List<AcMerchant> findMerchant(String condition) {
		String hql = "from AcMerchant where 1=1" + condition;
		return merchantDao.find(hql);
	}

}
