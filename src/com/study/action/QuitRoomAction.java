package com.study.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.study.Error;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.ResponseObject;
import com.study.RoomListManager;
import com.study.SqlManager;
import com.study.WatcherListManager;
//����������˳���ɾ�����ݿ��RoomID,watcherlistManager��roomID,RoomListManager��RoomID
//����ǹ����˳���ɾ��watcherlistManager��userID���������ݿ��watcherNUm
public class QuitRoomAction extends IAction{
	private static final String RequestParamKey_UserId = "userId";//�û�ID
	private static final String RequestParamKey_RoomId = "roomId";//�����ID

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			String userIdParam = getParam(request, RequestParamKey_UserId, "");
			int roomIdParam = getParam(request, RequestParamKey_RoomId, -1);

			if (roomIdParam < 0) {
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_NoRequestParam,
						Error.getNoRequestParamMsg(RequestParamKey_RoomId
								+ "ֵ<0"));
				responseObject.send(response);
				return;
			}

			dbConnection = SqlManager.getConnection();//��ȡ���ݿ�����
			//ִ��SQL���
			stmt = dbConnection.createStatement();
			

			String queryRoomIdSql = "SELECT `user_id`,`wathcer` FROM `RoomInfo` WHERE `room_id`="
					+ roomIdParam ;
			stmt.execute(queryRoomIdSql);
			ResultSet resultSet = stmt.getResultSet();
			if (resultSet != null) {
				while (resultSet.next()) {
					int watchNums = resultSet.getInt("wathcer");
					String userId = resultSet.getString("user_id");
					if (userId != null && userId.equals(userIdParam)) {
						// ˵���������˳�
						createrQuit( userId, response);
						RoomListManager.getInstance().removeRoom(""+ roomIdParam);
						WatcherListManager.getInstance().removeRoom(""+ roomIdParam);
					} else {
						// ˵���ǹ����˳�
						watcherQuit(roomIdParam, response, watchNums);
						WatcherListManager.getInstance().removeWatcher("" +roomIdParam,userIdParam );
					}

				}
			} else {
				// ��ѯʧ���ˣ���Ϊ���˳��ɹ������������Ƴ��󣬹������˳�ʱ
				ResponseObject responseObject = ResponseObject
						.getSuccessResponse(null);
				responseObject.send(response);
			}

		} finally {
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
	//�����˳������ٷ����
	public static void createrQuit( String userId,
			HttpServletResponse response) throws SQLException, IOException {
		Connection dbConnection = null;
		Statement stmt = null;
		dbConnection = SqlManager.getConnection();
		stmt = dbConnection.createStatement();
		String deleteRoomIdSql = "DELETE FROM `RoomInfo` WHERE `user_id`=\""
				+ userId + "\"";
		stmt.execute(deleteRoomIdSql);
		int updateCount = stmt.getUpdateCount();// ��ȡ��Ӱ�������
		// �����ɹ�
		if (updateCount > 0) {
			ResponseObject responseObject = ResponseObject
					.getSuccessResponse(null);
			responseObject.send(response);
		} else {
			ResponseObject responseObject = ResponseObject.getFailResponse(
					Error.errorCode_QuitFail, Error.getQuitFailMsg());
			responseObject.send(response);
		}
	}
	//�����˳�ֱ�����䣬���¹ۿ�����
	private void watcherQuit( int roomIdParam,
			HttpServletResponse response, int watchNums) throws SQLException,
			IOException {

		int fianlWatchNums = (watchNums - 1);
		if (fianlWatchNums < 0) {
			fianlWatchNums = 0;
		}
		Connection dbConnection = null;
		Statement stmt = null;
		dbConnection = SqlManager.getConnection();
		stmt = dbConnection.createStatement();
		String updateWatcherNumsSql = "UPDATE `RoomInfo` SET `wathcer`=\""
				+ fianlWatchNums + "\" WHERE `room_id`=\"" + roomIdParam + "\"";
		stmt.execute(updateWatcherNumsSql);
		int updateCount = stmt.getUpdateCount();// ��ȡ��Ӱ�������
		// �����ɹ�
		if (updateCount > 0) {
			ResponseObject responseObject = ResponseObject
					.getSuccessResponse(null);
			responseObject.send(response);
		} else {
			ResponseObject responseObject = ResponseObject.getFailResponse(
					Error.errorCode_QuitFail, Error.getQuitFailMsg());
			responseObject.send(response);
		}
	}

}
