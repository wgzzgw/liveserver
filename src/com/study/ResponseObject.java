package com.study;
/*
 * �Զ������˷������ݵĽṹ����JSON
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ResponseObject {
	public static final String CODE_SUCCESS = "1";
	public static final String CODE_FAIL = "0";
	
	public String code;//�����룺�ɹ����
	public String errCode;//���������
	public String errMsg;//���������Ϣ
	public Object data;//�ɹ����ص���Ϣ��object����
	
	private static Gson GsonInstance = new Gson();
	/*
	 * �ɹ����ء���json�ṹ
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
	 * ʧ�ܷ��ء���JSON�ṹ
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
	 * ���ͽ����Ӧ
	 */
	public void send(HttpServletResponse response) {

		if (response == null) {
			return;
		}
		//����Ӧ��ͷ�����ֺ�ֵ������Ӧ��ͷӦ���ڷ����κ��ĵ�����֮ǰ����
		//ָ���������ݱ����ʽ
		response.setHeader("Content-type", "text/html;charset=utf-8");
		//ָ��ʵ�ʱ����ʽ
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
