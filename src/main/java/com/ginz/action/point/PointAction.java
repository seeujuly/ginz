package com.ginz.action.point;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.ginz.model.TransactionRecords;
import com.ginz.service.AccountService;
import com.ginz.service.MessageService;
import com.ginz.service.PointService;
import com.ginz.util.base.DateFormatUtil;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;
import com.ginz.util.push.PushIOS;

//积分交易
@Namespace("/")
@Action(value = "pointAction")
public class PointAction extends BaseAction {

	private AccountService accountService;
	private MessageService messageService;
	private PointService pointService;
	
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
	
	public PointService getPointService() {
		return pointService;
	}
	
	@Autowired
	public void setPointService(PointService pointService) {
		this.pointService = pointService;
	}
	
	//转账
	@SuppressWarnings("unchecked")
	public void transferAccounts() throws IOException{
		
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String tAccountType = valueMap.get("tAccountType");	//目标账户类型
		String point = valueMap.get("point");	//转账积分
		
		AcUser user = accountService.loadUser(Long.parseLong(userId));
		AcUser tUser = accountService.loadUser(Long.parseLong(tUserId));
		
		Date nowDate = new Date();
		
		if(user!=null&&tUser!=null){
			
			if(user.getPoint()>=Long.parseLong(point)){
				
				user.setPoint(user.getPoint() - Long.parseLong(point));
				accountService.updateUser(user);
				
				tUser.setPoint(tUser.getPoint() + Long.parseLong(point));
				accountService.updateUser(tUser);
				
				TransactionRecords record = new TransactionRecords();
				record.setUserId(Long.parseLong(userId));
				record.setAccountType(accountType);
				record.setTargetUserId(Long.parseLong(tUserId));
				record.setTargetAccountType(tAccountType);
				record.setPoint(Long.parseLong(point));
				record.setTransaction_type(DictionaryUtil.TRANSACTION_NORMAL);
				record.setDescription("");
				record.setCreateTime(nowDate);
				record.setFlag(DictionaryUtil.DETELE_FLAG_00);
				
				//发送系统消息给发起用户
				sendMessage(Long.parseLong(userId),accountType,"操作成功，已成功转给用户:" + tUser.getNickName() + point + "积分!");
				
				//发送系统消息给目标用户
				sendMessage(Long.parseLong(tUserId),tAccountType,"收到用户:" + user.getNickName() + "转来的积分:" + point + "点!");
				//发送推送消息给目标用户
				if(tUser.getDeviceToken()!=null&&!tUser.getDeviceToken().equals("")){
					PushIOS.pushSingleDevice("收到用户:" + user.getNickName() + "转来的积分:" + point + "点!", tUser.getDeviceToken());	//通知社区用户有人评论..
				}
				
				jsonObject.put("result", "1");
				jsonObject.put("value", "SUCCESS!");
			}else{
				//发送系统消息给发起用户
				sendMessage(Long.parseLong(userId),accountType,"操作失败，您的账户积分不足，剩余积分为:" + user.getPoint() + "点!");
	
				jsonObject.put("result", "2");
				jsonObject.put("value", "操作失败，您的剩余积分不足!");
			}
			
		}
		
		out.print(jsonObject.toString());
		
	}
	
	//发送通知
	public void sendMessage(long tUserId,String tAccountType,String content){

		Date nowDate = new Date();
		
		MsgMessageInfo messageInfo = new MsgMessageInfo();
		messageInfo.setTargetUserId(tUserId);
		messageInfo.setTargetAccountType(tAccountType);
		messageInfo.setCreateTime(nowDate);
		messageInfo.setSubject(content);
		messageInfo.setContent(content);
		messageInfo.setMessageType(DictionaryUtil.MESSAGE_TYPE_CONSUME);
		messageInfo.setFlag(DictionaryUtil.DETELE_FLAG_00);
		MsgMessageInfo messageInfo2 = messageService.saveMessageInfo(messageInfo);
		
		MsgMessageBox messageBox = new MsgMessageBox();
		messageBox.setMessageId(messageInfo2.getId());
		messageBox.setUserId(tUserId);
		messageBox.setAccountType(tAccountType);
		messageBox.setReceiveDate(nowDate);
		messageBox.setReadFlag(DictionaryUtil.MESSAGE_UNREAD);
		messageBox.setFlag(DictionaryUtil.DETELE_FLAG_00);
		messageService.saveMessageBox(messageBox);
		
	}
	
	//查看历史记录
	@SuppressWarnings("unchecked")
	public void listHistory() throws IOException{
		
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
			String condition = "";
			condition = "and (userId = " + userId + " and accountType = '" + accountType + "') ";
			condition += "OR (targetUserId = " + userId + " AND targetAccountType = '" + accountType + "')";
			List<TransactionRecords> list =  pointService.listTransactionRecords(condition);
			
			if(list.size()>0){
				JSONArray jsonArray = new JSONArray();
				for(TransactionRecords record:list){
					JSONObject json = new JSONObject();
					if(StringUtils.equals(userId, record.getUserId().toString())&&StringUtils.equals(accountType, record.getAccountType())){
						json.put("sender", record.getUserId());
						json.put("sAccountType", record.getAccountType());
						json.put("receiver", record.getTargetUserId());
						json.put("rAccountType", record.getTargetAccountType());
						
					}else{
						json.put("sender", record.getTargetUserId());
						json.put("sAccountType", record.getTargetAccountType());
						json.put("receiver", record.getUserId());
						json.put("rAccountType", record.getAccountType());
					}
					json.put("point", record.getPoint());
					json.put("transactionType", record.getTransaction_type());
					json.put("description", record.getDescription());
					
					String createTime = DateFormatUtil.dateToStringM(record.getCreateTime());
					json.put("createTime", createTime);
					
					jsonArray.add(json);
				}
				
				jsonObject.put("result", "1");
				jsonObject.put("value", jsonArray);
			}else{
				jsonObject.put("result", "2");
				jsonObject.put("value", "没有任何的交易信息!");
			}
		}
		out.print(jsonObject.toString());
		
	}
	
	//查询积分
	@SuppressWarnings("unchecked")
	public void query() throws IOException{
		
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

		String point = "";
		if(StringUtils.equals(accountType, DictionaryUtil.ACCOUNT_TYPE_01)){
			AcUser user = accountService.loadUser(Long.parseLong(userId));
			if(user!=null){
				point = user.getPoint().toString();
			}
		}else if(StringUtils.equals(accountType, DictionaryUtil.ACCOUNT_TYPE_02)){
			AcProperty property = accountService.loadProperty(Long.parseLong(userId));
			if(property!=null){
				point = property.getPoint().toString();
			}
		}else if(StringUtils.equals(accountType, DictionaryUtil.ACCOUNT_TYPE_03)){
			AcMerchant merchant = accountService.loadMerchant(Long.parseLong(userId));
			if(merchant!=null){
				point = merchant.getPoint().toString();
			}
		}
		
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("point", point);
			
		out.print(jsonObject.toString());
		
	}
	
}
