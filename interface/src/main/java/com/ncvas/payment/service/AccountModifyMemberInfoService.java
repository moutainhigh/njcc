package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountLogin;

import java.util.Map;

/**
 * Created by caiqi on 2016/12/20.
 */
public interface AccountModifyMemberInfoService extends BaseService<AccountLogin> {
    public Map<String,Object> modifyMemberInfo(Map<String, String> paraMap) throws Exception;
}
