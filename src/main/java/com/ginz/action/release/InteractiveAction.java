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
import com.ginz.model.AcUser;
import com.ginz.model.PubComments;
import com.ginz.model.PubInteractive;
import com.ginz.model.PubPraise;
import com.ginz.service.AccountService;
import com.ginz.service.InteractiveService;
import com.ginz.service.ReplyService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.push.PushIOS;

@Namespace("/")
@Action(value = "interactiveAction")
public class InteractiveAction extends BaseAction{

	private InteractiveService interactiveService;
	private AccountService accountService;
	private ReplyService replyService;

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
	
	//发布积分互动信息
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
		String startTime = valueMap.get("startTime");
		String endTime = valueMap.get("endTime");
		
		AcUser user = accountService.loadUser(Long.parseLong(id));
		if(user!=null){
			PubInteractive interactive = new PubInteractive();
			interactive.setUserId(Long.parseLong(id));
			interactive.setSubject(subject);
			interactive.setContent(content);
			interactive.setCreateTime(new Date());
			interactive.setStartTime(DateFormatUtil.toDate(startTime));
			interactive.setEndTime(DateFormatUtil.toDate(endTime));
			interactive.setFlag(DictionaryUtil.DETELE_FLAG_00);
			interactiveService.saveInteractive(interactive);
		}
		
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//修改积分互动信息
	public void editInteractive() throws IOException{
		
		
		
	}
	
	//删除积分互动信息
	public void deleteInteractive() throws IOException{
		
		
		
	}
	
	//个人用户获取积分互动信息列表
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
		String userId = valueMap.get("userId");	//个人用户id
		String page = valueMap.get("page");
		int rows = 10;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject=new JSONObject();
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		
		if(user!=null){
			//搜索，信息匹配
			List<PubInteractive> interactiveList = interactiveService.findInteractive(" order by createTime desc ", Integer.parseInt(page), rows);
			
			if(interactiveList.size()>0){
				for(PubInteractive interactive:interactiveList){
					int praiseNum = replyService.countPraise(" and releaseId = " + interactive.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
					int commentNum = replyService.countComment(" and releaseId = " + interactive.getId() + " and releaseType = '" + DictionaryUtil.RELEASE_TYPE_02 + "'");
					JSONObject json = new JSONObject();
					json.put("id", interactive.getId());
					json.put("subject", interactive.getSubject());
					String picIds = interactive.getPicIds();
					String[] ids = picIds.split(",");
					json.put("picUrl", ids[0]);
					AcUser u = accountService.loadUser(interactive.getUserId());
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
	
	//个人用户获取积分互动信息详细内容
	@SuppressWarnings({ "unchecked", "static-access" })
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
		
		JSONObject jsonObject=new JSONObject();
		
		PubInteractive interactive = interactiveService.loadInteractive(Long.parseLong(id));
		if(interactive != null){
			jsonObject.fromObject(interactive);
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
				AcUser user = accountService.loadUser(uId);
				if(user != null){
					PushIOS.pushSingleDevice(user.getNickName() + "赞了你的信息", user.getDeviceToken());	//通知个人用户有人点赞..
					
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