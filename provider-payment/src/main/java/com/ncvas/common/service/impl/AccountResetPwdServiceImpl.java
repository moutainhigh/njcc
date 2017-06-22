package com.ncvas.common.service.impl;

import com.ncvas.payment.service.AccountResetPwdService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
@Service("accountResetLoginPwdService")
public class AccountResetPwdServiceImpl implements AccountResetPwdService {

    private static final Logger logger =LoggerFactory.getLogger(AccountSetPayPasswordServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;
    @Override
    public Map<String, Object> accountResetPwd(Map<String, String> paraMap) throws Exception {
        Map<String, Object> resultMap = anjiePayService.accountResetPwd(paraMap);
        return resultMap;
    }
}
