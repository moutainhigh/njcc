package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountWriteCardMapper;
import com.ncvas.common.service.bizImpl.AccountWriteCardBizServiceImpl;
import com.ncvas.payment.entity.AccountWriteCard;
import com.ncvas.payment.entity.AccountWriteCardDTO;
import com.ncvas.payment.service.AccountWriteCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xiecaixia on 2017-4-25.
 */
@Service("accountWriteCardService")
public class AccountWriteCardServiceImpl extends AbstractBaseService<AccountWriteCard> implements AccountWriteCardService {

    private static final Logger logger = LoggerFactory.getLogger(AccountWriteCardServiceImpl.class);
    @Autowired
    private AccountWriteCardMapper accountWriteCardMapper;

    @Override
    protected BaseMapper<AccountWriteCard> getBaseMapper() {
        return accountWriteCardMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountWriteCardDTO.class;
    }

    @Override
    public AccountWriteCard getByOrderId(String orderId) {
        return accountWriteCardMapper.getByOrderId(orderId);
    }

    @Override
    public List<String> queryGroupByAliascode(int beginNum, int endNum) {
        List<String> cardNos = accountWriteCardMapper.queryGroupByAliascode(beginNum, endNum);
        return cardNos;

    }

    @Override
    public Integer countGroupByAliascode() {
        Integer count = accountWriteCardMapper.countGroupByAliascode();
        return count;
    }


}
