package com.ginz.action.account;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.ginz.service.AccountService;
import com.ginz.service.ActivitiesService;
import com.ginz.service.EventService;
import com.ginz.service.MessageService;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "loginAction")
public class LoginAction extends BaseAction {

	private AccountService accountService;
	private MessageService messageService;
	private EventService eventService;
	private ActivitiesService activitiesService; 

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
	
	public EventService getEventService() {
		return eventService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public ActivitiesService getActivitiesService() {
		return activitiesService;
	}

	@Autowired
	public void setActivitiesService(ActivitiesService activitiesService) {
		this.activitiesService = activitiesService;
	}

	//个人用户登录
	@SuppressWarnings("unchecked")
	public void login() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String mobile = valueMap.get("mobile");
		String email = valueMap.get("email");
		String password = valueMap.get("password");
		
		JSONObject jsonObject=new JSONObject();
		String result = "";
		String headUrl = "";	//头像地址
		String name = "";	//用户名(昵称)
		String userId = "";	//随机生成的用户名,10位数字
		
		String condition = "";
		if(StringUtils.isNotEmpty(mobile)){
			condition += " and mobile = '" + mobile + "' ";
		}else if(StringUtils.isNotEmpty(email)){
			condition += " and email = '" + email + "' ";
		}
		List<AcUser> uList = accountService.findUser(condition);
		List<AcProperty> pList = accountService.findProperty(condition);
		List<AcMerchant> mList = accountService.findMerchant(condition);
		
		if(uList.size()>0){		//个人用户
			AcUser user = new AcUser();
			user.setMobile(mobile);
			user.setPassword(password);
			AcUser u = accountService.loginUser(user);
	    	if(u != null){
	    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, u.getStatus())){
	    			result = "1";
	    			name = u.getNickName();
	    			userId = u.getUserId();
	    			headUrl = u.getThumbnailUrl();
	    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, u.getStatus())){
	    			result = "2";
	    		}
	    	}else{
	    		result = "3";
	    	}
		}else if(pList.size()>0){		//社区用户
			AcProperty property = new AcProperty();
			property.setEmail(email);
			property.setPassword(password);
			AcProperty p = accountService.loginProperty(property);
	    	if(p != null){
	    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, p.getStatus())){
	    			result = "1";
	    			name = p.getPropertyName();
	    			userId = p.getUserId();
	    			headUrl = p.getThumbnailUrl();
	    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, p.getStatus())){
	    			result = "2";
	    		}
	    	}else{
	    		result = "3";
	    	}
		}else if(mList.size()>0){	//商户
			AcMerchant merchant = new AcMerchant();
			merchant.setEmail(email);
			merchant.setPassword(password);
			AcMerchant m = accountService.loginMerchant(merchant);
	    	if(m != null){
	    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, m.getStatus())){
	    			result = "1";
	    			name = m.getMerchantName();
	    			userId = m.getUserId();
	    			headUrl = m.getThumbnailUrl();
	    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, m.getStatus())){
	    			result = "2";
	    		}
	    	}else{
	    		result = "3";
	    	}
		}else{
			result = "4";
		}
		
		if(StringUtils.equals("1", result)){
			jsonObject.put("result", "1");
	    	jsonObject.put("value", "欢迎回来!");
	    	jsonObject.put("name", name);
	    	jsonObject.put("userId", userId);
	    	jsonObject.put("headUrl", headUrl);
	    	
	    	//发送系统消息给目标用户
	    	/*String value = name + ",welcome to ginz!";
			messageService.sendMessage("", userId, value, value, null, "", DictionaryUtil.MESSAGE_TYPE_SYS);*/
	    	
		}else if(StringUtils.equals("2", result)){
			jsonObject.put("result", "2");
	    	jsonObject.put("value", "账户异常");
		}else if(StringUtils.equals("3", result)){
			jsonObject.put("result", "3");
	    	jsonObject.put("value", "密码错误");
		}else if(StringUtils.equals("4", result)){
			jsonObject.put("result", "4");
			jsonObject.put("value", "账户不存在!");
		}
		
		out.print(jsonObject.toString());
	}
	
	//保存设备token和别名
	@SuppressWarnings("unchecked")
	public void saveDeviceInfo() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String deviceToken = valueMap.get("deviceToken");
		String deviceAccount = valueMap.get("deviceAccount");
		
		try {
			if(StringUtils.isNotEmpty(deviceToken)&&StringUtils.isNotEmpty(deviceAccount)){
				//保存之前清除原有deviceToken信息记录
				deleteDeviceInfo(deviceToken);
				
				if(StringUtils.equals(userId.substring(0, 1), "u")){
					AcUser user = accountService.loadUser(userId);
					user.setDeviceAccount(deviceAccount);
					user.setDeviceToken(deviceToken);
					accountService.updateUser(user);
				}else if(StringUtils.equals(userId.substring(0, 1), "p")){
					AcProperty property = accountService.loadProperty(userId);
					property.setDeviceAccount(deviceAccount);
					property.setDeviceToken(deviceToken);
					accountService.updateProperty(property);
				}else if(StringUtils.equals(userId.substring(0, 1), "m")){
					AcMerchant merchant = accountService.loadMerchant(userId);
					merchant.setDeviceAccount(deviceAccount);
					merchant.setDeviceToken(deviceToken);
					accountService.updateMerchant(merchant);
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	public void deleteDeviceInfo(String deviceToken){
		
		if(StringUtils.isNotEmpty(deviceToken)){
			String condition = " and deviceToken = '" + deviceToken + "' ";
			List<AcUser> uList = accountService.findUser(condition);
			List<AcProperty> pList = accountService.findProperty(condition);
			List<AcMerchant> mList = accountService.findMerchant(condition);
			if(uList.size()>0){
				for(AcUser user : uList){
					user.setDeviceToken("");
					user.setDeviceAccount("");
					accountService.updateUser(user);
				}
			}else if(pList.size()>0){
				for(AcProperty property : pList){
					property.setDeviceToken("");
					property.setDeviceAccount("");
					accountService.updateProperty(property);
				}
			}else if(mList.size()>0){
				for(AcMerchant merchant : mList){
					merchant.setDeviceToken("");
					merchant.setDeviceAccount("");
					accountService.updateMerchant(merchant);
				}
			}
		}
		
	}

}
