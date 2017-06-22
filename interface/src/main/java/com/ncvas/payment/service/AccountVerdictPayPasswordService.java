package com.ncvas.payment.service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月13日
 */
public interface AccountVerdictPayPasswordService {
    public Map<String,Object> accountVerdictPayPassword(String aliasCode,String payPwd,String type,String memberCode) throws Exception;
}
