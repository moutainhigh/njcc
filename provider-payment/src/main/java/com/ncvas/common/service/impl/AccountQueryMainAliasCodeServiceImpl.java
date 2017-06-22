package com.ncvas.common.service.impl;

import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountQueryMainAliasCodeService;
import com.ncvas.payment.service.AnjiePayService;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月06日
 */
@Service("accountQueryMainAliasCodeService")
public class AccountQueryMainAliasCodeServiceImpl implements AccountQueryMainAliasCodeService {
	private static final Logger logger =LoggerFactory.getLogger(AccountQueryMainAliasCodeServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Override
	public Map<String, Object> accountQueryMainAliasCode(String memberCode) throws Exception {
		Map<String, Object> paraMap = new HashMap<String,Object>();
		paraMap.put("memberCode",memberCode);
		Map<String, Object> resultMap = anjiePayService.accountQueryMainAliasCode(paraMap);
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
				temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
			}
			return temMap;
		}
		return resultMap;
	}

	public String getCardAccBalance(String memberCode) throws Exception {
		Map<String, Object> paraMap = new HashMap<String,Object>();
		paraMap.put("memberCode",memberCode);
		Map<String,Object> result = this.accountQueryMainAliasCode(memberCode);
		if(result!=null){
			if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
				return (String)result.get("accBalance");
			}else if(!StringUtil.isEmpty((String) result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
				return PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode();
			}
		}
		return PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode();
	}
}
