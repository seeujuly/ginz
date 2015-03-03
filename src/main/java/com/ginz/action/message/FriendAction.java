package com.ginz.action.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String message = valueMap.get("message");	//添加好友时的验证消息
		
		String nickName = "";
		String tNickName = "";
		String tDeviceToken = "";
		if(StringUtils.equals(userId.substring(0, 1), "u")){
			List<AcUser> list = accountService.findUser(" and userId = '" + userId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				nickName = list.get(0).getNickName();
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			List<AcProperty> list = accountService.findProperty(" and userId = '" + userId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				nickName = list.get(0).getPropertyName();
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			List<AcMerchant> list = accountService.findMerchant(" and userId = '" + userId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				nickName = list.get(0).getMerchantName();
			}
		}
		
		if(StringUtils.equals(tUserId.substring(0, 1), "u")){
			List<AcUser> list = accountService.findUser(" and userId = '" + tUserId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				tNickName = list.get(0).getNickName();
				tDeviceToken = list.get(0).getDeviceToken();
			}
		}else if(StringUtils.equals(tUserId.substring(0, 1), "p")){
			List<AcProperty> list = accountService.findProperty(" and userId = '" + tUserId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				tNickName = list.get(0).getPropertyName();
				tDeviceToken = list.get(0).getDeviceToken();
			}
		}else if(StringUtils.equals(tUserId.substring(0, 1), "m")){
			List<AcMerchant> list = accountService.findMerchant(" and userId = '" + tUserId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				tNickName = list.get(0).getMerchantName();
				tDeviceToken = list.get(0).getDeviceToken();
			}
		}

		if(StringUtils.isNotEmpty(tDeviceToken)){	//DeviceToken在登录时是必定保存的，由此来判断目标用户是否存在，或目标用户账户是否正常
			
			String condition = "";
			condition = "and (userId = '" + userId + "' and friendUserId = '" + tUserId + "') ";
			condition += "OR (userId = '" + tUserId + "' and friendUserId = '" + userId + "')";
			condition += " and state = 1 ";
			List<MsgFriend> list = friendService.listFriends(condition);
			
			if(list.size()>0){
				jsonObject.put("result", "1");
				jsonObject.put("value", "你们已经是好友了!");
			}else{
				MsgFriend friend = new MsgFriend();
				friend.setUserId(userId);
				friend.setNickName(nickName);
				friend.setFriendUserId(tUserId);
				friend.setFriendNickName(tNickName);
				friend.setState("0");	//状态:0.请求未确认;1.好友
				friend.setStartType("1");	//0: 别人添加我为好友 1:我添加别人为好友
				friend.setValidateMessage(message);
				friend.setCreateTime(new Date());
				friend.setFlag(DictionaryUtil.DETELE_FLAG_00);
				friendService.saveFriend(friend);
				
				
				String sValue = nickName + "请求添加您为好友!";
				String cValue = sValue + "附加消息：" + message;
				
				//发送推送消息给目标用户
				if(tDeviceToken!=null&&!tDeviceToken.equals("")){
					PushIOS.pushSingleDevice(sValue,tDeviceToken);
				}
				
				//发送系统消息给目标用户
				messageService.sendMessage(userId, tUserId, sValue, cValue, null, "", DictionaryUtil.MESSAGE_TYPE_FRIEND_REQUEST);
				
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		
		String nickName = "";
		String tDeviceToken = "";
		if(StringUtils.equals(userId.substring(0, 1), "u")){
			List<AcUser> list = accountService.findUser(" and userId = '" + userId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				nickName = list.get(0).getNickName();
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			List<AcProperty> list = accountService.findProperty(" and userId = '" + userId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				nickName = list.get(0).getPropertyName();
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			List<AcMerchant> list = accountService.findMerchant(" and userId = '" + userId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				nickName = list.get(0).getMerchantName();
			}
		}
		
		if(StringUtils.equals(tUserId.substring(0, 1), "u")){
			List<AcUser> list = accountService.findUser(" and userId = '" + tUserId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				tDeviceToken = list.get(0).getDeviceToken();
			}
		}else if(StringUtils.equals(tUserId.substring(0, 1), "p")){
			List<AcProperty> list = accountService.findProperty(" and userId = '" + tUserId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				tDeviceToken = list.get(0).getDeviceToken();
			}
		}else if(StringUtils.equals(tUserId.substring(0, 1), "m")){
			List<AcMerchant> list = accountService.findMerchant(" and userId = '" + tUserId + "' and status = '" + DictionaryUtil.ACCOUNT_STATUS_00 + "' ");
			if(list.size()>0){
				tDeviceToken = list.get(0).getDeviceToken();
			}
		}
		
		if(StringUtils.isNotEmpty(tDeviceToken)){	//DeviceToken在登录时是必定保存的，由此来判断目标用户是否存在，或目标用户账户是否正常
			List<MsgFriend> list = friendService.listFriends(" and userId = '" + tUserId + "' and friendUserId = '" + userId + "' ");
			if(list.size()>0){
				MsgFriend friend = list.get(0);
				if(StringUtils.equals(friend.getState(), "1")){
					jsonObject.put("result", "1");
					jsonObject.put("value", "你们已经是好友了!");
				}else{
					friend.setState("1");
					friendService.updateFriend(friend);
					
					String value = nickName + "接受了您的添加请求并添加您为好友!";
					
					//发送推送消息给目标用户
					if(tDeviceToken!=null&&!tDeviceToken.equals("")){
						PushIOS.pushSingleDevice(value,tDeviceToken);
					}
					
					//发送系统消息给目标用户
					messageService.sendMessage(userId, tUserId, value, value, null, "", DictionaryUtil.MESSAGE_TYPE_FRIEND_PASS);
					
					jsonObject.put("result", "2");
					jsonObject.put("value", "SUCCESS!");
					
				}
			}
			
			String condition = "";
			condition = "and (userId = '" + userId + "' and friendUserId = '" + tUserId + "') ";
			condition += "OR (userId = '" + tUserId + "' and friendUserId = '" + userId + "')";
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		
		String condition = "";
		condition = "and (userId = '" + userId + "' and friendUserId = '" + tUserId + "') ";
		condition += "OR (userId = '" + tUserId + "' and friendUserId = '" + userId + "')";
		List<MsgFriend> list = friendService.listFriends(condition);
		
		if(list.size()>0){
			for(MsgFriend friend : list){
				friendService.deleteFriend(friend.getId());
			}
			jsonObject.put("value", "SUCCESS!");
			out.print(jsonObject.toString());
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		
		String condition = "";
		condition = "and (userId = '" + userId + "' and friendUserId = '" + tUserId + "') ";
		condition += "OR (userId = '" + tUserId + "' and friendUserId = '" + userId + "')";
		List<MsgFriend> list = friendService.listFriends(condition);
		
		if(list.size()>0){
			for(MsgFriend friend : list){
				friendService.deleteFriend(friend.getId());
			}
			jsonObject.put("value", "SUCCESS!");
			out.print(jsonObject.toString());
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
		
		String condition = "";
		condition = "and (userId = '" + userId + "' ";
		condition += "OR friendUserId = '" + userId + "')";
		List<MsgFriend> list = friendService.listFriends(condition);
		
		if(list.size()>0){
			JSONArray userArray = new JSONArray();
			JSONArray propertyArray = new JSONArray();
			JSONArray merchantArray = new JSONArray();
			
			for(MsgFriend friend:list){
				JSONObject json = new JSONObject();
				String fId = "";
				if(StringUtils.equals(userId, friend.getUserId())){
					fId = friend.getFriendUserId();
				}else{
					fId = friend.getUserId();
				}
				
				json.put("userId", fId);
				if(StringUtils.equals(userId.substring(0, 1), "u")){
					AcUser u = accountService.loadUser(fId);
					json.put("name", u.getNickName());
					json.put("userId", u.getUserId());
					json.put("headUrl", u.getHeadPortrait());
					userArray.add(json);
				}else if(StringUtils.equals(userId.substring(0, 1), "p")){
					AcProperty p = accountService.loadProperty(fId);
					json.put("name", p.getPropertyName());
					json.put("userId", p.getUserId());
					json.put("headUrl", p.getPicUrl());
					propertyArray.add(json);
				}else if(StringUtils.equals(userId.substring(0, 1), "m")){
					AcMerchant m = accountService.loadMerchant(fId);
					json.put("name", m.getMerchantName());
					json.put("userId", m.getUserId());
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
	
	//获取好友头像和昵称
	@SuppressWarnings("unchecked")
	public void getFriendInfo() throws IOException{
	
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		String userNames[] = jsonString.split(",");
		
		List<String> userList = new ArrayList<String>();
		List<String> propertyList = new ArrayList<String>();
		List<String> merchantList = new ArrayList<String>();
		
		if(userNames.length>0){
			for(int i=0;i<userNames.length;i++){
				if(StringUtils.equals(userNames[i].substring(0, 1), "u")){		//个人用户
					userList.add(userNames[i]);
				}else if(StringUtils.equals(userNames[i].substring(0, 1), "p")){
					propertyList.add(userNames[i]);
				}else if(StringUtils.equals(userNames[i].substring(0, 1), "m")){
					merchantList.add(userNames[i]);
				}
			}
			
			JSONArray userArray = new JSONArray();
			JSONArray propertyArray = new JSONArray();
			JSONArray merchantArray = new JSONArray();
			
			if(userList.size()>0){
				for(int i=0;i<userList.size();i++){
					JSONObject json = new JSONObject();
					AcUser user = accountService.loadUser(userList.get(i));
					if(user!=null){
						json.put("name", user.getNickName());
						json.put("userId", user.getUserId());
						json.put("headUrl", user.getHeadPortrait());
						userArray.add(json);
					}
				}
			}
			
			if(propertyList.size()>0){
				for(int i=0;i<propertyList.size();i++){
					JSONObject json = new JSONObject();
					AcProperty property = accountService.loadProperty(propertyList.get(i));
					if(property!=null){
						json.put("userId", property.getId());
						json.put("name", property.getPropertyName());
						json.put("userId", property.getUserId());
						json.put("headUrl", property.getPicUrl());
						propertyArray.add(json);
					}
				}
			}

			if(merchantList.size()>0){
				for(int i=0;i<merchantList.size();i++){
					JSONObject json = new JSONObject();
					AcMerchant merchant = accountService.loadMerchant(merchantList.get(i));
					if(merchant!=null){
						json.put("userId", merchant.getId());
						json.put("name", merchant.getMerchantName());
						json.put("userId", merchant.getUserId());
						json.put("headUrl", merchant.getPicUrl());
						merchantArray.add(json);
					}
				}
			}	
			
			jsonObject.put("result", "1");
			jsonObject.put("value", "SUCCESS!");
			jsonObject.put("userArray", userArray);		//个人用户
			jsonObject.put("propertyArray", propertyArray);		//社区用户/物业
			jsonObject.put("merchantArray", merchantArray);		//商户
		}
		
		out.print(jsonObject.toString());
		
	}
	
}
