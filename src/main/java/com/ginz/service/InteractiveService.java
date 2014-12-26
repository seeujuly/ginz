package com.ginz.service;

import java.util.List;

import com.ginz.model.PubInteractive;

public interface InteractiveService {

	public PubInteractive loadInteractive(Long id);

	public PubInteractive saveInteractive(PubInteractive interactive);
	
	public PubInteractive updateInteractive(PubInteractive interactive);
	
	public void deleteInteractive(Long id);
	
	public List<PubInteractive> findInteractive(String condition);
	
	public List<PubInteractive> findInteractive(String condition, int page, int rows);
	
}
