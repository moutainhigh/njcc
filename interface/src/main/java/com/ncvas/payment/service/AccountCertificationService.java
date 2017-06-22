package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountCertification;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountCertificationService extends BaseService<AccountCertification>{
    public Map<String,Object> accountCertification(Map<String, String> paraMap) throws Exception;
    AccountCertification findByLoginName(String loginName);
}
