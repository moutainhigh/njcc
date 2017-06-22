package com.ncvas.common.service.impl;

import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountMemberInfoService;
import com.ncvas.payment.service.AnjiePayService;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/22.
 */
@Service("accountMemberInfoService")
public class AccountMemberInfoServiceImpl implements AccountMemberInfoService {
    private static final Logger logger =LoggerFactory.getLogger(AccountSetPayPasswordServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;

    @Override
    public Map<String, Object> accountMemberInfo(Map<String, String> paraMap) throws Exception {
        Map<String, Object> resultMap = anjiePayService.accountMemberinfo(paraMap);
        if (resultMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            Map<String, Object> temMap = new HashMap<>();
            if (resultMap.get("memberInfo") != null) {
                JSONObject json = JSONObject.fromObject(resultMap.get("memberInfo"));
                temMap.put("memberCode", String.valueOf(json.get("memberCode")));
//                temMap.put("loginName", paraMap.get("loginName"));
                temMap.put("idNo", String.valueOf(json.get("cerCode")));
                temMap.put("idType", String.valueOf(json.get("cerType")));
                temMap.put("memberName", String.valueOf(json.get("name")));
                temMap.put("mobile", String.valueOf(json.get("mobile")));
                temMap.put("sex", String.valueOf(json.get("sex")));
                temMap.put("address", String.valueOf(json.get("addr")));
                temMap.put("loginId", String.valueOf(json.get("nickname")));
                temMap.put("email", String.valueOf(json.get("email")));

                if(resultMap.get("zhCard") != null) {
                    //智汇账户号和医疗账户号
                    json = JSONObject.fromObject(resultMap.get("zhCard"));
                    temMap.put("zhAcctCode", String.valueOf(json.get("zhAcctCode")));
                    temMap.put("zhMedicalCode", String.valueOf(json.get("zhMedicalCode")));
                    temMap.put("mainAliasCode", String.valueOf(json.get("aliasCode")));
                    temMap.put("accBalance", String.valueOf(json.get("accBalance")));
                    temMap.put("cardCategory", String.valueOf(json.get("cardCategory")));
                }else {
                    temMap.put("zhAcctCode","");
                    temMap.put("zhMedicalCode","");
                    temMap.put("mainAliasCode", "");
                    temMap.put("accBalance", "");
                    temMap.put("cardCategory", "");
                }
                temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
            }else{
                temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.PAY_RESPONSE_ERROR_CODE.getCode());
                temMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),PayPlatformEnum.MEMBER_INFO_NO_MAINALIASCODE.getDescription());
            }
            return temMap;
        }
        return resultMap;
    }
}
