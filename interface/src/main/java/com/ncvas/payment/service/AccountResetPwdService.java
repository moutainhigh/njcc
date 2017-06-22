package com.ncvas.payment.service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
public interface AccountResetPwdService {
    public Map<String,Object> accountResetPwd(Map<String, String> paraMap) throws Exception;
}
