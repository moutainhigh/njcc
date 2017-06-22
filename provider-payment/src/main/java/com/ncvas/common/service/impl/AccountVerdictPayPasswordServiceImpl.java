package com.ncvas.common.service.impl;

import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountVerdictPayPasswordService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月13日
 */
@Service("accountVerdictPayPasswordService")
public class AccountVerdictPayPasswordServiceImpl implements AccountVerdictPayPasswordService {
	private static final Logger logger =LoggerFactory.getLogger(AccountVerdictPayPasswordServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Override
	public Map<String, Object> accountVerdictPayPassword(String aliasCode,String payPwd,String type,String memberCode) throws Exception{
		Map<String, Object> paraMap = new HashMap<String,Object>();
		paraMap.put("aliasCode",aliasCode);
		paraMap.put("payPwd",payPwd);
		paraMap.put("memberCode",memberCode);
		paraMap.put("type",type);
		Map<String, Object> resultMap = anjiePayService.accountVerdictPayPassword(paraMap);
		if(resultMap!=null&& resultMap.get("isVerify")!=null){
			Map<String, Object> temMap = new HashMap<>();
			Boolean isVerify = (Boolean) resultMap.get("isVerify");
			temMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
			if(isVerify){
				temMap.put("isVerify",PayPlatformEnum.IS_VERIFY_PWD.getCode());
			}else{
				temMap.put("isVerify",PayPlatformEnum.NO_VERIFY_PWD.getCode());
			}
			return temMap;
		}
		return resultMap;
	}
}
