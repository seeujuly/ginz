package com.ginz.action.release;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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
import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;
import com.ginz.model.Picture;
import com.ginz.model.PubComments;
import com.ginz.model.PubEvent;
import com.ginz.model.PubPraise;
import com.ginz.model.Reports;
import com.ginz.service.AccountService;
import com.ginz.service.EventService;
import com.ginz.service.MessageService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.base.StringUtil;
import com.ginz.util.base.ThumbnailUtil;
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
		String id = valueMap.get("id");	//个人用户id
		String subject = valueMap.get("subject");
		String content = valueMap.get("content");
		String label = valueMap.get("label");
		
		AcUser user = accountService.loadUser(Long.parseLong(id));
		
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
					picture.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
					picture.setUserId(user.getId());
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
		event.setUserId(Long.parseLong(id));
		event.setSubject(subject);
		event.setContent(content);
		event.setLabel(label);
		event.setPicIds(picIds);
		event.setCreateTime(new Date());
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
			if(StringUtils.equals(event.getUserId().toString(),userId)){
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
	public void getEventList() throws IOException{
		
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
		int rows = 10;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		
		if(user!=null){
			if(Integer.parseInt(page)>1){
				rows = 5;
			}
			
			String valueString = getHobbies(Long.parseLong(userId));
			String in = "";
			String notIn = "";
			if(!valueString.equals("")){
				in = valueString.substring(0, valueString.indexOf("|"));;
				in = in.replace(",", "|");
				notIn = valueString.substring(valueString.indexOf("|")+1,valueString.length());
				notIn = notIn.replace(",", "|");
			}
			
			HashMap<String,Object> rethm = eventService.seachEvents(in, notIn, Integer.parseInt(page), rows);
			List<Object> list = (List<Object>) rethm.get("list");
			if(list != null && !list.isEmpty()){
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Object[] obj = (Object[]) iterator.next();
					JSONObject json = new JSONObject();
					String id = String.valueOf(obj[0]==null?"":obj[0]);
					
					json.put("id", id);
					json.put("subject", String.valueOf(obj[1]==null?"":obj[1]));
					json.put("createTime", String.valueOf(obj[2]==null?"":obj[2]));
					String uId = String.valueOf(obj[3]==null?"":obj[3]);
					String picIds = String.valueOf(obj[4]==null?"":obj[4]);
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
						AcUser u = accountService.loadUser(Long.parseLong(uId));
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
					List<PubPraise> listPraise = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = " + userId);
					if(listPraise.size()>0){	//判断是否已赞过..
						json.put("isPraise", "1");
					}else{
						json.put("isPraise", "0");
					}
					jsonArray.add(json);
					
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
	
	//拼接兴趣喜好关键字
	public String getHobbies(Long userId){
		
		String valueString = "";
		AcUserDetail userDetail = accountService.loadUserDetail(userId);
		if(userDetail != null){
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
			List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = " + userId);
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
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = " + userId);
		if(list.size()>0){	//判断是否已赞过..删除点赞记录(取消点赞)
			replyService.deletePraise(list.get(0).getId());
			num--;
			jsonObject.put("result", "2");
			jsonObject.put("number", num+"");	//更新点赞数量
		}else{
			PubPraise praise = new PubPraise();
			praise.setReleaseId(Long.parseLong(id));
			praise.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
			praise.setUserId(Long.parseLong(userId));
			praise.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
			praise.setCreateTime(new Date());
			praise.setFlag(DictionaryUtil.DETELE_FLAG_00);
			replyService.savePraise(praise);
			
			num++;
			jsonObject.put("result", "1");
			jsonObject.put("number", num);	//更新点赞数量
			
			//发推送消息给发布该个人动态信息的个人用户
			PubEvent event = eventService.loadEvent(Long.parseLong(id));
			if(event != null){
				Long uId = event.getUserId();
				if(!userId.equals(uId)){	//如果是本人点赞，不发推送消息
					AcUser u = accountService.loadUser(uId);
					if(u!=null){
						AcUser user = accountService.loadUser(Long.parseLong(userId));
						if(user != null){
							if(u.getDeviceToken()!=null&&!u.getDeviceToken().equals("")){
								PushIOS.pushSingleDevice(user.getNickName() + "赞了你的信息!", u.getDeviceToken());	//通知个人用户有人点赞..
							}
							
							//发送系统消息给目标用户
							MsgMessageInfo messageInfo = new MsgMessageInfo();
							messageInfo.setTargetUserId(u.getId());
							messageInfo.setTargetAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
							messageInfo.setCreateTime(new Date());
							messageInfo.setSubject(user.getNickName() + "赞了你的信息!");
							messageInfo.setContent(user.getNickName() + "赞了你的信息!");
							messageInfo.setReleaseId(Long.parseLong(id));
							messageInfo.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
							messageInfo.setMessageType(DictionaryUtil.MESSAGE_TYPE_PRAISE);
							messageInfo.setFlag(DictionaryUtil.DETELE_FLAG_00);
							MsgMessageInfo messageInfo2 = messageService.saveMessageInfo(messageInfo);
							
							MsgMessageBox messageBox = new MsgMessageBox();
							messageBox.setMessageId(messageInfo2.getId());
							messageBox.setUserId(u.getId());
							messageBox.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
							messageBox.setReceiveDate(new Date());
							messageBox.setReadFlag(DictionaryUtil.MESSAGE_UNREAD);
							messageBox.setFlag(DictionaryUtil.DETELE_FLAG_00);
							messageService.saveMessageBox(messageBox);
						}
					}
				}
			}
		}
		out.print(jsonObject.toString());
		
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
		String id = valueMap.get("id");	//个人动态id
		String content = valueMap.get("content");
		
		PubComments comment = new PubComments();
		comment.setReleaseId(Long.parseLong(id));
		comment.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
		comment.setContent(content);
		comment.setUserId(Long.parseLong(userId));
		comment.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
		comment.setCreateTime(new Date());
		comment.setFlag(DictionaryUtil.DETELE_FLAG_00);
		replyService.saveComments(comment);
		
		int num = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
		jsonObject.put("result", "1");
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("number", num);	//更新评论数量
		
		out.print(jsonObject.toString());
		
		//发推送消息给发布该个人动态信息的个人用户
		PubEvent event = eventService.loadEvent(Long.parseLong(id));
		if(event != null){
			Long uId = event.getUserId();
			if(!userId.equals(uId)){	//如果是本人评论，不发推送消息
				AcUser u = accountService.loadUser(uId);
				if(u!=null){
					AcUser user = accountService.loadUser(Long.parseLong(userId));
					if(user != null){
						if(u.getDeviceToken()!=null&&!u.getDeviceToken().equals("")){
							PushIOS.pushSingleDevice(user.getNickName() + "评论了你的信息", u.getDeviceToken());	//通知个人用户有人评论
						}
						
						//发送系统消息给目标用户
						MsgMessageInfo messageInfo = new MsgMessageInfo();
						messageInfo.setTargetUserId(u.getId());
						messageInfo.setTargetAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
						messageInfo.setCreateTime(new Date());
						messageInfo.setSubject(user.getNickName() + "赞了你的信息!");
						messageInfo.setContent(user.getNickName() + "赞了你的信息!");
						messageInfo.setReleaseId(Long.parseLong(id));
						messageInfo.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
						messageInfo.setMessageType(DictionaryUtil.MESSAGE_TYPE_COMMENTS);
						messageInfo.setFlag(DictionaryUtil.DETELE_FLAG_00);
						MsgMessageInfo messageInfo2 = messageService.saveMessageInfo(messageInfo);
						
						MsgMessageBox messageBox = new MsgMessageBox();
						messageBox.setMessageId(messageInfo2.getId());
						messageBox.setUserId(u.getId());
						messageBox.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
						messageBox.setReceiveDate(new Date());
						messageBox.setReadFlag(DictionaryUtil.MESSAGE_UNREAD);
						messageBox.setFlag(DictionaryUtil.DETELE_FLAG_00);
						messageService.saveMessageBox(messageBox);
					}
				}
			}
		}
		
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
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' order by createTime desc ");
		if(list.size()>0){
			for(PubPraise praise:list){
				JSONObject json = new JSONObject();
				AcUser user = accountService.loadUser(praise.getUserId());
				if(user != null){
					json.put("id", user.getId());
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
		String id = valueMap.get("id");	//公告id
		String page = valueMap.get("page");
		int rows = 50;
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' order by createTime desc ", Integer.parseInt(page), rows);
		if(list.size()>0){
			for(PubComments comment:list){
				JSONObject json = new JSONObject();
				json.put("content", comment.getContent());
				String createTime = DateFormatUtil.dateToStringM(comment.getCreateTime());
				json.put("createTime", createTime);
				AcUser user = accountService.loadUser(comment.getUserId());
				if(user != null){
					json.put("id", user.getId());
					json.put("name", user.getNickName());
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
		String accountType = valueMap.get("accountType");
		String description = valueMap.get("description");
		
		Reports report = new Reports();
		
		report.setReleaseId(Long.parseLong(id));
		report.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
		report.setDescription(description);
		report.setUserId(Long.parseLong(userId));
		report.setAccountType(accountType);
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
	
}
