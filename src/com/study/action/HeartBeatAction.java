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
import com.study.RoomListManager;
import com.study.SqlManager;
import com.study.WatcherListManager;

/*
 * �����������ַ���˺Ϳͻ��˵�ʱ������
 */
public class HeartBeatAction extends IAction {
	private static final String RequestParamKey_UserId = "userId";
	private static final String RequestParamKey_RoomId = "roomId";
	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException, SQLException {
		
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

			dbConnection = SqlManager.getConnection();
			stmt = dbConnection.createStatement();

			String queryRoomIdSql = "SELECT `user_id` FROM `RoomInfo` WHERE `room_id`=\""
					+ roomIdParam + "\"";
			stmt.execute(queryRoomIdSql);
			ResultSet resultSet = stmt.getResultSet();
			if (resultSet != null && resultSet.next()) {
				while (resultSet.next()) {
					String userId = resultSet.getString("user_id");
					if (userId != null && userId.equals(userIdParam)) {
						// ˵������������
						RoomListManager.getInstance().updateRoom(roomIdParam +"");
					} else {
						// ˵���ǹ�������
						WatcherListManager.getInstance().updateRoomUser(roomIdParam + "", userIdParam);
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

}
