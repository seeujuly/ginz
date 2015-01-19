package com.ginz.service;

import java.util.HashMap;
import java.util.List;

import com.ginz.model.MsgFriend;

public interface FriendService {

	//好友表操作
	public MsgFriend loadFriend(Long id);

	public MsgFriend saveFriend(MsgFriend friend);
	
	public MsgFriend updateFriend(MsgFriend friend);
	
	public void deleteFriend(Long id);
	
	public List<MsgFriend> listFriends(String condition);
	
	//查询好友列表
	public HashMap<String, Object> listFriends(Long userId, String accountType);
	
}
