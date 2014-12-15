package com.ginz.util.push;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;

public class Test {

	public static void main(String []args){
			
		MessageIOS message = new MessageIOS();
		message.setExpireTime(86400);
		message.setAlert("ios test");
				
		XingeApp xinge = new XingeApp(2200069882L, PushDictionary.SECRET_KEY);
		//JSONObject ret = xinge.pushSingleDevice(PushDictionary.DEVICE_TOKEN, message,2);
		JSONObject ret = xinge.pushSingleAccount(0, PushDictionary.TEST_ACCOUNT, message, XingeApp.IOSENV_DEV);
		System.out.println(ret);
	}
	
}