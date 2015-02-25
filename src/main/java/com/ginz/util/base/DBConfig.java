package com.ginz.util.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

public class DBConfig {
	// 数据库及server配置文件路径
	private static final String ACTIONPATH = "/config.properties";
	private static DBConfig instance = null;

	private String DB_username = null;
	private String DB_password = null;
	private String DB_url=null;
	private String DB_driver=null;

	private DBConfig() {
	}

	public String getDB_username() {
		return DB_username;
	}

	public String getDB_password() {
		return DB_password;
	}


	public String getDB_url() {
		return DB_url;
	}

	public void setDB_url(String dB_url) {
		DB_url = dB_url;
	}

	public String getDB_driver() {
		return DB_driver;
	}

	public void setDB_driver(String dB_driver) {
		DB_driver = dB_driver;
	}

	public void setDB_username(String dB_username) {
		DB_username = dB_username;
	}

	public void setDB_password(String dB_password) {
		DB_password = dB_password;
	}

	public static DBConfig getInstance() {
		if (instance == null) {
			instance = new DBConfig().getNewDBConfig();
		}
		return instance;
	}

	private DBConfig getNewDBConfig() {

		DBConfig dc = new DBConfig();
		Properties prop = new Properties();
		InputStream is= null;

		try {
	         	is=this.getClass().getResourceAsStream(ACTIONPATH); 
				prop.load(is);
				dc.DB_username = prop.getProperty("jdbc_username");
				dc.DB_password = prop.getProperty("jdbc_password");
				dc.DB_driver = prop.getProperty("driverClassName");
				dc.DB_url = prop.getProperty("jdbc_url");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dc;
	}
	public static void main(String []args) throws URISyntaxException{
		String path = null;
		path = DBConfig.class.getClassLoader().getResource("").toURI()
				.getPath();
		
		System.out.println(path);
	}
}