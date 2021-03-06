package com.ginz.action.release;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.ginz.model.AcMerchant;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.Picture;
import com.ginz.model.PubComments;
import com.ginz.model.PubNotice;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.CommunityService;
import com.ginz.service.MessageService;
import com.ginz.service.NoticeService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.base.StringUtil;
import com.ginz.util.base.ThumbnailUtil;
import com.ginz.util.push.PushIOS;

//公告
@Namespace("/")
@Action(value = "noticeAction")
public class NoticeAction extends BaseAction{

	private NoticeService noticeService;
	private AccountService accountService;
	private ReplyService replyService;
	private CommunityService communityService;
	private PictureService pictureService;
	private MessageService messageService;

	public NoticeService getNoticeService() {
		return noticeService;
	}

	@Autowired
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
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
	
	public CommunityService getCommunityService() {
		return communityService;
	}

	@Autowired
	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
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
	
	//发布公告
	@SuppressWarnings("unchecked")
	public void releaseNotice() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//社区用户id
		String communityId = valueMap.get("communityId");
		String subject = valueMap.get("subject");
		String startTime = valueMap.get("startTime");
		String endTime = valueMap.get("endTime");
		String isOpen = valueMap.get("isOpen");
		String sendTime = valueMap.get("sendTime");
		
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
			//String path = request.getSession().getServletContext().getRealPath("/upload/");
			
