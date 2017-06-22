package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * Created by xiecaixia on 2017-4-25.
 * 写卡渠道记录原型
 */
public class AccountWriteCard extends ValueObject {
    private static final long serialVersionUID = -3488614699548268043L;
    /**
     *写卡渠道 0 APP写卡、1 自助终端写卡
     */
    private String channelType;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 支付卡号
     */
    private String aliascode;
    /**
     * 充值卡号
     */
    private String cardNo;
    /**
     * 写卡类型, 1为自己写卡, 2为别人写卡
     */
    private String writeType;
    /**
     * 写卡是否成功 0为失败  1为成功
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 写卡时间
     */
    private String writeTime;
    /**
     * 请求来源 1智汇app 2手环 3 医疗
     */
    private String reqBizProj;

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getWriteType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
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

    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
    }

    public String getAliascode() {
        return aliascode;
    }

    public void setAliascode(String aliascode) {
        this.aliascode = aliascode;
    }

    public String getReqBizProj() {
        return reqBizProj;
    }

    public void setReqBizProj(String reqBizProj) {
        this.reqBizProj = reqBizProj;
    }

    @Override
    public String toString() {
        return "AccountWriteCard{" +
                "channelType='" + channelType + '\'' +
                ", orderId='" + orderId + '\'' +
                ", loginName='" + loginName + '\'' +
                ", aliascode='" + aliascode + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", writeType='" + writeType + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", writeTime='" + writeTime + '\'' +
                ", reqBizProj='" + reqBizProj + '\'' +
                '}';
    }
}
