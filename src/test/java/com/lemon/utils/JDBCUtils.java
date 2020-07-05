package com.lemon.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.lemon.constants.Constants;
//企业里用连接池来管理
public class JDBCUtils {

	public static Connection getConnection() {
		// 定义数据库连接对象
		Connection conn = null;
		try {
			// 你导入的数据库驱动包， mysql。
			conn = DriverManager.
					getConnection(Constants.JDBC_URL, Constants.JDBC_USERNAMEL, Constants.JDBC_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
	public static void close(Connection conn) {
		try {
			if(conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}