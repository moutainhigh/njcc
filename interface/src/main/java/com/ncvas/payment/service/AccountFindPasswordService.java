package com.ncvas.payment.service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/22.
 */
public interface AccountFindPasswordService {
    public Map<String,Object> accountFindPassword(String loginName, String type, String flag) throws Exception;
}
