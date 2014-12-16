package com.ginz.action.release;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.ginz.model.PubComments;
import com.ginz.model.PubNotice;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.NoticeService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "noticeAction")
public class NoticeAction extends BaseAction{

	private NoticeService noticeService;
	private AccountService accountService;
	private ReplyService replyService;

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
			notice.setFlag(DictionaryUtil.DETELE_FLAG_00);
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
						int praiseNum = countPraise(notice.getId());
						int commentNum = countComment(notice.getId());
						JSONObject json = JSONObject.fromObject(notice);
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
			
			int num = countPraise(Long.parseLong(id));
			jsonObject.put("result", "1");
			jsonObject.put("value", "SUCCESS!");
			jsonObject.put("number", num);	//更新点赞数量
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
		
		int num = countComment(Long.parseLong(id));
		jsonObject.put("result", "1");
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("number", num);	//更新点赞数量
		
		out.print(jsonObject.toString());
		
	}
	
	//统计点赞数量
	public int countPraise(Long releaseId){
		
		List<PubPraise> list = replyService.findPraise(" and releaseId = " + releaseId + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		return list.size();
		
	}
	
	//统计评论数量
	public int countComment(Long releaseId){
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + releaseId + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		return list.size();
		
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
				JSONObject json = JSONObject.fromObject(praise);
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
		
		List<PubComments> list = replyService.findComments(" and releaseId = " + id + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_01 + "'");
		if(list.size()>0){
			for(PubComments comment:list){
				JSONObject json = JSONObject.fromObject(comment);
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
			jsonObject.put("number", list.size());
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "还没有人评论过!");
			jsonObject.put("number", 0);
		}
		
		out.print(jsonObject.toString());
		
	}
	
}
