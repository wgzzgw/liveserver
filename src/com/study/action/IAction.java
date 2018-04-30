package com.study.action;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 * action���ࣨ�ӿڻ��ࣩ
 */
public abstract  class IAction {
	/*
	 * �����߼���������������ʵ��
	 */
	public abstract void doAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException, SQLException;
	/*
	 * ��ȡ��������е�ֵ�������򷵻أ����򷵻�Ĭ��ֵ
	 */
	public static String getParam(HttpServletRequest req, String key,
			String defaultValue) {
		String paramValue = req.getParameter(key);
		if (paramValue == null || paramValue.equals("")) {
			return defaultValue;
		} else {
			return paramValue;
		}
	}
	/*
	 * ͬ��,��������
	 */
	public static int getParam(HttpServletRequest req, String key,
			int defaultValue) {
		String paramValue = req.getParameter(key);
		if (paramValue == null || paramValue.equals("")) {
			return defaultValue;
		} else {
			try {
				return Integer.valueOf(paramValue);
			} catch (Exception e) {
				return defaultValue;
			}
		}
	}


}
