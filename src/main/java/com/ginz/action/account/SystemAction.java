package com.ginz.action.account;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import com.ginz.model.Picture;
import com.ginz.model.PubComments;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.ActivitiesService;
import com.ginz.service.EventService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.FileUtil;
import com.ginz.util.base.JsonUtil;

//系统设置
@Namespace("/")
@Action(value = "systemAction")
public class SystemAction extends BaseAction {

	private AccountService accountService;
	private ActivitiesService activitiesService;
	private EventService eventService;
	private ReplyService replyService;
	private PictureService pictureService;
	
	public AccountService getAccountService() {
		return accountService;
	}
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	public ActivitiesService getActivitiesService() {
		return activitiesService;
	}

	@Autowired
	public void setActivitiesService(ActivitiesService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public EventService getEventService() {
		return eventService;
	}
	
	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	
	public ReplyService getReplyService() {
		return replyService;
	}

	@Autowired
	public void setReplyService(ReplyService replyService) {
		this.replyService = replyService;
	}
	
	public PictureService getPictureService() {
		return pictureService;
	}

	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}

	//我赞过的信息列表
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void listPraised() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");		//用户id-唯一标识

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		List<PubPraise> praiseList = replyService.findPraise(" and userId = '" + userId + "'");
		
		if(praiseList.size()>0){
			String noticeIds = "";	//用户参与的所有社区公告信息id集合		
			String eventIds = "";	//用户参与的所有社区生活信息id集合
			String activityIds = "";	//用户参与的所有互动交易信息id集合
			
			for(PubPraise praise : praiseList){
				if(StringUtils.equals(praise.getReleaseType(), DictionaryUtil.RELEASE_TYPE_01)){	//社区公告
					if(StringUtils.isNotEmpty(noticeIds)){
						noticeIds += "," + praise.getReleaseId();
					}else{
						noticeIds += praise.getReleaseId();
					}
				}else if(StringUtils.equals(praise.getReleaseType(), DictionaryUtil.RELEASE_TYPE_02)){	//社区生活
					if(StringUtils.isNotEmpty(eventIds)){
						eventIds += "," + praise.getReleaseId();
					}else{
						eventIds += praise.getReleaseId();
					}
				}else if(StringUtils.equals(praise.getReleaseType(), DictionaryUtil.RELEASE_TYPE_03)){	//活动/交易
					if(StringUtils.isNotEmpty(activityIds)){
						activityIds += "," + praise.getReleaseId();
					}else{
						activityIds += praise.getReleaseId();
					}
				}
			}
			
			HashMap<String,Object> rethm = eventService.listAllRelease(noticeIds, eventIds, activityIds);
			List<Object> list = (List<Object>) rethm.get("list");	//查询内容与用户兴趣爱好接近的信息
			if(list != null && !list.isEmpty()){
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){	//遍历后放入临时对象ReleaseDemo中，形成临时的releaseList
					Object[] obj = (Object[]) iterator.next();
					JSONObject json = new JSONObject();
					
					String releaseType = String.valueOf(obj[0]==null?"":obj[0]);
					String id = String.valueOf(obj[1]==null?"":obj[1]);
					String subject = String.valueOf(obj[2]==null?"":obj[2]);
					String uId = String.valueOf(obj[4]==null?"":obj[4]);
					String picIds = String.valueOf(obj[5]==null?"":obj[5]);
					Date createDate = DateFormatUtil.toDate(String.valueOf(obj[3]==null?"":obj[3]));
					
					json.put("releaseType", releaseType);
					json.put("id", id);
					json.put("subject", subject);
					json.put("createTime", DateFormatUtil.dateToStringM(createDate));
					
					if(picIds!=null&&!picIds.equals("")){
						String[] ids = picIds.split(",");
						if(ids.length>0){
							Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
							if(picture!=null){
								json.put("picUrl", picture.getThumbnailUrl());
							}
						}
					}
					if(uId!=null&&!uId.equals("")){
						if(StringUtils.equals(releaseType, DictionaryUtil.RELEASE_TYPE_01)){
							AcProperty property = accountService.loadProperty(uId);
							if(property!=null){
								json.put("userId", uId);
								json.put("name", property.getPropertyName());
								json.put("headUrl", property.getPicUrl());
							}
						}else if(StringUtils.equals(releaseType, DictionaryUtil.RELEASE_TYPE_02)||StringUtils.equals(releaseType, DictionaryUtil.RELEASE_TYPE_03)){
							AcUser user = accountService.loadUser(uId);
							if(user != null){
								json.put("userId", uId);
								json.put("name", user.getNickName());
								json.put("headUrl", user.getHeadPortrait());
							}
						}
					}
					
					if(id!=null&&!id.equals("")){
						int praiseNum = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + releaseType + "'");
						int commentNum = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + releaseType + "'");
						json.put("praiseNum", praiseNum+"");
						json.put("commentNum", commentNum+"");
					}
					jsonArray.add(json);
				}
				jsonObject.put("result", "1");
				jsonObject.put("value", jsonArray);
			}
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "还未赞过任何信息!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//我评论过的信息列表
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void listCommented() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");		//用户id-唯一标识

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		List<PubComments> commentsList = replyService.findComments(" and userId = '" + userId + "'");
		
		if(commentsList.size()>0){
			String noticeIds = "";	//用户参与的所有社区公告信息id集合		
			String eventIds = "";	//用户参与的所有社区生活信息id集合
			String activityIds = "";	//用户参与的所有互动交易信息id集合
			
			for(PubComments comment : commentsList){
				if(StringUtils.equals(comment.getReleaseType(), DictionaryUtil.RELEASE_TYPE_01)){	//社区公告
					if(StringUtils.isNotEmpty(noticeIds)){
						if(!noticeIds.contains(comment.getReleaseId().toString())){
							noticeIds += "," + comment.getReleaseId();
						}
					}else{
						noticeIds += comment.getReleaseId();
					}
				}else if(StringUtils.equals(comment.getReleaseType(), DictionaryUtil.RELEASE_TYPE_02)){	//社区生活
					if(StringUtils.isNotEmpty(eventIds)){
						if(!eventIds.contains(comment.getReleaseId().toString())){
							eventIds += "," + comment.getReleaseId();
						}
					}else{
						eventIds += comment.getReleaseId();
					}
				}else if(StringUtils.equals(comment.getReleaseType(), DictionaryUtil.RELEASE_TYPE_03)){	//活动/交易
					if(StringUtils.isNotEmpty(activityIds)){
						
						if(!activityIds.contains(comment.getReleaseId().toString())){
							activityIds += "," + comment.getReleaseId();
						}
					}else{
						activityIds += comment.getReleaseId();
					}
				}
			}
			
			HashMap<String,Object> rethm = eventService.listAllRelease(noticeIds, eventIds, activityIds);
			List<Object> list = (List<Object>) rethm.get("list");	//查询内容与用户兴趣爱好接近的信息
			if(list != null && !list.isEmpty()){
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){	//遍历后放入临时对象ReleaseDemo中，形成临时的releaseList
					Object[] obj = (Object[]) iterator.next();
					JSONObject json = new JSONObject();
					
					String releaseType = String.valueOf(obj[0]==null?"":obj[0]);
					String id = String.valueOf(obj[1]==null?"":obj[1]);
					String subject = String.valueOf(obj[2]==null?"":obj[2]);
					String uId = String.valueOf(obj[4]==null?"":obj[4]);
					String picIds = String.valueOf(obj[5]==null?"":obj[5]);
					Date createDate = DateFormatUtil.toDate(String.valueOf(obj[3]==null?"":obj[3]));
					
					json.put("releaseType", releaseType);
					json.put("id", id);
					json.put("subject", subject);
					json.put("createTime", DateFormatUtil.dateToStringM(createDate));
					
					if(picIds!=null&&!picIds.equals("")){
						String[] ids = picIds.split(",");
						if(ids.length>0){
							Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
							if(picture!=null){
								json.put("picUrl", picture.getThumbnailUrl());
							}
						}
					}
					if(uId!=null&&!uId.equals("")){
						if(StringUtils.equals(releaseType, DictionaryUtil.RELEASE_TYPE_01)){
							AcProperty property = accountService.loadProperty(uId);
							if(property!=null){
								json.put("userId", uId);
								json.put("name", property.getPropertyName());
								json.put("headUrl", property.getPicUrl());
							}
						}else if(StringUtils.equals(releaseType, DictionaryUtil.RELEASE_TYPE_02)||StringUtils.equals(releaseType, DictionaryUtil.RELEASE_TYPE_03)){
							AcUser user = accountService.loadUser(uId);
							if(user != null){
								json.put("userId", uId);
								json.put("name", user.getNickName());
								json.put("headUrl", user.getHeadPortrait());
							}
						}
					}
					if(id!=null&&!id.equals("")){
						int praiseNum = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + releaseType + "'");
						int commentNum = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + releaseType + "'");
						json.put("praiseNum", praiseNum+"");
						json.put("commentNum", commentNum+"");
					}
					jsonArray.add(json);
				}
				jsonObject.put("result", "1");
				jsonObject.put("value", jsonArray);
			}
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "还未评论过任何信息!");
		}
		out.print(jsonObject.toString());
		
	}
	
	//获取所有的用户个人标签
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getPersonalTabs(){
						
		HashMap<String,Object> rethm = accountService.getTabs();
		List<Object> list = (List<Object>) rethm.get("list");	
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			String content = "";
			while(iterator.hasNext()){	
				Object obj = (Object) iterator.next();
				String valueString = String.valueOf(obj==null?"":obj);
				if(StringUtils.isNotEmpty(valueString)){
					String tabs[] = valueString.split(",");
					if(tabs.length>0){
						List<String> tabList = new LinkedList<String>();  //去除标签数组中的重复项
					    for(int i = 0; i < tabs.length; i++) {  
					        if(!tabList.contains(tabs[i])) {  
					        	tabList.add(tabs[i]);  
					        }  
					    }
					    
					    for(int i = 0; i<tabList.size(); i++){
					    	if(StringUtils.isNotEmpty(content)){
					    		content += "," + tabList.get(i);
					    	}else{
					    		content += tabList.get(i);
					    	}
					    }
					}
				}
			}
			FileUtil.write("D://test.txt",content);
		}
		
	}
	
	//获取所有的社区生活信息标签
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getEventLabels() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray jsonArray = new JSONArray();
		
		HashMap<String,Object> rethm = eventService.getLabels();
		List<Object> list = (List<Object>) rethm.get("list");	
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){	
				Object obj = (Object) iterator.next();
				String valueString = String.valueOf(obj==null?"":obj);
				if(StringUtils.isNotEmpty(valueString)){
					String labels[] = valueString.split(",");
					if(labels.length>0){
						List<String> labelList = new LinkedList<String>();  //去除标签数组中的重复项
					    for(int i = 0; i < labels.length; i++) {  
					        if(!labelList.contains(labels[i])) {  
					        	labelList.add(labels[i]);  
					        }  
					    }
					    
					    for(int i = 0; i<labelList.size(); i++){
					    	jsonArray.add(labelList.get(0));
					    }
					}
				}
			}
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("eventLabelArray", jsonArray);
		out.print(jsonObject.toString());
		
	}

	//获取所有的互动交易信息标签
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getActivityLabels() throws IOException{
						
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray jsonArray = new JSONArray();
		
		HashMap<String,Object> rethm = activitiesService.getLabels();
		List<Object> list = (List<Object>) rethm.get("list");	
		if(list != null && !list.isEmpty()){
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){	
				Object obj = (Object) iterator.next();
				String valueString = String.valueOf(obj==null?"":obj);
				if(StringUtils.isNotEmpty(valueString)){
					String labels[] = valueString.split(",");
					if(labels.length>0){
						List<String> labelList = new LinkedList<String>();  //去除标签数组中的重复项
					    for(int i = 0; i < labels.length; i++) {  
					        if(!labelList.contains(labels[i])) {  
					        	labelList.add(labels[i]);  
					        }  
					    }
					    
					    for(int i = 0; i<labelList.size(); i++){
					    	jsonArray.add(labelList.get(0));
					    }
					}
				}
			}
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("activityLabelArray", jsonArray);
		out.print(jsonObject.toString());
		
	}
	
	
}
