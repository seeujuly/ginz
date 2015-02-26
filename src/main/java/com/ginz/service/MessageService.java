package com.ginz.service;

import java.util.List;

import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;

public interface MessageService {

	// 消息部分
	public MsgMessageInfo loadMessageInfo(Long id);

	public MsgMessageInfo saveMessageInfo(MsgMessageInfo messageInfo);

	public void deleteMessageInfo(Long id);

	public List<MsgMessageInfo> listMessageInfo(String condition);

	// 通知收件箱部分
	public MsgMessageBox loadMessageBox(Long id);

	public MsgMessageBox saveMessageBox(MsgMessageBox messageBox);

	public void deleteMessageBox(Long id);

	public List<MsgMessageBox> listMessageBox(String condition);

	// 发布新的信息，并保存在收件箱
	public void sendMessage(String userId, String targetUserId, String subject,
			String content, Long releaseId, String releaseType,
			String messageType);

}
