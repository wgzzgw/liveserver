package com.study;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * ��ȡ���ݿ����ӵ���
 */
public class SqlManager {
	private static final String dbPro = "jdbc:mysql://";
	private static final String host = "192.168.1.90";// ip��ַ/������
	private static final String port = "30431";// �˿ں�
	private static final String dbName = "d34deca5";// ���ݿ�����
	private static final String charset = "?useUnicode=true&charactsetEncoding=utf-8";// �ַ���

	private static final String url = dbPro + host + ":" + port + "/" + dbName
			+ charset;
	private static final String user = "fdcb1942";//���ݿ��˺�
	private static final String password = "2b7613ef";//���ݿ�����
	/*
	 * ��ȡ���ݿ����ӣ�����ֵ��Connection
	 */
	public static Connection getConnection() throws SQLException {//�����ȥcatch exception
		return DriverManager.getConnection(url, user, password);
	}
}
