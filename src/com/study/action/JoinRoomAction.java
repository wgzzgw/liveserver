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
	private static final String RequestParamKey_UserId = "userId";//用户ID
	private static final String RequestParamKey_RoomId = "roomId";//房间ID
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			String userIdParam = getParam(request, RequestParamKey_UserId, "");
			int roomIdParam = getParam(request, RequestParamKey_RoomId, -1);

			if (roomIdParam < 0) {
				//房间号不存在
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_NoRequestParam,
						Error.getNoRequestParamMsg(RequestParamKey_RoomId + "值<0"));
				responseObject.send(response);
				return;
			}

			dbConnection = SqlManager.getConnection();//获取数据库连接
			//执行SQL语句
			stmt = dbConnection.createStatement();
			// 数据库就已经完全建立起来了。

			String queryRoomIdSql = "SELECT `user_id`,`watcher` FROM `RoomInfo` WHERE `room_id`=\""
					+ roomIdParam + "\"";
			stmt.execute(queryRoomIdSql);
			ResultSet resultSet = stmt.getResultSet();
			if (resultSet != null && resultSet.next()) {
				while (resultSet.next()) {
					int watchNums = resultSet.getInt("watcher_nums");
					String userId = resultSet.getString("user_id");
					if (userId != null && userId.equals(userIdParam)) {
						// 说明是主播加入,更新room的最后使用时间
						RoomListManager.getInstance().updateRoom(userId);
					} else {
						// 说明是观众加入
						WatcherListManager.getInstance().updateRoomUser(roomIdParam+"", userId);
						watcherJoin(stmt, roomIdParam, response, watchNums);
					}
				}
			} else {
				// 查询失败了，也认为是添加成功。
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

		// 成功不成功都认为成功了。
		ResponseObject responseObject = ResponseObject.getSuccessResponse(null);
		responseObject.send(response);
	}


}
