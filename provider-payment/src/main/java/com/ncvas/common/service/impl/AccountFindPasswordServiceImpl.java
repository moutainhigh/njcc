package com.ncvas.common.service.impl;

import com.ncvas.payment.service.AccountFindPasswordService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/22.
 */
@Service("accountFindPasswordService")
public class AccountFindPasswordServiceImpl implements AccountFindPasswordService {

    private static final Logger logger =LoggerFactory.getLogger(AccountSetPayPasswordServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;

    @Override
    public Map<String, Object> accountFindPassword(String loginName, String type, String flag) throws Exception {
        Map<String, String> paraMap = new HashMap<String,String>();
        paraMap.put("loginName", loginName);
        paraMap.put("type", type);
        paraMap.put("flag", flag);
        Map<String,Object> resultMap = anjiePayService.accountFindPassword(paraMap);
        return resultMap;
    }

}
