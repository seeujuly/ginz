package com.ginz.action.release;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.Picture;
import com.ginz.model.PubComments;
import com.ginz.model.PubNotice;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.CommunityService;
import com.ginz.service.NoticeService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.push.PushIOS;

@Namespace("/")
@Action(value = "noticeAction")
public class NoticeAction extends BaseAction{

	private NoticeService noticeService;
	private AccountService accountService;
	private ReplyService replyService;
	private CommunityService communityService;
	private PictureService pictureService;

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
		String id = valueMap.get("id");	//社区用户id
		String communityId = valueMap.get("communityId");
		String subject = valueMap.get("subject");
		String content = valueMap.get("content");
		String startTime = valueMap.get("startTime");
		String endTime = valueMap.get("endTime");
		
		AcProperty property = accountService.loadProperty(Long.parseLong(id));
		
		String picIds = "";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date nowDate = new Date();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;  
		String[] fileNames = wrapper.getFileNames("images");
		File[] files = wrapper.getFiles("images");
		String ext = "";
		//String path = request.getSession().getServletContext().getRealPath("/upload/");
		String path = "F:/upload";
		if(files.length>0&&fileNames.length>0){
			for(int i=0;i<files.length;i++){
				ext = fileNames[i].substring(fileNames[i].lastIndexOf("."), fileNames[i].length());
				String filePath = path + "/" + sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
				FileUtils.copyFile(files[i], new File(filePath)); 
				
				Picture picture = new Picture();
				picture.setUrl(filePath);
				picture.setAccountType(DictionaryUtil.ACCOUNT_TYPE_02);
				picture.setUserId(property.getId());
				picture.setIsHeadPortrait(DictionaryUtil.STATE_NO);
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
		
		PubNotice notice = new PubNotice();
		notice.setPropertyId(Long.parseLong(id));
		notice.setCommunityId(Long.parseLong(communityId));
		notice.setSubject(subject);
		notice.setContent(content);
		notice.setPicIds(picIds);
		notice.setCreateTime(nowDate);
		notice.setStartTime(DateFormatUtil.toDate(startTime));
		notice.setEndTime(DateFormatUtil.toDate(endTime));
		notice.setFlag(DictionaryUtil.DETELE_FLAG_00);
		noticeService.saveNotice(notice);
			
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
		//推送给该社区内的所有用户
		List<String> accountList = new ArrayList<String>();
		List<AcUser> userList = accountService.findUser(" and communityId = " + communityId);
		if(userList.size()>0){
			for(AcUser user:userList){
				String account =  user.getDeviceAccount();
				accountList.add(account);
			}
		}
		PushIOS.pushAccountList(subject, accountList);
		
	}
	
	//删除公告
	public void deleteNotice() throws IOException{
		
		
		
	}
	
	//个人用户获取公告列表
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
		String userId = valueMap.get("userId");	//个人用户id
		String page = valueMap.get("page");
		int rows = 10;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		
		if(user!=null){
			Long communityId = user.getCommunityId();
			if(communityId!=null&&communityId!=0){
				List<PubNotice> noticeList = noticeService.findNotice(" and communityId = " + communityId + " order by createTime desc ", Integer.parseInt(page), rows);
				if(noticeList.size()>0){
					for(PubNotice notice:noticeList){
						int praiseNum = replyService.countPraise(" and releaseId = " + notice.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
						int commentNum = replyService.countComment(" and releaseId = " + notice.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
						//JSONObject json = JSONObject.fromObject(notice);
						JSONObject json = new JSONObject();
						json.put("id", notice.getId());
						json.put("subject", notice.getSubject());
						String picIds = notice.getPicIds();
						if(picIds!=null&&picIds.equals("")){
							String[] ids = picIds.split(",");
							json.put("picUrl", ids[0]);
						}
						AcProperty property = accountService.loadProperty(notice.getPropertyId());
						if(property != null){
							json.put("name", property.getPropertyName());
						}
						json.put("praiseNum", praiseNum);
						json.put("commentNum", commentNum);
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
			}else{
				jsonObject.put("result", "3");
				jsonObject.put("page", page);
				jsonObject.put("value", "您还未添加社区信息!");
			}
			
		}
		out.print(jsonObject.toString());
		
	}
	
	//个人用户获取公告详细内容
	@SuppressWarnings({ "unchecked", "static-access" })
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
		
		JSONObject jsonObject=new JSONObject();
		
		PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
		if(notice != null){
			jsonObject.fromObject(notice);
		}
		
		out.print(jsonObject.toString());
		
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
		String userId = valueMap.get("userId");	//点赞操作的用户id-个人用户
		String id = valueMap.get("id");	//公告id
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' and userId = " + userId);
		if(list.size()>0){	//判断是否已赞过..
			jsonObject.put("result", "2");
			jsonObject.put("value", "您已赞过!");
		}else{
			PubPraise praise = new PubPraise();
			praise.setReleaseId(Long.parseLong(id));
			praise.setReleaseType(DictionaryUtil.RELEASE_TYPE_01);
			praise.setUserId(Long.parseLong(userId));
			praise.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
			praise.setCreateTime(new Date());
			praise.setFlag(DictionaryUtil.DETELE_FLAG_00);
			replyService.savePraise(praise);
			
			int num = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
			jsonObject.put("result", "1");
			jsonObject.put("value", "SUCCESS!");
			jsonObject.put("number", num);	//更新点赞数量
			
			//发推送消息给发布该公告的社区用户
			PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
			if(notice != null){
				Long propertyId = notice.getPropertyId();
				AcProperty property = accountService.loadProperty(propertyId);
				if(property != null){
					AcUser user = accountService.loadUser(Long.parseLong(userId));
					if(user != null){
						PushIOS.pushSingleDevice(user.getNickName() + "赞了你的信息", property.getDeviceToken());	//通知社区用户有人点赞..
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
		String userId = valueMap.get("userId");	//发表评论的用户id-个人用户
		String id = valueMap.get("id");	//公告id
		String content = valueMap.get("content");
		
		PubComments comment = new PubComments();
		comment.setReleaseId(Long.parseLong(id));
		comment.setReleaseType(DictionaryUtil.RELEASE_TYPE_01);
		comment.setContent(content);
		comment.setUserId(Long.parseLong(userId));
		comment.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
		comment.setCreateTime(new Date());
		comment.setFlag(DictionaryUtil.DETELE_FLAG_00);
		replyService.saveComments(comment);
		
		int num = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		jsonObject.put("result", "1");
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("number", num);	//更新评论数量
		
		out.print(jsonObject.toString());
		
		//发推送消息给发布该公告的社区用户
		PubNotice notice = noticeService.loadNotice(Long.parseLong(id));
		if(notice != null){
			Long propertyId = notice.getPropertyId();
			AcProperty property = accountService.loadProperty(propertyId);
			if(property != null){
				AcUser user = accountService.loadUser(Long.parseLong(userId));
				if(user != null){
					PushIOS.pushSingleDevice(user.getNickName() + "评论了你的信息", property.getDeviceToken());	//通知社区用户有人评论..
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
		int rows = 5;
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "' order by createTime desc ", Integer.parseInt(page), rows);
		if(list.size()>0){
			for(PubComments comment:list){
				JSONObject json = new JSONObject();
				json.put("content", comment.getContent());
				json.put("createTime", comment.getCreateTime());
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
	
}
