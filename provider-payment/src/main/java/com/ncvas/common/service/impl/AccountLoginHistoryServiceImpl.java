package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountLoginHistroyMapper;
import com.ncvas.payment.entity.AccountLoginHistory;
import com.ncvas.payment.entity.AccountLoginHistoryDTO;
import com.ncvas.payment.service.AccountLoginHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/11/22.
 */
@Service(" accountLoginHistoryService")
public class AccountLoginHistoryServiceImpl extends AbstractBaseService<AccountLoginHistory> implements AccountLoginHistoryService {

    @Autowired
    private AccountLoginHistroyMapper accountLoginHistroyMapper;

    @Override
    protected BaseMapper<AccountLoginHistory> getBaseMapper() {
        return accountLoginHistroyMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountLoginHistoryDTO.class;
    }

    @Override
    public int reportsPeople(AccountLoginHistoryDTO accountLoginHistoryDTO) {
        return accountLoginHistroyMapper.reportsPeople(accountLoginHistoryDTO);
    }
}
