package com.ncvas.common.dao;


import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountQuickPayOrder;
import org.apache.ibatis.annotations.Param;

/**
 * Created by caiqi on 2016/12/19.
 */
public interface AccountQuickPayOrderMapper extends BaseMapper<AccountQuickPayOrder> {
    AccountQuickPayOrder getByOrderId(@Param("orderID")String orderID);
    void updateStatus(@Param("orderID")String id, @Param("status")String status, @Param("remark")String remark);
}
