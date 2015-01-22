package com.ginz.action.release;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.ginz.model.PubActivities;
import com.ginz.model.PubComments;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.ActivitiesService;
import com.ginz.service.EventService;
import com.ginz.service.NoticeService;
import com.ginz.service.PictureService;
import com.ginz.util.base.AnalyzerUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;

//搜索界面
@Namespace("/")
@Action(value = "searchAction")
public class SearchAction extends BaseAction {

	private AccountService accountService;
	private PictureService pictureService;
	private NoticeService noticeService;
	private EventService eventService;
	private ActivitiesService activitiesService;
	
	public AccountService getAccountService() {
		return accountService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public PictureService getPictureService() {
		return pictureService;
	}

	@Autowired
	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}

	public NoticeService getNoticeService() {
		return noticeService;
	}

	@Autowired
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
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

	//搜用户-物业
	@SuppressWarnings("unchecked")
	public void searchProperty() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String keyWord = valueMap.get("keyWord");	//关键字
		
		keyWord = AnalyzerUtil.analyze(keyWord);
		String[] keys = keyWord.split("|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				condition += " and  propertyName REGEXP '" + keys[i] + "'";
			}
		}
		
		List<AcProperty> propertyList = accountService.findProperty(condition);
		if(propertyList.size()>0){
			for(AcProperty property:propertyList){
				JSONObject json = new JSONObject();
				json.put("id", property.getId());
				json.put("name", property.getPropertyName());
				json.put("headUrl", property.getPicUrl());
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("result", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//搜用户-个人用户
	@SuppressWarnings("unchecked")
	public void searchUser() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String keyWord = valueMap.get("keyWord");	//关键字
		
		keyWord = AnalyzerUtil.analyze(keyWord);
		String[] keys = keyWord.split("|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				condition += " and  nickName REGEXP '" + keys[i] + "'";
			}
		}
		
		List<AcUser> userList = accountService.findUser(condition);
		if(userList.size()>0){
			for(AcUser user:userList){
				JSONObject json = new JSONObject();
				json.put("id", user.getId());
				json.put("name", user.getNickName());
				json.put("headUrl", user.getHeadPortrait());
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("result", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//搜用户-商家
	@SuppressWarnings("unchecked")
	public void searchMerchant() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String keyWord = valueMap.get("keyWord");	//关键字
		
		keyWord = AnalyzerUtil.analyze(keyWord);
		String[] keys = keyWord.split("|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				condition += " and  merchantName REGEXP '" + keys[i] + "'";
			}
		}
		
		List<AcMerchant> merchantList = accountService.findMerchant(condition);
		if(merchantList.size()>0){
			for(AcMerchant merchant:merchantList){
				JSONObject json = new JSONObject();
				json.put("id", merchant.getId());
				json.put("name", merchant.getMerchantName());
				json.put("headUrl", merchant.getPicUrl());
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("result", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//搜信息-活动/交易
	
	
	//搜信息-社区生活
	
	
	//搜信息-公告
	
	
	
	
}
