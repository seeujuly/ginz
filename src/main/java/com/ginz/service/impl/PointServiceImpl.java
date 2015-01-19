package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.TransactionRecords;
import com.ginz.service.PointService;

@Service("pointService")
public class PointServiceImpl implements PointService {

	private BaseDao<TransactionRecords> transactionRecordsDao;
	
	public BaseDao<TransactionRecords> getTransactionRecordsDao() {
		return transactionRecordsDao;
	}

	@Autowired
	public void setTransactionRecordsDao(
			BaseDao<TransactionRecords> transactionRecordsDao) {
		this.transactionRecordsDao = transactionRecordsDao;
	}

	//交易记录
	@Override
	public TransactionRecords loadTransactionRecords(Long id) {
		return transactionRecordsDao.get(TransactionRecords.class, id);
	}

	@Override
	public TransactionRecords saveTransactionRecords(
			TransactionRecords record) {
		return transactionRecordsDao.save(record);
	}

	@Override
	public void deleteTransactionRecords(Long id) {
		TransactionRecords t = transactionRecordsDao.get(TransactionRecords.class, id);
		if (t != null) {
			transactionRecordsDao.delete(t);
		}
	}

	@Override
	public List<TransactionRecords> listTransactionRecords(String condition) {
		String hql = "from TransactionRecords where 1=1" + condition;
		return transactionRecordsDao.find(hql);
	}

}
