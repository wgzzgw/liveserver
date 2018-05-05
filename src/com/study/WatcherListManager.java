package com.study;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WatcherListManager {
	private static WatcherListManager manager;//����ģʽ
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
	 * key:�����,value:���ڵ���Ϣ id:time
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
			service.shutdown();//�رն�ʱ��
		}
	}
	/*
	 * ���·����û�
	 */
	public void updateRoomUser(final String roomId, String userId){
		if(watcherMap.containsKey(roomId)){
			return;
		}
		//��ȡ���ڼ���
		Map<String,Long> watchers = watcherMap.get(roomId);
		if(watchers == null){
			watchers = new HashMap<String,Long>();
			watcherMap.put(roomId, watchers);
			//ִ�ж�ʱ��������
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			Runnable command = new Runnable() {
				@Override
				public void run() {
					Map<String,Long> watchers = watcherMap.get(roomId);
					if(watchers != null){
						for(String userId : watchers.keySet()){
							long lastUpdateTime = watchers.get(userId);
							if(lastUpdateTime + 10 * 1000 > System.currentTimeMillis())
							{	//˵�������������Ч��.
								//�������
							}else{
								//������Ч����Ҫ��map��ɾ�����userid
								watchers.remove(userId);
							}
						}
					}
				}
			};
			/*
			 * ��ָ��Ƶ������ִ��ĳ������
			 * ����һ��ִ���߳�
			 * ����������ʼ���ӳ�
			 * �����������ο�ʼ��ִ�е���Сʱ����
			 * �����ģ���ʱ��λ
			 */
			service.scheduleWithFixedDelay(command, 0, 10, TimeUnit.SECONDS);
			roomUpdateTimerMap.put(roomId, service);
		}
		//��ID��ֱ�Ӹ���ʱ��
		watchers.put(userId, System.currentTimeMillis());	
	}
	/*
	 * ɾ�������û�������QuitRoomAction
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
	 * ��ȡ������ڣ�����GetWatchersAction,�����Ѿ����ڵĹ��ڼ���
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
		//����watchID
		return watchers.keySet();
	}
}
