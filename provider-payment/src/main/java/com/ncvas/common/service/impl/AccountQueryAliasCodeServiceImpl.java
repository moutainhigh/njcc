package com.ncvas.common.service.impl;

import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountQueryAliasCodeService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.payment.service.PrepaidOrderInquiryService;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/24.
 */
@Service("accountQueryAliasCodeService")
public class AccountQueryAliasCodeServiceImpl implements AccountQueryAliasCodeService {

    private static final Logger logger =LoggerFactory.getLogger(AccountQueryAliasCodeServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;
    @Autowired
    private PrepaidOrderInquiryService prepaidOrderInquiryService;

    @Override
    public Map<String, Object> accountQueryAliasCode(String aliasCode) throws Exception {
        Map<String, String> paraMap = new HashMap<String,String>();
        paraMap.put("aliasCode", aliasCode);
        Map<String, Object> resultMap = anjiePayService.accountQueryAliasCode(paraMap);
        if (resultMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            Map<String, Object> temMap = new HashMap<>();
            if (resultMap.get("zhCard") != null) {
                JSONObject json = JSONObject.fromObject(resultMap.get("zhCard"));
                temMap.put("accBalance", String.valueOf(json.get("accBalance")));
                temMap.put("aliasCode", String.valueOf(json.get("aliasCode")));
                temMap.put("cardBalance", String.valueOf(json.get("cardBalance")));
                if (!StringUtil.isEmpty(String.valueOf(json.get("cardCategory")))) {
//						01：市民卡A卡 02：市民卡B卡 03：金陵通卡、紫金卡 04：市民卡C卡 05：旅游年卡 06：助老卡
                    String cardcategory = String.valueOf(json.get("cardCategory"));
//						重写类型，适配app
                    if("03".equals(cardcategory)){
                        if ("0000".equals(String.valueOf(json.get("custClass")))) {
                            cardcategory = "07";//金陵通记名卡
                        } else {
                            cardcategory="08";//紫金卡
                        }
                    }else if ("00".equals(cardcategory)){
                        /**00是支付那边返回回来的， 给app的文档写了09*/
                        cardcategory="09";//虚拟卡
                    }
                    temMap.put("cardcategory",cardcategory);
                }
                temMap.put("cardClass", String.valueOf(json.get("cardClass")));
                temMap.put("cardStatus", String.valueOf(json.get("cardStatus")));
                temMap.put("cardType", String.valueOf(json.get("cardType")));
                temMap.put("custClass", String.valueOf(json.get("custClass")));
                temMap.put("custStatus", String.valueOf(json.get("custStatus")));
                temMap.put("isNotCard", String.valueOf(json.get("isNotCard")));
                temMap.put("memberCode", String.valueOf(json.get("memberCode")));
                temMap.put("name", String.valueOf(json.get("name")));
                temMap.put("psgnCode", String.valueOf(json.get("psgnCode")));
                temMap.put("registered", String.valueOf(json.get("registered")));
                temMap.put("setMain", String.valueOf(json.get("setMain")));
                temMap.put("setPwd", String.valueOf(json.get("setPwd")));
                temMap.put("socialCode", String.valueOf(json.get("socialCode")));
                temMap.put("photo", String.valueOf(json.get("photo")));
                temMap.put("zhAcctCode", String.valueOf(json.get("zhAcctCode")));
                temMap.put("zhMedicalCode", String.valueOf(json.get("zhMedicalCode")));
                temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
                temMap.put("rows",this.prepaidOrderInquiry(paraMap));
            }
            return temMap;
        }
        return resultMap;
    }


    public List<Map<String,Object>> prepaidOrderInquiry(Map<String, String> paraMap)throws Exception{
        Map<String, Object> result = prepaidOrderInquiryService.prepaidOrderInquiry(paraMap);
        List<Map<String,Object>> resultList = new ArrayList<>();
        if(result != null){
            if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                resultList.add(result);
                return resultList;
            }else {
                return resultList;
            }
        }
        return resultList;
    }


    public Boolean verifyCardIsOpen(Map<String, String> paraMap) throws Exception{
        Map<String, Object> resultMap = anjiePayService.accountQueryAliasCode(paraMap);
        if (resultMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            if (resultMap.get("zhCard") != null) {
                JSONObject json = JSONObject.fromObject(resultMap.get("zhCard"));
                if(PayPlatformEnum.NO_PWD.getCode().equals(String.valueOf(json.get("setPwd")))){
                    return true;
                }else {
                  return false;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public String getAliasCodeBalanceNjcc(String aliasCode) throws Exception {
        String accbalanceNjcc = PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode();
        Map<String, String> tempParams = new HashMap<String, String>();
        tempParams.put("aliasCode", aliasCode);
        Map<String, Object> result = this.accountQueryAliasCode(aliasCode);
        if(result!=null){
            if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                accbalanceNjcc = (String)result.get("accBalance");
            }
        }
        return accbalanceNjcc;
    }
}
