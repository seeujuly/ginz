package com.ginz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;
import com.ginz.service.MessageService;

@Service("messageService")
public class MessageServiceImpl implements MessageService {

	private BaseDao<MsgMessageInfo> messageInfoDao;
	private BaseDao<MsgMessageBox> messageBoxDao;
	
	public BaseDao<MsgMessageInfo> getMessageInfoDao() {
		return messageInfoDao;
	}

	@Autowired
	public void setMessageInfoDao(BaseDao<MsgMessageInfo> messageInfoDao) {
		this.messageInfoDao = messageInfoDao;
	}

	public BaseDao<MsgMessageBox> getMessageBoxDao() {
		return messageBoxDao;
	}

	@Autowired
	public void setMessageBoxDao(BaseDao<MsgMessageBox> messageBoxDao) {
		this.messageBoxDao = messageBoxDao;
	}

	//消息部分
	@Override
	public MsgMessageInfo loadMessageInfo(Long id) {
		return messageInfoDao.get(MsgMessageInfo.class, id);
	}

	@Override
	public MsgMessageInfo saveMessageInfo(MsgMessageInfo messageInfo) {
		return messageInfoDao.save(messageInfo);
	}

	@Override
	public void deleteMessageInfo(Long id) {
		MsgMessageInfo m = messageInfoDao.get(MsgMessageInfo.class, id);
		if (m != null) {
			messageInfoDao.delete(m);
		}
	}

	@Override
	public List<MsgMessageInfo> listMessageInfo(String condition) {
		String hql = "from MsgMessageInfo where 1=1" + condition;
		return messageInfoDao.find(hql);
	}
	
	//通知收件箱部分
	@Override
	public MsgMessageBox loadMessageBox(Long id) {
		return messageBoxDao.get(MsgMessageBox.class, id);
	}

	@Override
	public MsgMessageBox saveMessageBox(MsgMessageBox messageBox) {
		return messageBoxDao.save(messageBox);
	}

	@Override
	public void deleteMessageBox(Long id) {
		MsgMessageBox m = messageBoxDao.get(MsgMessageBox.class, id);
		if (m != null) {
			messageBoxDao.delete(m);
		}
	}

	@Override
	public List<MsgMessageBox> listMessageBox(String condition) {
		String hql = "from MsgMessageBox where 1=1" + condition;
		return messageBoxDao.find(hql);
	}

}
