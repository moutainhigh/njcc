package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountQuickPayOrder;

import java.util.Map;

/**
 * Created by caiqi on 2016/12/19.
 */
public interface AccountQuickPayOrderService extends BaseService<AccountQuickPayOrder> {

    public Map<String,Object> quickPayOrder(Map<String, String> paraMap) throws Exception;

    AccountQuickPayOrder getByOrderId(String orderId);

    void updateStatus(String id, String status, String remark);
}
