package com.ncvas.payment.service;

import com.ncvas.payment.enums.SmsTypeEnum;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountGetCheckCodeService {
    public Map<String,Object> accountGetCheckCode(String loginName,String mobile,String orderID,SmsTypeEnum businessType) throws Exception;
}
