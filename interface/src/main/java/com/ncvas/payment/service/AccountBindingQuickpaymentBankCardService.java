package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountQuickpaymentBankCard;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
public interface AccountBindingQuickpaymentBankCardService extends BaseService<AccountQuickpaymentBankCard> {

    public Map<String,Object> accountBindingQuickpaymentBankCard(Map<String, String> paraMap) throws Exception;
}
