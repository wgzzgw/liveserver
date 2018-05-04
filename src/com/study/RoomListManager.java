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
	private static RoomListManager manager;//单例模式
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
	
	private Map<String, Long> roomMap = new HashMap<String, Long>();//存储房间信息，主要存储对应房间所存在的最新使用时间
	private ScheduledExecutorService service = Executors
			.newSingleThreadScheduledExecutor();//定时周期执行指定的任务，单线程实例
	private RoomListManager() {
		addExistRooms();//从数据库中查询已经存在的roomId,放到roomMap里面去
		startMangeTimer();
	}
	private void addExistRooms() {
		// 操作数据库
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			//获取数据库连接
			dbConnection = SqlManager.getConnection();
			//执行sql语句
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
				// 查询失败了
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
	//更新房间信息——最后的使用时间
	public void updateRoom(String roomId) {
		roomMap.put(roomId, System.currentTimeMillis());
	}
	//执行定时周期任务
	private void startMangeTimer() {
		System.out.println("RoomListManager 开始10秒钟执行的任务");
	/*
	 * 按指定频率周期执行某个任务
	 * 参数一：执行线程
	 * 参数二：初始化延迟
	 * 参数三：两次开始的执行的最小时间间隔
	 * 参数四：计时单位
	 */
		service.scheduleWithFixedDelay(command , 0, 10, TimeUnit.SECONDS);
	}
	private Runnable command = new Runnable() {
		@Override
		public void run() {
			// 10秒钟执行的任务，检测房间列表的最后更新时间
			System.out.println("RoomListManager 10秒钟执行的任务");
			for (String roomId : roomMap.keySet()) {
				long lastUpdateTime = roomMap.get(roomId);
				if (lastUpdateTime + 10 * 1000 > System.currentTimeMillis()) {
					// 说明这个房间是有效的.
					// 无需操作
					System.out.println(roomId + "RoomListManager 这个房间是有效的");
				} else {
					// 房间无效，需要从数据库中删除这个roomid，超过10秒没有更新房间
					System.out.println(roomId + "RoomListManager 房间无效，需要从数据库中删除这个roomid");
					deleteRoom(roomId);
					roomMap.remove(roomId);
					WatcherListManager.getInstance().removeRoom(roomId);
				}
			}
			
		}
	};
	/*
	 * 删除某房间
	 */
	protected void deleteRoom(String roomId) {
		// 操作数据库
		Connection dbConnection = null;
		Statement stmt = null;
		try {
			dbConnection = SqlManager.getConnection();//获取数据库连接
			//执行SQL语句
			stmt = dbConnection.createStatement();
			String deleteRoomIdSql = "DELETE FROM `RoomInfo` WHERE `room_id`=\""
					+ roomId + "\"";
			stmt.execute(deleteRoomIdSql);
			int updateCount = stmt.getUpdateCount();// 获取受影响的行数
			System.out.println(roomId + "RoomListManager 删除影响行数：" + updateCount);
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
