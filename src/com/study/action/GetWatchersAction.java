package com.study.action;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.study.ResponseObject;
import com.study.WatcherListManager;
import com.study.Error;
/*
 * 获取观众列表
 */
public class GetWatchersAction  extends IAction{
	private static final String RequestParamKey_RoomId = "roomId";
	@Override
	public void doAction(HttpServletRequest req,
			HttpServletResponse resp) throws IOException, SQLException {
		int roomIdParam = getParam(req, RequestParamKey_RoomId, -1);
		if (roomIdParam < 0) {
			ResponseObject responseObject = ResponseObject.getFailResponse(
					Error.errorCode_NoRequestParam,
					Error.getNoRequestParamMsg(RequestParamKey_RoomId
							+ "值<0"));
			responseObject.send(resp);
			return;
		}
		
		Set<String> watcherIdSet = WatcherListManager.getInstance().getWatchers(roomIdParam+"");
		ResponseObject responseObject = ResponseObject
				.getSuccessResponse(watcherIdSet);
		responseObject.send(resp);
	}
}
