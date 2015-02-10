package com.ginz.util.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	//对map做排序，并输出前10项
	public static String sort(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		}); // 排序
		
		String valueString = "";
		if(infoIds.size()>10){
			for (int i = 0; i < 10; i++) { // 输出
				Entry<String, Integer> id = infoIds.get(i);
				valueString += "," + id.getKey();
			}
		}else{
			for (int i = 0; i < infoIds.size(); i++) { // 输出
				Entry<String, Integer> id = infoIds.get(i);
				valueString += "," + id.getKey();
			}
		}
		if(valueString.length()>0){
			if(valueString.substring(0, 1).equals(",")){
				valueString = valueString.substring(1, valueString.length());
			}
		}
		return valueString;
	}
	
}
