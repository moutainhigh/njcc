package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountSetMainAliasCodeMapper;
import com.ncvas.common.service.biz.UpdateTokenBizService;
import com.ncvas.payment.entity.*;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountCertificationService;
import com.ncvas.payment.service.AccountRegisterNjccVirtualService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.util.EncryptUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月13日
 */
@Service("accountRegisterNjccVirtualService")
public class AccountRegisterNjccVirtualServiceImpl extends AbstractBaseService<AccountSetMainAliasCode> implements AccountRegisterNjccVirtualService {
	private static final Logger logger =LoggerFactory.getLogger(AccountRegisterNjccVirtualServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Autowired
	private AccountSetMainAliasCodeMapper accountSetMainAliasCodeMapper;
	@Autowired
	private AccountCertificationService accountCertificationService;
	@Autowired
	private UpdateTokenBizService updateTokenBizService;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	protected BaseMapper<AccountSetMainAliasCode> getBaseMapper() {
		return accountSetMainAliasCodeMapper;
	}

	@Override
	protected Class<? extends ValueObject> getEntityDTOClass() {
		return AccountRegisterDTO.class;
	}

	@Override
	public Map<String, Object> accountRegisterNjccVirtual(String memberCode, String bankAcctId, String memberName, String socialCode, String payPwd,String loginName) throws Exception{
		Map<String, Object> paraMap = new HashMap<String,Object>();
		paraMap.put("memberCode",memberCode);
		paraMap.put("bankAcctId",bankAcctId);
		paraMap.put("memberName",memberName);
		paraMap.put("socialCode",socialCode);
		paraMap.put("payPwd",payPwd);

		Map<String, Object> resultMap = anjiePayService.accountRegisterNjccVirtual(paraMap);
		if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			AccountSetMainAliasCodeDTO dto = new AccountSetMainAliasCodeDTO();
			dto.setAliasCode(bankAcctId);
			dto.setMemberCode(memberCode);
			dto.setMemberName(memberName);
			dto.setSocialCode(socialCode);
			//			fuyou  富有
			dto.setRemark(EncryptUtils.aesEncrypt((String)paraMap.get("payPwd"),"fuyou"));
			List<AccountSetMainAliasCode> accountSetMainAliasCodeList = accountSetMainAliasCodeMapper.findByMemberCode(dto);
			if (accountSetMainAliasCodeList.size() == 0) {
				this.doCreate(dto);
				logger.info("njcc====>>返回参数保存数据完成!");
			}else {
				dto.setUpdatedate(new Date());
				dto.setId(accountSetMainAliasCodeList.get(0).getId());
				this.doUpdate(dto);
				logger.info("njcc||返回参数修改数据完成!");
			}

			AccountCertificationDTO acdto = new AccountCertificationDTO();
			acdto.setIdNo((String)paraMap.get("socialCode"));
			AccountCertification accountCertification = accountCertificationService.doQuery(acdto);
			boolean aqpExist = true;
			if(accountCertification==null){
				aqpExist = false;
			}
			accountCertification.setIdNo(socialCode);
			accountCertification.setIdName(memberName);
			accountCertification.setIdType(PayPlatformEnum.ACCOUNT_QUICKPAYMENT_BANK_CARD_IDTYPE.getCode());
			accountCertification.setLoginName(loginName);
			if(aqpExist){
				accountCertificationService.doUpdate(accountCertification);
				logger.info("njcc====>>更新数据到实名表完成!");
			}else {
				accountCertificationService.doCreate(accountCertification);
				logger.info("njcc====>>保存数据到实名表完成!");
			}
			/**开通虚拟卡成功， 更新token1和token2的主卡号*/
			String temp2 = (String) redisTemplate.opsForValue().get(dto.getMemberCode() + RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
			Map<String, Object> json = net.sf.json.JSONObject.fromObject(temp2);
			String tokenval =(String)json.get("token");
			updateTokenBizService.updateToken(tokenval, dto.getAliasCode(), dto.getMemberCode(),dto.getMemberName(),dto.getSocialCode());
			Map<String,Object> result = new HashMap<>();
			result.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
			result.put("bankAcctId",bankAcctId);
			result.put("memberCode",memberCode);
			result.put("memberName",memberName);
			result.put("socialCode",socialCode);
			result.put("loginName",loginName);
			return result;
		}
		return resultMap;
	}
}
