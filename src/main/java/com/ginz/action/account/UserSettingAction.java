package com.ginz.action.account;

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
import com.ginz.model.AcMerchant;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.AcUserDetail;
import com.ginz.model.MsgFriend;
import com.ginz.model.Picture;
import com.ginz.service.AccountService;
import com.ginz.service.EventService;
import com.ginz.service.FriendService;
import com.ginz.service.PictureService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.base.ThumbnailUtil;

@Namespace("/")
@Action(value = "userSettingAction")
public class UserSettingAction extends BaseAction {

	private AccountService accountService;
	private PictureService pictureService;
	private EventService eventService;
	private FriendService friendService;

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
	
	public EventService getEventService() {
		return eventService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	
	public FriendService getFriendService() {
		return friendService;
	}

	@Autowired
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}

	//上传头像
	@SuppressWarnings("unchecked")
	public void upload() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		
		AcUser user = accountService.loadUser(userId);
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date nowDate = new Date();
		
		MultiPartRequestWrapper wrapper = null;
		
		//判断http body中是否存在file，存在file才可转为(MultiPartRequestWrapper)request
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
					String fileName = "h" + sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
					String dir = DictionaryUtil.PIC_HEAD_PORTRAIT;
					String thumbnailDir = DictionaryUtil.PIC_THUMBNAIL;
					FileUtils.copyFile(files[i], new File(path + dir + fileName)); 
					
					ThumbnailUtil ccc = new ThumbnailUtil(path + dir + fileName, path + thumbnailDir + dir + fileName);
					ccc.resize(Integer.parseInt(resize),Integer.parseInt(resize));
					
					Picture picture = new Picture();
					picture.setUrl(serverUrl + dir + fileName);
					picture.setThumbnailUrl(serverUrl + thumbnailDir + dir + fileName);
					picture.setFileName(fileName);
					picture.setUserId(userId);
					picture.setPicType(DictionaryUtil.PIC_TYPE_PORTRAIT);
					picture.setCreateTime(nowDate);
					picture.setFlag(DictionaryUtil.DETELE_FLAG_00);
					pictureService.savePicture(picture);
					
					user.setHeadPortrait(serverUrl + dir + fileName);
					user.setThumbnailUrl(serverUrl + thumbnailDir + dir + fileName);
					AcUser u = accountService.updateUser(user);
					
