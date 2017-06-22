package com.ncvas.common.service.impl;

import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountCheckPayPwdService;
import com.ncvas.payment.service.AnjiePayService;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/29.
 */
@Service("accountCheckPayPwdService")
public class AccountCheckPayPwdServiceImpl implements AccountCheckPayPwdService {

    private static final Logger logger =LoggerFactory.getLogger(AccountCheckPayPwdServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;
    @Override
    public Map<String, Object> accountCheckPayPwd(Map<String, String> paraMap) throws Exception {
        Map<String,Object> resultMap = anjiePayService.accountQueryAliasCode(paraMap);
        if(resultMap!=null&& PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get("responseCode"))){
            Map<String, Object> temMap = new HashMap<>();
            if(resultMap.get("zhCard")!=null){
                JSONObject json = JSONObject.fromObject(resultMap.get("zhCard"));
                String setPwd = (String)json.get("setPwd");
                temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
                if(PayPlatformEnum.IS_PWD.getCode().equals(setPwd)){
                    temMap.put("setPwd",PayPlatformEnum.IS_PWD.getCode());
                }else {
                    temMap.put("setPwd",PayPlatformEnum.NO_PWD.getCode());
                }
                return  temMap;
            }
        }
        return resultMap;
    }
}
