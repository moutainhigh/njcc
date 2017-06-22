package com.ncvas.common.service.impl;

import com.ncvas.payment.service.AccountSetPayPasswordService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountSetPayPasswordService")
public class AccountSetPayPasswordServiceImpl implements AccountSetPayPasswordService {
	private static final Logger logger =LoggerFactory.getLogger(AccountSetPayPasswordServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Override
	public Map<String, Object> accountSetPayPassword(Map<String, Object> paraMap) throws Exception{
		Map<String,Object> resultMap = anjiePayService.accountSetPayPassword(paraMap);
		return resultMap;
	}
}
