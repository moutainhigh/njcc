package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountCertificationMapper;
import com.ncvas.payment.entity.AccountCertification;
import com.ncvas.payment.entity.AccountCertificationDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountCertificationService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountCertificationService")
public class AccountCertificationServiceImpl extends AbstractBaseService<AccountCertification> implements AccountCertificationService {
	private static final Logger logger =LoggerFactory.getLogger(AccountCertificationServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Autowired
	private AccountCertificationMapper accountCertificationMapper;
	@Override
	protected BaseMapper<AccountCertification> getBaseMapper() {
		return accountCertificationMapper;
	}

	@Override
	protected Class<? extends ValueObject> getEntityDTOClass() {
		return AccountCertificationDTO.class;
	}

	@Override
	public Map<String, Object> accountCertification(Map<String, String> paraMap) throws Exception {
		Map<String,Object> resultMap = anjiePayService.accountCertification(paraMap);
		if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			AccountCertificationDTO dto = new AccountCertificationDTO();
			dto.setLoginName(paraMap.get("loginName"));
			dto.setIdName(paraMap.get("name"));
			dto.setIdType(paraMap.get("idType"));
			dto.setIdNo(paraMap.get("idNo"));
			this.doCreate(dto);
			logger.info("njcc====>>AccountCertification返回参数保存数据完成!");
		}
		return resultMap;
	}

	@Override
	public AccountCertification findByLoginName(String loginName) {
		AccountCertification accountCertification = accountCertificationMapper.findByLoginName(loginName);
		return accountCertification;
	}
}
