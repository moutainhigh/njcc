package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * Created by caiqi on 2016/11/23.
 */
public class AccountWithdraw extends ValueObject {

    private String loginName;
    private String amount;
    private String bankAcct;
    private String aliascode;

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

    public String getBankAcct() {
        return bankAcct;
    }

    public void setBankAcct(String bankAcct) {
        this.bankAcct = bankAcct;
    }

    public String getAliascode() {
        return aliascode;
    }

    public void setAliascode(String aliascode) {
        this.aliascode = aliascode;
    }
}
