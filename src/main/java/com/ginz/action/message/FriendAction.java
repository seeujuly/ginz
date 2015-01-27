package com.ginz.action.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcMerchant;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.MsgFriend;
import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;
import com.ginz.service.AccountService;
import com.ginz.service.FriendService;
import com.ginz.service.MessageService;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.push.PushIOS;

@Namespace("/")
@Action(value = "friendAction")
public class FriendAction extends BaseAction {

	private FriendService friendService;
	private AccountService accountService;
	private MessageService messageService;
	
	public FriendService getFriendService() {
		return friendService;
	}

	@Autowired
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}
	
	public AccountService getAccountService() {
		return accountService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	public MessageService getMessageService() {
		return messageService;
	}

	@Autowired
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	//添加好友-向他人发起添加好友申请
	@SuppressWarnings("unchecked")
	public void requestFriend() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String tAccountType = valueMap.get("tAccountType");	//目标账户类型
		String message = valueMap.get("message");	//添加好友时的验证消息
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		AcUser tUser = accountService.loadUser(Long.parseLong(tUserId));
		
		if(user!=null&&tUser!=null){
			
			String condition = "";
			condition = "and (userId = " + userId + " and accountType = '" + accountType + "' and friendUserId = " + tUserId + " AND friendAccountType = '" + tAccountType + "') ";
			condition += "OR (userId = " + tUserId + " and accountType = '" + tAccountType + "' and friendUserId = " + userId + " AND friendAccountType = '" + accountType + "')";
			condition += " and state = 1 ";
			List<MsgFriend> list = friendService.listFriends(condition);
			
			if(list.size()>0){
				jsonObject.put("result", "1");
				jsonObject.put("value", "你们已经是好友了!");
			}else{
				MsgFriend friend = new MsgFriend();
				friend.setUserId(Long.parseLong(userId));
				friend.setAccountType(accountType);
				friend.setUserName(user.getNickName());
				friend.setFriendUserId(Long.parseLong(tUserId));
				friend.setFriendAccountType(tAccountType);
				friend.setFriendUserName(tUser.getNickName());
				friend.setState("0");	//状态:0.请求未确认;1.好友
				friend.setStartType("1");	//0: 别人添加我为好友 1:我添加别人为好友
				friend.setValidateMessage(message);
				friend.setCreateTime(new Date());
				friend.setFlag(DictionaryUtil.DETELE_FLAG_00);
				friendService.saveFriend(friend);
				
				//发送推送消息给目标用户
				if(tUser.getDeviceToken()!=null&&!tUser.getDeviceToken().equals("")){
					PushIOS.pushSingleDevice(user.getNickName() + "请求添加您为好友!",tUser.getDeviceToken());
				}
				
				//发送系统消息给目标用户
				MsgMessageInfo messageInfo = new MsgMessageInfo();
				messageInfo.setUserId(Long.parseLong(userId));
				messageInfo.setAccountType(accountType);
				messageInfo.setTargetUserId(Long.parseLong(tUserId));
				messageInfo.setTargetAccountType(tAccountType);
				messageInfo.setCreateTime(new Date());
				messageInfo.setSubject(user.getNickName() + "请求添加您为好友!");
				messageInfo.setContent(user.getNickName() + "请求添加您为好友!附加消息：" + message);
				messageInfo.setMessageType(DictionaryUtil.MESSAGE_TYPE_FRIEND_REQUEST);
				messageInfo.setFlag(DictionaryUtil.DETELE_FLAG_00);
				MsgMessageInfo messageInfo2 = messageService.saveMessageInfo(messageInfo);
				
				MsgMessageBox messageBox = new MsgMessageBox();
				messageBox.setMessageId(messageInfo2.getId());
				messageBox.setUserId(Long.parseLong(tUserId));
				messageBox.setAccountType(tAccountType);
				messageBox.setReceiveDate(new Date());
				messageBox.setReadFlag(DictionaryUtil.MESSAGE_UNREAD);
				messageBox.setFlag(DictionaryUtil.DETELE_FLAG_00);
				messageService.saveMessageBox(messageBox);
				
				jsonObject.put("result", "2");
				jsonObject.put("value", "SUCCESS!");
				
			}
		}
		out.print(jsonObject.toString());
	}
	
	//添加好友-同意他人的添加好友申请
	@SuppressWarnings("unchecked")
	public void addFriend() throws IOException{
	
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String tAccountType = valueMap.get("tAccountType");	//目标账户类型
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		AcUser tUser = accountService.loadUser(Long.parseLong(tUserId));
		
		if(user!=null&&tUser!=null){
			List<MsgFriend> list = friendService.listFriends(" and userId = " + tUserId + " and accountType = '" + tAccountType + "' and friendUserId = " + userId + " AND friendAccountType = '" + accountType + "' ");
			if(list.size()>0){
				MsgFriend friend = list.get(0);
				if(StringUtils.equals(friend.getState(), "1")){
					jsonObject.put("result", "1");
					jsonObject.put("value", "你们已经是好友了!");
				}else{
					friend.setState("1");
					friendService.updateFriend(friend);
					
					//发送推送消息给目标用户
					if(tUser.getDeviceToken()!=null&&!tUser.getDeviceToken().equals("")){
						PushIOS.pushSingleDevice(user.getNickName() + "接受了您的添加请求并添加您为好友!",tUser.getDeviceToken());
					}
					
					//发送系统消息给目标用户
					MsgMessageInfo messageInfo = new MsgMessageInfo();
					messageInfo.setUserId(Long.parseLong(userId));
					messageInfo.setAccountType(accountType);
					messageInfo.setTargetUserId(Long.parseLong(tUserId));
					messageInfo.setTargetAccountType(tAccountType);
					messageInfo.setCreateTime(new Date());
					messageInfo.setSubject(user.getNickName() + "接受了您的添加请求并添加您为好友!");
					messageInfo.setContent(user.getNickName() + "接受了您的添加请求并添加您为好友!");
					messageInfo.setMessageType(DictionaryUtil.MESSAGE_TYPE_FRIEND_PASS);
					messageInfo.setFlag(DictionaryUtil.DETELE_FLAG_00);
					MsgMessageInfo messageInfo2 = messageService.saveMessageInfo(messageInfo);
					
					MsgMessageBox messageBox = new MsgMessageBox();
					messageBox.setMessageId(messageInfo2.getId());
					messageBox.setUserId(Long.parseLong(tUserId));
					messageBox.setAccountType(tAccountType);
					messageBox.setReceiveDate(new Date());
					messageBox.setReadFlag(DictionaryUtil.MESSAGE_UNREAD);
					messageBox.setFlag(DictionaryUtil.DETELE_FLAG_00);
					messageService.saveMessageBox(messageBox);
					
					jsonObject.put("result", "2");
					jsonObject.put("value", "SUCCESS!");
					
				}
			}
			
			String condition = "";
			condition = "and (userId = " + userId + " and accountType = '" + accountType + "' and friendUserId = " + tUserId + " AND friendAccountType = '" + tAccountType + "') ";
			condition += "OR (userId = " + tUserId + " and accountType = '" + tAccountType + "' and friendUserId = " + userId + " AND friendAccountType = '" + accountType + "')";
			condition += " and state = 0 ";
			List<MsgFriend> listFriends = friendService.listFriends(condition);
			if(list.size()>0){
				for(MsgFriend friend : listFriends){
					friendService.deleteFriend(friend.getId());
				}
			}
			
		}
		out.print(jsonObject.toString());
		
	}
	
	//拒绝他人的添加好友申请
	@SuppressWarnings("unchecked")
	public void refuseFriend() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String tAccountType = valueMap.get("tAccountType");	//目标账户类型
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		AcUser tUser = accountService.loadUser(Long.parseLong(tUserId));
		
		if(user!=null&&tUser!=null){
			
			String condition = "";
			condition = "and (userId = " + userId + " and accountType = '" + accountType + "' and friendUserId = " + tUserId + " AND friendAccountType = '" + tAccountType + "') ";
			condition += "OR (userId = " + tUserId + " and accountType = '" + tAccountType + "' and friendUserId = " + userId + " AND friendAccountType = '" + accountType + "')";
			List<MsgFriend> list = friendService.listFriends(condition);
			
			if(list.size()>0){
				for(MsgFriend friend : list){
					friendService.deleteFriend(friend.getId());
				}
				jsonObject.put("value", "SUCCESS!");
				out.print(jsonObject.toString());
			}
		}
		
	}
	
	//删除好友
	@SuppressWarnings("unchecked")
	public void deleteFriend() throws IOException{
	
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String tAccountType = valueMap.get("tAccountType");	//目标账户类型
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		AcUser tUser = accountService.loadUser(Long.parseLong(tUserId));
		
		if(user!=null&&tUser!=null){
			
			String condition = "";
			condition = "and (userId = " + userId + " and accountType = '" + accountType + "' and friendUserId = " + tUserId + " AND friendAccountType = '" + tAccountType + "') ";
			condition += "OR (userId = " + tUserId + " and accountType = '" + tAccountType + "' and friendUserId = " + userId + " AND friendAccountType = '" + accountType + "')";
			List<MsgFriend> list = friendService.listFriends(condition);
			
			if(list.size()>0){
				for(MsgFriend friend : list){
					friendService.deleteFriend(friend.getId());
				}
				jsonObject.put("value", "SUCCESS!");
				out.print(jsonObject.toString());
			}
		}
		
	}
	
	//显示好友列表
	@SuppressWarnings("unchecked")
	public void listFriends() throws IOException{
	
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		
		if(user!=null){
			
			String condition = "";
			condition = "and (userId = " + userId + " and accountType = '" + accountType + "') ";
			condition += "OR (friendUserId = " + userId + " AND friendAccountType = '" + accountType + "')";
			List<MsgFriend> list = friendService.listFriends(condition);
			
			if(list.size()>0){
				JSONArray userArray = new JSONArray();
				JSONArray propertyArray = new JSONArray();
				JSONArray merchantArray = new JSONArray();
				
				for(MsgFriend friend:list){
					JSONObject json = new JSONObject();
					Long fId = 0L;
					String fAccountType = "";
					if(StringUtils.equals(userId, friend.getUserId().toString())){
						fId = friend.getFriendUserId();
						fAccountType = friend.getFriendAccountType();
					}else{
						fId = friend.getUserId();
						fAccountType = friend.getAccountType();
					}
					
					json.put("userId", fId);
					json.put("accountType", fAccountType);
					if(StringUtils.equals(fAccountType, DictionaryUtil.ACCOUNT_TYPE_01)){
						AcUser u = accountService.loadUser(fId);
						json.put("name", u.getNickName());
						json.put("headUrl", u.getHeadPortrait());
						userArray.add(json);
					}else if(StringUtils.equals(fAccountType, DictionaryUtil.ACCOUNT_TYPE_02)){
						AcProperty p = accountService.loadProperty(fId);
						json.put("name", p.getPropertyName());
						json.put("headUrl", p.getPicUrl());
						propertyArray.add(json);
					}else if(StringUtils.equals(fAccountType, DictionaryUtil.ACCOUNT_TYPE_03)){
						AcMerchant m = accountService.loadMerchant(fId);
						json.put("name", m.getMerchantName());
						json.put("headUrl", m.getPicUrl());
						merchantArray.add(json);
					}
				}
				
				jsonObject.put("result", "1");
				jsonObject.put("value", "SUCCESS!");
				jsonObject.put("userArray", userArray);		//个人用户
				jsonObject.put("propertyArray", propertyArray);		//社区用户/物业
				jsonObject.put("merchantArray", merchantArray);		//商户
			}else{
				jsonObject.put("result", "2");
				jsonObject.put("value", "您还未添加任何好友!");
			}
			
			out.print(jsonObject.toString());
		}
		
	}
	
}
