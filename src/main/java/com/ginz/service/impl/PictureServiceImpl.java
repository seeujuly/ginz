package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.Picture;
import com.ginz.service.PictureService;

@Service("pictureService")
public class PictureServiceImpl implements PictureService {

	private BaseDao<Picture> pictureDao;
	
	public BaseDao<Picture> getPictureDao() {
		return pictureDao;
	}

	@Autowired
	public void setPictureDao(BaseDao<Picture> pictureDao) {
		this.pictureDao = pictureDao;
	}

	@Override
	public Picture loadPicture(Long id) {
		return pictureDao.get(Picture.class, id);
	}

	@Override
	public Picture savePicture(Picture picture) {
		return pictureDao.save(picture);
	}

	@Override
	public Picture updatePicture(Picture picture) {
		return pictureDao.update(picture);
	}

	@Override
	public void deletePicture(Long id) {
		Picture p = pictureDao.get(Picture.class, id);
		if (p != null) {
			pictureDao.delete(p);
		}
	}

	@Override
	public List<Picture> findPicture(String condition) {
		String hql = "from Picture where 1=1" + condition;
		return pictureDao.find(hql);
	}

}
