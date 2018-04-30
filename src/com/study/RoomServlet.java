package com.study;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.action.CreateRoomAction;
import com.study.action.GetRoomListAction;
import com.study.action.GetWatchersAction;
import com.study.action.HeartBeatAction;
import com.study.action.JoinRoomAction;
import com.study.action.QuitRoomAction;


public class RoomServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String RequestParamKey_Action = "action";//请求what action
	
	//action类型
	private static final String RequestAction_Create = "create";//创建房间
	private static final String RequestAction_Join = "join";//加入房间
	private static final String RequestAction_Quit = "quit";//退出房间
	private static final String RequestAction_GetList = "getList";//直播列表
	private static final String RequestAction_GetWatcher = "getWatcher";//观众人数
	private static final String RequestAction_HeartBeat = "heartBeat";//心跳包

	
	/*private static final String Param_User_id = "userId";//主播ID
	private static final String Param_User_avatar = "userAvatar";//主播头像
	private static final String Param_User_name = "userName";//主播昵称
	private static final String Param_Live_cover = "liveCover";//直播封面
	private static final String Param_Live_title = "liveTitle";//直播标题
*/	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		// 处理用户的请求，判断action请求
			String action = req.getParameter(RequestParamKey_Action);
			if (action == null || "".equals(action)) {
				/*
				 * 空action处理
				 */
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_NoAction, Error.getNoActionMsg());
				responseObject.send(resp);
				return;
			}
			try {
				if (RequestAction_Create.equals(action)) {
					// 创建一个直播房间。
					new CreateRoomAction().doAction(req, resp);
				} else if (RequestAction_Join.equals(action)) {
					// 加入一个直播房间。
					new JoinRoomAction().doAction(req, resp);
				} else if (RequestAction_Quit.equals(action)) {
					// 退出一个直播房间。
					new QuitRoomAction().doAction(req, resp);
				} else if (RequestAction_GetList.equals(action)) {
					// 获取直播房间列表。
					new GetRoomListAction().doAction(req, resp);
				} else if (RequestAction_GetWatcher.equals(action)) {
					// 获取房间中的观众
					new GetWatchersAction().doAction(req, resp);
				} else if (RequestAction_HeartBeat.equals(action)) {
					// 心跳包
					new HeartBeatAction().doAction(req, resp);
				}else {
					ResponseObject responseObject = ResponseObject.getFailResponse(
							Error.errorCode_NoRequestParam,
							Error.getNoRequestParamMsg(RequestParamKey_Action));
					responseObject.send(resp);
				}
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				// 数据库异常，返回错误信息
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_Exception,
						Error.getExceptionMsg(e.getMessage()));
				responseObject.send(resp);
			}
		}

		/*//获取请求参数——主播ID
		String userId = req.getParameter(Param_User_id);
		if(userId==null||userId.isEmpty()) {
			//报错——没有参数
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(Param_User_id));
			failObj.send(resp);
			return ;
		}
		//获取请求参数——主播昵称
		String userName  = req.getParameter(Param_User_name);
		if(userName==null) {
			userName="";
		}
		//获取请求参数——主播头像
		String userAvatar  = req.getParameter(Param_User_avatar);
		if(userAvatar==null) {
			userAvatar="";
		}
		//获取请求参数——直播标题
		String liveTitle  = req.getParameter(Param_Live_title);
		if(liveTitle==null) {
			liveTitle="";
		}
		//获取请求参数——直播封面
		String liveCover  = req.getParameter(Param_Live_cover);
		if(liveCover ==null) {
			liveCover ="";
		}
		//获取到参数后，保存到服务器mysql中去
		String driveName="jdbc:mysql://";
		String host="192.168.1.90";//主机号
		String port="30431";//端口
		String dbName="d34deca5";//数据库名字
		String url=driveName+host+":"+port+"/"+dbName;
		String user="fdcb1942";//数据库账号
		String password="2b7613ef";//数据库密码
		
		Connection dbcon=null;
		Statement st=null;
		try {
			//获取数据库连接
			 dbcon=DriverManager.getConnection(url, user,password);
			//执行SQL语句
			 st=dbcon.createStatement();
			//sql语句
			String sqlStr="INSERT INTO `roominfo`(`room_id`, `user_id`, `user_avatar`,"
					+ " `live_cover`, `live_title`, `wathcer`, `user_name`) "
					+ "VALUES ("+"0,"+"\""+userId+"\""+","+"\""+userAvatar+"\""+","+"\""+liveCover+"\""+","+
					"\""+liveTitle+"\""+",0,"+"\""+userName+"\""+")";
			st.execute(sqlStr);
			int updateCount=st.getUpdateCount();//返回受影响的行数
			if(updateCount>0) {
				//插入执行成功
				//String queryRoomSql="SELECT `room_id` FROM `roominfo` WHERE `user_id` ="+"\""+userId+"\"";
				String queryRoomSql="SELECT * FROM `roominfo` WHERE `user_id` ="+"\""+userId+"\"";
				try {
				st.execute(queryRoomSql);
				ResultSet rs=st.getResultSet();
				if(rs!=null&&!rs.wasNull()) {
					int roomId=0;
					RoomInfo roomInfo = new RoomInfo();
					while(rs.next()) {
						roomId=rs.getInt("room_id");
						int roomId = rs.getInt("room_id");
						int watchNums = rs.getInt("watcher_nums");
						roomInfo.roomId = roomId;
						roomInfo.userId = userId;
						roomInfo.userName = userName;
						roomInfo.userAvatar = userAvatar;
						roomInfo.liveTitle = liveTitle;
						roomInfo.liveCover = liveCover;
						roomInfo.watcherNums = watchNums;
					}
					//获取到Roomid后，将结果发送出去
					PrintWriter writer=resp.getWriter();
					writer.println(roomId+"");
					ResponseObject responseObject = ResponseObject
							.getSuccessResponse(roomInfo);
					responseObject.send(resp);

				}
			}
				catch (SQLException e) {
					e.printStackTrace();
					// 查询失败了
					ResponseObject responseObject = ResponseObject
							.getFailResponse(Error.errorCode_Exception,
									Error.getExceptionMsg(e.getMessage()));
					responseObject.send(resp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			//防止st,dbcon长期占用数据库资源
			try {
				if (st != null) {
					st.close();
				}
				if (dbcon != null) {
					dbcon.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	// http://XXXX.com?action=create&userId=xxx&userAvatar=xxx&...
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
