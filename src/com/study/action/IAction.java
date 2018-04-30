package com.study.action;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 * action基类（接口基类）
 */
public abstract  class IAction {
	/*
	 * 具体逻辑操作方法，子类实现
	 */
	public abstract void doAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException, SQLException;
	/*
	 * 获取请求参数中的值，若有则返回，否则返回默认值
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
	 * 同上,方法重载
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
