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
import com.ginz.util.base.RandomUtil;

@Namespace("/")
@Action(value = "registerAction")
public class RegisterAction extends BaseAction {

	private AccountService accountService;

	public AccountService getAccountService() {
		return accountService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	//个人用户注册
	@SuppressWarnings("unchecked")
	public void register() throws IOException{
		
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			
			Map<String,String[]> map = request.getParameterMap();
			String a[] = map.get("json");
			String jsonString = a[0];
			Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
			
			String accountType = valueMap.get("accounttype");
			String password = valueMap.get("password");
			
			if(StringUtils.equals(DictionaryUtil.ACCOUNT_TYPE_01, accountType)){	//个人用户注册
				String mobile = valueMap.get("mobile");
				
				AcUser user = new AcUser();
				user.setMobile(mobile);
				user.setPassword(password);
				
				String userName = RandomUtil.digitsRandom();
				List<AcUser> list =  accountService.findUser(" and userName = '" + userName + "'");
				if(list.size()>0){
					userName = RandomUtil.digitsRandom();
				}
				user.setUserName(userName);
				user.setNickName(userName);
				user.setStatus(DictionaryUtil.ACCOUNT_STATUS_00);
				user.setFlag(DictionaryUtil.DETELE_FLAG_00);
				accountService.saveUser(user);
			}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_TYPE_02, accountType)){	//社区用户(物业)注册
				
			}else if(StringUtils.equals(DictionaryUtil.ACCOUNT_TYPE_03, accountType)){	//商户注册
				
			}
			
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("value", "SUCCESS!");
			out.print(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//邮箱查重
	@SuppressWarnings("unchecked")
	public void checkEmail() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String email = valueMap.get("email");
		
		List<AcUser> uList = accountService.findUser(" and " + email + " = '" + email + "' ");
		List<AcProperty> pList = accountService.findProperty(" and " + email + " = '" + email + "' ");
		List<AcMerchant> mList = accountService.findMerchant(" and " + email + " = '" + email + "' ");
		
		if(uList.size()>0||pList.size()>0||mList.size()>0){
			jsonObject.put("value", "false");
		}else{
			jsonObject.put("value", "true");
		}
		out.print(jsonObject.toString());
		
	}
	
	//手机查重
	@SuppressWarnings("unchecked")
	public void checkMobile() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String mobile = valueMap.get("mobile");
		
		List<AcUser> list = accountService.findUser(" and mobile = '" + mobile + "' ");
		if(list.size()>0){
			jsonObject.put("value", "false");
			out.print(jsonObject.toString());
		}else{
			jsonObject.put("value", "true");
			out.print(jsonObject.toString());
		}
		
	}
	
}
