package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public class AccountCertification extends ValueObject {

	private static final long serialVersionUID = 1562941152601702517L;
	private String loginName;
	private String idName;
	private String idType;
	private String idNo;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}