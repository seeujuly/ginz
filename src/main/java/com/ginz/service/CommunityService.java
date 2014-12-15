package com.ginz.service;

import java.util.List;

import com.ginz.model.Community;

public interface CommunityService {

	public Community load(Long id);

	public void save(Community community);
	
	public void update(Community community);
	
	public void delete(Long id);
	
	public List<Community> find(String condition);
		
}
