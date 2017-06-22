package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public class AccountRegister extends ValueObject {
	private static final long serialVersionUID = 1562941152601702517L;
	private String loginName;
	private String loginPwd;
	private String regType;
	private String nickname;
	private String referee;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	@Override
	public String toString() {
		return "AccountRegister{" +
				"loginName='" + loginName + '\'' +
				", loginPwd='" + loginPwd + '\'' +
				", regType='" + regType + '\'' +
				", nickname='" + nickname + '\'' +
				", referee='" + referee + '\'' +
				'}';
	}
}