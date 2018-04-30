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
		//��ȡ�������󣬱��浽������mysql��ȥ
		String userId = getParam(request, RequestParamKey_UserId, "");//��ȡ�����������������ID
		int giftExp = getParam(request, RequestParamKey_GiftExp, -1);//��ȡ��������������ﾭ��ֵ
		if(userId==null||userId.isEmpty()) {
			//������û�в���
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(RequestParamKey_UserId));
			failObj.send(response);
			return ;
		}
		if(giftExp<0) {
			//������û�в���
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(RequestParamKey_GiftExp));
			failObj.send(response);
			return ;
		}
		dbConnection = SqlManager.getConnection();//��ȡ���ݿ�����
		//ִ��SQL���
		stmt = dbConnection.createStatement();
		//sql���
		String queryUserSql="SELECT * FROM `userinfo` WHERE `user_id`=\""+userId+"\"";
		stmt.execute(queryUserSql);
		ResultSet userResult=stmt.getResultSet();
		UserInfo userInfo=null;
		if(userResult==null||!userResult.next()) {
			//˵��û���û���Ϣ�������µ��û���Ϣ
			boolean insertSuccess=insertNewUser(dbConnection,userId);
			if(insertSuccess) {
				userInfo=updateUserInfo(dbConnection,userId,giftExp);
			}
		}else {
			//���û���Ϣ�������û���Ϣ
			userInfo=updateUserInfo(dbConnection,userId,giftExp);
		}
		//���û���Ϣ���أ���APP����������
		ResponseObject responseObject = ResponseObject
				.getSuccessResponse(userInfo);
		responseObject.send(response);
	}

	/*
	 * �����µ��û����û�����ȥ
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
	 * �����û���Ϣ���û�����ȥ
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
				//��ȡ�û���Ϣ
				int dbGetNum = 0;
				int dbSendNum = 0;
				int dbExp = 0;
				int dbLevel = 0;

				dbGetNum = resultSet.getInt("get_nums");
				dbSendNum = resultSet.getInt("send_nums");
				dbExp = resultSet.getInt("exp");
				dbLevel = resultSet.getInt("user_level");
			
				//�����µĸ����;���
				dbGetNum++;
				dbExp+=giftExp/2;
				dbLevel=dbExp/200+1;
				//�������ݿ�
				String updateUserSql="UPDATE `userinfo` SET "
						+ "`user_level`="+dbLevel+",`get_nums`="+dbGetNum+",`exp`="+dbExp+
						" WHERE `user_id`=\""+userId+"\"";
				statement.execute(updateUserSql);
				int updateCount=statement.getUpdateCount();
				if(updateCount>0) {
					//���³ɹ�
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
