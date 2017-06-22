package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountLoginHistory;
import com.ncvas.payment.entity.AccountLoginHistoryDTO;

/**
 * Created by Administrator on 2016/11/22.
 */
public interface AccountLoginHistoryService extends BaseService<AccountLoginHistory>{

    public int reportsPeople(AccountLoginHistoryDTO accountLoginHistoryDTO);
}
