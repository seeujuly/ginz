package com.ginz.util.base;

import java.sql.*;

public class DBHelper {

	public static Connection getCon() {
		Connection con = null;
		DBConfig config = DBConfig.getInstance();
		String user = config.getDB_username();
		String password = config.getDB_password();

		String url = config.getDB_url();
		String driver = config.getDB_driver();
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public static void closeAll(ResultSet se, PreparedStatement state,
			Connection con) {
		try {
			if (se != null) {
				se.close();
			}
			if (state != null) {
				state.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}