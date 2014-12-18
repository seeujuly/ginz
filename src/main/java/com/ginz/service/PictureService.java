package com.ginz.service;

import java.util.List;

import com.ginz.model.Picture;

public interface PictureService {

	public Picture loadPicture(Long id);

	public void savePicture(Picture picture);
	
	public void updatePicture(Picture picture);
	
	public void deletePicture(Long id);
	
	public List<Picture> findPicture(String condition);
	
}
