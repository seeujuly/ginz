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
	
	//获取服务器当前时间
	public void getTime() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		Date nowDate = new Date();
		String time = DateFormatUtil.dateToStringS(nowDate);
		out.print(time);
		
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
		String tUserId = valueMap.get("tUserId");	//目标用户id
		String point = valueMap.get("point");	//转账积分
		
		AcUser user = accountService.loadUser(userId);
		AcUser tUser = accountService.loadUser(tUserId);
		
		Date nowDate = new Date();
		
		if(user!=null&&tUser!=null){
			
			if(user.getPoint()>=Long.parseLong(point)){
				
				user.setPoint(user.getPoint() - Long.parseLong(point));
				accountService.updateUser(user);
				
				tUser.setPoint(tUser.getPoint() + Long.parseLong(point));
				accountService.updateUser(tUser);
				
				TransactionRecords record = new TransactionRecords();
				record.setUserId(userId);
				record.setTargetUserId(tUserId);
				record.setPoint(Long.parseLong(point));
				record.setTransaction_type(DictionaryUtil.TRANSACTION_NORMAL);
				record.setDescription("");
				record.setCreateTime(nowDate);
				record.setFlag(DictionaryUtil.DETELE_FLAG_00);
				pointService.saveTransactionRecords(record);
				
				//发送系统消息给发起用户
				String value = "操作成功，已成功转给用户:" + tUser.getNickName() + point + "积分!";
				messageService.sendMessage(null, userId, value, value, null, "", DictionaryUtil.MESSAGE_TYPE_CONSUME);
				
				//发送系统消息和推送消息给目标用户
				String tValue = "收到用户:" + user.getNickName() + "转来的积分:" + point + "点!";
				messageService.sendMessage(null, tUserId, tValue, tValue, null, "", DictionaryUtil.MESSAGE_TYPE_CONSUME);
				if(tUser.getDeviceToken()!=null&&!tUser.getDeviceToken().equals("")){
					PushIOS.pushSingleDevice(tValue, tUser.getDeviceToken());	//通知社区用户有人评论..
				}
				
				jsonObject.put("result", "1");
				jsonObject.put("value", "SUCCESS!");
			}else{
				//发送系统消息给发起用户
				String value = "操作失败，您的账户积分不足，剩余积分为:" + user.getPoint() + "点!";
				messageService.sendMessage(null, userId, value, value, null, "", DictionaryUtil.MESSAGE_TYPE_CONSUME);
	
				jsonObject.put("result", "2");
				jsonObject.put("value", "操作失败，您的剩余积分不足!");
			}
			
		}
		
		out.print(jsonObject.toString());
		
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
		
		AcUser user = accountService.loadUser(userId);
		
		if(user!=null){
			String condition = "";
			condition = "and (userId =  '" + userId + "' ";
			condition += "OR targetUserId = '" + userId + "')";
			List<TransactionRecords> list =  pointService.listTransactionRecords(condition);
			
			if(list.size()>0){
				JSONArray jsonArray = new JSONArray();
				for(TransactionRecords record:list){
					JSONObject json = new JSONObject();
					if(StringUtils.equals(userId, record.getUserId())){
						json.put("sender", record.getUserId());
						json.put("receiver", record.getTargetUserId());
						
					}else{
						json.put("sender", record.getTargetUserId());
						json.put("receiver", record.getUserId());
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

		String point = "";
		if(StringUtils.equals(userId.substring(0, 1), "u")){
			AcUser user = accountService.loadUser(userId);
			if(user!=null){
				point = user.getPoint().toString();
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "p")){
			AcProperty property = accountService.loadProperty(userId);
			if(property!=null){
				point = property.getPoint().toString();
			}
		}else if(StringUtils.equals(userId.substring(0, 1), "m")){
			AcMerchant merchant = accountService.loadMerchant(userId);
			if(merchant!=null){
				point = merchant.getPoint().toString();
			}
		}
		
		jsonObject.put("value", "SUCCESS!");
		jsonObject.put("point", point);
			
		out.print(jsonObject.toString());
		
	}
	
}
