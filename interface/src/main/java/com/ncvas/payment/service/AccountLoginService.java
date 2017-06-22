package com.ncvas.payment.service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/21.
 */
public interface AccountLoginService {

    /**
     * 登录（“10602”）
     */
    public Map<String,Object> accountLogin(Map reqMap) throws Exception;
}
