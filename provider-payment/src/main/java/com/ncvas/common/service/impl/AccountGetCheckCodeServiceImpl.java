package com.ncvas.common.service.impl;

import com.ncvas.base.enums.RedisEnum;
import com.ncvas.common.entity.Sendsms;
import com.ncvas.common.service.SendsmsService;
import com.ncvas.payment.entity.PayPlatformConfig;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.enums.SmsTypeEnum;
import com.ncvas.payment.service.AccountGetCheckCodeService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountGetCheckCodeService")
public class AccountGetCheckCodeServiceImpl implements AccountGetCheckCodeService {
	private static final Logger logger =LoggerFactory.getLogger(AccountGetCheckCodeServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private SendsmsService sendsmsService;
	@Autowired
	private PayPlatformConfig payPlatformConfig;

	@Override
	public Map<String, Object> accountGetCheckCode(String loginName,String mobile,String orderID,SmsTypeEnum businessType) throws Exception{
		//if(!businessType.getCode().equals(SmsTypeEnum.QUICK_PAY_MOBILE.getCode())){
		//	orderID = payPlatformConfig.getSmsDefaultOrderId();
		//}
		String redisRegistSmsKey = mobile+ RedisEnum.SMS_KEY_REGISTER.getCode();
		Map<String, String> payReq = new HashMap<String,String>();
		payReq.put("loginName",loginName);
		payReq.put("mobile",mobile);
		payReq.put("orderID",orderID);
		payReq.put("businessType", businessType.getFlag());
		Map<String,Object> resultMap = anjiePayService.accountGetCheckCode(payReq);
		if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			String checkCode = (String)resultMap.get("checkCode");
			//因为支付不返回验证码给我们，所以此处不做验证码校验
			//if(SmsTypeEnum.ACTIVE_REGISTER.getCode().equals(businessType.getCode()))redisTemplate.opsForValue().set(redisRegistSmsKey,checkCode);
			Sendsms sendsms = new Sendsms();
			sendsms.setMobile(mobile);
			sendsms.setSendType(businessType.getCode());
			sendsms.setFeedBackMsg(checkCode);
			sendsmsService.doCreate(sendsms);
		}
		return resultMap;
	}

}
