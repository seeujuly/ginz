package com.ginz.action.release;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcUser;
import com.ginz.model.Picture;
import com.ginz.model.PubComments;
import com.ginz.model.PubPersonalStatus;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.PersonalStatusService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.push.PushIOS;

@Namespace("/")
@Action(value = "interactiveAction")
public class PersonalStatusAction extends BaseAction {

	private AccountService accountService;
	private ReplyService replyService;
	private PersonalStatusService personalStatusService;
	private PictureService pictureService;
	
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
	
	public PersonalStatusService getPersonalStatusService() {
		return personalStatusService;
	}

	@Autowired
	public void setPersonalStatusService(PersonalStatusService personalStatusService) {
		this.personalStatusService = personalStatusService;
	}
	
	public PictureService getPictureService() {
		return pictureService;
	}

	@Autowired
	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}

	//发布个人动态信息
	@SuppressWarnings("unchecked")
	public void release() throws IOException{
		
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
		
		AcUser user = accountService.loadUser(Long.parseLong(id));
		
		String picIds = "";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date nowDate = new Date();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;  
		String[] fileNames = wrapper.getFileNames("images");
		File[] files = wrapper.getFiles("images");
		String ext = "";
		
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");   
		Properties p = new Properties();   
		p.load(inputStream);   
		String serverUrl = p.getProperty("server_path");
		String path = p.getProperty("server_dir");
		//String path = request.getSession().getServletContext().getRealPath("/upload/");
		
		if(user!=null){
			if(files.length>0&&fileNames.length>0){
				for(int i=0;i<files.length;i++){
					ext = fileNames[i].substring(fileNames[i].lastIndexOf("."), fileNames[i].length());
					String fileName = sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
					String dir = DictionaryUtil.PIC_RELEASE_PERSONALSTATUS;
					FileUtils.copyFile(files[i], new File(path + dir + fileName)); 
					
					Picture picture = new Picture();
					picture.setUrl(serverUrl + dir + fileName);
					picture.setFileName(fileName);
					picture.setAccountType(DictionaryUtil.ACCOUNT_TYPE_02);
					picture.setUserId(user.getId());
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
		
			PubPersonalStatus personalStatus = new PubPersonalStatus();
			personalStatus.setUserId(Long.parseLong(id));
			personalStatus.setSubject(subject);
			personalStatus.setContent(content);
			personalStatus.setPicIds(picIds);
			personalStatus.setCreateTime(new Date());
			personalStatus.setFlag(DictionaryUtil.DETELE_FLAG_00);
			personalStatusService.savePersonalStatus(personalStatus);
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//删除个人动态信息
	@SuppressWarnings("unchecked")
	public void deleteInteractive() throws IOException{
		
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
		
		PubPersonalStatus personalStatus = personalStatusService.loadPersonalStatus(Long.parseLong(id));
		if(personalStatus != null){
			if(StringUtils.equals(personalStatus.getUserId().toString(),userId)){
				personalStatusService.deletePersonalStatus(Long.parseLong(id));
			}
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//个人用户获取个人动态信息列表
	@SuppressWarnings("unchecked")
	public void getInteractiveList() throws IOException{
		
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
			List<PubPersonalStatus> personalStatusList = personalStatusService.findPersonalStatus(" and userId = " + userId + " order by createTime desc ", Integer.parseInt(page), rows);
			
			if(personalStatusList.size()>0){
				for(PubPersonalStatus personalStatus:personalStatusList){
					int praiseNum = replyService.countPraise(" and releaseId = " + personalStatus.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "'");
					int commentNum = replyService.countComment(" and releaseId = " + personalStatus.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "'");
					JSONObject json = new JSONObject();
					json.put("id", personalStatus.getId());
					json.put("subject", personalStatus.getSubject());
					String picIds = personalStatus.getPicIds();
					String[] ids = picIds.split(",");
					json.put("picUrl", ids[0]);
					AcUser u = accountService.loadUser(personalStatus.getUserId());
					if(u != null){
						json.put("name", u.getNickName());
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
				jsonObject.put("value", "没有更多的积分互动信息!");
			}
			
		}
		out.print(jsonObject.toString());
		
	}
	
	//个人用户获取个人动态信息详细内容
	@SuppressWarnings("unchecked")
	public void getInteractiveDetail() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//个人动态信息id
		
		JSONObject json = new JSONObject();
		
		PubPersonalStatus personalStatus = personalStatusService.loadPersonalStatus(Long.parseLong(id));
		if(personalStatus != null){
			json = JSONObject.fromObject(personalStatus);
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
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "' and userId = " + userId);
		if(list.size()>0){	//判断是否已赞过..
			jsonObject.put("result", "2");
			jsonObject.put("value", "您已赞过!");
		}else{
			PubPraise praise = new PubPraise();
			praise.setReleaseId(Long.parseLong(id));
			praise.setReleaseType(DictionaryUtil.RELEASE_TYPE_00);
			praise.setUserId(Long.parseLong(userId));
			praise.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
			praise.setCreateTime(new Date());
			praise.setFlag(DictionaryUtil.DETELE_FLAG_00);
			replyService.savePraise(praise);
			
			int num = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "'");
			jsonObject.put("result", "1");
			jsonObject.put("value", "SUCCESS!");
			jsonObject.put("number", num);	//更新点赞数量
			
			//发推送消息给发布该个人动态信息的个人用户
			PubPersonalStatus personalStatus = personalStatusService.loadPersonalStatus(Long.parseLong(id));
			if(personalStatus != null){
				Long uId = personalStatus.getUserId();
				AcUser user = accountService.loadUser(uId);
				if(user != null){
					PushIOS.pushSingleDevice(user.getNickName() + "赞了你的信息", user.getDeviceToken());	//通知个人用户有人点赞..
					
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
		comment.setReleaseType(DictionaryUtil.RELEASE_TYPE_00);
		comment.setContent(content);
		comment.setUserId(Long.parseLong(userId));
		comment.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
		comment.setCreateTime(new Date());
		comment.setFlag(DictionaryUtil.DETELE_FLAG_00);
		replyService.saveComments(comment);
		
		int num = replyService.countComment(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "'");
		jsonObject.put("result", "1");
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("number", num);	//更新评论数量
		
		out.print(jsonObject.toString());
		
		//发推送消息给发布该个人动态信息的个人用户
		PubPersonalStatus personalStatus = personalStatusService.loadPersonalStatus(Long.parseLong(id));
		if(personalStatus != null){
			Long uId = personalStatus.getUserId();
			AcUser user = accountService.loadUser(uId);
			if(user != null){
				PushIOS.pushSingleDevice(user.getNickName() + "评论了你的信息", user.getDeviceToken());	//通知个人用户有人评论
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
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "' order by createTime desc ");
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
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_00 + "' order by createTime desc ", Integer.parseInt(page), rows);
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
