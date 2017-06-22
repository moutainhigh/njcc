package com.ncvas.payment.service;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月24日
 */
public interface AccountCheckNjccCardListService {
    public Map<String,Object> accountCheckNjccCardList(String memberCode) throws Exception;
}
