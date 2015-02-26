package com.ginz.action.release;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcUser;
import com.ginz.model.AcUserDetail;
import com.ginz.model.Picture;
import com.ginz.model.PubActivities;
import com.ginz.model.PubComments;
import com.ginz.model.PubEvent;
import com.ginz.model.PubPraise;
import com.ginz.model.Reports;
import com.ginz.service.AccountService;
import com.ginz.service.ActivitiesService;
import com.ginz.service.EventService;
import com.ginz.service.MessageService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.CosineSimilarAlgorithm;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.base.StringUtil;
import com.ginz.util.base.ThumbnailUtil;
import com.ginz.util.model.ReleaseDemo;
import com.ginz.util.push.PushIOS;

//社区生活
@Namespace("/")
@Action(value = "eventAction")
public class EventAction extends BaseAction {

	private AccountService accountService;
	private ReplyService replyService;
	private EventService eventService;
	private PictureService pictureService;
	private MessageService messageService;
	private ActivitiesService activitiesService;
	
	public AccountService getAccountService() {
		return accountService;
	}
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	public ReplyService getReplyService() {
		return replyService;
	}
	
	@Autowired
	public void setReplyService(ReplyService replyService) {
		this.replyService = replyService;
	}
	
	public EventService getEventService() {
		return eventService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public PictureService getPictureService() {
		return pictureService;
	}

	@Autowired
	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}

	public MessageService getMessageService() {
		return messageService;
	}

	@Autowired
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	public ActivitiesService getActivitiesService() {
		return activitiesService;
	}

	@Autowired
	public void setActivitiesService(ActivitiesService activitiesService) {
		this.activitiesService = activitiesService;
	}

	//发布个人动态信息
	@SuppressWarnings("unchecked")
	public void releaseEvent() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//个人用户id
		String subject = valueMap.get("subject");
		String label = valueMap.get("label");
		
		String picIds = "";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date nowDate = new Date();
		
		MultiPartRequestWrapper wrapper = null;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {
			wrapper = (MultiPartRequestWrapper) request;
			String[] fileNames = wrapper.getFileNames("images");
			File[] files = wrapper.getFiles("images");
			
			String ext = "";
			
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");   
			Properties p = new Properties();   
			p.load(inputStream);   
			String serverUrl = p.getProperty("server_path");
			String path = p.getProperty("server_dir");
			String resize = p.getProperty("release_thumbnail_size");
			
			if(files.length>0&&fileNames.length>0){
				for(int i=0;i<files.length;i++){
					ext = fileNames[i].substring(fileNames[i].lastIndexOf("."), fileNames[i].length());
					String fileName = sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
					String dir = DictionaryUtil.PIC_RELEASE_EVENT;
					String thumbnailDir = DictionaryUtil.PIC_THUMBNAIL;
					FileUtils.copyFile(files[i], new File(path + dir + fileName)); 
					
					File file = new File(path + thumbnailDir + dir);
					if (!file.exists()) {
						file.mkdir();
					}
					ThumbnailUtil ccc = new ThumbnailUtil(path + dir + fileName, path + thumbnailDir + dir + fileName);
					ccc.resize(Integer.parseInt(resize),Integer.parseInt(resize));
					
					Picture picture = new Picture();
					picture.setUrl(serverUrl + dir + fileName);
					picture.setThumbnailUrl(serverUrl + thumbnailDir + dir + fileName);
					picture.setFileName(fileName);
					picture.setUserId(userId);
					picture.setPicType(DictionaryUtil.PIC_TYPE_RELEASE);
					picture.setCreateTime(nowDate);
					picture.setFlag(DictionaryUtil.DETELE_FLAG_00);
					Picture picture2 = pictureService.savePicture(picture);
					
					if(picIds.equals("")){
						picIds += picture2.getId();
					}else{
						picIds += "," + picture2.getId();
					}
				}
			}
		}
		
