package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * Created by Administrator on 2016/11/22.
 */
public class AccountLoginHistory extends ValueObject{

    private static final long serialVersionUID = -5417218222634175854L;
    private String memberCode;
    private String memberName;
    private String custName;
    private String mobile;
    /**
     *请求来源 1为智汇APP 2为医疗APP
     */
    private String reqBizType;

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

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReqBizType() {
        return reqBizType;
    }

    public void setReqBizType(String reqBizType) {
        this.reqBizType = reqBizType;
    }
}
