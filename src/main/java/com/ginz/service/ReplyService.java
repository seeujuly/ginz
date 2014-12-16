package com.ginz.service;

import java.util.List;

import com.ginz.model.PubComments;
import com.ginz.model.PubPraise;

public interface ReplyService {

	//评论
	public PubComments loadComments(Long id);

	public void saveComments(PubComments comments);
	
	public void updateComments(PubComments comments);
	
	public void deleteComments(Long id);
	
	public List<PubComments> findComments(String condition);
	
	public List<PubComments> findComments(String condition, int page, int rows);
	
	//点赞
	public PubPraise loadPraise(Long id);

	public void savePraise(PubPraise praise);
	
	public void updatePraise(PubPraise praise);
	
	public void deletePraise(Long id);
	
	public List<PubPraise> findPraise(String condition);
	
	public List<PubPraise> findPraise(String condition, int page, int rows);
	
}