		PubEvent event = new PubEvent();
		event.setUserId(userId);
		event.setSubject(subject);
		event.setLabel(label);
		event.setPicIds(picIds);
		event.setCreateTime(new Date());
		event.setStatus(DictionaryUtil.RELEASE_MSG_STATE_00);
		event.setFlag(DictionaryUtil.DETELE_FLAG_00);
		eventService.saveEvent(event);
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//删除个人动态信息
	@SuppressWarnings("unchecked")
	public void deleteEvent() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//个人动态信息id
		String userId = valueMap.get("userId");	//个人用户id
		
		PubEvent event = eventService.loadEvent(Long.parseLong(id));
		if(event != null){
			if(StringUtils.equals(event.getUserId(),userId)){
				eventService.deleteEvent(Long.parseLong(id));	//删除个人动态信息
				
				List<PubPraise> praiseList = replyService.findPraise(" and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and releaseId = " + event.getId());
				if(praiseList.size()>0){	//删除个人动态信息的点赞
					for(PubPraise praise:praiseList){
						replyService.deletePraise(praise.getId());
					}
				}
				List<PubComments> commentsList = replyService.findComments(" and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and releaseId = " + event.getId());
				if(commentsList.size()>0){	//删除个人动态信息的相关评论
					for(PubComments comment:commentsList){
						replyService.deleteComments(comment.getId());
					}
				}
				
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");   
				Properties p = new Properties();   
				p.load(inputStream);   
				String path = p.getProperty("server_dir");	//读取服务器上图片存放目录
				
				String picIds = event.getPicIds();
				if(picIds!=null&&!picIds.equals("")){	//删除信息中的图片(删除数据表中的记录)
					String[] ids = picIds.split(",");
					if(ids.length>0){
						for(int i=0;i<ids.length;i++){
							Picture picture = pictureService.loadPicture(Long.parseLong(ids[i]));
							String url = picture.getUrl();
							String thumbnailUrl = picture.getThumbnailUrl();
							
							int index = StringUtil.getCharacterPosition(3,"/",url);	//获取url中第3个“/”的位置
							url = path + url.substring(index, url.length());
							thumbnailUrl = path + thumbnailUrl.substring(index, thumbnailUrl.length());
							File file = new File(url);
							if(file != null){
								FileUtils.forceDelete(file);
							}
							File thumbnailFile = new File(thumbnailUrl);
							if(thumbnailFile != null){
								FileUtils.forceDelete(thumbnailFile);
							}
							pictureService.deletePicture(Long.parseLong(ids[i]));
						}
					}
				}
			}
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//个人用户获取个人动态信息列表
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getEventList() throws IOException, ParseException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//需要查看的个人用户id
		String page = valueMap.get("page");
		int rows = 20;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		AcUser user = accountService.loadUser(userId);
		
		if(user!=null){
			String valueString = getHobbies(user.getUserId());
			String in = "";
			String notIn = "";
			if(!valueString.equals("")){
				if(valueString.contains("|")){
					in = valueString.substring(0, valueString.indexOf("|"));;
					in = in.replace(",", "|");
					notIn = valueString.substring(valueString.indexOf("|")+1,valueString.length());
					notIn = notIn.replace(",", "|");
				}else{
					in = in.replace(",", "|");
				}
			}
			
			List<ReleaseDemo> todayList = new ArrayList();	//今天发布的消息列表
			List<ReleaseDemo> ztList = new ArrayList();		//昨天发布的消息列表
			List<ReleaseDemo> qtList = new ArrayList();		//前天发布的消息列表
			List<ReleaseDemo> dqtList = new ArrayList();	//大前天发布的消息列表
			List<ReleaseDemo> otherList = new ArrayList();	//大前天之前发布的消息列表
			
			HashMap<String,Object> rethm = eventService.seachEvents(in, notIn);
			List<Object> list = (List<Object>) rethm.get("list");	//查询内容与用户兴趣爱好接近的信息
			if(list != null && !list.isEmpty()){
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){	//遍历后放入临时对象ReleaseDemo中，形成临时的releaseList
					Object[] obj = (Object[]) iterator.next();
					String subject = String.valueOf(obj[1]==null?"":obj[1]);
					String label = String.valueOf(obj[5]==null?"":obj[5]);
					String createTime = String.valueOf(obj[2]==null?"":obj[2]);
					
					ReleaseDemo release = new ReleaseDemo();
					release.setId(String.valueOf(obj[0]==null?"":obj[0]));
					release.setSubject(subject);
					release.setCreateTime(createTime);
					release.setUserId(String.valueOf(obj[3]==null?"":obj[3]));
					release.setPicIds(String.valueOf(obj[4]==null?"":obj[4]));
					release.setLabel(label);
					
					if(StringUtils.isEmpty(in)){
						release.setSimilarity(0);
					}else{
						double similarity =	CosineSimilarAlgorithm.getSimilarity(subject + label, in);	//计算信息主题内容标签和用户兴趣爱好的相似度
						if(similarity>0){
							release.setSimilarity(similarity);
						}else{
							release.setSimilarity(0.0);
						}
					}
					
					if(StringUtils.isNotEmpty(createTime)){
						int flag = DateFormatUtil.judgeDate(DateFormatUtil.toDate(createTime));
						if(flag==0){
							todayList.add(release);
						}else if(flag==-1){
							ztList.add(release);
						}else if(flag==-2){
							qtList.add(release);
						}else if(flag==-3){
							dqtList.add(release);
						}else if(flag<-3){
							otherList.add(release);
						}
					}
				}
				
				if(todayList.size()>0){
					JSONArray todayArray = addToArray(todayList,userId);
					if(todayArray.size()>0){
						for(int i=0;i<todayArray.size();i++){
							JSONObject json = todayArray.getJSONObject(i);
							jsonArray.add(json);
						}
					}
				}
				if(ztList.size()>0){
					JSONArray todayArray = addToArray(ztList,userId);
					if(todayArray.size()>0){
						for(int i=0;i<todayArray.size();i++){
							JSONObject json = todayArray.getJSONObject(i);
							jsonArray.add(json);
						}
					}
				}
				if(qtList.size()>0){
					JSONArray todayArray = addToArray(qtList,userId);
					if(todayArray.size()>0){
						for(int i=0;i<todayArray.size();i++){
							JSONObject json = todayArray.getJSONObject(i);
							jsonArray.add(json);
						}
					}
				}
				if(dqtList.size()>0){
					JSONArray todayArray = addToArray(dqtList,userId);
					if(todayArray.size()>0){
						for(int i=0;i<todayArray.size();i++){
							JSONObject json = todayArray.getJSONObject(i);
							jsonArray.add(json);
						}
					}
				}
				if(otherList.size()>0){
					JSONArray todayArray = addToArray(otherList,userId);
					if(todayArray.size()>0){
						for(int i=0;i<todayArray.size();i++){
							JSONObject json = todayArray.getJSONObject(i);
							jsonArray.add(json);
						}
					}
				}
				
		        int pageNum = Integer.parseInt(page);
		        if(pageNum == 1){
					if(jsonArray.size()<rows){
						jsonObject.put("value", jsonArray);
					}else{
						jsonObject.put("value", jsonArray.subList(0,rows));
					}
				}else if(pageNum > 1){
					if((jsonArray.size()>(pageNum-1)*rows)&&jsonArray.size()<pageNum*rows){
						jsonObject.put("value", jsonArray.subList((pageNum-1)*rows, jsonArray.size()));
					}else{
						jsonObject.put("value", jsonArray.subList((pageNum-1)*rows, pageNum*rows));
					}
				}
				
				jsonObject.put("result", "1");
				jsonObject.put("page", page);
				jsonObject.put("value", jsonArray);
			}else{
				jsonObject.put("result", "2");
				jsonObject.put("page", page);
				jsonObject.put("value", "没有更多的积分互动信息!");
			}
			
		}
		out.print(jsonObject.toString());
		
	}
	
	//按相似度对List重新排序，并添加到jsonArray
	public JSONArray addToArray(List<ReleaseDemo> releaseList,String userId){
		JSONArray jsonArray = new JSONArray();
		
		Collections.sort(releaseList, new Comparator<ReleaseDemo>() {	//按相似度重新排序releaseList
			public int compare(ReleaseDemo arg0, ReleaseDemo arg1) {
				BigDecimal data = new BigDecimal(arg0.getSimilarity());
				BigDecimal data1 = new BigDecimal(arg1.getSimilarity());
				return data1.compareTo(data);	//按相似度从高到低排序
			}
		});
		
		for (ReleaseDemo release : releaseList) {	//遍历添加到jsonArray中输出
			JSONObject json = new JSONObject();
			
			String id = release.getId();
			String uId = release.getUserId();
			String picIds = release.getPicIds();
			
			json.put("id", id);
			json.put("subject", release.getSubject());
			json.put("createTime", release.getCreateTime());
			
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
				AcUser u = accountService.loadUser(uId);
				if(u != null){
					json.put("userId", uId);
					json.put("name", u.getNickName());
					json.put("headUrl", u.getHeadPortrait());
				}
			}
			if(id!=null&&!id.equals("")){
				int praiseNum = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
				int commentNum = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
				json.put("praiseNum", praiseNum+"");
				json.put("commentNum", commentNum+"");
			}
			List<PubPraise> listPraise = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = '" + userId + "'");
			if(listPraise.size()>0){	//判断是否已赞过..
				json.put("isPraise", "1");
			}else{
				json.put("isPraise", "0");
			}
			jsonArray.add(json);
		}
		return jsonArray;
	}
	
	//拼接兴趣喜好关键字
	public String getHobbies(String userId){
		
		String valueString = "";
		List<AcUserDetail> list = accountService.findUserDetail(" and userId = '" + userId + "'");
		if(list.size()>0){
			AcUserDetail userDetail = list.get(0);
			if(userDetail != null){
				if(userDetail.getPersonalTag()!=null&&!userDetail.getPersonalTag().equals("")){
					valueString += "," + userDetail.getPersonalTag();
				}
				if(userDetail.getCatering()!=null&&!userDetail.getCatering().equals("")){
					valueString += "," + userDetail.getCatering();
				}
				if(userDetail.getSocialContact()!=null&&!userDetail.getSocialContact().equals("")){
					valueString += "," + userDetail.getSocialContact();
				}
				if(userDetail.getTravel()!=null&&!userDetail.getTravel().equals("")){
					valueString += "," + userDetail.getTravel();
				}
				if(userDetail.getSports()!=null&&!userDetail.getSports().equals("")){
					valueString += "," + userDetail.getSports();
				}
				if(userDetail.getMusic()!=null&&!userDetail.getMusic().equals("")){
					valueString += "," + userDetail.getMusic();
				}
				if(userDetail.getOthers()!=null&&!userDetail.getOthers().equals("")){
					valueString += "," + userDetail.getOthers();
				}
				if(userDetail.getCommunityNeed()!=null&&!userDetail.getCommunityNeed().equals("")){
					valueString += "," + userDetail.getCommunityNeed();
				}
				if(userDetail.getDislike()!=null&&!userDetail.getDislike().equals("")){
					valueString += "|" + userDetail.getDislike();
				}
			}
		}
		
		return valueString;
	}
	
	//个人用户获取个人动态信息详细内容
	@SuppressWarnings("unchecked")
	public void getEventDetail() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//个人动态信息id
		String userId = valueMap.get("userId");	//用户id
		
		JSONObject json = new JSONObject();
		
		PubEvent event = eventService.loadEvent(Long.parseLong(id));
		if(event != null){
			json = JSONObject.fromObject(event);
			String createTime = DateFormatUtil.dateToStringM(event.getCreateTime());
			json.remove("createTime");
			json.put("createTime", createTime);
			AcUser user = accountService.loadUser(event.getUserId());
			if(user!=null){
				json.put("name", user.getNickName());
				json.put("headUrl", user.getHeadPortrait());
			}
			List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = '" + userId + "'");
			if(list.size()>0){	//判断是否已赞过..
				json.put("isPraise", "1");
			}else{
				json.put("isPraise", "0");
			}
			String picIds = event.getPicIds();
			if(picIds!=null&&!picIds.equals("")){
				String[] ids = picIds.split(",");
				if(ids.length>0){
					for(int i=0;i<ids.length;i++){
						Picture picture = pictureService.loadPicture(Long.parseLong(ids[i]));
						if(picture!=null){
							json.put("image"+(i+1), picture.getThumbnailUrl());
						}
					}
				}
			}
		}
		
		out.print(json.toString());
		
	}
	
	//对个人动态信息点赞
	@SuppressWarnings("unchecked")
	public void doPraise() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//点赞操作的用户id-个人用户
		String id = valueMap.get("id");	//个人动态信息id
		
		int num = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = '" + userId + "'");
		if(list.size()>0){	//判断是否已赞过..删除点赞记录(取消点赞)
			replyService.deletePraise(list.get(0).getId());
			num--;
			jsonObject.put("result", "2");
			jsonObject.put("number", num+"");	//更新点赞数量
		}else{
			PubPraise praise = new PubPraise();
			praise.setReleaseId(Long.parseLong(id));
			praise.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
			praise.setUserId(userId);
			praise.setCreateTime(new Date());
			praise.setFlag(DictionaryUtil.DETELE_FLAG_00);
			replyService.savePraise(praise);
			
			num++;
			jsonObject.put("result", "1");
			jsonObject.put("number", num);	//更新点赞数量
			
			//发推送消息给发布该个人动态信息的个人用户
			PubEvent event = eventService.loadEvent(Long.parseLong(id));
			if(event != null){
				String uId = event.getUserId();
				if(!userId.equals(uId)){	//如果是本人点赞，不发推送消息
					AcUser u = accountService.loadUser(uId);
					if(u!=null){
						AcUser user = accountService.loadUser(userId);
						if(user != null){
							String value = user.getNickName() + "赞了你的信息!";
							
							//发送推送给目标用户
							if(u.getDeviceToken()!=null&&!u.getDeviceToken().equals("")){
								PushIOS.pushSingleDevice(value, u.getDeviceToken());	//通知个人用户有人点赞..
							}
							
							//发送系统通知给目标用户
							messageService.sendMessage(null, uId, value, value, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_02, DictionaryUtil.MESSAGE_TYPE_PRAISE);
						}
					}
				}
			}
		}
		out.print(jsonObject.toString());
		
		countSysTab(userId);
		
	}
	
	//对个人动态信息发表评论
	@SuppressWarnings("unchecked")
	public void doComment() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//发表评论的用户id-个人用户
		String id = valueMap.get("id");	//社区生活信息id
		String commentId = valueMap.get("commentId");	//回复的评论id，非回复评论而是直接对活动发表评论则为空或是0
		String content = valueMap.get("content");
		
		AcUser user = accountService.loadUser(userId);
		if(user != null){
			PubEvent event = eventService.loadEvent(Long.parseLong(id));
			if(event != null){
				PubComments comment = new PubComments();
				comment.setReleaseId(Long.parseLong(id));
				comment.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
				comment.setContent(content);
				comment.setUserId(userId);
				comment.setCreateTime(new Date());
				if(StringUtils.isNotEmpty(commentId)){
					comment.setFollowId(Long.parseLong(commentId));
				}
				comment.setFlag(DictionaryUtil.DETELE_FLAG_00);
				replyService.saveComments(comment);
				
				int num = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
				jsonObject.put("result", "1");
				jsonObject.put("value", "SUCCESS!");
				jsonObject.put("number", num);	//更新评论数量
				out.print(jsonObject.toString());
				
				String uId = event.getUserId();	//发布信息的用户id
				String value = "你的信息有了新评论!";
				String value2 = "你的评论有了新回复!";
				String value3 = "你评论过的信息有了新的评论!";
				if(!userId.equals(uId)){	//非本人发表评论
					
					AcUser u = accountService.loadUser(uId);
					if(u != null){
						if(StringUtils.isNotEmpty(commentId)){		//回复评论
							//推送并发送系统消息给信息发布人
							if(u.getDeviceToken()!=null&&!u.getDeviceToken().equals("")){
								PushIOS.pushSingleDevice(value, u.getDeviceToken());	
							}
							messageService.sendMessage(null, uId, value, value, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_03, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
						
							//发送系统消息给回复的那条评论的发布人
							PubComments c = replyService.loadComments(Long.parseLong(commentId));
							if(c!=null){
								messageService.sendMessage(null, c.getUserId(), value2, value2, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_03, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
							}
						
						}else{		//对信息发布新的评论
							//推送并发送系统消息给信息发布人
							if(u.getDeviceToken()!=null&&!u.getDeviceToken().equals("")){
								PushIOS.pushSingleDevice(value, u.getDeviceToken());	
							}
							messageService.sendMessage(null, uId, value, value, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_03, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
						
							//发送系统消息给其他已经评论过该信息的用户
							List<PubComments> list = replyService.findComments(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_03 + "' ");
							if(list.size()>0){
								for(PubComments c : list){
									messageService.sendMessage(null, c.getUserId(), value3, value3, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_03, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
								}
							}
						}
					}
					
				}else{	//本人发表评论，不发推送消息
					if(StringUtils.isNotEmpty(commentId)){		//回复评论
						//发送系统消息给所回复评论的发布人
						PubComments c = replyService.loadComments(Long.parseLong(commentId));
						if(c!=null){
							messageService.sendMessage(null, c.getUserId(), value2, value2, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_03, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
						}
					}else{		//对自己的信息发布新的评论
						//发送系统消息给其他已经评论过该信息的用户
						List<PubComments> list = replyService.findComments(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_03 + "' ");
						if(list.size()>0){
							for(PubComments c : list){
								messageService.sendMessage(null, c.getUserId(), value3, value3, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_03, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
							}
						}
						
					}
				}
			}else{
				jsonObject.put("result", "2");
				jsonObject.put("value", "该信息已被删除!");
			}
		}
		countSysTab(userId);
		
	}
	
	//显示点赞的list(只显示给发布信息的用户)
	@SuppressWarnings("unchecked")
	public void listPraise() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//积分互动信息id
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' order by createTime desc ");
		if(list.size()>0){
			for(PubPraise praise:list){
				JSONObject json = new JSONObject();
				AcUser user = accountService.loadUser(praise.getUserId());
				if(user != null){
					json.put("userId", user.getUserId());
					json.put("name", user.getNickName());
				}
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
			jsonObject.put("number", list.size());
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "还没有人点过赞!");
			jsonObject.put("number", 0);
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//显示评论的list
	@SuppressWarnings("unchecked")
	public void listComment() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//信息id
		String page = valueMap.get("page");
		int rows = 50;
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' order by createTime desc ", Integer.parseInt(page), rows);
		if(list.size()>0){
			for(PubComments comment:list){
				JSONObject json = new JSONObject();
				json.put("commentId", comment.getId());
				json.put("content", comment.getContent());
				String createTime = DateFormatUtil.dateToStringM(comment.getCreateTime());
				json.put("createTime", createTime);
				AcUser user = accountService.loadUser(comment.getUserId());
				if(user != null){
					json.put("userId", user.getUserId());
					json.put("name", user.getNickName());
					json.put("headUrl", user.getHeadPortrait());
				}
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
			jsonObject.put("page", page);
			jsonObject.put("number", list.size());
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "还没有人评论过!");
			jsonObject.put("page", page);
			jsonObject.put("number", 0);
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//举报
	@SuppressWarnings("unchecked")
	public void report() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//消息id
		String userId = valueMap.get("userId");
		String description = valueMap.get("description");
		
		Reports report = new Reports();
		
		report.setReleaseId(Long.parseLong(id));
		report.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
		report.setDescription(description);
		report.setUserId(userId);
		report.setCreateTime(new Date());
		report.setFlag(DictionaryUtil.DETELE_FLAG_00);
		replyService.saveReport(report);
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//信息关闭
	@SuppressWarnings("unchecked")
	public void close() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//消息id
		
		PubEvent event = eventService.loadEvent(Long.parseLong(id));
		if(event!=null){
			event.setStatus(DictionaryUtil.RELEASE_MSG_STATE_01);
			event.setCloseTime(new Date());
			eventService.updateEvent(event);
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//对用户点赞或评论的信息做统计，统计其参与的信息标签出现的频率最高的10个标签
	public void countSysTab(String userId) throws IOException{
		
		//查询用户全部的点赞和评论
		List<PubPraise> praiseList = replyService.findPraise(" and userId = '" + userId + "'");
		List<PubComments> commentsList = replyService.findComments(" and userId = '" + userId + "' GROUP BY releaseType, releaseId ");
		
		List<Long> eventList = new ArrayList<Long>();	//用户参与的所有社区生活信息
		List<Long> activityList = new ArrayList<Long>();	//用户参与的所有互动交易信息
		
		if(praiseList.size()>0){
			for(PubPraise praise : praiseList){
				if(StringUtils.equals(praise.getReleaseType(), DictionaryUtil.RELEASE_TYPE_02)){	//社区生活
					if(!eventList.contains(praise.getReleaseId())) {  
						eventList.add(praise.getReleaseId());  
			        }  
				}else if(StringUtils.equals(praise.getReleaseType(), DictionaryUtil.RELEASE_TYPE_03)){		//互动交易
					if(!activityList.contains(praise.getReleaseId())) {  
						activityList.add(praise.getReleaseId());  
			        } 
				}
			}
		}
		
		if(commentsList.size()>0){
			for(PubComments comments : commentsList){
				if(StringUtils.equals(comments.getReleaseType(), DictionaryUtil.RELEASE_TYPE_02)){	//社区生活
					if(!eventList.contains(comments.getReleaseId())) {  
						eventList.add(comments.getReleaseId());  
			        }  
				}else if(StringUtils.equals(comments.getReleaseType(), DictionaryUtil.RELEASE_TYPE_03)){		//互动交易
					if(!activityList.contains(comments.getReleaseId())) {  
						activityList.add(comments.getReleaseId());  
			        } 
				}
			}
		}
		
		//遍历查询信息条，取全部的label
		List<String> labelList = new ArrayList<String>();	//全部的label list
		if(eventList.size()>0){
			for(int i=0;i<eventList.size();i++){
				PubEvent event = eventService.loadEvent(eventList.get(i));
				if(event!=null){
					String label = event.getLabel();
					if(StringUtils.isNotEmpty(label)){
						String[] keys = label.split(",");
						if(keys.length>0){
							for(int k=0;k<keys.length;k++){
								labelList.add(keys[k]);
							}
						}
					}
				}
			}
		}
		if(activityList.size()>0){
			for(int i=0;i<activityList.size();i++){
				PubActivities activity = activitiesService.loadActivities(activityList.get(i));
				if(activity!=null){
					String label = activity.getLabel();
					if(StringUtils.isNotEmpty(label)){
						String[] keys = label.split(",");
						if(keys.length>0){
							for(int k=0;k<keys.length;k++){
								labelList.add(keys[k]);
							}
						}
					}
				}
			}
		}
		
		//统计
		Map<String, Integer> map = new HashMap<String, Integer>();// 用于统计各个标签的个数，排序
		for(int i=0;i<labelList.size();i++){
			String word = labelList.get(i);
			if (map.containsKey(word)) { // HashMap不允许重复的key，所以利用这个特性，去统计单词的个数
				int count = map.get(word);
				map.put(word, count + 1); // 如果HashMap已有这个标签，则设置它的数量加1
			} else{
				map.put(word, 1); //如果没有这个标签，则新填入，数量为1
			}
		}
		String valueString = StringUtil.sort(map); // 调用排序的方法，排序并输出！
		
		//保存进个人详细表
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = accountService.loadUserDetail(user.getUserId());
			userDetail.setSystemTag(valueString);
			accountService.updateUserDetail(userDetail);
		}
	}
	
}
