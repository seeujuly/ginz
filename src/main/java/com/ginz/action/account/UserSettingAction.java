package com.ginz.action.account;

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
import com.ginz.service.AccountService;
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
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		
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
					String fileName = sdf.format(nowDate) + "_" + UUID.randomUUID().toString() + ext; 
					String dir = DictionaryUtil.PIC_RELEASE_INTERACTIVE;
					String thumbnailDir = DictionaryUtil.PIC_THUMBNAIL;
					FileUtils.copyFile(files[i], new File(path + dir + fileName)); 
					
					ThumbnailUtil ccc = new ThumbnailUtil(path + dir + fileName, path + thumbnailDir + dir + fileName);
					ccc.resize(Integer.parseInt(resize),Integer.parseInt(resize));
					
					Picture picture = new Picture();
					picture.setUrl(serverUrl + dir + fileName);
					picture.setThumbnailUrl(serverUrl + thumbnailDir + dir + fileName);
					picture.setFileName(fileName);
					picture.setAccountType(DictionaryUtil.ACCOUNT_TYPE_01);
					picture.setUserId(user.getId());
					picture.setIsHeadPortrait(DictionaryUtil.STATE_NO);
					picture.setCreateTime(nowDate);
					picture.setFlag(DictionaryUtil.DETELE_FLAG_00);
					pictureService.savePicture(picture);
					
					user.setHeadPortrait(serverUrl + dir + fileName);
					user.setThumbnailUrl(serverUrl + thumbnailDir + dir + fileName);
					accountService.saveUser(user);
				}
			}
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	@SuppressWarnings("unchecked")
	public void save() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		
		String email = valueMap.get("email");
		String mobile = valueMap.get("mobile");
		String gender = valueMap.get("gender");
		String realName = valueMap.get("realName");
		String nickName = valueMap.get("nickName");
		String birthday = valueMap.get("birthday");
		
		//根据生日计算星座
		Date date = DateFormatUtil.stringToDate(birthday);
		int month = DateFormatUtil.getMonth(date);
		int day = DateFormatUtil.getDay(date);
		String constellation = DateFormatUtil.getAstro(month,day);
		
		List<AcUser> userList = accountService.findUser(" and mobile = '" + mobile + "' ");
		if(userList.size()>0){
			AcUser user = userList.get(0);
			user.setEmail(email);
			user.setRealName(realName);
			user.setNickName(nickName);
			accountService.updateUser(user);
			
			AcUserDetail userDetail;
			List<AcUserDetail> detailList = accountService.findUserDetail(" and user = " + user.getId());
			if(detailList.size()>0){
				userDetail = detailList.get(0);
			}else{
				userDetail = new AcUserDetail();
				userDetail.setUserId(user.getId());
			}
			userDetail.setGender(gender);
			userDetail.setConstellation(constellation);
			userDetail.setBirthday(DateFormatUtil.stringToDate(birthday));
			userDetail.setAge(DateFormatUtil.getAgeByBirthday(DateFormatUtil.stringToDate(birthday)));
			accountService.updateUserDetail(userDetail);
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
}
