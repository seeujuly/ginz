package com.ginz.action.release;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.PubNotice;
import com.ginz.service.AccountService;
import com.ginz.service.NoticeService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "noticeAction")
public class NoticeAction extends BaseAction{

	private NoticeService noticeService;
	private AccountService accountService;

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
	
	//发布公告
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
		String id = valueMap.get("id");	//社区用户id
		String communityId = valueMap.get("communityId");
		String subject = valueMap.get("subject");
		String content = valueMap.get("content");
		String startTime = valueMap.get("startTime");
		String endTime = valueMap.get("endTime");
		
		AcProperty property = accountService.loadProperty(Long.parseLong(id));
		if(property!=null){
			PubNotice notice = new PubNotice();
			notice.setPropertyId(Long.parseLong(id));
			notice.setCommunityId(Long.parseLong(communityId));
			notice.setSubject(subject);
			notice.setContent(content);
			notice.setStartTime(DateFormatUtil.toDate(startTime));
			notice.setEndTime(DateFormatUtil.toDate(endTime));
			noticeService.saveNotice(notice);
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//推送公告给个人用户
	@SuppressWarnings("unchecked")
	public void pushNotice() throws IOException{
		
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
						JSONObject json = JSONObject.fromObject(notice);
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
	
}
