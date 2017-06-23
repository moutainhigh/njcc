package com.ncvas.common.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016/9/28
 */
public class BlackList extends ValueObject {

    private String mobile;
    private String aliascode;
    private String loginid;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAliascode() {
        return aliascode;
    }

    public void setAliascode(String aliascode) {
        this.aliascode = aliascode;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
}
