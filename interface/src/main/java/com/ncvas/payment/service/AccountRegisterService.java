package com.ncvas.payment.service;

import com.ncvas.payment.entity.AccountRegister;
import com.ncvas.payment.entity.AccountRegisterDTO;

import java.util.List;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountRegisterService {
    int reportsRegisterPeople(AccountRegisterDTO accountRegisterDTO);
    public Map<String,Object> accountRegister(Map<String, String> paraMap) throws Exception;
    public List<AccountRegisterDTO> reportsPeopleAndType(AccountRegisterDTO accountRegisterDTO);

    /**
     * 获取所有不同的请求来源
     * @return
     */
    List<AccountRegister> queryReqBizType();
}
