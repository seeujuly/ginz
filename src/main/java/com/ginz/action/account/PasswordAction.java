package com.ginz.action.account;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcUser;
import com.ginz.service.AccountService;
import com.ginz.util.base.Encrypt;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "passwordAction")
public class PasswordAction extends BaseAction {

	private AccountService accountService;

	public AccountService getAccountService() {
		return accountService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	//个人用户修改密码
	@SuppressWarnings("unchecked")
	public void changePassword() throws IOException{
		
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
		
		List<AcUser> list = accountService.findUser(" and mobile = '" + mobile + "' ");
		if(list.size()>0){
			AcUser user = list.get(0);
			user.setPassword(Encrypt.e(password));
			accountService.updateUser(user);
			
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("value", "SUCCESS!");
			out.print(jsonObject.toString());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void checkMobile() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String mobile = valueMap.get("mobile");
		
		List<AcUser> list = accountService.findUser(" and mobile = '" + mobile + "' ");
		if(list.size()>0){
			//AcUser user = list.get(0);
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("value", "true");
			out.print(jsonObject.toString());
		}else{
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("value", "false");
			out.print(jsonObject.toString());
		}
		
	}
	
}
