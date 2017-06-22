package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountQuickpaymentBankCard;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月07日
 */
public interface AccountUnBinDingQuickPayMentBankCardService extends BaseService<AccountQuickpaymentBankCard> {
    public Map<String, Object> accountUnBinDingQuickPayMentBankCard(String loginName,String bankAcct) throws Exception;
}
