package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountCertificationMapper;
import com.ncvas.common.dao.AccountQuickpaymentBankCardMapper;
import com.ncvas.payment.entity.AccountQuickpaymentBankCard;
import com.ncvas.payment.entity.AccountQuickpaymentBankCardDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountBindingQuickpaymentBankCardService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
@Service("accountBindingQuickpaymentBankCardService")
public class AccountBindingQuickpaymentBankCardServiceImpl extends AbstractBaseService<AccountQuickpaymentBankCard> implements AccountBindingQuickpaymentBankCardService {

    @Autowired
    private AccountQuickpaymentBankCardMapper accountQuickpaymentBankCardMapper;

    private static final Logger logger =LoggerFactory.getLogger(AccountBindingQuickpaymentBankCardServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;

    @Autowired
    private AccountCertificationMapper accountCertificationMapper;

    @Override
    public Map<String, Object> accountBindingQuickpaymentBankCard(Map<String, String> paraMap) throws Exception {
        Map<String,Object> resultMap = anjiePayService.accountBindingQuickpaymentBankCard(paraMap);
        if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {

            // 将银行卡信息保存到对应的实名认证表中
            /*AccountCertificationDTO accountCertificationDTO = new AccountCertificationDTO();
            accountCertificationDTO.setIdNo(paraMap.get("idNo"));
            List<AccountCertification> accountCertificationList =  accountCertificationMapper.findByIdNo(accountCertificationDTO);
            accountCertificationDTO.setBankAcct(paraMap.get("bankAcct"));
            accountCertificationDTO.setBankCode(paraMap.get("bankCode"));
            accountCertificationDTO.setMobile(paraMap.get("mobile"));
            if (accountCertificationList.size() > 0 && StringUtils.isBlank(accountCertificationList.get(0).getBankAcct())) {
                accountCertificationMapper.update(accountCertificationDTO);
            }else if (accountCertificationList.size() == 0) {
                accountCertificationDTO.setLoginName(paraMap.get("loginName"));
                accountCertificationDTO.setIdType(paraMap.get("idType"));
                accountCertificationDTO.setIdNo(paraMap.get("idNo"));
                accountCertificationMapper.add(accountCertificationDTO);
            }
            logger.info("njcc====>>AccountCertification返回参数保存数据完成!");*/

            //新增一条绑定快捷银行卡的记录
            AccountQuickpaymentBankCardDTO accountQuickpaymentBankCardDTO = new AccountQuickpaymentBankCardDTO();
            accountQuickpaymentBankCardDTO.setLoginName(paraMap.get("loginName"));
            accountQuickpaymentBankCardDTO.setBankAcct(paraMap.get("bankAcct"));
            accountQuickpaymentBankCardDTO.setBankCode(paraMap.get("bankCode"));
            accountQuickpaymentBankCardDTO.setIdType(paraMap.get("idType"));
            accountQuickpaymentBankCardDTO.setIdNo(paraMap.get("idNo"));
            accountQuickpaymentBankCardDTO.setMobile(paraMap.get("mobile"));
            this.doCreate(accountQuickpaymentBankCardDTO);
            logger.info("njcc====>>AccountQuickpaymentBankCard返回参数保存数据完成!");

        }
        return resultMap;
    }

    @Override
    protected BaseMapper<AccountQuickpaymentBankCard> getBaseMapper() {
        return accountQuickpaymentBankCardMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountQuickpaymentBankCardDTO.class;
    }
}
