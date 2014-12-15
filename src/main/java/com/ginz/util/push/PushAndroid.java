package com.ginz.util.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.Style;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;

//android推送接口
public class PushAndroid {

	//单个设备下发透传消息
	protected JSONObject pushSingleDeviceMessage(String title, String content, String deviceToken) {
		
		Message message = new Message();
		message.setTitle(title);
		message.setContent(content);
		message.setType(Message.TYPE_MESSAGE);	//1：通知 2：透传消息，必填
		message.setExpireTime(259200);	//消息离线存储时间
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushSingleDevice(deviceToken, message);
		
		return ret;
	}
	
	//单个设备下发通知消息
	protected JSONObject pushSingleDeviceNotification(String title, String content, String deviceToken, String url) {
		
		Message message = new Message();
		message.setType(Message.TYPE_NOTIFICATION);	//1：通知 2：透传消息，必填
		message.setExpireTime(259200);	//消息离线存储时间
		Style style = new Style(1);
		style = new Style(3,1,0,1,0);
		ClickAction action = new ClickAction();
		action.setActionType(ClickAction.TYPE_URL);
		action.setUrl(url);
		message.setTitle(title);
		message.setContent(content);
		message.setStyle(style);
		message.setAction(action);
		/*Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		message.setCustom(custom);
		TimeInterval acceptTime = new TimeInterval(0,0,23,59);
		message.addAcceptTime(acceptTime);*/
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);	
		JSONObject ret = xinge.pushSingleDevice(deviceToken, message);
		return (ret);
	}
	
	//单个设备下发通知Intent
	//setIntent()的内容需要使用intent.toUri(Intent.URI_INTENT_SCHEME)方法来得到序列化后的Intent(自定义参数也包含在Intent内）
	//终端收到后可通过intent.parseUri()来反序列化得到Intent
	protected JSONObject pushSingleDeviceNotificationIntent(String title, String content, String deviceToken){
		
		Message message = new Message();
		message.setType(Message.TYPE_NOTIFICATION);	//1：通知 2：透传消息，必填
		message.setTitle(title);
		message.setContent(content);
		message.setExpireTime(259200);	//消息离线存储时间
		Style style = new Style(1);
		ClickAction action = new ClickAction();
		action.setActionType(ClickAction.TYPE_INTENT);
		action.setIntent("intent:10086#Intent;scheme=tel;action=android.intent.action.DIAL;S.key=value;end");
		message.setStyle(style);
		message.setAction(action);
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);	
		JSONObject ret = xinge.pushSingleDevice(deviceToken, message);
		return (ret);
	}
	
	//下发单个账号
	protected JSONObject pushSingleAccount(String title, String content, String account) {
		
		Message message = new Message();
		message.setExpireTime(259200);
		message.setTitle(title);
		message.setContent(content);
		message.setType(Message.TYPE_MESSAGE);
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);	
		JSONObject ret = xinge.pushSingleAccount(0, account, message);
		return (ret);
	}
	
	//下发多个账号
	protected JSONObject pushAccountList() {
		
		Message message = new Message();
		message.setExpireTime(259200);
		message.setTitle("title");
		message.setContent("content");
		message.setType(Message.TYPE_MESSAGE);
		List<String> accountList = new ArrayList<String>();
		accountList.add("joelliu");
		accountList.add("joelliu");
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);	
		JSONObject ret = xinge.pushAccountList(0, accountList, message);
		return (ret);
	}
	
	//下发所有设备
	protected JSONObject pushAllDevice(String title, String content, String url){
		
		Message message = new Message();
		message.setType(Message.TYPE_NOTIFICATION);
		Style style = new Style(1);
		style = new Style(3,1,0,1,0);
		ClickAction action = new ClickAction();
		action.setActionType(ClickAction.TYPE_URL);
		action.setUrl(url);
		/*Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		message.setCustom(custom);
		TimeInterval acceptTime1 = new TimeInterval(0,0,23,59);
		message.addAcceptTime(acceptTime1);*/
		message.setTitle(title);
		message.setContent(content);
		message.setStyle(style);
		message.setAction(action);
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);	
		JSONObject ret = xinge.pushAllDevice(0, message);
		return (ret);
	}
	
	//下发标签选中设备
	protected JSONObject pushTags(String title, String content, String url){
		
		List<String> tagList = new ArrayList<String>();
		tagList.add("joelliu");
		tagList.add("phone");
		
		Message message = new Message();
		message.setType(Message.TYPE_NOTIFICATION);
		Style style = new Style(1);
		style = new Style(3,1,0,1,0);
		ClickAction action = new ClickAction();
		action.setActionType(ClickAction.TYPE_URL);
		action.setUrl(url);
		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		message.setTitle(title);
		message.setContent(content);
		message.setStyle(style);
		message.setAction(action);
		message.setCustom(custom);
		TimeInterval acceptTime1 = new TimeInterval(0,0,23,59);
		message.addAcceptTime(acceptTime1);
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushTags(0, tagList, "OR", message);
		return (ret);
	}
	
	//查询设备数量
	protected JSONObject demoQueryDeviceCount() {
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.queryDeviceCount();
		return (ret);
	}
	
}
