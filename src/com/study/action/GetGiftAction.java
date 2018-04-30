package com.study.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.Error;
import com.study.ResponseObject;
import com.study.SqlManager;
import com.study.UserInfo;

public class GetGiftAction extends IAction{
	private static final String RequestParamKey_UserId = "userId";
	private static final String RequestParamKey_GiftExp = "giftExp";

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		//获取到参数后，保存到服务器mysql中去
		String userId = getParam(request, RequestParamKey_UserId, "");//获取请求参数——接收者ID
		int giftExp = getParam(request, RequestParamKey_GiftExp, -1);//获取请求参数——礼物经验值
		if(userId==null||userId.isEmpty()) {
			//报错——没有参数
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(RequestParamKey_UserId));
			failObj.send(response);
			return ;
		}
		if(giftExp<0) {
			//报错——没有参数
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(RequestParamKey_GiftExp));
			failObj.send(response);
			return ;
		}
		dbConnection = SqlManager.getConnection();//获取数据库连接
		//执行SQL语句
		stmt = dbConnection.createStatement();
		//sql语句
		String queryUserSql="SELECT * FROM `userinfo` WHERE `user_id`=\""+userId+"\"";
		stmt.execute(queryUserSql);
		ResultSet userResult=stmt.getResultSet();
		UserInfo userInfo=null;
		if(userResult==null||!userResult.next()) {
			//说明没有用户信息，插入新的用户信息
			boolean insertSuccess=insertNewUser(dbConnection,userId);
			if(insertSuccess) {
				userInfo=updateUserInfo(dbConnection,userId,giftExp);
			}
		}else {
			//有用户信息，更新用户信息
			userInfo=updateUserInfo(dbConnection,userId,giftExp);
		}
		//将用户信息返回，供APP更新数据用
		ResponseObject responseObject = ResponseObject
				.getSuccessResponse(userInfo);
		responseObject.send(response);
	}

	/*
	 * 插入新的用户到用户表中去
	 */
	private boolean insertNewUser(Connection dbConnection, String userId) {
		Statement st=null;
		try {
			st=dbConnection.createStatement();
			String insertUserSql="INSERT INTO `userinfo`(`user_id`, `user_level`, `send_nums`, `get_nums`, `exp`) "
					+"VALUES ("+"\""+userId+"\","+"0,0,0,0"+")";
			st.execute(insertUserSql);
			int updateCount=st.getUpdateCount();
			return (updateCount>0);
		}catch (SQLException e) {
			return false;
		}finally {
			try {
				if(st!=null) {
					st.close();
				}
			}catch (SQLException e) {
			e.printStackTrace();
			}
		}
	}
	/*
	 * 更新用户信息到用户表中去
	 */
	private UserInfo updateUserInfo(Connection dbConnection, String userId, int giftExp) {
		Statement statement=null;
		UserInfo userInfo=null;
		try {
			statement=dbConnection.createStatement();
			String queryUserSql="SELECT * FROM `userinfo` WHERE `user_id`=\""+userId+"\"";
			statement.execute(queryUserSql);
			ResultSet resultSet=statement.getResultSet();
			if(resultSet!=null&&resultSet.next()) {
				//获取用户信息
				int dbGetNum = 0;
				int dbSendNum = 0;
				int dbExp = 0;
				int dbLevel = 0;

				dbGetNum = resultSet.getInt("get_nums");
				dbSendNum = resultSet.getInt("send_nums");
				dbExp = resultSet.getInt("exp");
				dbLevel = resultSet.getInt("user_level");
			
				//增加新的个数和经验
				dbGetNum++;
				dbExp+=giftExp/2;
				dbLevel=dbExp/200+1;
				//更新数据库
				String updateUserSql="UPDATE `userinfo` SET "
						+ "`user_level`="+dbLevel+",`get_nums`="+dbGetNum+",`exp`="+dbExp+
						" WHERE `user_id`=\""+userId+"\"";
				statement.execute(updateUserSql);
				int updateCount=statement.getUpdateCount();
				if(updateCount>0) {
					//更新成功
					userInfo=new UserInfo();
					userInfo.user_id=userId;
					userInfo.send_nums=dbSendNum;
					userInfo.get_nums=dbGetNum;
					userInfo.user_level=dbLevel;
				}
			}
			return userInfo;
		}catch (SQLException e) {
			return null;
		}finally {
			if(statement!=null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
