package com.ginz.util.timer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ginz.util.base.DBHelper;
import com.ginz.util.base.FileUtil;

public class TimeTask {

	public String getTabDir() throws IOException{
		
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");   
		Properties p = new Properties();   
		p.load(inputStream);   
		String tabDir = p.getProperty("tabs_dir");
		
		File file = new File(tabDir);
		if (!file.exists()) {
			file.mkdir();
		}
		return tabDir;
	
	}
	
	//获取所有的用户个人标签
	public void getPersonalTabs(){
		
		String sql = " SELECT GROUP_CONCAT(personal_tag SEPARATOR ',') as tabs FROM ac_user_detail ";
		String valueString = "";
		try {
			Connection con = DBHelper.getCon();
			Statement sm = null;
			ResultSet rs = null;
			sm = con.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				valueString = rs.getString("tabs");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(StringUtils.isNotEmpty(valueString)){
			commonMethod(valueString, "personalTag");
		}
		
	}
	
	//获取所有的社区生活信息标签
	public void getEventLabel() {
		
		String sql = " SELECT GROUP_CONCAT(label SEPARATOR ',') as labels FROM pub_event ";
		String valueString = "";
		try {
			Connection con = DBHelper.getCon();
			Statement sm = null;
			ResultSet rs = null;
			sm = con.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				valueString = rs.getString("labels");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(StringUtils.isNotEmpty(valueString)){
			commonMethod(valueString, "eventLabel");
		}
		
	}

	//获取所有的互动交易信息标签
	public void getActivityLabels(){
						
		String sql = " SELECT GROUP_CONCAT(label SEPARATOR ',') as labels FROM pub_activities ";
		String valueString = "";
		try {
			Connection con = DBHelper.getCon();
			Statement sm = null;
			ResultSet rs = null;
			sm = con.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				valueString = rs.getString("labels");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(StringUtils.isNotEmpty(valueString)){
			commonMethod(valueString, "activityLabel");
		}
	}
	
	//通用方法：去除标签数组中的重复项，并写入文件
	public void commonMethod(String valueString,String fileName){
		String content = "";
		String labels[] = valueString.split(",");
		if(labels.length>0){
			List<String> labelsList = new LinkedList<String>();  //去除标签数组中的重复项
		    for(int i = 0; i < labels.length; i++) {  
		        if(StringUtils.isNotEmpty(labels[i])&&!labelsList.contains(labels[i])) {  
		        	labelsList.add(labels[i]);  
		        }  
		    }
		    for(int i = 0; i<labelsList.size(); i++){
		    	if(StringUtils.isNotEmpty(content)){
		    		content += "," + labelsList.get(i);
		    	}else{
		    		content += labelsList.get(i);
		    	}
		    }
		}
		try {
			String tabDir = getTabDir();
			String destFile = fileName + ".txt";
			FileUtil.write(tabDir + "/" + destFile,content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
