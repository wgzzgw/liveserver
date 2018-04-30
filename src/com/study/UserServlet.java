package com.study;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.action.CreateRoomAction;
import com.study.action.GetGiftAction;
import com.study.action.GetRoomListAction;
import com.study.action.SendGiftAction;

public class UserServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String RequestParamKey_Action = "action";//����what action
	
	//action����
	private static final String RequestAction_SendGift = "sendGift";//��������action
	private static final String RequestAction_GetGift = "getGift";//��������action

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
				// �����û��������ж�action����
					String action = req.getParameter(RequestParamKey_Action);
					if (action == null || "".equals(action)) {
						/*
						 * ��action����
						 */
						ResponseObject responseObject = ResponseObject.getFailResponse(
								Error.errorCode_NoAction, Error.getNoActionMsg());
						responseObject.send(resp);
						return;
					}
					if (RequestAction_SendGift.equals(action)) {
						try {
							new SendGiftAction().doAction(req, resp);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (RequestAction_GetGift.equals(action)) {
						try {
							new GetGiftAction().doAction(req, resp);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						ResponseObject responseObject = ResponseObject.getFailResponse(
								Error.errorCode_NoRequestParam,
								Error.getNoRequestParamMsg(RequestParamKey_Action));
						responseObject.send(resp);
					}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}
}
