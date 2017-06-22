package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountRegisterMapper;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.entity.AccountRegister;
import com.ncvas.payment.entity.AccountRegisterDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountLoginService;
import com.ncvas.payment.service.AccountRegisterService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.util.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountRegisterService")
public class AccountRegisterServiceImpl extends AbstractBaseService<AccountRegister> implements AccountRegisterService {
	@Autowired
	private AccountRegisterMapper accountRegisterMapper;
	@Value("#{configProperties['encryptKey']}")
	private String encryptKey;
	@Override
	protected BaseMapper<AccountRegister> getBaseMapper() {
		return accountRegisterMapper;
	}

	@Override
	protected Class<? extends ValueObject> getEntityDTOClass() {
		return AccountRegisterDTO.class;
	}
	private static final Logger logger =LoggerFactory.getLogger(AccountRegisterServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;
	@Autowired
	private AccountLoginService accountLoginService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public Map<String, Object> accountRegister(Map<String, String> paraMap) throws Exception{
		Map<String,Object> resMap = new HashMap<>();
		String loginPwd = (String)paraMap.get("loginPwd");
		String loginName = (String)paraMap.get("loginName");
		String nickname = (String)paraMap.get("nickname");
		String checkCode = (String)paraMap.get("checkCode");
		String referee = (String) paraMap.get("referee");
		String regType = (String) paraMap.get("regType");
		String orderId = (String) paraMap.get("orderId");
		//因为支付不返回验证码给我们，所以此处不做验证码校验
		//String redisSmsKey = loginName+ RedisEnum.SMS_KEY_REGISTER.getCode();
		//String redisSmsCode = (String)redisTemplate.opsForValue().get(redisSmsKey);
		//if(StringUtil.isEmpty(redisSmsCode)){
		//	resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),"5");
		//	resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"请获取验证码");
		//	return resMap;
		//}else if(!checkCode.equals(redisSmsCode)){
		//	resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),"5");
		//	resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"验证码不正确");
		//	return resMap;
		//}
		Map<String, String> payReq = new HashMap<String,String>();
		payReq.put("loginPwd",loginPwd);
		payReq.put("loginName",loginName);
		payReq.put("type", PayPlatformEnum.ACCOUNT_REGISTER_TYPE_PERSONAL.getCode());
		payReq.put("nickname", nickname);
		payReq.put("referee", referee);
		payReq.put("orderId", orderId);
		payReq.put("checkCode", checkCode);
		Map<String,Object> resultMap = anjiePayService.accountRegister(payReq);
		if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			//redisTemplate.delete(redisSmsKey);
			AccountRegister ar = new AccountRegister();
			ar.setLoginName(payReq.get("loginName"));
//			ar.setLoginPwd(payReq.get("loginPwd"));
			ar.setLoginPwd(EncryptUtils.aesEncrypt(payReq.get("loginPwd"),encryptKey));
			ar.setRegType(StringUtil.isEmpty(regType)?PayPlatformEnum.ACCOUNT_REGISTER_REG_TYPE_NJCC.getCode():regType);
			ar.setNickname(payReq.get("nickname"));
			ar.setReferee(payReq.get("referee"));
			this.doCreate(ar);
			resultMap = accountLoginService.accountLogin(payReq);
		}
		return resultMap;
	}

	@Override
	public int reportsRegisterPeople(AccountRegisterDTO accountRegisterDTO) {
		return accountRegisterMapper.reportsRegisterPeople(accountRegisterDTO);
	}

	@Override
	public List<AccountRegisterDTO> reportsPeopleAndType(AccountRegisterDTO accountRegisterDTO) {
		return accountRegisterMapper.reportsPeopleAndType(accountRegisterDTO);
	}

	@Override
	public List<AccountRegister> queryReqBizType() {
		return accountRegisterMapper.queryReqBizType();
	}

}
