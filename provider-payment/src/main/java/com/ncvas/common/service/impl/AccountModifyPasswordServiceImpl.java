package com.ncvas.common.service.impl;


import com.ncvas.payment.service.AccountModifyPasswordService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountModifyPasswordService")
public class AccountModifyPasswordServiceImpl implements AccountModifyPasswordService {
	private static final Logger logger =LoggerFactory.getLogger(AccountModifyPasswordServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Override
	public Map<String, Object> accountModifyPassword(Map<String, String> paraMap) throws Exception {
		Map<String,Object> resultMap = anjiePayService.accountModifyPassword(paraMap);
		return resultMap;
	}
}
