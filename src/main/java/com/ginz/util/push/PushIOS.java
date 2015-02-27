package com.ginz.util.push;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;

//IOS推送接口
public class PushIOS {

	//单个设备下发通知消息
	public static JSONObject pushSingleDevice(String content, String deviceToken) {
		
		MessageIOS message = new MessageIOS();
		message.setAlert(content);
		message.setExpireTime(259200);
		//message.setSound("beep.wav");
		message.setSound("tweet.wav");
		/*message.setBadge(1);
		TimeInterval acceptTime1 = new TimeInterval(0,0,23,59);
		message.addAcceptTime(acceptTime1);
		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		message.setCustom(custom);*/
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);	
		JSONObject ret = xinge.pushSingleDevice(deviceToken, message, XingeApp.IOSENV_PROD);
		return (ret);
	}
	
	//单个账号下发通知消息
	public static JSONObject pushSingleAccount(String content, String account) {
		
		MessageIOS message = new MessageIOS();
		message.setAlert(content);
		message.setExpireTime(259200);
		/*message.setBadge(1);
		message.setSound("beep.wav");
		TimeInterval acceptTime1 = new TimeInterval(0,0,23,59);
		message.addAcceptTime(acceptTime1);
		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		message.setCustom(custom);*/
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushSingleAccount(0, account, message, XingeApp.IOSENV_PROD);
		return (ret);
	}
	
	//多个账号下发通知消息
	public static JSONObject pushAccountList(String content, Map<String,Object> custom, List<String> accountList, String sendTime) {
		
		MessageIOS message = new MessageIOS();
		message.setAlert(content);
		message.setExpireTime(259200);
		message.setCustom(custom);
		if(StringUtils.isNotEmpty(sendTime)){
			message.setSendTime(sendTime);
		}
		/*message.setBadge(1);
		message.setSound("beep.wav");
		List<String> accountList = new ArrayList<String>();
		accountList.add("joelliu");
		accountList.add("joelliu");*/
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushAccountList(0, accountList, message, XingeApp.IOSENV_PROD);
		return (ret);
	}
	
	//下发所有设备
	public static JSONObject pushAllDevice(String content){
		
		MessageIOS message = new MessageIOS();
		message.setAlert(content);
		message.setExpireTime(259200);
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushAllDevice(0, message, XingeApp.IOSENV_PROD);
		return (ret);
	}
	
	//下发标签选中设备
	public static JSONObject pushTags(String content){
		
		MessageIOS message = new MessageIOS();
		message.setAlert(content);
		message.setExpireTime(259200);
		
		List<String> tagList = new ArrayList<String>();
		tagList.add("joelliu");
		tagList.add("phone");
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushTags(0, tagList, "OR", message, XingeApp.IOSENV_PROD);
		return (ret);
	}
	
	//查询设备数量
	public static JSONObject demoQueryDeviceCount() {
		
		XingeApp xinge = new XingeApp(PushDictionary.ACCESS_ID, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.queryDeviceCount();
		return (ret);
	}
		
}
