package com.ncvas.payment.service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountTransactionService {
    public Map<String, Object> accountTransaction(Map<String, String> paraMap) throws Exception;
}
