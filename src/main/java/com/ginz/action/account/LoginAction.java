package com.ginz.action.account;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "loginAction")
public class LoginAction extends BaseAction {

	private AccountService accountService;

	public AccountService getAccountService() {
		return accountService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	//个人用户登录
	@SuppressWarnings("unchecked")
	public void loginUser() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String mobile = valueMap.get("mobile");
		String password = valueMap.get("password");
		
		JSONObject jsonObject=new JSONObject();
		
		AcUser user = new AcUser();
		user.setMobile(mobile);
		AcUser u = accountService.loginUser(user);
		if (u != null) {
			user.setPassword(password);
			AcUser u2 = accountService.loginUser(user);
	    	if(u2 != null){
	    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, u2.getStatus())){
	    			jsonObject.put("result", "1");
			    	jsonObject.put("value", "欢迎回来!");
	    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, u2.getStatus())){
	    			jsonObject.put("result", "2");
			    	jsonObject.put("value", "账户异常");
	    		}
	    	}else{
	    		jsonObject.put("result", "3");
		    	jsonObject.put("value", "密码错误");
	    	}
		}else{
			jsonObject.put("result", "4");
			jsonObject.put("value", "账户不存在!");
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//社区用户登录
	@SuppressWarnings("unchecked")
	public void loginProperty() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String email = valueMap.get("email");
		String password = valueMap.get("password");
		
		JSONObject jsonObject=new JSONObject();
		
		AcProperty property = new AcProperty();
		property.setEmail(email);
		AcProperty p = accountService.loginProperty(property);
		if (p != null) {
			property.setPassword(password);
			AcProperty p2 = accountService.loginProperty(property);
	    	if(p2 != null){
	    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, p2.getStatus())){
	    			jsonObject.put("result", "1");
			    	jsonObject.put("value", "欢迎回来!");
	    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, p2.getStatus())){
	    			jsonObject.put("result", "2");
			    	jsonObject.put("value", "账户异常");
	    		}
	    	}else{
	    		jsonObject.put("result", "3");
		    	jsonObject.put("value", "密码错误");
	    	}
		}else{
			jsonObject.put("result", "4");
			jsonObject.put("value", "账户不存在!");
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//商户登录
	@SuppressWarnings("unchecked")
	public void loginMerchant() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String email = valueMap.get("email");
		String password = valueMap.get("password");
		
		JSONObject jsonObject=new JSONObject();
		
		AcMerchant merchant = new AcMerchant();
		merchant.setEmail(email);
		AcMerchant m = accountService.loginMerchant(merchant);
		if (m != null) {
			merchant.setPassword(password);
			AcMerchant m2 = accountService.loginMerchant(merchant);
	    	if(m2 != null){
	    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, m2.getStatus())){
	    			jsonObject.put("result", "1");
			    	jsonObject.put("value", "欢迎回来!");
	    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, m2.getStatus())){
	    			jsonObject.put("result", "2");
			    	jsonObject.put("value", "账户异常");
	    		}
	    	}else{
	    		jsonObject.put("result", "3");
		    	jsonObject.put("value", "密码错误");
	    	}
		}else{
			jsonObject.put("result", "4");
			jsonObject.put("value", "账户不存在!");
		}
		
		out.print(jsonObject.toString());
		
	}
	
}
