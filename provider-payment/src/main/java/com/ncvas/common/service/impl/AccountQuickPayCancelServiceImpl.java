package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountQuickPayCancelMapper;
import com.ncvas.payment.entity.AccountQuickPayCancel;
import com.ncvas.payment.entity.AccountQuickPayCancelDTO;
import com.ncvas.payment.enums.PayEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountQuickPayCancelService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
@Service("accountQuickPayCancelService")
public class AccountQuickPayCancelServiceImpl extends AbstractBaseService<AccountQuickPayCancel> implements AccountQuickPayCancelService {

    @Autowired
    private AccountQuickPayCancelMapper accountQuickPayCancelMapper;

    @Override
    protected BaseMapper<AccountQuickPayCancel> getBaseMapper() {
        return accountQuickPayCancelMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountQuickPayCancelDTO.class;
    }
    private static final Logger logger =LoggerFactory.getLogger(AccountQuickPayCancelServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;

    @Override
    public Map<String, Object> accountQuickPayCancel(String orderNo,String loginName) throws Exception {
        Map cancelMap = new HashMap();
        cancelMap.put("orderNo",orderNo);
        Map<String,Object> resultMap = anjiePayService.accountQuickPayCancel(cancelMap);
        AccountQuickPayCancel accountQuickPayCancel = new AccountQuickPayCancel();
        accountQuickPayCancel.setOrderNo(orderNo);
        accountQuickPayCancel.setLoginName(loginName);
        if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            accountQuickPayCancel.setStatus(PayEnum.PAY_SUCCESS.getCode());
            accountQuickPayCancel.setRemark("撤销成功");
        }else{
            accountQuickPayCancel.setStatus(PayEnum.PAY_FAILURE.getCode());
            accountQuickPayCancel.setRemark((String)resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
        }
        this.doCreate(accountQuickPayCancel);
        return resultMap;
    }

}
