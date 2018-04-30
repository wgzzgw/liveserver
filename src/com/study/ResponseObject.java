package com.study;
/*
 * 自定义服务端返回数据的结构——JSON
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ResponseObject {
	public static final String CODE_SUCCESS = "1";
	public static final String CODE_FAIL = "0";
	
	public String code;//返回码：成功与否
	public String errCode;//具体错误码
	public String errMsg;//具体错误信息
	public Object data;//成功返回的信息，object类型
	
	private static Gson GsonInstance = new Gson();
	/*
	 * 成功返回——json结构
	 */
	public static ResponseObject getSuccessResponse(Object data) {
		ResponseObject responseObject = new ResponseObject();
		responseObject.code = CODE_SUCCESS;
		responseObject.errCode = "";
		responseObject.errMsg = "";
		responseObject.data = data;
		return responseObject;
	}
	/*
	 * 失败返回——JSON结构
	 */
	public static ResponseObject getFailResponse(String errCode, String errMsg) {
		ResponseObject responseObject = new ResponseObject();
		responseObject.code = CODE_FAIL;
		responseObject.errCode = errCode;
		responseObject.errMsg = errMsg;
		responseObject.data = null;
		return responseObject;
	}
	/*
	 * 发送结果响应
	 */
	public void send(HttpServletResponse response) {

		if (response == null) {
			return;
		}
		//设置应答头的名字和值，设置应答头应该在发送任何文档内容之前进行
		//指定返回内容编码格式
		response.setHeader("Content-type", "text/html;charset=utf-8");
		//指定实际编码格式
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.println(GsonInstance.toJson(this));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(writer!=null) {
				writer.close();
			}
		}
	}
}
