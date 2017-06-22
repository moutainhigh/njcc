package com.ncvas.payment.service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年12月13日
 */
public interface AccountRegisterNjccVirtualService {
    public Map<String,Object> accountRegisterNjccVirtual(String memberCode, String bankAcctId, String memberName, String socialCode, String payPwd,String loginName) throws Exception;
}