					jsonObject.put("headUrl", u.getHeadPortrait());
				}
			}
		}
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//上传背景图
	@SuppressWarnings("unchecked")
	public void uploadBG() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		
		AcUser user = accountService.loadUser(userId);
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date nowDate = new Date();
		
		MultiPartRequestWrapper wrapper = null;
		
		//判断http body中是否存在file，存在file才可转为(MultiPartRequestWrapper)request
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
			
			if(files.length>0&&fileNames.length>0){
				for(int i=0;i<files.length;i++){
					ext = fileNames[i].substring(fileNames[i].lastIndexOf("."), fileNames[i].length());
					String fileName = "b" + sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
					String dir = DictionaryUtil.PIC_BACKGROUND;
					FileUtils.copyFile(files[i], new File(path + dir + fileName)); 
					
					Picture picture = new Picture();
					picture.setUrl(serverUrl + dir + fileName);
					picture.setFileName(fileName);
					picture.setUserId(userId);
					picture.setPicType(DictionaryUtil.PIC_TYPE_BG);
					picture.setCreateTime(nowDate);
					picture.setFlag(DictionaryUtil.DETELE_FLAG_00);
					pictureService.savePicture(picture);
					
					user.setBackground(serverUrl + dir + fileName);
					AcUser u = accountService.updateUser(user);
					
					jsonObject.put("bgUrl", u.getBackground());
				}
			}
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//初始化个人基本信息页面
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() throws IOException{
		
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		
		String name = "";
		String headUrl = "";
		String bgUrl = "";
		
		//判断所要查看的目标用户的账户类型
		AcUser user = null;
		AcProperty property = null;
		AcMerchant merchant = null;
		if(StringUtils.equals(tUserId.substring(0, 1), "u")){
			user = accountService.loadUser(tUserId);
			if(user!=null){
				name = user.getNickName();
				headUrl = user.getHeadPortrait();
				bgUrl = user.getBackground();
			}
		}else if(StringUtils.equals(tUserId.substring(0, 1), "p")){
			property = accountService.loadProperty(tUserId);
			if(property!=null){
				name = property.getPropertyName();
				headUrl = property.getPicUrl();
				bgUrl = property.getBackground();
			}
		}else if(StringUtils.equals(tUserId.substring(0, 1), "m")){
			merchant = accountService.loadMerchant(tUserId);
			if(merchant!=null){
				name = merchant.getMerchantName();
				headUrl = merchant.getPicUrl();
				bgUrl = merchant.getBackground();	
			}
		}
		
		if(user!=null||property!=null||merchant!=null){
			jsonObject.put("result", "1");
			jsonObject.put("name", name);
			jsonObject.put("headUrl", headUrl);
			jsonObject.put("bgUrl", bgUrl);
			HashMap<String,Object> rethm = eventService.listRelease(tUserId);
			List<Object> list = (List<Object>) rethm.get("list");
			if(list != null && !list.isEmpty()){
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Object[] obj = (Object[]) iterator.next();
					JSONObject json = new JSONObject();
					json.put("id", String.valueOf(obj[0]==null?"":obj[0]));
					json.put("subject", String.valueOf(obj[1]==null?"":obj[1]));
					String picIds = String.valueOf(obj[2]==null?"":obj[2]);
					if(picIds!=null&&!picIds.equals("")){
						String[] ids = picIds.split(",");
						if(ids.length>0){
							Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
							if(picture!=null){
								json.put("picUrl", picture.getThumbnailUrl());
							}
						}
					}
					json.put("releaseType", String.valueOf(obj[3]==null?"":obj[3]));
					jsonArray.add(json);
					
				}
			}
			if(!StringUtils.equals(userId, tUserId)){	//查看自己的主页时不判断
				String condition = "";
				condition = "and (userId = '" + userId + "' and friend_userId = '" + tUserId + "') ";
				condition += "OR (userId = '" + tUserId + "' and friend_userId = '" + userId + "')";
				List<MsgFriend> friendList = friendService.listFriends(condition);
				if(friendList.size()>0){
					jsonObject.put("isFriend", "0");	//是好友关系
				}else{
					jsonObject.put("isFriend", "1");	//不是好友关系
				}
			}
			
			HashMap<String,Object> rethMap = friendService.getFriendMap(tUserId);
			jsonObject.put("friendNum", rethMap.get("cnt"));	//好友数
			jsonObject.put("releaseNum", rethm.get("cnt"));		//发布的消息数
			jsonObject.put("value", jsonArray);
		}else{
			
			jsonObject.put("result", "2");
			jsonObject.put("value", "查无此人或该用户已被封号");
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//初始化个人详细信息页面
	@SuppressWarnings({ "unchecked" })
	public void initDetail() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		
		String userId = valueMap.get("userId");	//用户id-唯一标识
		
		if(StringUtils.equals(userId.substring(0, 1), "u")){
			AcUser user = accountService.loadUser(userId);
			if(user!=null){
				AcUserDetail userDetail = accountService.loadUserDetail(userId);
				if(userDetail!=null){
					jsonObject = JSONObject.fromObject(userDetail);
					jsonObject.remove("createTime");
					jsonObject.remove("birthday");
					jsonObject.remove("workStart");
					jsonObject.remove("workEnd");
					String birthday = "";
					if(userDetail.getBirthday()!=null){
						birthday = DateFormatUtil.dateToString(userDetail.getBirthday());
					}
					jsonObject.put("birthday", birthday);
				}
				jsonObject.put("nickName", user.getNickName());
				jsonObject.put("email", user.getEmail());
				jsonObject.put("address", user.getAddress());
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			AcProperty property = accountService.loadProperty(userId);
			if(property!=null){
				jsonObject.put("nickName", property.getPropertyName());
				jsonObject.put("email", property.getEmail());
				jsonObject.put("address", property.getAddress());
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			AcMerchant merchant = accountService.loadMerchant(userId);
			if(merchant!=null){
				jsonObject.put("nickName", merchant.getMerchantName());
				jsonObject.put("email", merchant.getEmail());
				jsonObject.put("address", merchant.getAddress());
			}
		}
		jsonObject.put("result", "1");
		out.print(jsonObject.toString());
		
	}
	
	/**
	 * 保存个人信息
	 */
	//添加社区
	@SuppressWarnings("unchecked")
	public void saveCommunity() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String communityId = valueMap.get("communityId");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			user.setCommunityId(Long.parseLong(communityId));
			accountService.updateUser(user);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存邮箱
	@SuppressWarnings("unchecked")
	public void saveEmail() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String email = valueMap.get("email");
		
		List<AcUser> uList = accountService.findUser(" and email = '" + email + "' ");
		boolean flag;
		if(uList.size()>0){
			if(StringUtils.equals(userId, uList.get(0).getUserId())){
				flag = true;
			}else{
				flag = false;
			}
		}else{
			flag = true;
		}
		
		if(flag){
			AcUser user = accountService.loadUser(userId);
			if(user!=null){
				user.setEmail(email);
				accountService.updateUser(user);
			}
			jsonObject.put("value", "SUCCESS!");
		}else{
			jsonObject.put("value", "该邮箱已存在！");
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//保存昵称
	@SuppressWarnings("unchecked")
	public void saveNickName() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String nickName = valueMap.get("nickName");
		
		List<AcUser> uList = accountService.findUser(" and nickName = '" + nickName + "' ");
		boolean flag;
		if(uList.size()>0){
			if(StringUtils.equals(userId, uList.get(0).getUserId())){
				flag = true;
			}else{
				flag = false;
			}
		}else{
			flag = true;
		}

		if(flag){
			AcUser user = accountService.loadUser(userId);
			if(user!=null){
				user.setNickName(nickName);
				accountService.updateUser(user);
			}
			jsonObject.put("value", "SUCCESS!");
		}else{
			jsonObject.put("value", "该昵称已存在！");
		}
		out.print(jsonObject.toString());
		
	}
	
	//保存地址
	@SuppressWarnings("unchecked")
	public void saveAddress() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String address = valueMap.get("address");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			user.setAddress(address);
			accountService.updateUser(user);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}

	//保存性别
	@SuppressWarnings("unchecked")
	public void saveGender() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String gender = valueMap.get("gender");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setGender(gender);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存生日，年龄，星座
	@SuppressWarnings("unchecked")
	public void saveBirthDay() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		
		String userId = valueMap.get("userId");
		String birthday = valueMap.get("birthday");
		
		//根据生日计算星座
		Date date = DateFormatUtil.stringToDate(birthday);
		int month = DateFormatUtil.getMonth(date);
		int day = DateFormatUtil.getDay(date);
		String constellation = DateFormatUtil.getAstro(month,day);
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setConstellation(constellation);
			userDetail.setBirthday(DateFormatUtil.stringToDate(birthday));
			userDetail.setAge(DateFormatUtil.getAgeByBirthday(DateFormatUtil.stringToDate(birthday)));
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存情感状况
	@SuppressWarnings("unchecked")
	public void saveEmotionalState() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String emotionalState = valueMap.get("emotionalState");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setEmotionalState(emotionalState);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存职业
	@SuppressWarnings("unchecked")
	public void saveCareer() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String career = valueMap.get("career");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setCareer(career);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存公司
	@SuppressWarnings("unchecked")
	public void saveCompany() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String company = valueMap.get("company");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setCompany(company);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存学校
	@SuppressWarnings("unchecked")
	public void saveSchool() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String school = valueMap.get("school");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setSchool(school);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}

	//保存餐饮喜好
	@SuppressWarnings("unchecked")
	public void saveCatering() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String catering = valueMap.get("catering");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setCatering(catering);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存社交喜好
	@SuppressWarnings("unchecked")
	public void saveSocialContact() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String socialContact = valueMap.get("socialContact");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setSocialContact(socialContact);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存旅游喜好
	@SuppressWarnings("unchecked")
	public void saveTravel() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String travel = valueMap.get("travel");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setTravel(travel);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存运动喜好
	@SuppressWarnings("unchecked")
	public void saveSports() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String sports = valueMap.get("sports");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setSports(sports);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存音乐喜好
	@SuppressWarnings("unchecked")
	public void saveMusic() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String music = valueMap.get("music");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setMusic(music);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存其他喜好
	@SuppressWarnings("unchecked")
	public void saveOthers() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String others = valueMap.get("others");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setOthers(others);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存社区需求
	@SuppressWarnings("unchecked")
	public void saveCommunityNeed() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String communityNeed = valueMap.get("communityNeed");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setCommunityNeed(communityNeed);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存不喜欢
	@SuppressWarnings("unchecked")
	public void saveDislike() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String dislike = valueMap.get("dislike");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(user.getUserId());
			userDetail.setDislike(dislike);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//保存标签
	@SuppressWarnings("unchecked")
	public void saveTab() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");
		String tab = valueMap.get("tab");
		
		AcUser user = accountService.loadUser(userId);
		if(user!=null){
			AcUserDetail userDetail = getUserDetail(userId);
			userDetail.setPersonalTag(tab);
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//通用方法
	public AcUserDetail getUserDetail(String userId){
		AcUserDetail userDetail = new AcUserDetail();
		List<AcUserDetail> detailList = accountService.findUserDetail(" and userId = '" + userId + "'");
		if(detailList.size()>0){
			userDetail = detailList.get(0);
		}else{
			userDetail.setUserId(userId);
		}
		return userDetail;
	}

}
