package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016年12月19日
 */
public class AccountUsertransfer extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3186795376264775220L;

	private String orderId;	//订单号
	private String ordertime; //交易时间
	private String orderno;	//交易流水号
	private String orderamt;	//交易金额
	private String outacccode;	//转出卡面号
	private String inacccode;	//转入卡面号
	private String outaccbalance;
	private String inaccbalance;
	private String transtype; //00：智汇账户转智汇账户01：智汇账户转医疗账户02：智汇账户转助老账户10：医疗账户转智汇账户11：医疗账户转医疗账户20：助老账户转智汇账户21：助老帐户转助老账户
	private Integer status;//状态 1 失败 2成功
	private String remark;//备注(失败的)
	private String loginName;//登陆名
	private String msg;//app端标记
	/**
	 *请求来源 1为智汇APP 2为医疗APP
	 */
	private String reqBizType;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getOrderamt() {
		return orderamt;
	}

	public void setOrderamt(String orderamt) {
		this.orderamt = orderamt;
	}

	public String getOutacccode() {
		return outacccode;
	}

	public void setOutacccode(String outacccode) {
		this.outacccode = outacccode;
	}

	public String getInacccode() {
		return inacccode;
	}

	public void setInacccode(String inacccode) {
		this.inacccode = inacccode;
	}

	public String getOutaccbalance() {
		return outaccbalance;
	}

	public void setOutaccbalance(String outaccbalance) {
		this.outaccbalance = outaccbalance;
	}

	public String getInaccbalance() {
		return inaccbalance;
	}

	public void setInaccbalance(String inaccbalance) {
		this.inaccbalance = inaccbalance;
	}

	public String getTranstype() {
		return transtype;
	}

	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReqBizType() {
		return reqBizType;
	}

	public void setReqBizType(String reqBizType) {
		this.reqBizType = reqBizType;
	}
}
