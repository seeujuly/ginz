package com.ginz.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginz.dao.BaseDao;
import com.ginz.model.MsgFriend;
import com.ginz.service.FriendService;

@Service("friendService")
public class FriendServiceImpl implements FriendService {

	private BaseDao<MsgFriend> friendDao;
	
	public BaseDao<MsgFriend> getFriendDao() {
		return friendDao;
	}

	@Autowired
	public void setFriendDao(BaseDao<MsgFriend> friendDao) {
		this.friendDao = friendDao;
	}

	@Override
	public MsgFriend loadFriend(Long id) {
		return friendDao.get(MsgFriend.class, id);
	}

	@Override
	public MsgFriend saveFriend(MsgFriend friend) {
		return friendDao.save(friend);
	}

	@Override
	public MsgFriend updateFriend(MsgFriend friend) {
		return friendDao.update(friend);
	}

	@Override
	public void deleteFriend(Long id) {
		MsgFriend m = friendDao.get(MsgFriend.class, id);
		if (m != null) {
			friendDao.delete(m);
		}
	}

	@Override
	public List<MsgFriend> listFriends(String condition){
		String hql = "from MsgFriend where 1=1" + condition;
		return friendDao.find(hql);
	}
	
	//查询好友列表
	@Override
	public HashMap<String, Object> getFriendMap(String userId){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.userId as userId,t.friend_nick_name as nickName ");
		sb.append(" from msg_friend t WHERE t.userId = '" + userId + "' ");
		sb.append(" UNION SELECT t.userId as userId,t.nick_name as nickName ");
		sb.append(" from msg_friend t WHERE t.friend_userId = '" + userId + "' ");
		sb.append(" ORDER BY nickName ASC ");	
		
		hm.put("list", friendDao.queryBySql(sb.toString()));
		hm.put("cnt", friendDao.queryBySql(sb.toString()).size());
		return hm;
		
	}
	
}