			if(files.length>0&&fileNames.length>0){
				for(int i=0;i<files.length;i++){
					ext = fileNames[i].substring(fileNames[i].lastIndexOf("."), fileNames[i].length());
					String fileName = sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
					String dir = DictionaryUtil.PIC_RELEASE_NOTICE;
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
		
		PubNotice notice = new PubNotice();
		notice.setPropertyId(userId);
		if(StringUtils.isNotEmpty(communityId)){
			notice.setCommunityId(Long.parseLong(communityId));
		}
		notice.setSubject(subject);
		notice.setPicIds(picIds);
		notice.setCreateTime(nowDate);
		if(startTime!=null&&!startTime.equals("")){
			notice.setStartTime(DateFormatUtil.toDate(startTime));
		}
		if(endTime!=null&&!endTime.equals("")){
			notice.setEndTime(DateFormatUtil.toDate(endTime));
		}
		notice.setFlag(DictionaryUtil.DETELE_FLAG_00);
		PubNotice notice2 = noticeService.saveNotice(notice);
			
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
		//推送给该社区内的所有用户
		if(StringUtils.equals(isOpen, "1")){	//是否公开：0不公开，1公开
			List<String> accountList = new ArrayList<String>();
			List<AcUser> userList = accountService.findUser(" and communityId = " + communityId);
			if(userList.size()>0){
				for(AcUser user:userList){
					if(user.getDeviceToken()!=null&&!user.getDeviceToken().equals("")){
						String account =  user.getDeviceAccount();
						accountList.add(account);
					}
					
					//发送系统消息给目标用户
					messageService.sendMessage(null, user.getUserId(), subject, subject, notice2.getId(),DictionaryUtil.RELEASE_TYPE_01, DictionaryUtil.MESSAGE_TYPE_PUSH);

				}
				Map<String,Object> keyMap = new HashMap<String, Object>();
				keyMap.put("id", notice2.getId());
				keyMap.put("userId", userId);
				keyMap.put("accountType", DictionaryUtil.ACCOUNT_TYPE_02);
				PushIOS.pushAccountList(subject, keyMap, accountList,sendTime);
			}
		}
		
	}
	
	//删除公告
	@SuppressWarnings("unchecked")
	public void deleteNotice() throws IOException{
		
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
		String userId = valueMap.get("userId");	//社区用户id
		
		PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
		if(notice != null){
			if(StringUtils.equals(notice.getPropertyId(),userId)){
				noticeService.deleteNotice(Long.parseLong(id));	//删除个人动态信息
				
				List<PubPraise> praiseList = replyService.findPraise(" and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' and releaseId = " + notice.getId());
				if(praiseList.size()>0){	//删除个人动态信息的点赞
					for(PubPraise praise:praiseList){
						replyService.deletePraise(praise.getId());
					}
				}
				List<PubComments> commentsList = replyService.findComments(" and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' and releaseId = " + notice.getId());
				if(commentsList.size()>0){	//删除个人动态信息的相关评论
					for(PubComments comment:commentsList){
						replyService.deleteComments(comment.getId());
					}
				}
				
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");   
				Properties p = new Properties();   
				p.load(inputStream);   
				String path = p.getProperty("server_dir");	//读取服务器上图片存放目录
				
				String picIds = notice.getPicIds();
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
	
	//获取公告列表
	@SuppressWarnings("unchecked")
	public void getNoticeList() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String page = valueMap.get("page");
		int rows = 20;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		String condition = "";
		if(StringUtils.equals(userId.substring(0, 1), "u")){
			AcUser user = accountService.loadUser(userId);
			if(user!=null){
				Long communityId = user.getCommunityId();
				if(communityId!=null&&communityId!=0){
					condition = " and communityId = " + communityId + " order by createTime desc ";
				}else{
					jsonObject.put("result", "3");
					jsonObject.put("page", page);
					jsonObject.put("value", "您还未添加社区信息!");
				}
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			AcProperty property = accountService.loadProperty(userId);
			if(property!=null){
				condition += " and propertyId = '" + userId + "' order by createTime desc ";
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			//AcMerchant merchant = accountService.loadMerchant(userId);
			
		}
	
		List<PubNotice> noticeList = noticeService.findNotice(condition, Integer.parseInt(page), rows);
		if(noticeList.size()>0){
			for(PubNotice notice:noticeList){
				int praiseNum = replyService.countPraise(" and releaseId = " + notice.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
				int commentNum = replyService.countComment(" and releaseId = " + notice.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
				JSONObject json = new JSONObject();
				json.put("id", notice.getId());
				json.put("subject", notice.getSubject());
				json.put("createTime", DateFormatUtil.dateToStringS(notice.getCreateTime()));
				String picIds = notice.getPicIds();
				if(picIds!=null&&!picIds.equals("")){
					String[] ids = picIds.split(",");
					if(ids.length>0){
						Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
						if(picture!=null){
							json.put("picUrl", picture.getUrl());
						}
					}
				}
				AcProperty property = accountService.loadProperty(notice.getPropertyId());
				if(property != null){
					json.put("userId", property.getUserId());
					json.put("name", property.getPropertyName());
					json.put("headUrl", property.getPicUrl());
				}
				json.put("praiseNum", praiseNum+"");
				json.put("commentNum", commentNum+"");
				List<PubPraise> listPraise = replyService.findPraise(" and releaseId = " + notice.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' and userId = '" + userId + "'");
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
			jsonObject.put("value", "没有更多的公告信息!");
		}
			
		out.print(jsonObject.toString());
		
	}
	
	//获取公告详细内容
	@SuppressWarnings("unchecked")
	public void getNoticeDetail() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//公告id
		String userId = valueMap.get("userId");	//用户id
		
		JSONObject json = new JSONObject();
		PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
		if(notice != null){
			json = JSONObject.fromObject(notice);
			String createTime = DateFormatUtil.dateToStringM(notice.getCreateTime());
			json.remove("createTime");
			json.put("createTime", createTime);
			AcProperty property = accountService.loadProperty(notice.getPropertyId());
			if(property!=null){
				json.put("name", property.getPropertyName());
				json.put("headUrl", property.getPicUrl());
			}
			
			int praiseNum = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
			int commentNum = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
			json.put("praiseNum", praiseNum+"");
			json.put("commentNum", commentNum+"");
			
			List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' and userId = '" + userId + "'");
			if(list.size()>0){	//判断是否已赞过..
				json.put("isPraise", "1");
			}else{
				json.put("isPraise", "0");
			}
			String picIds = notice.getPicIds();
			if(picIds!=null&&!picIds.equals("")){
				String[] ids = picIds.split(",");
				if(ids.length>0){
					for(int i=0;i<ids.length;i++){
						Picture picture = pictureService.loadPicture(Long.parseLong(ids[i]));
						if(picture!=null){
							json.put("image"+(i+1), picture.getUrl());
						}
					}
					
				}
			}
		}
		
		out.print(json.toString());
		
	}
	
	//对公告点赞
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
		String userId = valueMap.get("userId");	//点赞操作的用户id
		String id = valueMap.get("id");	//公告id
		
		int num = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' and userId = '" + userId + "'");
		if(list.size()>0){	//判断是否已赞过..删除点赞记录(取消点赞)
			replyService.deletePraise(list.get(0).getId());
			num--;
			jsonObject.put("result", "2");
			jsonObject.put("number", num+"");	//更新点赞数量
		}else{
			PubPraise praise = new PubPraise();
			praise.setReleaseId(Long.parseLong(id));
			praise.setReleaseType(DictionaryUtil.RELEASE_TYPE_01);
			praise.setUserId(userId);
			praise.setCreateTime(new Date());
			praise.setFlag(DictionaryUtil.DETELE_FLAG_00);
			replyService.savePraise(praise);
			
			num++;
			jsonObject.put("result", "1");
			jsonObject.put("number", num+"");	//更新点赞数量
			
			//发推送消息给发布该公告的社区用户
			PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
			if(notice != null){
				String propertyId = notice.getPropertyId();
				if(!userId.equals(propertyId)){	//如果是本人点赞，不发推送消息
					AcProperty property = accountService.loadProperty(propertyId);
					AcUser user = accountService.loadUser(userId);	//点赞的用户
					if(property != null){
						String value = "赞了你的信息!";
						
						//发送推送给目标用户
						if(property.getDeviceToken()!=null&&!property.getDeviceToken().equals("")){
							PushIOS.pushSingleDevice(user.getNickName() + value, property.getDeviceToken());	//通知社区用户有人点赞..
						}
						
						//发送系统通知给目标用户
						messageService.sendMessage(userId, propertyId, value, value, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_01, DictionaryUtil.MESSAGE_TYPE_PRAISE);
					}
				}
			}
		}
		out.print(jsonObject.toString());
		
	}
	
	//对公告发表评论
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
		String userId = valueMap.get("userId");	//发表评论的用户id
		String id = valueMap.get("id");	//公告id
		String content = valueMap.get("content");
		
		PubComments comment = new PubComments();
		comment.setReleaseId(Long.parseLong(id));
		comment.setReleaseType(DictionaryUtil.RELEASE_TYPE_01);
		comment.setContent(content);
		comment.setUserId(userId);
		comment.setCreateTime(new Date());
		comment.setFlag(DictionaryUtil.DETELE_FLAG_00);
		replyService.saveComments(comment);
		
		int num = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		jsonObject.put("result", "1");
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("number", num+"");	//更新评论数量
		
		out.print(jsonObject.toString());
		
		//发推送消息给发布该公告的社区用户
		PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
		if(notice != null){
			String propertyId = notice.getPropertyId();
			if(!userId.equals(propertyId)){	//如果是本人评论，不发推送消息
				AcProperty property = accountService.loadProperty(propertyId);
				AcUser user = accountService.loadUser(userId);	//评论的用户
				if(property != null){
					String value = "评论了你的信息!";
					
					//发送推送给目标用户
					if(property.getDeviceToken()!=null&&!property.getDeviceToken().equals("")){
						PushIOS.pushSingleDevice(user.getNickName() + value, property.getDeviceToken());	//通知社区用户有人评论..
					}
					
					//发送系统通知给目标用户
					messageService.sendMessage(userId, propertyId, value, value, Long.parseLong(id), DictionaryUtil.RELEASE_TYPE_01, DictionaryUtil.MESSAGE_TYPE_COMMENTS);
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
		String id = valueMap.get("id");	//公告id
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		if(list.size()>0){
			for(PubPraise praise:list){
				JSONObject json = new JSONObject();
				String userId = praise.getUserId();
				if(StringUtils.isNotEmpty(userId)){
					if(StringUtils.equals(userId.substring(0, 1), "u")){
						AcUser user = accountService.loadUser(userId);
						if(user != null){
							json.put("userId", user.getUserId());
							json.put("name", user.getNickName());
							json.put("headUrl", user.getHeadPortrait());
						}
					}else if(StringUtils.equals(userId.substring(0, 1), "p")){
						AcProperty property = accountService.loadProperty(userId);
						if(property!=null){
							json.put("userId", property.getUserId());
							json.put("name", property.getPropertyName());
							json.put("headUrl", property.getPicUrl());
						}
					}else if(StringUtils.equals(userId.substring(0, 1), "m")){
						AcMerchant merchant = accountService.loadMerchant(userId);
						if(merchant!=null){
							json.put("userId", merchant.getUserId());
							json.put("name", merchant.getMerchantName());
							json.put("headUrl", merchant.getPicUrl());
						}
					}
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
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' order by createTime desc ", Integer.parseInt(page), rows);
		if(list.size()>0){
			for(PubComments comment:list){
				JSONObject json = new JSONObject();
				json.put("content", comment.getContent());
				String createTime = DateFormatUtil.dateToStringM(comment.getCreateTime());
				json.put("createTime", createTime);
				
				String userId = comment.getUserId();
				if(StringUtils.isNotEmpty(userId)){
					if(StringUtils.equals(userId.substring(0, 1), "u")){
						AcUser user = accountService.loadUser(userId);
						if(user != null){
							json.put("userId", user.getUserId());
							json.put("name", user.getNickName());
							json.put("headUrl", user.getHeadPortrait());
						}
					}else if(StringUtils.equals(userId.substring(0, 1), "p")){
						AcProperty property = accountService.loadProperty(userId);
						if(property!=null){
							json.put("userId", property.getUserId());
							json.put("name", property.getPropertyName());
							json.put("headUrl", property.getPicUrl());
						}
					}else if(StringUtils.equals(userId.substring(0, 1), "m")){
						AcMerchant merchant = accountService.loadMerchant(userId);
						if(merchant!=null){
							json.put("userId", merchant.getUserId());
							json.put("name", merchant.getMerchantName());
							json.put("headUrl", merchant.getPicUrl());
						}
					}
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
	
}
