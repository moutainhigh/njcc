package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public class AccountBindNjccCard extends ValueObject {
	private static final long serialVersionUID = 1562941152601702517L;
	private String aliasCode;
	private String memberCode;
	private String memberName;
	private String socialCode;
	private String registered;
	private String bindingStatus;
	private String remark;
	private String loginName;
	private String cardCategory;
	private String reqBizType;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAliasCode() {
		return aliasCode;
	}

	public void setAliasCode(String aliasCode) {
		this.aliasCode = aliasCode;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getSocialCode() {
		return socialCode;
	}

	public void setSocialCode(String socialCode) {
		this.socialCode = socialCode;
	}

	public String getRegistered() {
		return registered;
	}

	public void setRegistered(String registered) {
		this.registered = registered;
	}

	public String getBindingStatus() {
		return bindingStatus;
	}

	public void setBindingStatus(String bindingStatus) {
		this.bindingStatus = bindingStatus;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCardCategory() {
		return cardCategory;
	}

	public void setCardCategory(String cardCategory) {
		this.cardCategory = cardCategory;
	}

	public String getReqBizType() {
		return reqBizType;
	}

	public void setReqBizType(String reqBizType) {
		this.reqBizType = reqBizType;
	}

	@Override
	public String toString() {
		return "AccountBindNjccCard{" +
				"aliasCode='" + aliasCode + '\'' +
				", memberCode='" + memberCode + '\'' +
				", memberName='" + memberName + '\'' +
				", socialCode='" + socialCode + '\'' +
				", registered='" + registered + '\'' +
				", bindingStatus='" + bindingStatus + '\'' +
				", remark='" + remark + '\'' +
				", loginName='" + loginName + '\'' +
				", cardCategory='" + cardCategory + '\'' +
				", reqBizType='" + reqBizType + '\'' +
				'}';
	}
}