package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountQuickPayOrderMapper;
import com.ncvas.payment.entity.AccountQuickPayOrder;
import com.ncvas.payment.entity.AccountQuickPayOrderDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountQuickPayOrderService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.payment.service.biz.AnjiePayBizService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caiqi on 2016/12/19.
 */
@Service("accountQuickPayOrderService")
public class AccountQuickPayOrderServiceImpl extends AbstractBaseService<AccountQuickPayOrder> implements AccountQuickPayOrderService {

    @Autowired
    private AccountQuickPayOrderMapper accountQuickPayOrderMapper;

    @Autowired
    private AnjiePayService anjiePayService;
    @Autowired
    private AnjiePayBizService anjiePayBizService;

    private static final Logger logger =LoggerFactory.getLogger(AccountQuickPayOrderServiceImpl.class);

    @Override
    public Map<String, Object> quickPayOrder(Map<String, String> paraMap) throws Exception {
        Map res = new HashMap();
        res.put("aliasCode",paraMap.get("payeeAliasCode"));
        Map<String,Object> resultMap = anjiePayService.accountQueryAliasCode(res);
        if (PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            /*JSONObject json = JSONObject.fromObject(resultMap.get("zhCard"));
            if (json.get("cardCategory").equals("09")) {
                resultMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),2);
                resultMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"虚拟卡不能快充预约！");
                return resultMap;
            }*/
            Map<String,Object> zhCard = (Map<String, Object>) resultMap.get("zhCard");
            if (zhCard.get("cardCategory").equals("00")) {
                resultMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),2);
                resultMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"虚拟卡不能快充预约！");
                return resultMap;
            }
        }
        resultMap = anjiePayBizService.accountQuickPayOrder(paraMap);
        if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
            Map<String,String> temp = (Map<String, String>) resultMap.get("responseDetail");
            resultMap.put("cdtalias",temp.get("ALIASCODE"));
            resultMap.put("orderseq",temp.get("ORDERNO"));
            resultMap.put("payalias",temp.get("MSTALIASCODE"));
            resultMap.put("transamt",temp.get("ORDERAMT"));
            resultMap.put("transtime",temp.get("ORDERTIME"));
            resultMap.put("accbalance",temp.get("ACCBALANCE"));
            resultMap.remove("responseDetail");
        }
        return resultMap;
        /*Map<String,Object> resultMap = anjiePayService.accountQuickPayOreder(paraMap);
        AccountQuickPayOrderDTO dto = new AccountQuickPayOrderDTO();
        dto.setLoginName(paraMap.get("loginName"));
        dto.setOrderID(paraMap.get("orderID"));
        dto.setAmount(String.valueOf(Double.parseDouble(paraMap.get("amount"))/100));
        dto.setPayeeAliasCode(paraMap.get("payeeAliasCode"));
        if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            dto.setStatus(PayPlatformEnum.QUICK_PAY_ORDER_SUCCESS.getCode());
            dto.setRemark(PayPlatformEnum.QUICK_PAY_ORDER_SUCCESS.getDescription());
        }else {
            dto.setStatus(PayPlatformEnum.QUICK_PAY_ORDER_FAILURE.getCode());
            dto.setRemark(String.valueOf(resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode())));
        }
        this.doCreate(dto);
        return resultMap;*/
    }

    @Override
    public AccountQuickPayOrder getByOrderId(String orderId) {
        return accountQuickPayOrderMapper.getByOrderId(orderId);
    }

    @Override
    public void updateStatus(String orderID, String status, String remark) {
        accountQuickPayOrderMapper.updateStatus(orderID, status, remark);
    }

    @Override
    protected BaseMapper<AccountQuickPayOrder> getBaseMapper() {
        return accountQuickPayOrderMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountQuickPayOrderDTO.class;
    }
}
