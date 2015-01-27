package com.ginz.util.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args){
		
		String url = "http://10.0.0.11/activity/20141230154218_7f2108bd-06ca-4c4a-9e0e-699e9dd4ebb1.jpg";
		//url = url.substring(url.indexOf("/", 3), url.length());
		
		Matcher slashMatcher = Pattern.compile("/").matcher(url);
		int mIdx = 0;
		while(slashMatcher.find()) {
		   mIdx++;
		   //当"/"符号第三次出现的位置
		   if(mIdx == 3){
		      break;
		   }
		}
		
		url = url.substring(slashMatcher.start(), url.length());
		System.out.println(url);
	}
	
}


