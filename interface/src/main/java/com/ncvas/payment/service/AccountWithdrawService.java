package com.ncvas.payment.service;

import com.ncvas.base.entity.PaySystemMemoDto;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
public interface AccountWithdrawService {

    public Map<String,Object> accountWithdraw(String memberCode, Map<String, String> paraMap, String reqSource) throws Exception;

    public Map<String,Object> accountWithdraw2(String memberCode, Map<String, String> paraMap) throws Exception;
}
