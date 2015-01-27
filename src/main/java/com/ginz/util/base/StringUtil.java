package com.ginz.util.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作类
 */
public class StringUtil {

	//获取字符串中第N次出现的指定字符位置
	public static int getCharacterPosition(int num, String pattern, String string){
	    //这里是获取pattern符号的位置
	    Matcher slashMatcher = Pattern.compile(pattern).matcher(string);
	    int mIdx = 0;
	    while(slashMatcher.find()) {
	       mIdx++;
	       //当"/"符号第三次出现的位置
	       if(mIdx == num){
	          break;
	       }
	    }
	    return slashMatcher.start();
	 }
	
}
