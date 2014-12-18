package com.ginz.util.push;

import org.json.JSONObject;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;

public class Test {

	public static void main(String []args){
			
		MessageIOS message = new MessageIOS();
		message.setExpireTime(86400);
		message.setAlert("赞了你的信息");
				
		XingeApp xinge = new XingeApp(2200069882L, PushDictionary.SECRET_KEY);
		JSONObject ret = xinge.pushSingleDevice(PushDictionary.DEVICE_TOKEN, message,2);
		//JSONObject ret = xinge.pushSingleAccount(0, "DEBUG_DEVICE_01", message, XingeApp.IOSENV_DEV);
		System.out.println(ret);
	}
	
}
