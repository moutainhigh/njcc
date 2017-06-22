package com.ncvas.payment.service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/27.
 */
public interface AccountVerifyHissService {
    public Map<String,Object> hisVerify(Map<String, String> paraMap) throws Exception;
    /**
     * 查询医疗账户余额
     *@param aliasCode 智汇卡号
     */
    public String getBalance(String aliasCode) throws Exception;
}
