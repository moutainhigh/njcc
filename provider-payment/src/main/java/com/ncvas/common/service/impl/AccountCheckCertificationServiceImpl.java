package com.ncvas.common.service.impl;

import com.ncvas.payment.service.AccountCheckCertificationService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/22.
 */
@Service("accountCheckCertificationService")
public class AccountCheckCertificationServiceImpl implements AccountCheckCertificationService {

    private static final Logger logger =LoggerFactory.getLogger(AccountSetPayPasswordServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;

    @Override
    public Map<String, Object> accountCheckCertification(Map<String, String> paraMap) throws Exception {
        Map<String, Object> resultMap = anjiePayService.accountCheckCertification(paraMap);
        return resultMap;
    }
}
