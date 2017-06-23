package com.ncvas.handler;


import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeVO;

public interface ApiHandler {
	/**
	 * 执行接口业务
	 * @param params
	 * @return
	 */
	public ResponeVO doHandler(Object params);
	/**
	 * 检查参数
	 * @param params
	 * @throws ApiMessageException
	 */
	public void checkRequestParams(Object params) throws ApiMessageException;
	/**
	 * 参数对象（dataMsg）
	 * @return
	 */
	public Class<?> getRequestParams();
	
	/**成功*/
	public final static ResponeVO RESP_SUCCESS = new ResponeVO(0, "成功");
	/**JSON格式错误*/
	public final static ResponeVO RESP_JSON_FORMAT_ERROR = new ResponeVO(1, "JSON格式错误");
	/**未知的业务编码*/
	public final static ResponeVO RESP_UNKNOWN_SERVICE_CODE_ERROR = new ResponeVO(1, "未知的业务编码");
	/**服务器繁忙*/
	public final static ResponeVO RESP_SERVICE_BUSY_ERROR = new ResponeVO(3, "服务器繁忙");
	/**失败，其他未知错误*/
	public final static ResponeVO RESP_UNKNOWN_ERROR = new ResponeVO(4, "失败，其他未知错误");
	/**验证码错误*/
	public final static ResponeVO RESP_AUTH_CODE_ERROR = new ResponeVO(5, "验证码错误或已失效");
	/**登录超时或者未登录*/
	public final static ResponeVO RESP_LOGOUT_ERROR = new ResponeVO(6, "登录超时或者未登录");
	/**请求参数错误*/
	public final static ResponeVO RESP_PARAMS_ERROR = new ResponeVO(7, "请求参数错误");
	/**签名信息错误*/
	public final static ResponeVO RESP_SIGN_ERROR = new ResponeVO(8, "签名信息错误");
	/*系统异常，请联系管理员*/
	public final static ResponeVO RESP_SYSTEM_ERROR = new ResponeVO(8, "系统异常，请联系管理员");
	/**身份证不一致*/
	public final static ResponeVO RESP_CERTNO_NO_SAME = new ResponeVO(10, "身份证不一致");

}
