package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

import java.util.Date;

/**
 * Created by caiqi on 2016/11/25.
 */
public class AccountSetMainAliasCode extends ValueObject {

    private String aliasCode;
    private String memberCode;
    private Date updatedate;
    private String remark;
    private String socialCode;
    private String memberName;

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

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
