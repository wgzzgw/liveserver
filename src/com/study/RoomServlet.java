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
	
	private static final String RequestParamKey_Action = "action";//����what action
	
	//action����
	private static final String RequestAction_Create = "create";//��������
	private static final String RequestAction_Join = "join";//���뷿��
	private static final String RequestAction_Quit = "quit";//�˳�����
	private static final String RequestAction_GetList = "getList";//ֱ���б�
	private static final String RequestAction_GetWatcher = "getWatcher";//��������
	private static final String RequestAction_HeartBeat = "heartBeat";//������

	
	/*private static final String Param_User_id = "userId";//����ID
	private static final String Param_User_avatar = "userAvatar";//����ͷ��
	private static final String Param_User_name = "userName";//�����ǳ�
	private static final String Param_Live_cover = "liveCover";//ֱ������
	private static final String Param_Live_title = "liveTitle";//ֱ������
*/	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		// �����û��������ж�action����
			String action = req.getParameter(RequestParamKey_Action);
			if (action == null || "".equals(action)) {
				/*
				 * ��action����
				 */
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_NoAction, Error.getNoActionMsg());
				responseObject.send(resp);
				return;
			}
			try {
				if (RequestAction_Create.equals(action)) {
					// ����һ��ֱ�����䡣
					new CreateRoomAction().doAction(req, resp);
				} else if (RequestAction_Join.equals(action)) {
					// ����һ��ֱ�����䡣
					new JoinRoomAction().doAction(req, resp);
				} else if (RequestAction_Quit.equals(action)) {
					// �˳�һ��ֱ�����䡣
					new QuitRoomAction().doAction(req, resp);
				} else if (RequestAction_GetList.equals(action)) {
					// ��ȡֱ�������б�
					new GetRoomListAction().doAction(req, resp);
				} else if (RequestAction_GetWatcher.equals(action)) {
					// ��ȡ�����еĹ���
					new GetWatchersAction().doAction(req, resp);
				} else if (RequestAction_HeartBeat.equals(action)) {
					// ������
					new HeartBeatAction().doAction(req, resp);
				}else {
					ResponseObject responseObject = ResponseObject.getFailResponse(
							Error.errorCode_NoRequestParam,
							Error.getNoRequestParamMsg(RequestParamKey_Action));
					responseObject.send(resp);
				}
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				// ���ݿ��쳣�����ش�����Ϣ
				ResponseObject responseObject = ResponseObject.getFailResponse(
						Error.errorCode_Exception,
						Error.getExceptionMsg(e.getMessage()));
				responseObject.send(resp);
			}
		}

		/*//��ȡ���������������ID
		String userId = req.getParameter(Param_User_id);
		if(userId==null||userId.isEmpty()) {
			//������û�в���
			ResponseObject failObj=ResponseObject.getFailResponse(Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(Param_User_id));
			failObj.send(resp);
			return ;
		}
		//��ȡ����������������ǳ�
		String userName  = req.getParameter(Param_User_name);
		if(userName==null) {
			userName="";
		}
		//��ȡ���������������ͷ��
		String userAvatar  = req.getParameter(Param_User_avatar);
		if(userAvatar==null) {
			userAvatar="";
		}
		//��ȡ�����������ֱ������
		String liveTitle  = req.getParameter(Param_Live_title);
		if(liveTitle==null) {
			liveTitle="";
		}
		//��ȡ�����������ֱ������
		String liveCover  = req.getParameter(Param_Live_cover);
		if(liveCover ==null) {
			liveCover ="";
		}
		//��ȡ�������󣬱��浽������mysql��ȥ
		String driveName="jdbc:mysql://";
		String host="192.168.1.90";//������
		String port="30431";//�˿�
		String dbName="d34deca5";//���ݿ�����
		String url=driveName+host+":"+port+"/"+dbName;
		String user="fdcb1942";//���ݿ��˺�
		String password="2b7613ef";//���ݿ�����
		
		Connection dbcon=null;
		Statement st=null;
		try {
			//��ȡ���ݿ�����
			 dbcon=DriverManager.getConnection(url, user,password);
			//ִ��SQL���
			 st=dbcon.createStatement();
			//sql���
			String sqlStr="INSERT INTO `roominfo`(`room_id`, `user_id`, `user_avatar`,"
					+ " `live_cover`, `live_title`, `wathcer`, `user_name`) "
					+ "VALUES ("+"0,"+"\""+userId+"\""+","+"\""+userAvatar+"\""+","+"\""+liveCover+"\""+","+
					"\""+liveTitle+"\""+",0,"+"\""+userName+"\""+")";
			st.execute(sqlStr);
			int updateCount=st.getUpdateCount();//������Ӱ�������
			if(updateCount>0) {
				//����ִ�гɹ�
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
					//��ȡ��Roomid�󣬽�������ͳ�ȥ
					PrintWriter writer=resp.getWriter();
					writer.println(roomId+"");
					ResponseObject responseObject = ResponseObject
							.getSuccessResponse(roomInfo);
					responseObject.send(resp);

				}
			}
				catch (SQLException e) {
					e.printStackTrace();
					// ��ѯʧ����
					ResponseObject responseObject = ResponseObject
							.getFailResponse(Error.errorCode_Exception,
									Error.getExceptionMsg(e.getMessage()));
					responseObject.send(resp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			//��ֹst,dbcon����ռ�����ݿ���Դ
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
