package com.ncvas.payment.service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/24.
 */
public interface AccountQueryAliasCodeService {
    public Map<String,Object> accountQueryAliasCode(String aliasCode) throws Exception;

    public Boolean verifyCardIsOpen(Map<String, String> paraMap) throws Exception;

    /**
     * 根据aliasCode获取智汇余额
     * @param aliasCode  智汇卡号
     */
    public String getAliasCodeBalanceNjcc(String aliasCode) throws Exception;
}
