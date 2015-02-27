package com.ginz.util.push;

import org.json.JSONObject;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;

public class Test {

	public static void main(String []args){
			
		MessageIOS message = new MessageIOS();
		message.setExpireTime(86400);
		message.setAlert("测试定时");
		message.setSendTime("2015-02-27 14:31:00");	//year-mon-day hour:min:sec
				
		XingeApp xinge = new XingeApp(2200069882L, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushSingleDevice(PushDictionary.DEVICE_TOKEN, message,1);
		//JSONObject ret = xinge.pushSingleAccount(0, "DEBUG_DEVICE_01", message, XingeApp.IOSENV_DEV);
		System.out.println(ret);
	}
	
}
