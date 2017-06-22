package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountQuickPayCancel;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
public interface AccountQuickPayCancelService extends BaseService<AccountQuickPayCancel> {

    public Map<String, Object> accountQuickPayCancel(String orderNo,String loginName) throws Exception;
}
