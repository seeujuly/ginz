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
		String deviceToken = valueMap.get("devicetoken");
		
		JSONObject jsonObject=new JSONObject();
		String result = "";
		String accountType = "";
		if(StringUtils.isNotEmpty(mobile)){	//个人用户
			AcUser user = new AcUser();
			user.setMobile(mobile);
			AcUser u = accountService.loginUser(user);
			if (u != null) {
				user.setPassword(password);
				AcUser u2 = accountService.loginUser(user);
		    	if(u2 != null){
		    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, u2.getStatus())){
		    			result = "1";
		    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, u2.getStatus())){
		    			result = "2";
		    		}
		    	}else{
		    		result = "3";
		    	}
			}else{
				result = "4";
			}
			accountType = DictionaryUtil.ACCOUNT_TYPE_01;
		}else if(StringUtils.isNotEmpty(email)){
			List<AcProperty> pList = accountService.findProperty(" and email = '" + email + "' ");
			List<AcMerchant> mList = accountService.findMerchant(" and email = '" + email + "' ");
			
			if(pList.size()>0){	//社区用户
				AcProperty property = new AcProperty();
				property.setEmail(email);
				property.setPassword(password);
				AcProperty p = accountService.loginProperty(property);
		    	if(p != null){
		    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, p.getStatus())){
		    			result = "1";
		    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, p.getStatus())){
		    			result = "2";
		    		}
		    	}else{
		    		result = "3";
		    	}
		    	accountType = DictionaryUtil.ACCOUNT_TYPE_02;
			}else if(mList.size()>0){	//商户
				
				AcMerchant merchant = new AcMerchant();
				merchant.setEmail(email);
				merchant.setPassword(password);
				AcMerchant m = accountService.loginMerchant(merchant);
		    	if(m != null){
		    		if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_00, m.getStatus())){
		    			result = "1";
		    		}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_STATUS_01, m.getStatus())){
		    			result = "2";
		    		}
		    	}else{
		    		result = "3";
		    	}
		    	accountType = DictionaryUtil.ACCOUNT_TYPE_03;
			}else{
				result = "4";
			}
			
		}
		
		if(StringUtils.equals("1", result)){
			jsonObject.put("result", "1");
	    	jsonObject.put("value", "欢迎回来!");
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
		jsonObject.put("accountType", accountType);
		out.print(jsonObject.toString());
	}
	
}
