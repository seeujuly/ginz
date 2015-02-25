package com.ginz.util.timer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.ginz.util.base.DBHelper;
import com.ginz.util.base.FileUtil;

public class TimeTask {

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
		
		String content = "";
		if(StringUtils.isNotEmpty(valueString)){
			String tabs[] = valueString.split(",");
			if(tabs.length>0){
				List<String> tabList = new LinkedList<String>();  //去除标签数组中的重复项
			    for(int i = 0; i < tabs.length; i++) {  
			        if(!tabList.contains(tabs[i])) {  
			        	tabList.add(tabs[i]);  
			        }  
			    }
			    for(int i = 0; i<tabList.size(); i++){
			    	if(StringUtils.isNotEmpty(content)){
			    		content += "," + tabList.get(i);
			    	}else{
			    		content += tabList.get(i);
			    	}
			    }
			}
			try {
				File file = new File("D://personalTag.txt");
				if(file.exists()){
					FileUtil.copyTo("D://personalTag.txt","D://copy/");
					FileUtils.forceDelete(file);
				}
				FileUtil.write("D://personalTag.txt",content);
				File copyFile = new File("D://copy/personalTag.txt");
				if(copyFile.exists()){
					FileUtils.forceDelete(copyFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		
		String content = "";
		if(StringUtils.isNotEmpty(valueString)){
			String labels[] = valueString.split(",");
			if(labels.length>0){
				List<String> labelsList = new LinkedList<String>();  //去除标签数组中的重复项
			    for(int i = 0; i < labels.length; i++) {  
			        if(!labelsList.contains(labels[i])) {  
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
				File file = new File("D://eventLabel.txt");
				if(file.exists()){
					FileUtil.copyTo("D://eventLabel.txt","D://copy/");
					FileUtils.forceDelete(file);
				}
				FileUtil.write("D://eventLabel.txt",content);
				File copyFile = new File("D://copy/eventLabel.txt");
				if(copyFile.exists()){
					FileUtils.forceDelete(copyFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		
		String content = "";
		if(StringUtils.isNotEmpty(valueString)){
			String labels[] = valueString.split(",");
			if(labels.length>0){
				List<String> labelsList = new LinkedList<String>();  //去除标签数组中的重复项
			    for(int i = 0; i < labels.length; i++) {  
			        if(!labelsList.contains(labels[i])) {  
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
				File file = new File("D://activityLabel.txt");
				if(file.exists()){
					FileUtil.copyTo("D://activityLabel.txt","D://copy/");
					FileUtils.forceDelete(file);
				}
				FileUtil.write("D://activityLabel.txt",content);
				File copyFile = new File("D://copy/activityLabel.txt");
				if(copyFile.exists()){
					FileUtils.forceDelete(copyFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//通用方法：去除标签数组中的重复项，并写入文件
	public void commonMethod(String valueString){
		String content = "";
		String labels[] = valueString.split(",");
		if(labels.length>0){
			List<String> labelsList = new LinkedList<String>();  //去除标签数组中的重复项
		    for(int i = 0; i < labels.length; i++) {  
		        if(!labelsList.contains(labels[i])) {  
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
			File file = new File("D://activityLabel.txt");
			if(file.exists()){
				FileUtil.copyTo("D://activityLabel.txt","D://copy/");
				FileUtils.forceDelete(file);
			}
			FileUtil.write("D://activityLabel.txt",content);
			File copyFile = new File("D://copy/activityLabel.txt");
			if(copyFile.exists()){
				FileUtils.forceDelete(copyFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
