package com.ncvas.payment.service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月06日
 */
public interface AccountQueryMainAliasCodeService {
    public Map<String,Object> accountQueryMainAliasCode(String memberCode) throws Exception;

    public String getCardAccBalance(String memberCode) throws Exception;
}
