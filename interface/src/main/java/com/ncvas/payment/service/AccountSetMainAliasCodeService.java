package com.ncvas.payment.service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/24.
 */
public interface AccountSetMainAliasCodeService {
    public Map<String,Object> accountSetMainAliasCode(String aliasCode, String memberCode, String token, String loginName, String custname, String payPwd,String isCheck,String orderId,String checkCode,String mobile) throws Exception;
//根据卡号获取主卡绑定信息
    public boolean checkMainAliasCodeOpen(String mainAliaCode) throws Exception;
}
