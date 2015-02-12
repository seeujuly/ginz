package com.ginz.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;
import com.ginz.service.MessageService;
import com.ginz.util.base.DictionaryUtil;

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
	
	//发布新的信息，并保存在收件箱
	public void sendMessage(Long userId, String accountType, Long targetUserId,
			String targetAccountType, String subject, String content,
			Long releaseId, String releaseType, String messageType){
		
		Date nowDate = new Date();
		
		MsgMessageInfo messageInfo = new MsgMessageInfo();
		messageInfo.setUserId(userId);
		messageInfo.setAccountType(accountType);
		messageInfo.setTargetUserId(targetUserId);
		messageInfo.setTargetAccountType(targetAccountType);
		messageInfo.setCreateTime(nowDate);
		messageInfo.setSubject(subject);
		messageInfo.setContent(content);
		messageInfo.setReleaseId(releaseId);
		messageInfo.setReleaseType(releaseType);
		messageInfo.setMessageType(messageType);
		messageInfo.setFlag(DictionaryUtil.DETELE_FLAG_00);
		MsgMessageInfo messageInfo2 = messageInfoDao.save(messageInfo);
		
		MsgMessageBox messageBox = new MsgMessageBox();
		messageBox.setMessageId(messageInfo2.getId());
		messageBox.setUserId(targetUserId);
		messageBox.setAccountType(targetAccountType);
		messageBox.setReceiveDate(nowDate);
		messageBox.setReadFlag(DictionaryUtil.MESSAGE_UNREAD);
		messageBox.setFlag(DictionaryUtil.DETELE_FLAG_00);
		messageBoxDao.save(messageBox);
		
	}

}
