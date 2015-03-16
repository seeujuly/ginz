package com.ginz.action.release;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
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
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.Community;
import com.ginz.model.Picture;
import com.ginz.service.AccountService;
import com.ginz.service.ActivitiesService;
import com.ginz.service.CommunityService;
import com.ginz.service.EventService;
import com.ginz.service.NoticeService;
import com.ginz.service.PictureService;
import com.ginz.util.base.AnalyzerUtil;
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
	private CommunityService communityService;
	
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
	
	public CommunityService getCommunityService() {
		return communityService;
	}

	@Autowired
	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}

	//搜用户-物业
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		String[] keys = keyWord.split("\\|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				condition += " and  property_name REGEXP '" + keys[i] + "'";
			}
		}
		
		HashMap<String,Object> rethm = accountService.findPropertyBySql(condition);
		List<Object> list = (List<Object>) rethm.get("list");
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				Object[] obj = (Object[]) iterator.next();
				JSONObject json = new JSONObject();
				json.put("userId", String.valueOf(obj[0]==null?"":obj[0]));
				json.put("name", String.valueOf(obj[1]==null?"":obj[1]));
				json.put("headUrl", String.valueOf(obj[2]==null?"":obj[2]));
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//搜用户-商家
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		String[] keys = keyWord.split("\\|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				condition += " and  merchant_name REGEXP '" + keys[i] + "'";
			}
		}
		
		HashMap<String,Object> rethm = accountService.findMerchantBySql(condition);
		List<Object> list = (List<Object>) rethm.get("list");
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				Object[] obj = (Object[]) iterator.next();
				JSONObject json = new JSONObject();
				json.put("userId", String.valueOf(obj[0]==null?"":obj[0]));
				json.put("name", String.valueOf(obj[1]==null?"":obj[1]));
				json.put("headUrl", String.valueOf(obj[2]==null?"":obj[2]));
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//搜用户-个人用户
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		String[] keys = keyWord.split("\\|");
		
		String userCondition = "";
		String detailCondition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				userCondition += " and nick_name REGEXP '" + keys[i] + "'";
			}
			for(int i=0;i<keys.length;i++){
				detailCondition += " and CONCAT(catering,',',social_contact,',',travel,',',sports,',',music,',',others,',',community_need) REGEXP '" + keys[i] + "'";
			}
		}
		
		HashMap<String,Object> rethm = accountService.searchUser(userCondition, detailCondition);
		List<Object> list = (List<Object>) rethm.get("list");
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				Object[] obj = (Object[]) iterator.next();
				JSONObject json = new JSONObject();
				json.put("userId", String.valueOf(obj[0]==null?"":obj[0]));
				json.put("name", String.valueOf(obj[1]==null?"":obj[1]));
				json.put("headUrl", String.valueOf(obj[2]==null?"":obj[2]));
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
		
	//搜信息-活动/交易
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void searchActivity() throws IOException{
		
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
		//String userId = valueMap.get("userId");	//用户唯一标识
		String keyWord = valueMap.get("keyWord");	//关键字
		
		keyWord = AnalyzerUtil.analyze(keyWord);
		String[] keys = keyWord.split("\\|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				if(StringUtils.isNotEmpty(keys[i])){
					condition += " and CONCAT(subject,',',label,',',place) REGEXP '" + keys[i] + "'";
				}
			}
		}
		/*List<AcUser> userList = new ArrayList<AcUser>();
		List<AcProperty> propertyList = new ArrayList<AcProperty>();
		List<AcMerchant> merchantList = new ArrayList<AcMerchant>();
		if(StringUtils.equals(userId.substring(0, 1), "u")){		//个人用户
			userList = accountService.findUser(" userId = '" + userId + "' ");
			if(userList.size()>0){
				AcUser user = userList.get(0);
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			propertyList = accountService.findProperty("  ");
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			merchantList = accountService.findMerchant("  ");
		}*/
		
		HashMap<String,Object> rethm = activitiesService.findActivitiesBySql(condition);
		List<Object> list = (List<Object>) rethm.get("list");	
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				Object[] obj = (Object[]) iterator.next();
				
				JSONObject json = new JSONObject();
				json.put("id", String.valueOf(obj[0]==null?"":obj[0]));
				json.put("subject", String.valueOf(obj[1]==null?"":obj[1]));
				json.put("createTime", String.valueOf(obj[2]==null?"":obj[2]));
				String picIds = String.valueOf(obj[3]==null?"":obj[3]);
				if(picIds!=null&&!picIds.equals("")){
					String[] ids = picIds.split(",");
					if(ids.length>0){
						Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
						if(picture!=null){
							json.put("picUrl", picture.getThumbnailUrl());
						}
					}
				}
				json.put("userId", String.valueOf(obj[4]==null?"":obj[4]));
				jsonArray.add(json);
			}
			
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//搜信息-社区生活
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void searchEvent() throws IOException{
		
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
		//String userId = valueMap.get("userId");	//用户唯一标识
		String keyWord = valueMap.get("keyWord");	//关键字
		
		keyWord = AnalyzerUtil.analyze(keyWord);
		String[] keys = keyWord.split("\\|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				if(StringUtils.isNotEmpty(keys[i])){
					condition += " and CONCAT(subject,',',label) REGEXP '" + keys[i] + "'";
				}
			}
		}

		HashMap<String,Object> rethm = eventService.findEventBySql(condition);
		List<Object> list = (List<Object>) rethm.get("list");	
		
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				Object[] obj = (Object[]) iterator.next();
				
				JSONObject json = new JSONObject();
				json.put("id", String.valueOf(obj[0]==null?"":obj[0]));
				json.put("subject", String.valueOf(obj[1]==null?"":obj[1]));
				json.put("createTime", String.valueOf(obj[2]==null?"":obj[2]));
				String picIds = String.valueOf(obj[3]==null?"":obj[3]);
				if(picIds!=null&&!picIds.equals("")){
					String[] ids = picIds.split(",");
					if(ids.length>0){
						Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
						if(picture!=null){
							json.put("picUrl", picture.getThumbnailUrl());
						}
					}
				}
				json.put("userId", String.valueOf(obj[4]==null?"":obj[4]));
				jsonArray.add(json);
			}
			
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "无!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//搜信息-公告
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void searchNotice() throws IOException{
		
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
		String userId = valueMap.get("userId");	//用户id
		String keyWord = valueMap.get("keyWord");	//关键字
		
		keyWord = AnalyzerUtil.analyze(keyWord);
		String[] keys = keyWord.split("\\|");
		String condition = "";
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
				if(StringUtils.isNotEmpty(keys[i])){
					condition += " and subject REGEXP '" + keys[i] + "'";
				}
			}
		}
		
		if(StringUtils.equals(userId.substring(0, 1), "u")){		//个人用户查看自己所属小区公告
			List<AcUser> userList = accountService.findUser(" and userId = '" + userId + "' ");
			if(userList.size()>0){
				AcUser user = userList.get(0);
				if(user!=null){
					if(StringUtils.isEmpty(user.getCommunityId().toString())){
						jsonObject.put("result", "3");
						jsonObject.put("value", "您还未添加社区信息!");
					}else{
						HashMap<String,Object> rethm = noticeService.findNoticeBySql(" and community_id = " + user.getCommunityId() + condition);
						List<Object> list = (List<Object>) rethm.get("list");	
						if(list != null && !list.isEmpty()){
							Iterator iterator = list.iterator();
							while(iterator.hasNext()){
								Object[] obj = (Object[]) iterator.next();
								
								JSONObject json = new JSONObject();
								json.put("id", String.valueOf(obj[0]==null?"":obj[0]));
								json.put("subject", String.valueOf(obj[1]==null?"":obj[1]));
								json.put("createTime", String.valueOf(obj[2]==null?"":obj[2]));
								String picIds = String.valueOf(obj[3]==null?"":obj[3]);
								if(picIds!=null&&!picIds.equals("")){
									String[] ids = picIds.split(",");
									if(ids.length>0){
										Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
										if(picture!=null){
											json.put("picUrl", picture.getThumbnailUrl());
										}
									}
								}
								json.put("userId", String.valueOf(obj[4]==null?"":obj[4]));
								jsonArray.add(json);
							}
							
							jsonObject.put("result", "1");
							jsonObject.put("value", jsonArray);
						}else{
							jsonObject.put("result", "2");
							jsonObject.put("value", "当前没有任何公告!");
						}
					}
				}
			}
			
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){		//物业查看所管理的各个小区的公告
			List<AcProperty> propertyList = accountService.findProperty(" userId = '" + userId + "' ");
			if(propertyList.size()>0){
				AcProperty property = propertyList.get(0);
				if(property!=null){
					List<Community> communityList = communityService.find(" propertyId = " + property.getId());
					if(communityList.size()>0){
						String communityIds = "";
						for(Community community : communityList){
							if(StringUtils.isEmpty(communityIds)){
								communityIds += community.getId();
							}else{
								communityIds += "," + community.getId();
							}
						}
						HashMap<String,Object> rethm = noticeService.findNoticeBySql(" and community_id in (" + communityIds + ")" + condition);
						List<Object> list = (List<Object>) rethm.get("list");	
						if(list != null && !list.isEmpty()){
							Iterator iterator = list.iterator();
							while(iterator.hasNext()){
								Object[] obj = (Object[]) iterator.next();
								
								JSONObject json = new JSONObject();
								json.put("id", String.valueOf(obj[0]==null?"":obj[0]));
								json.put("subject", String.valueOf(obj[1]==null?"":obj[1]));
								json.put("createTime", String.valueOf(obj[2]==null?"":obj[2]));
								String picIds = String.valueOf(obj[3]==null?"":obj[3]);
								if(picIds!=null&&!picIds.equals("")){
									String[] ids = picIds.split(",");
									if(ids.length>0){
										Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
										if(picture!=null){
											json.put("picUrl", picture.getThumbnailUrl());
										}
									}
								}
								json.put("userId", String.valueOf(obj[4]==null?"":obj[4]));
								jsonArray.add(json);
							}
							
							jsonObject.put("result", "1");
							jsonObject.put("value", jsonArray);
						}else{
							jsonObject.put("result", "2");
							jsonObject.put("value", "当前没有任何公告!");
						}
					}else{
						jsonObject.put("result", "3");
						jsonObject.put("value", "您还未添加社区信息!");
					}
				}
			}
		}
		/*else if(StringUtils.equals(userId.substring(0, 1), "m")){
			merchantList = accountService.findMerchant(" userId = '" + userId + "' ");
		}AcMerchant*/

		out.print(jsonObject.toString());
		
	}
	
}
