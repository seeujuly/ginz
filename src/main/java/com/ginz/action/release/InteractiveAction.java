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
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcUser;
import com.ginz.model.AcUserDetail;
import com.ginz.model.Picture;
import com.ginz.model.PubComments;
import com.ginz.model.PubInteractive;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.InteractiveService;
import com.ginz.service.PictureService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.base.ThumbnailUtil;
import com.ginz.util.push.PushIOS;

//互动积分
@Namespace("/")
@Action(value = "interactiveAction")
public class InteractiveAction extends BaseAction{

	private InteractiveService interactiveService;
	private AccountService accountService;
	private ReplyService replyService;
	private PictureService pictureService;

	public InteractiveService getInteractiveService() {
		return interactiveService;
	}

	@Autowired
	public void setInteractiveService(InteractiveService interactiveService) {
		this.interactiveService = interactiveService;
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
	
	public PictureService getPictureService() {
		return pictureService;
	}

	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}

	//发布积分互动信息
	@SuppressWarnings("unchecked")
	public void releaseInteractive() throws IOException{
		
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
		String content = valueMap.get("content");
		String place = valueMap.get("place");
		String cost = valueMap.get("cost");
		String startTime = valueMap.get("startTime");
		String endTime = valueMap.get("endTime");
		String limit = valueMap.get("limit");
		String label = valueMap.get("label");
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		
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
					String dir = DictionaryUtil.PIC_RELEASE_INTERACTIVE;
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
		
		if(user!=null){
			PubInteractive interactive = new PubInteractive();
			interactive.setUserId(Long.parseLong(userId));
			interactive.setSubject(subject);
			interactive.setContent(content);
			interactive.setLabel(label);
			interactive.setPlace(place);
			interactive.setCost(cost);
			interactive.setNumberLimit(limit);
			interactive.setPicIds(picIds);
			interactive.setStatus(DictionaryUtil.RELEASE_MSG_STATE_00);
			interactive.setCreateTime(new Date());
			if(startTime!=null&&!startTime.equals("")){
				interactive.setStartTime(DateFormatUtil.toDate(startTime));
			}
			if(endTime!=null&&!endTime.equals("")){
				interactive.setEndTime(DateFormatUtil.toDate(endTime));
			}
			interactive.setFlag(DictionaryUtil.DETELE_FLAG_00);
			interactiveService.saveInteractive(interactive);
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
		//推送给匹配的用户
		/*List<String> accountList = new ArrayList<String>();
		List<AcUser> userList = accountService.findUser(" and communityId = " + communityId);
		if(userList.size()>0){
			for(AcUser user:userList){
				String account =  user.getDeviceAccount();
				accountList.add(account);
			}
		}
		PushIOS.pushAccountList(subject, accountList);*/
		
	}
	
	//删除积分互动信息
	public void deleteInteractive() throws IOException{
		
	}
	
	//个人用户获取积分互动信息列表
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getInteractiveList() throws IOException{
		
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
		
		AcUser user = new AcUser();
		if(userId!=null&&!userId.equals("")){
			user = accountService.loadUser(Long.parseLong(userId));
		}
		
		if(user!=null){
			if(Integer.parseInt(page)>1){
				rows = 5;
			}
			
			String valueString = getHobbies(Long.parseLong(userId));
			String in = valueString.substring(0, valueString.indexOf("|"));;
			in = in.replace(",", "|");
			String notIn = valueString.substring(valueString.indexOf("|")+1,valueString.length());
			notIn = notIn.replace(",", "|");
			
			HashMap<String,Object> rethm = interactiveService.seachInteractive(in, notIn, Integer.parseInt(page), rows);
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
					jsonArray.add(json);
				}
				
				jsonObject.put("result", "1");
				jsonObject.put("page", page);
				jsonObject.put("value", jsonArray);
			}else{
				jsonObject.put("result", "2");
				jsonObject.put("page", page);
				jsonObject.put("value", "没有更多的活动信息!");
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
	
	//个人用户获取积分互动信息详细内容
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
		String id = valueMap.get("id");	//积分互动信息id
		
		JSONObject json = new JSONObject();
		
		PubInteractive interactive = interactiveService.loadInteractive(Long.parseLong(id));
		if(interactive != null){
			json = JSONObject.fromObject(interactive);
			String startTime = DateFormatUtil.dateToStringM(interactive.getStartTime());
			json.remove("startTime");
			json.put("startTime", startTime);
			AcUser user = accountService.loadUser(interactive.getUserId());
			if(user!=null){
				json.put("name", user.getNickName());
				json.put("headUrl", user.getThumbnailUrl());
			}
			String picIds = interactive.getPicIds();
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
	
	//活动报名(点击报名参加)
	@SuppressWarnings("unchecked")
	public void signUpActivities() throws IOException {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//报名用户id
		String id = valueMap.get("id");	//活动id
		
		PubInteractive interactive = interactiveService.loadInteractive(Long.parseLong(id));
		if(interactive != null){
			if(interactive.getStatus().equals("0")){
				String signUp = interactive.getSignUp();
				if(signUp.equals("")){
					signUp += userId;
				}else{
					signUp += "," + userId;
				}
				interactive.setSignUp(signUp);
				interactiveService.saveInteractive(interactive);
				
				jsonObject.put("result", "1");
				jsonObject.put("value", "SUCCESS!");
			}else if(interactive.getStatus().equals("1")){
				jsonObject.put("result", "2");
				jsonObject.put("value", "该活动参加人数已满！");
			}else if(interactive.getStatus().equals("2")){
				jsonObject.put("result", "3");
				jsonObject.put("value", "该活动已经结束！");
			}
		}
		out.print(jsonObject.toString());
		
	}
	
	//确定参加活动操作(通过参加活动的申请)
	@SuppressWarnings("unchecked")
	public void comfirmActivity() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//报名用户id
		String id = valueMap.get("id");	//活动id
		
		PubInteractive interactive = interactiveService.loadInteractive(Long.parseLong(id));
		if(interactive != null){
			if(interactive.getStatus().equals("0")){
				String joinIn = interactive.getJoinIn();
				if(joinIn.equals("")){
					joinIn += userId;
				}else{
					joinIn += "," + userId;
				}
				/*String[] ids = joinIn.split(",");
				if(ids.length==Integer.parseInt(interactive.getLimit())){
					interactive.setStatus("1");
				}*/
				interactive.setJoinIn(joinIn);
				interactiveService.saveInteractive(interactive);
				
				jsonObject.put("result", "1");
				jsonObject.put("value", "SUCCESS!");
			}else if(interactive.getStatus().equals("1")){
				jsonObject.put("result", "2");
				jsonObject.put("value", "该活动参加人数已满！");
			}else if(interactive.getStatus().equals("2")){
				jsonObject.put("result", "3");
				jsonObject.put("value", "该活动已经结束！");
			}
		}
		out.print(jsonObject.toString());
		
	}
	
	//对积分互动信息点赞
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
		String id = valueMap.get("id");	//积分互动信息id
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' and userId = " + userId);
		if(list.size()>0){	//判断是否已赞过..
			jsonObject.put("result", "2");
			jsonObject.put("value", "您已赞过!");
		}else{
			PubPraise praise = new PubPraise();
			praise.setReleaseId(Long.parseLong(id));
			praise.setReleaseType(DictionaryUtil.RELEASE_TYPE_02);
			praise.setUserId(Long.parseLong(userId));
			praise.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
			praise.setCreateTime(new Date());
			praise.setFlag(DictionaryUtil.DETELE_FLAG_00);
			replyService.savePraise(praise);
			
			int num = replyService.countPraise(" and releaseId = " + Long.parseLong(id) + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
			jsonObject.put("result", "1");
			jsonObject.put("value", "SUCCESS!");
			jsonObject.put("number", num);	//更新点赞数量
			
			//发推送消息给发布该积分互动信息的个人用户
			PubInteractive interactive = interactiveService.loadInteractive(Long.parseLong(id));
			if(interactive != null){
				Long uId = interactive.getUserId();
				AcUser u = accountService.loadUser(uId);
				if(u!=null){
					AcUser user = accountService.loadUser(Long.parseLong(userId));
					if(user != null){
						PushIOS.pushSingleDevice(user.getNickName() + "赞了你的信息", u.getDeviceToken());	//通知个人用户有人点赞..
						
					}
				}
			}
		}
		out.print(jsonObject.toString());
		
	}
	
	//对积分互动信息发表评论
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
		
		//发推送消息给发布该积分互动信息的个人用户
		PubInteractive interactive = interactiveService.loadInteractive(Long.parseLong(id));
		if(interactive != null){
			Long uId = interactive.getUserId();
			AcUser u = accountService.loadUser(uId);
			if(u!=null){
				AcUser user = accountService.loadUser(Long.parseLong(userId));
				if(user != null){
					PushIOS.pushSingleDevice(user.getNickName() + "评论了你的信息", u.getDeviceToken());	//通知个人用户有人评论
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
		int rows = 5;
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "' order by createTime desc ", Integer.parseInt(page), rows);
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
