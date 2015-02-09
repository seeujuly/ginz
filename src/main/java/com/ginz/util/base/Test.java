package com.ginz.util.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ginz.util.push.PushIOS;

public class Test {

	public static void main(String[] args) throws IOException {
		//changeFileName(new File("E:/document/20世纪中文小说100强"));
		/*JSONArray jsonArray = new JSONArray();
		String valueString = "宅,逗比,拉风老年,猫奴,任性,吃货,逗比,败家,逗比小二货";
		String tabs[] = valueString.split(",");
		if(tabs.length>0){
			List<String> tabList = new LinkedList<String>();  //去除标签数组中的重复项
		    for(int i = 0; i < tabs.length; i++) {  
		        if(!tabList.contains(tabs[i])) {  
		        	tabList.add(tabs[i]);  
		        }  
		    }
		    
		    for(int i = 0; i<tabList.size(); i++){
		    	jsonArray.add(tabList.get(i));
		    }
		}
		System.out.println(jsonArray.toString());*/
		String keyWord = "车| ";
		String[] keys = keyWord.split("\\|");
		if(keys.length>0){
			for(int i=0;i<keys.length;i++){
					System.out.println(i + "=" + keys[i]);
			}
		}
		
		
		//JSONObject json = PushIOS.pushSingleDevice("测试铃声", "8a0a240b24142f08c5e2362ec942658b95061d8949eb2f6ff1de593932a19910");
		//System.out.println(json.toString());
		
	}

	public static void changeFileName(File file) {
		String dirPath = file.getAbsolutePath();// 目录路径
		System.out.println(dirPath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				String originalName = f.getName();
				String path = dirPath + "\\";
				//String newName = originalName.substring(0, StringUtil.getCharacterPosition(4, "0", originalName)-1) + ".mobi";
				String newName = originalName.substring(0, originalName.lastIndexOf(".")-1) + ".mobi";
				File finalName = new File(path + newName);
				f.renameTo(finalName);
			}
		}
	}

}
