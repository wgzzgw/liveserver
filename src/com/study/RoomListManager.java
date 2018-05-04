package com.study;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RoomListManager {
	private static RoomListManager manager;//����ģʽ
	public static RoomListManager getInstance() {
		if (manager == null) {
			synchronized (RoomListManager.class) {
				if (manager == null) {
					manager = new RoomListManager();
				}
			}
		}
		return manager;
	}
	
	private Map<String, Long> roomMap = new HashMap<String, Long>();//�洢������Ϣ����Ҫ�洢��Ӧ���������ڵ�����ʹ��ʱ��
	private ScheduledExecutorService service = Executors
			.newSingleThreadScheduledExecutor();//��ʱ����ִ��ָ�������񣬵��߳�ʵ��
	private RoomListManager() {
		addExistRooms();//�����ݿ��в�ѯ�Ѿ����ڵ�roomId,�ŵ�roomMap����ȥ
		startMangeTimer();
	}
	private void addExistRooms() {
		// �������ݿ�
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			//��ȡ���ݿ�����
			dbConnection = SqlManager.getConnection();
			//ִ��sql���
			stmt = dbConnection.createStatement();
			String getRoomIdSql = "SELECT `room_id` FROM `RoomInfo`";
			stmt.execute(getRoomIdSql);
			ResultSet resultSet = stmt.getResultSet();
			if (resultSet != null && resultSet.next()) {
				List<String> roomList = new ArrayList<String>();
				while (resultSet.next()) {
					String roomId = resultSet.getInt("room_id") + "";
					updateRoom(roomId);
				}
			} else {
				// ��ѯʧ����
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
	//���·�����Ϣ��������ʹ��ʱ��
	public void updateRoom(String roomId) {
		roomMap.put(roomId, System.currentTimeMillis());
	}
	//ִ�ж�ʱ��������
	private void startMangeTimer() {
		System.out.println("RoomListManager ��ʼ10����ִ�е�����");
	/*
	 * ��ָ��Ƶ������ִ��ĳ������
	 * ����һ��ִ���߳�
	 * ����������ʼ���ӳ�
	 * �����������ο�ʼ��ִ�е���Сʱ����
	 * �����ģ���ʱ��λ
	 */
		service.scheduleWithFixedDelay(command , 0, 10, TimeUnit.SECONDS);
	}
	private Runnable command = new Runnable() {
		@Override
		public void run() {
			// 10����ִ�е����񣬼�ⷿ���б��������ʱ��
			System.out.println("RoomListManager 10����ִ�е�����");
			for (String roomId : roomMap.keySet()) {
				long lastUpdateTime = roomMap.get(roomId);
				if (lastUpdateTime + 10 * 1000 > System.currentTimeMillis()) {
					// ˵�������������Ч��.
					// �������
					System.out.println(roomId + "RoomListManager �����������Ч��");
				} else {
					// ������Ч����Ҫ�����ݿ���ɾ�����roomid������10��û�и��·���
					System.out.println(roomId + "RoomListManager ������Ч����Ҫ�����ݿ���ɾ�����roomid");
					deleteRoom(roomId);
					roomMap.remove(roomId);
					WatcherListManager.getInstance().removeRoom(roomId);
				}
			}
			
		}
	};
	/*
	 * ɾ��ĳ����
	 */
	protected void deleteRoom(String roomId) {
		// �������ݿ�
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			dbConnection = SqlManager.getConnection();//��ȡ���ݿ�����
			//ִ��SQL���
			stmt = dbConnection.createStatement();
			String deleteRoomIdSql = "DELETE FROM `RoomInfo` WHERE `room_id`=\""
					+ roomId + "\"";
			stmt.execute(deleteRoomIdSql);
			int updateCount = stmt.getUpdateCount();// ��ȡ��Ӱ�������
			System.out.println(roomId + "RoomListManager ɾ��Ӱ��������" + updateCount);
		} catch (SQLException e) {
			e.printStackTrace();
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
	public void removeRoom(String roomId) {
		roomMap.remove(roomId);
	}
}
