package com.study;

/*
 * �������������Ϣ��
 */
public class Error {
	//ͨ�õ�error
	public static final String errorCode_NoAction = "404";
	private static final String errorMsg_NoAction = "û��Action����";

	public static final String errorCode_Exception = "500";
	private static final String errorMsg_Exception = "�������쳣";
	
	public static final String errorCode_NoRequestParam = "405";
	private static final String errorMsg_NoRequestParam = "ȱ�ٱ�Ҫ����";
	
	public static final String errorCode_ErrorParam = "502";
	private static final String errorMsg_ErrorParam = "����ֵ����ȷ";
	
	//�ض�action��error,600,610,620,630
	public static final String errorCode_CreateFail = "600";
	private static final String errorMsg_CreateFail = "����ֱ������ʧ��";

	public static final String errorCode_QueryFail = "601";
	private static final String errorMsg_QueryFail = "��ȡֱ������ʧ��";

	public static final String errorCode_QueryListFail = "602";
	private static final String errorMsg_QueryListFail = "��ȡֱ�������б�ʧ��";

	public static final String errorCode_QuitFail = "603";
	private static final String errorMsg_QuitFail = "�˳�ֱ������ʧ��";
	
	public static String getNoActionMsg() {
		return errorMsg_NoAction;
	}
	
	public static String getErrorParamMsg(String requestParam) {
		return errorMsg_ErrorParam + ":" + requestParam;
	}
	
	public static String getNoRequestParamMsg(String requestParam) {
		return errorMsg_NoRequestParam + ":" + requestParam;
	}
	
	public static String getExceptionMsg(String e) {
		return errorMsg_Exception + ":" + e;
	}
	
	public static String getCreateFailMsg() {
		return errorMsg_CreateFail;
	}
	
	public static String getQueryFailMsg() {
		return errorMsg_QueryFail;
	}
	
	public static String getQueryListFailMsg() {
		return errorMsg_QueryListFail;
	}
	
	public static String getQuitFailMsg() {
		return errorMsg_QuitFail;
	}

	
}
