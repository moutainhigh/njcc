package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountQuickpaymentBankCardMapper;
import com.ncvas.payment.entity.AccountQuickpaymentBankCard;
import com.ncvas.payment.entity.AccountQuickpaymentBankCardDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountUnBinDingQuickPayMentBankCardService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月07日
 */
@Service("AccountUnBinDingQuickPayMentBankCardService")
public class AccountUnBinDingQuickPayMentBankCardServiceImpl extends AbstractBaseService<AccountQuickpaymentBankCard> implements AccountUnBinDingQuickPayMentBankCardService {
	private static final Logger logger =LoggerFactory.getLogger(AccountUnBinDingQuickPayMentBankCardServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;
	@Autowired
	private AccountQuickpaymentBankCardMapper mapper;

	@Override
	protected BaseMapper<AccountQuickpaymentBankCard> getBaseMapper() {
		return mapper;
	}

	@Override
	protected Class<? extends ValueObject> getEntityDTOClass() {
		return AccountQuickpaymentBankCardDTO.class;
	}

	@Override
	public Map<String, Object> accountUnBinDingQuickPayMentBankCard(String loginName,String bankAcct) throws Exception {
		Map<String, Object> unbindParaMap = new HashMap<String, Object>();
		unbindParaMap.put("loginName",loginName);
		unbindParaMap.put("bankAcct",bankAcct);
		Map<String, Object> unBinPayCardMap = anjiePayService.accountUnBinDingQuickPayMentBankCard(unbindParaMap);
		if(unBinPayCardMap!=null&& PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(unBinPayCardMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			AccountQuickpaymentBankCardDTO abc = new AccountQuickpaymentBankCardDTO();
			abc.setLoginName(loginName);
			abc.setBankAcct(bankAcct);
			AccountQuickpaymentBankCard aqpmbc = this.doQuery(abc);
			if(aqpmbc!=null){
				aqpmbc.setBindType(PayPlatformEnum.ACCOUNT_QUICKPAYMENT_BANK_CARD_UNBINDINGSTATUS.getCode());
				int updateInt  = this.doUpdate(aqpmbc);
				if (updateInt==1){
					logger.info("update用户操作银行卡表信息====>>success");
				}else {
					logger.info("update用户操作银行卡表信息====>>error");
				}
			}
		}
		return unBinPayCardMap;
	}
}
