package com.ginz.service;

import java.util.List;

import com.ginz.model.PubPersonalStatus;

public interface PersonalStatusService {

	//个人动态信息
	public PubPersonalStatus loadPersonalStatus(Long id);

	public void savePersonalStatus(PubPersonalStatus personalStatus);
	
	public void deletePersonalStatus(Long id);
	
	public List<PubPersonalStatus> findPersonalStatus(String condition);
	
	public List<PubPersonalStatus> findPersonalStatus(String condition, int page, int rows);
	
}
