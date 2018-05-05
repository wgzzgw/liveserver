package com.study;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WatcherListManager {
	private static WatcherListManager manager;//单例模式
	public static WatcherListManager getInstance()
	{
		if(manager == null){
			synchronized (WatcherListManager.class) {
				if(manager == null){
					manager = new WatcherListManager();
				}
			}
		}
		return manager;
	}
	private WatcherListManager()
	{
	}
	/*
	 * watcherupdate
	 * key:房间号,value:观众的信息 id:time
	 */
	private Map<String,Map<String,Long>> watcherMap = new HashMap<String,Map<String,Long>>();
	/*
	 * roomupdate
	 */
	private Map<String,ScheduledExecutorService> roomUpdateTimerMap = new HashMap<String,ScheduledExecutorService>();
	public void addRoom(String roomId){
		watcherMap.put(roomId,null);
	}
	public void removeRoom(String roomId){
		watcherMap.remove(roomId);
		ScheduledExecutorService service = roomUpdateTimerMap.remove(roomId);
		if(service!= null){
			service.shutdown();//关闭定时器
		}
	}
	/*
	 * 更新房间用户
	 */
	public void updateRoomUser(final String roomId, String userId){
		if(watcherMap.containsKey(roomId)){
			return;
		}
		//获取观众集合
		Map<String,Long> watchers = watcherMap.get(roomId);
		if(watchers == null){
			watchers = new HashMap<String,Long>();
			watcherMap.put(roomId, watchers);
			//执行定时周期任务
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			Runnable command = new Runnable() {
				@Override
				public void run() {
					Map<String,Long> watchers = watcherMap.get(roomId);
					if(watchers != null){
						for(String userId : watchers.keySet()){
							long lastUpdateTime = watchers.get(userId);
							if(lastUpdateTime + 10 * 1000 > System.currentTimeMillis())
							{	//说明这个观众是有效的.
								//无需操作
							}else{
								//观众无效，需要从map中删除这个userid
								watchers.remove(userId);
							}
						}
					}
				}
			};
			/*
			 * 按指定频率周期执行某个任务
			 * 参数一：执行线程
			 * 参数二：初始化延迟
			 * 参数三：两次开始的执行的最小时间间隔
			 * 参数四：计时单位
			 */
			service.scheduleWithFixedDelay(command, 0, 10, TimeUnit.SECONDS);
			roomUpdateTimerMap.put(roomId, service);
		}
		//有ID，直接更新时间
		watchers.put(userId, System.currentTimeMillis());	
	}
	/*
	 * 删除房间用户，用于QuitRoomAction
	 */
	public void removeWatcher(String roomId, String userId) {
		if(!watcherMap.containsKey(roomId)){
			return;
		}
		
		Map<String,Long> watchers = watcherMap.get(roomId);
		if(watchers == null){
			return;
		}
		
		watchers.remove(userId);
	}
	/*
	 * 获取房间观众，用于GetWatchersAction,返回已经存在的观众集合
	 */
	public Set<String> getWatchers(String roomId) {
		Set<String> watcherList = new HashSet<String>();
		if(!watcherMap.containsKey(roomId)){
			return watcherList;
		}
		Map<String,Long> watchers = watcherMap.get(roomId);
		if(watchers == null){
			return watcherList;
		}
		//返回watchID
		return watchers.keySet();
	}
}
