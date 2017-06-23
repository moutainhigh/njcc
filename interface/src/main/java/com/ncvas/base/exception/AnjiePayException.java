package com.ncvas.base.exception;
/**
 * 安捷支付处理异常
 * @author TWY.TOM
 *
 */
public class AnjiePayException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5230857621974153227L;
	
	public static final AnjiePayException EX_PAY_ERROR = new AnjiePayException(7700010, "支付失败");
	public static final AnjiePayException EX_NET_ERROR = new AnjiePayException(7700011, "网络错误");
	public static final AnjiePayException EX_GET_SIGN_ERROR = new AnjiePayException(7700012, "获取签名信息错误");
	public static final AnjiePayException EX_NULL_PARAM_ERROR = new AnjiePayException(7700013, "空的参数");
	
	private int code;
	private String error;
	
	private AnjiePayException(int code, String error) {
		this.code = code;
		this.error = error;
	}
	
	private AnjiePayException(int code, String error, Throwable ex) {
		super(ex);
		this.code = code;
		this.error = error;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "AnjiePayException [code=" + code + ", error=" + error + "]\n[" + super.toString() + "]";
	}
	
}
