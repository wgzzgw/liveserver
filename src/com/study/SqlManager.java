package com.study;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * 获取数据库连接的类
 */
public class SqlManager {
	private static final String dbPro = "jdbc:mysql://";
	private static final String host = "192.168.1.90";// ip地址/主机号
	private static final String port = "30431";// 端口号
	private static final String dbName = "d34deca5";// 数据库名字
	private static final String charset = "?useUnicode=true&charactsetEncoding=utf-8";// 字符集

	private static final String url = dbPro + host + ":" + port + "/" + dbName
			+ charset;
	private static final String user = "fdcb1942";//数据库账号
	private static final String password = "2b7613ef";//数据库密码
	/*
	 * 获取数据库连接，返回值：Connection
	 */
	public static Connection getConnection() throws SQLException {//让外层去catch exception
		return DriverManager.getConnection(url, user, password);
	}
}
