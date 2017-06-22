package com.ncvas.common.service.impl;

import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.payment.service.AccountVerifyHissService;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/27.
 */
@Service("hisVerifyService")
public class AccountVerifyHissServiceImpl implements AccountVerifyHissService {

    private static final Logger logger =LoggerFactory.getLogger(AccountVerifyHissServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;
    @Override
    public Map<String, Object> hisVerify(Map<String, String> paraMap) throws Exception {
        Map<String, Object> resultMap = anjiePayService.hisVerify(paraMap);
        if (resultMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            Map<String, Object> temMap = new HashMap<>();
            if (resultMap.get("rescontent") != null) {
                JSONObject json = JSONObject.fromObject(resultMap.get("rescontent"));
                temMap.put("psgncode", String.valueOf(json.get("PSGNCODE")));
                temMap.put("aliascode", String.valueOf(json.get("ALIASCODE")));
                temMap.put("accstatus", String.valueOf(json.get("ACCSTATUS")));
                temMap.put("accbalance", String.valueOf(json.get("ACCBALANCE")));
                temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
            }
            return temMap;
        }
        return resultMap;
    }

    @Override
    public String getBalance(String aliasCode) throws Exception {
        Map<String, String> paraMap = new HashMap<String,String>();
        paraMap.put("aliasCode",aliasCode);
        Map<String, Object> resultMap = this.hisVerify(paraMap);
        String balance = null;
        if (resultMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            balance = (String) resultMap.get("accbalance");
            return balance;
        }else {
            return PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode();
        }
    }
}
