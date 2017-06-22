package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public class AccountQuickPayCancel extends ValueObject {
	private static final long serialVersionUID = 1562941152601702517L;
	private String orderNo;
	private String loginName;
	private String remark;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "AccountQuickPayCancel{" +
				"orderNo='" + orderNo + '\'' +
				", loginName='" + loginName + '\'' +
				", remark='" + remark + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}