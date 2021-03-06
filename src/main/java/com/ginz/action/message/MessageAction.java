package com.ginz.action.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.AcMerchant;
import com.ginz.model.AcProperty;
import com.ginz.model.AcUser;
import com.ginz.model.MsgMessageBox;
import com.ginz.model.MsgMessageInfo;
import com.ginz.model.Picture;
import com.ginz.model.PubActivities;
import com.ginz.model.PubEvent;
import com.ginz.model.PubNotice;
import com.ginz.service.AccountService;
import com.ginz.service.ActivitiesService;
import com.ginz.service.EventService;
import com.ginz.service.MessageService;
import com.ginz.service.NoticeService;
import com.ginz.service.PictureService;
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
	private NoticeService noticeService;
	private EventService eventService;
	private ActivitiesService activitiesService;
	private PictureService pictureService;
	
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

	public NoticeService getNoticeService() {
		return noticeService;
	}

	@Autowired
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	public EventService getEventService() {
		return eventService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public ActivitiesService getActivitiesService() {
		return activitiesService;
	}

	@Autowired
	public void setActivitiesService(ActivitiesService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public PictureService getPictureService() {
		return pictureService;
	}

	@Autowired
	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
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
		
		try {
			String condition = " and userId = '" + userId + "' ";
			List<MsgMessageBox> list = messageService.listMessageBox(condition);
			if(list.size()>0){
				JSONArray jsonArray = new JSONArray();
				for(MsgMessageBox message:list){		//（消息id，发送人id/，头像url，内容）
					JSONObject json = new JSONObject();
					long messageId = message.getMessageId();
					MsgMessageInfo messageInfo = messageService.loadMessageInfo(messageId);
					if(messageInfo!=null){
						json.put("messageId", messageInfo.getId());
						json.put("subject", messageInfo.getSubject());
						json.put("messageType", messageInfo.getMessageType());
						json.put("releaseType", messageInfo.getReleaseType());
						json.put("releaseId", messageInfo.getReleaseId());
						String createTime = DateFormatUtil.dateToStringM(messageInfo.getCreateTime());
						json.put("createTime", createTime);
						
						if(StringUtils.isNotEmpty(messageInfo.getReleaseType())&&messageInfo.getReleaseId()!=null){
							String picIds = "";
							if(StringUtils.equals(messageInfo.getReleaseType(), DictionaryUtil.RELEASE_TYPE_01)){	//社区公告
								PubNotice notice = noticeService.loadNotice(messageInfo.getReleaseId());
								if(notice != null){
									picIds = notice.getPicIds();
								}
							}else if(StringUtils.equals(messageInfo.getReleaseType(), DictionaryUtil.RELEASE_TYPE_02)){		//社区生活
								PubEvent event = eventService.loadEvent(messageInfo.getReleaseId());
								if(event != null){
									picIds = event.getPicIds(); 
								}
							}else if(StringUtils.equals(messageInfo.getReleaseType(), DictionaryUtil.RELEASE_TYPE_03)){		//活动/交易
								PubActivities activity = activitiesService.loadActivities(messageInfo.getReleaseId());
								if(activity != null){
									picIds = activity.getPicIds(); 
								}
							}
							if(picIds!=null&&!picIds.equals("")){
								String[] ids = picIds.split(",");
								if(ids.length>0){
									Picture picture = pictureService.loadPicture(Long.parseLong(ids[0]));
									if(picture!=null){
										json.put("picUrl", picture.getThumbnailUrl());
									}
								}
							}
						}
						
						String uId = messageInfo.getUserId();
						if(StringUtils.isNotEmpty(uId)){
							if(StringUtils.equals(uId.substring(0, 1), "u")){
								AcUser user = accountService.loadUser(uId);
								if(user != null){
									json.put("userId", user.getUserId());
									json.put("name", user.getNickName());
									json.put("headUrl", user.getHeadPortrait());
								}
							}else if(StringUtils.equals(uId.substring(0, 1), "p")){
								AcProperty property = accountService.loadProperty(uId);
								if(property!=null){
									json.put("userId", property.getUserId());
									json.put("name", property.getPropertyName());
									json.put("headUrl", property.getPicUrl());
								}
							}else if(StringUtils.equals(uId.substring(0, 1), "m")){
								AcMerchant merchant = accountService.loadMerchant(uId);
								if(merchant!=null){
									json.put("userId", merchant.getUserId());
									json.put("name", merchant.getMerchantName());
									json.put("headUrl", merchant.getPicUrl());
								}
							}
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
		} catch (Exception e) {
			e.printStackTrace();
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
			
			String uId = messageInfo.getUserId();
			if(StringUtils.isNotEmpty(uId)){
				if(StringUtils.equals(uId.substring(0, 1), "u")){
					AcUser user = accountService.loadUser(uId);
					if(user != null){
						jsonObject.put("userId", user.getUserId());
						jsonObject.put("name", user.getNickName());
						jsonObject.put("headUrl", user.getHeadPortrait());
					}
				}else if(StringUtils.equals(uId.substring(0, 1), "p")){
					AcProperty property = accountService.loadProperty(uId);
					if(property!=null){
						jsonObject.put("userId", property.getUserId());
						jsonObject.put("name", property.getPropertyName());
						jsonObject.put("headUrl", property.getPicUrl());
					}
				}else if(StringUtils.equals(uId.substring(0, 1), "m")){
					AcMerchant merchant = accountService.loadMerchant(uId);
					if(merchant!=null){
						jsonObject.put("userId", merchant.getUserId());
						jsonObject.put("name", merchant.getMerchantName());
						jsonObject.put("headUrl", merchant.getPicUrl());
					}
				}
			}
			
			String createTime = DateFormatUtil.dateToStringM(messageInfo.getCreateTime());
			jsonObject.put("createTime", createTime);
			jsonObject.put("messageType", messageInfo.getMessageType());
		
			MsgMessageBox messageBox = messageService.loadMessageBox(Long.parseLong(messageId));
			if(messageBox!=null){
				messageBox.setReadFlag(DictionaryUtil.MESSAGE_READ);
				messageService.updateMessageBox(messageBox);
			}
			
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
		
		String condition = " and userId = '" + userId + "' and readFlag = '" + DictionaryUtil.MESSAGE_UNREAD + "' ";
		List<MsgMessageBox> list = messageService.listMessageBox(condition);
		if(list.size()>0){
			jsonObject.put("messageNum", list.size() + "");
		}else{
			jsonObject.put("messageNum", "0");
		}
		
		out.print(jsonObject.toString());
		
	}
	
}
