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
 * 创建房间接口
 */
public class CreateRoomAction extends IAction{
	
	
	private static final String RequestParamKey_UserId = "userId";//主播ID
	private static final String RequestParamKey_UserAvatar = "userAvatar";//主播头像
	private static final String RequestParamKey_UserName = "userName";//主播昵称
	private static final String RequestParamKey_LiveTitle = "liveTitle";//直播标题
	private static final String RequestParamKey_LiveCover = "liveCover";//直播封面

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			//获取到参数后，保存到服务器mysql中去
			String userId = getParam(request, RequestParamKey_UserId, "");//获取请求参数——主播ID
			String userName = getParam(request, RequestParamKey_UserName, "");//获取请求参数——主播昵称
			String userAvatar = getParam(request, RequestParamKey_UserAvatar, "");//获取请求参数——主播头像
			String liveTitle = getParam(request, RequestParamKey_LiveTitle, "");//获取请求参数——主播标题
			String liveCover = getParam(request, RequestParamKey_LiveCover, "");//获取请求参数——主播封面
			if(userId==null||userId.isEmpty()) {
				//报错——没有参数
				ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
						Error.getNoRequestParamMsg(RequestParamKey_UserId));
				failObj.send(response);
				return ;
			}
			dbConnection = SqlManager.getConnection();//获取数据库连接
			//执行SQL语句
			stmt = dbConnection.createStatement();

			//先删除之前可能没有删除的房间号
			/*QuitRoomAction.createrQuit( userId, null);*/

			//sql语句
			String sqlStr="INSERT INTO `roominfo`(`room_id`, `user_id`, `user_avatar`,"
					+ " `live_cover`, `live_title`, `wathcer`, `user_name`) "
					+ "VALUES ("+"0,"+"\""+userId+"\""+","+"\""+userAvatar+"\""+","+"\""+liveCover+"\""+","+
					"\""+liveTitle+"\""+",0,"+"\""+userName+"\""+")";
			stmt.execute(sqlStr);
			int updateCount=stmt.getUpdateCount();//返回受影响的行数
			//插入成功
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
					RoomListManager.getInstance().updateRoom(""+ roomInfo.roomId);//直播房间列表更新
					WatcherListManager.getInstance().addRoom(""+ roomInfo.roomId);//观众列表更新
					//获取到Roomid后，将结果发送出去
					ResponseObject responseObject = ResponseObject
							.getSuccessResponse(roomInfo);
					responseObject.send(response);
				} else {
					// 查询失败了
					ResponseObject responseObject = ResponseObject
							.getFailResponse(Error.errorCode_QueryFail,
									Error.getQueryFailMsg());
					responseObject.send(response);
				}
			} else {
				// 插入失败了。
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_CreateFail, Error.getCreateFailMsg());
				responseObject.send(response);
			}
		} finally {
			//防止stmt,dbconnection长期占用数据库资源
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