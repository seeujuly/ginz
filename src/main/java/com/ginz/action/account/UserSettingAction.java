package com.ginz.action.account;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.ginz.model.AcUserDetail;
import com.ginz.service.AccountService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "userSettingAction")
public class UserSettingAction extends BaseAction {

	private AccountService accountService;

	public AccountService getAccountService() {
		return accountService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@SuppressWarnings("unchecked")
	public void save() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		
		String email = valueMap.get("email");
		String mobile = valueMap.get("mobile");
		String gender = valueMap.get("gender");
		String realName = valueMap.get("realName");
		String nickName = valueMap.get("nickName");
		String birthday = valueMap.get("birthday");
		
		//根据生日计算星座
		Date date = DateFormatUtil.stringToDate(birthday);
		int month = DateFormatUtil.getMonth(date);
		int day = DateFormatUtil.getDay(date);
		String constellation = DateFormatUtil.getAstro(month,day);
		
		List<AcUser> userList = accountService.findUser(" and mobile = '" + mobile + "' ");
		if(userList.size()>0){
			AcUser user = userList.get(0);
			user.setEmail(email);
			user.setRealName(realName);
			user.setNickName(nickName);
			accountService.updateUser(user);
			
			AcUserDetail userDetail;
			List<AcUserDetail> detailList = accountService.findUserDetail(" and user = " + user.getId());
			if(detailList.size()>0){
				userDetail = detailList.get(0);
			}else{
				userDetail = new AcUserDetail();
				userDetail.setUserId(user.getId());
			}
			userDetail.setGender(gender);
			userDetail.setConstellation(constellation);
			userDetail.setBirthday(DateFormatUtil.stringToDate(birthday));
			userDetail.setAge(DateFormatUtil.getAgeByBirthday(DateFormatUtil.stringToDate(birthday)));
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
}
