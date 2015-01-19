package com.ginz.service;

import java.util.List;

import com.ginz.model.TransactionRecords;

public interface PointService {

	//交易记录
	public TransactionRecords loadTransactionRecords(Long id);

	public TransactionRecords saveTransactionRecords(TransactionRecords record);
	
	public void deleteTransactionRecords(Long id);
	
	public List<TransactionRecords> listTransactionRecords(String condition);
	
}
