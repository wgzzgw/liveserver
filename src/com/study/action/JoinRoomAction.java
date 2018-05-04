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

public class JoinRoomAction extends IAction{
	private static final String RequestParamKey_UserId = "userId";//�û�ID
	private static final String RequestParamKey_RoomId = "roomId";//����ID
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			String userIdParam = getParam(request, RequestParamKey_UserId, "");
			int roomIdParam = getParam(request, RequestParamKey_RoomId, -1);

			if (roomIdParam < 0) {
				//����Ų�����
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_NoRequestParam,
						Error.getNoRequestParamMsg(RequestParamKey_RoomId + "ֵ<0"));
				responseObject.send(response);
				return;
			}

			dbConnection = SqlManager.getConnection();//��ȡ���ݿ�����
			//ִ��SQL���
			stmt = dbConnection.createStatement();
			// ���ݿ���Ѿ���ȫ���������ˡ�

			String queryRoomIdSql = "SELECT `user_id`,`watcher` FROM `RoomInfo` WHERE `room_id`=\""
					+ roomIdParam + "\"";
			stmt.execute(queryRoomIdSql);
			ResultSet resultSet = stmt.getResultSet();
			if (resultSet != null && resultSet.next()) {
				while (resultSet.next()) {
					int watchNums = resultSet.getInt("watcher_nums");
					String userId = resultSet.getString("user_id");
					if (userId != null && userId.equals(userIdParam)) {
						// ˵������������,����room�����ʹ��ʱ��
						RoomListManager.getInstance().updateRoom(userId);
					} else {
						// ˵���ǹ��ڼ���
						WatcherListManager.getInstance().updateRoomUser(roomIdParam+"", userId);
						watcherJoin(stmt, roomIdParam, response, watchNums);
					}
				}
			} else {
				// ��ѯʧ���ˣ�Ҳ��Ϊ����ӳɹ���
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
	private void watcherJoin(Statement stmt, int roomIdParam,
			HttpServletResponse response, int watchNums) throws SQLException,
			IOException {

		int fianlWatchNums = (watchNums + 1);

		String updateWatcherNumsSql = "UPDATE `RoomInfo` SET `watcher`=\""
				+ fianlWatchNums + "\" WHERE `room_id`=\"" + roomIdParam + "\"";
		stmt.execute(updateWatcherNumsSql);

		// �ɹ����ɹ�����Ϊ�ɹ��ˡ�
		ResponseObject responseObject = ResponseObject.getSuccessResponse(null);
		responseObject.send(response);
	}


}
