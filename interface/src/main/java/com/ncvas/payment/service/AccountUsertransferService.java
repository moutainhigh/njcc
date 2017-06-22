package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountUsertransfer;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月19日
 */
public interface AccountUsertransferService extends BaseService<AccountUsertransfer> {
    Map<String, Object> accountUsertransfer(String loginName, String type, String amount, String payeeAliasCode, String msg, String password, String memberId, String loginId, String memberCode, String aliascode, String reqBizType, String reqSource, String bizType) throws Exception;

    //(订单表重构后版本)
    Map<String, Object> accountUsertransfer2(String loginName, String type, String amount, String payeeAliasCode, String msg, String password, String memberId, String loginId, String memberCode, String aliascode, String reqBizType, String reqSource, String bizType) throws Exception;
}
