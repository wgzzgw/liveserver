package com.study.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.ResponseObject;
import com.study.RoomInfo;
import com.study.RoomListManager;
import com.study.SqlManager;
import com.study.WatcherListManager;
import com.study.Error;
/*
 * ��������ӿ�
 */
public class CreateRoomAction extends IAction{
	
	
	private static final String RequestParamKey_UserId = "userId";//����ID
	private static final String RequestParamKey_UserAvatar = "userAvatar";//����ͷ��
	private static final String RequestParamKey_UserName = "userName";//�����ǳ�
	private static final String RequestParamKey_LiveTitle = "liveTitle";//ֱ������
	private static final String RequestParamKey_LiveCover = "liveCover";//ֱ������

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			//��ȡ�������󣬱��浽������mysql��ȥ
			String userId = getParam(request, RequestParamKey_UserId, "");//��ȡ���������������ID
			String userName = getParam(request, RequestParamKey_UserName, "");//��ȡ����������������ǳ�
			String userAvatar = getParam(request, RequestParamKey_UserAvatar, "");//��ȡ���������������ͷ��
			String liveTitle = getParam(request, RequestParamKey_LiveTitle, "");//��ȡ�������������������
			String liveCover = getParam(request, RequestParamKey_LiveCover, "");//��ȡ�������������������
			if(userId==null||userId.isEmpty()) {
				//������û�в���
				ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
						Error.getNoRequestParamMsg(RequestParamKey_UserId));
				failObj.send(response);
				return ;
			}
			dbConnection = SqlManager.getConnection();//��ȡ���ݿ�����
			//ִ��SQL���
			stmt = dbConnection.createStatement();

			//��ɾ��֮ǰ����û��ɾ���ķ����
			/*QuitRoomAction.createrQuit( userId, null);*/

			//sql���
			String sqlStr="INSERT INTO `roominfo`(`room_id`, `user_id`, `user_avatar`,"
					+ " `live_cover`, `live_title`, `wathcer`, `user_name`) "
					+ "VALUES ("+"0,"+"\""+userId+"\""+","+"\""+userAvatar+"\""+","+"\""+liveCover+"\""+","+
					"\""+liveTitle+"\""+",0,"+"\""+userName+"\""+")";
			stmt.execute(sqlStr);
			int updateCount=stmt.getUpdateCount();//������Ӱ�������
			//����ɹ�
			if (updateCount > 0) {
				String queryRoomIdSql = "SELECT `room_id`,`wathcer` FROM `RoomInfo` WHERE `user_id`=\""
						+ userId + "\"";
				stmt.execute(queryRoomIdSql);
				ResultSet resultSet = stmt.getResultSet();
				if (resultSet != null) {
					RoomInfo roomInfo = new RoomInfo();
					while (resultSet.next()) {
						int roomId = resultSet.getInt("room_id");
						int watchNums = resultSet.getInt("wathcer");
						roomInfo.roomId = roomId;
						roomInfo.userId = userId;
						roomInfo.userName = userName;
						roomInfo.userAvatar = userAvatar;
						roomInfo.liveTitle = liveTitle;
						roomInfo.liveCover = liveCover;
						roomInfo.wathcer= watchNums;
					}
					RoomListManager.getInstance().updateRoom(""+ roomInfo.roomId);//ֱ�������б����
					WatcherListManager.getInstance().addRoom(""+ roomInfo.roomId);//�����б����
					//��ȡ��Roomid�󣬽�������ͳ�ȥ
					ResponseObject responseObject = ResponseObject
							.getSuccessResponse(roomInfo);
					responseObject.send(response);
				} else {
					// ��ѯʧ����
					ResponseObject responseObject = ResponseObject
							.getFailResponse(Error.errorCode_QueryFail,
									Error.getQueryFailMsg());
					responseObject.send(response);
				}
			} else {
				// ����ʧ���ˡ�
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_CreateFail, Error.getCreateFailMsg());
				responseObject.send(response);
			}
		} finally {
			//��ֹstmt,dbconnection����ռ�����ݿ���Դ
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}
}