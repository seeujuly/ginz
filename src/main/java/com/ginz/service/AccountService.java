package com.ginz.service;

import java.util.List;

import com.ginz.model.AcMerchant;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.AcUserDetail;

public interface AccountService {

	//个人用户部分
	public AcUser loginUser(AcUser user);
	
	public AcUser loadUser(Long id);

	public AcUser saveUser(AcUser user);
	
	public AcUser updateUser(AcUser user);
	
	public void deleteUser(Long id);
	
	public List<AcUser> findUser(String condition);
	
	//个人用户明细部分
	public AcUserDetail loadUserDetail(Long id);

	public AcUserDetail saveUserDetail(AcUserDetail userDetail);
	
	public AcUserDetail updateUserDetail(AcUserDetail userDetail);
	
	public void deleteUserDetail(Long id);
	
	public List<AcUserDetail> findUserDetail(String condition);
	
	//社区用户部分
	public AcProperty loginProperty(AcProperty property);
	
	public AcProperty loadProperty(Long id);

	public AcProperty saveProperty(AcProperty property);
	
	public AcProperty updateProperty(AcProperty property);
	
	public void deleteProperty(Long id);
	
	public List<AcProperty> findProperty(String condition);
	
	//商户部分
	public AcMerchant loginMerchant(AcMerchant merchant);
	
	public AcMerchant loadMerchant(Long id);

	public AcMerchant saveMerchant(AcMerchant merchant);
	
	public AcMerchant updateMerchant(AcMerchant merchant);
	
	public void deleteMerchant(Long id);
	
	public List<AcMerchant> findMerchant(String condition);
	
}
