package com.ginz.action.message;

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
import com.ginz.model.AcUser;
import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;
import com.ginz.service.AccountService;
import com.ginz.service.MessageService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;

/**
 * 系统通知收件箱操作类
 * @author Tony.liu
 */
@Namespace("/")
@Action(value = "messageAction")
public class MessageAction extends BaseAction {

	private AccountService accountService;
	private MessageService messageService;
	
	public AccountService getAccountService() {
		return accountService;
	}
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	public MessageService getMessageService() {
		return messageService;
	}
	
	@Autowired
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	//显示通知列表
	@SuppressWarnings("unchecked")
	public void listMessages() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		if(user!=null){
			String condition = " and userId = " + userId + " and accountType = '" + accountType + "' and readFlag = '" + DictionaryUtil.MESSAGE_UNREAD + "' ";
			List<MsgMessageBox> list = messageService.listMessageBox(condition);
			if(list.size()>0){
				JSONArray jsonArray = new JSONArray();
				for(MsgMessageBox message:list){		//（消息id，发送人id/，头像url，内容）
					JSONObject json = new JSONObject();
					long messageId = message.getMessageId();
					MsgMessageInfo messageInfo = messageService.loadMessageInfo(messageId);
					if(messageInfo!=null){
						json.put("messageId", messageId);
						json.put("subject", messageInfo.getSubject());
						json.put("messageType", messageInfo.getMessageType());
						long uId = messageInfo.getUserId();
						AcUser u = accountService.loadUser(uId);
						if(u!=null){
							json.put("userId", uId);
							json.put("name", u.getNickName());
							json.put("headUrl", u.getHeadPortrait());
						}
					}
					jsonArray.add(json);
				}
				jsonObject.put("result", "1");
				jsonObject.put("value", jsonArray);
			}else{
				jsonObject.put("result", "2");
				jsonObject.put("value", "目前没有通知消息!");
			}
		}
		out.print(jsonObject.toString());
	}
	
	//读取通知
	@SuppressWarnings("unchecked")
	public void readMessage() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String messageId = valueMap.get("messageId");	//消息id
		
		MsgMessageInfo messageInfo = messageService.loadMessageInfo(Long.parseLong(messageId));
		if(messageInfo!=null){
			jsonObject.put("messageId", messageId);
			jsonObject.put("subject", messageInfo.getSubject());
			jsonObject.put("content", messageInfo.getContent());
			long userId = messageInfo.getUserId();
			AcUser user = accountService.loadUser(userId);
			if(user!=null){
				jsonObject.put("userId", userId+"");
				jsonObject.put("name", user.getNickName());
				jsonObject.put("headUrl", user.getHeadPortrait());
			}
			String createTime = DateFormatUtil.dateToStringM(messageInfo.getCreateTime());
			jsonObject.put("createTime", createTime);
			jsonObject.put("messageType", messageInfo.getMessageType());
		}
		out.print(jsonObject.toString());
		
	}
	
	//删除通知
	@SuppressWarnings("unchecked")
	public void deleteMessage() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		String ids[] = jsonString.split(",");
		if(ids.length>0){
			for(int i=0;i<ids.length;i++){
				String messageId = ids[i];
				List<MsgMessageBox> list = messageService.listMessageBox(" and messageId = " + Long.parseLong(messageId));
				if(list.size()>0){
					MsgMessageBox messageBox = list.get(0);
					messageService.deleteMessageBox(messageBox.getId());
				}
				messageService.deleteMessageInfo(Long.parseLong(messageId));
				
			}
		}
		jsonObject.put("value", "SUCCESS!");
		out.print(jsonObject.toString());
		
	}
	
	//统计未读通知数量
	@SuppressWarnings("unchecked")
	public void countUnread() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String userId = valueMap.get("userId");	//用户id
		String accountType = valueMap.get("accountType");	//账户类型
		
		String condition = " and userId = " + userId + " and accountType = '" + accountType + "' and readFlag = '" + DictionaryUtil.MESSAGE_UNREAD + "' ";
		List<MsgMessageBox> list = messageService.listMessageBox(condition);
		if(list.size()>0){
			jsonObject.put("messageNum", list.size() + "");
		}else{
			jsonObject.put("messageNum", "0");
		}
		
		out.print(jsonObject.toString());
		
	}
	
}
