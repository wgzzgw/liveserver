package com.study.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.Error;
import com.study.ResponseObject;
import com.study.RoomInfo;
import com.study.SqlManager;

public class GetRoomListAction  extends IAction{
	private static final String RequestParamKey_PageIndex = "pageIndex";//页数
	private static final int PageSize = 20;//页内大小
	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection dbConnection = null;
		Statement stmt = null;
		int pageIndex =-1;
		try {
			 pageIndex = getParam(request, RequestParamKey_PageIndex, 0);//获取请求参数——页数
			
			dbConnection = SqlManager.getConnection();//获取数据库连接
			//执行SQL语句
			stmt = dbConnection.createStatement();
			//sql语句
			String queryRoomListSql = "SELECT * FROM `RoomInfo` LIMIT "
					+ pageIndex + " , " + PageSize;
			stmt.execute(queryRoomListSql);
			
			ResultSet resultSet = stmt.getResultSet();
			if (resultSet != null&&resultSet.next()) {
			//查询成功
					//存储查询后的数据-list
				List<RoomInfo> roomList = new ArrayList<RoomInfo>();
				while (resultSet.next()) {
					RoomInfo roomInfo = new RoomInfo();
					roomInfo.roomId = resultSet.getInt("room_id");
					roomInfo.userId = resultSet.getString("user_id");
					roomInfo.userName = resultSet.getString("user_name");
					roomInfo.userAvatar = resultSet.getString("user_avatar");
					roomInfo.liveTitle = resultSet.getString("live_title");
					roomInfo.liveCover = resultSet.getString("live_poster");
					roomInfo.wathcer = resultSet.getInt("wathcer");
					roomList.add(roomInfo);
				}
				//获取到数据后，，将结果发送出去
				ResponseObject responseObject = ResponseObject
						.getSuccessResponse(roomList);
				responseObject.send(response);
			}else {
				// 查询失败了
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_QueryListFail, Error.getQueryListFailMsg());
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
		if(pageIndex<0) {
			//报错——请求页数参数不正确
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_ErrorParam,
					Error.getErrorParamMsg(RequestParamKey_PageIndex));
			failObj.send(response);
			return ;
		}
	}

}
