package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

import java.util.Date;

/**
 * Created by caiqi on 2016/12/19.
 */
public class AccountQuickPayOrder extends ValueObject {

    private static final long serialVersionUID = 1649297843895522827L;
    private String orderID;
    private String loginName;
    private String amount;
    private String payeeAliasCode;
    private String status;
    private String remark;
    private String deviceid;
    private String devicename;
    private String devicemac;
    private String devicemodel;
    private String sourcetype;
    private String aliascode;  //充值卡面号
    private String accbalance; //账号余额
    private String mstaliascode;//付款方卡面号
    /**
     *请求来源 1为智汇APP 2为医疗APP
     */
    private String reqBizType;
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayeeAliasCode() {
        return payeeAliasCode;
    }

    public void setPayeeAliasCode(String payeeAliasCode) {
        this.payeeAliasCode = payeeAliasCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDevicemac() {
        return devicemac;
    }

    public void setDevicemac(String devicemac) {
        this.devicemac = devicemac;
    }

    public String getDevicemodel() {
        return devicemodel;
    }

    public void setDevicemodel(String devicemodel) {
        this.devicemodel = devicemodel;
    }

    public String getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
    }

    public String getAliascode() {
        return aliascode;
    }

    public void setAliascode(String aliascode) {
        this.aliascode = aliascode;
    }

    public String getAccbalance() {
        return accbalance;
    }

    public void setAccbalance(String accbalance) {
        this.accbalance = accbalance;
    }

    public String getMstaliascode() {
        return mstaliascode;
    }

    public void setMstaliascode(String mstaliascode) {
        this.mstaliascode = mstaliascode;
    }

    public String getReqBizType() {
        return reqBizType;
    }

    public void setReqBizType(String reqBizType) {
        this.reqBizType = reqBizType;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
