package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountSetMainAliasCodeMapper;
import com.ncvas.common.service.biz.UpdateTokenBizService;
import com.ncvas.payment.entity.AccountSetMainAliasCode;
import com.ncvas.payment.entity.AccountSetMainAliasCodeDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountQueryAliasCodeService;
import com.ncvas.payment.service.AccountSetMainAliasCodeService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.util.AmountUtils;
import com.ncvas.util.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/24.
 */
@Service("accountSetMainAliasCodeService")
public class AccountSetMainAliasCodeServiceImpl extends AbstractBaseService<AccountSetMainAliasCode> implements AccountSetMainAliasCodeService {

    private static final Logger logger = LoggerFactory.getLogger(AccountSetMainAliasCodeServiceImpl.class);
    @Override
    protected BaseMapper<AccountSetMainAliasCode> getBaseMapper() {
        return accountSetMainAliasCodeMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountSetMainAliasCodeDTO.class;
    }
    @Autowired
    private AnjiePayService anjiePayService;
    @Autowired
    private AccountSetMainAliasCodeMapper accountSetMainAliasCodeMapper;
    @Autowired
    private UpdateTokenBizService updateTokenBizService;
    @Autowired
    private AccountQueryAliasCodeService accountQueryAliasCodeService;
//    @Autowired
//    private RedPacketShareBizService redPacketShareBizService;
    @Override
    public Map<String, Object> accountSetMainAliasCode(String aliasCode, String memberCode, String token, String loginName, String custname, String payPwd,String isCheck,String orderId,String checkCode,String mobile) throws Exception {

        Map<String, String> paraMap = new HashMap<String,String>();
        paraMap.put("aliasCode", aliasCode);
        paraMap.put("memberCode", memberCode);
        paraMap.put("payPwd", payPwd);

        Map<String, String> resetParaMap = new HashMap<String,String>();
        resetParaMap.put("loginName", loginName);
        resetParaMap.put("type", PayPlatformEnum.ACCOUNT_MODIFY_PASSWORD_TYPE_NJCC_PAYPWD.getCode());
        resetParaMap.put("password", payPwd);

        paraMap.put("isCheck", isCheck);
        if(isCheck.equals("1")){
            paraMap.put("orderId", orderId);
            paraMap.put("checkCode", checkCode);
            paraMap.put("mobile", mobile);
        }

        Map<String, Object> resultMap = anjiePayService.accountSetMainAliasCode(paraMap);
        if (resultMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            AccountSetMainAliasCodeDTO dto = new AccountSetMainAliasCodeDTO();
            dto.setAliasCode(AmountUtils.fixNumberRemovePre0(aliasCode));
            dto.setMemberCode(memberCode);
            //			fuyou  富有
            dto.setRemark(EncryptUtils.aesEncrypt(payPwd, "fuyou"));
            List<AccountSetMainAliasCode> accountSetMainAliasCodeList = accountSetMainAliasCodeMapper.findByMemberCode(dto);
            if (accountSetMainAliasCodeList.size() == 0) {
                this.doCreate(dto);
                logger.info("njcc====>>返回参数保存数据完成!");
            } else {
                dto.setUpdatedate(new Date());
                dto.setId(accountSetMainAliasCodeList.get(0).getId());
                this.doUpdate(dto);
                logger.info("njcc||返回参数修改数据完成!");
            }
            //流程定制为不需要主动调用重置密码，支付那边做
            //重置支付密码
            /*Map<String, Object> resetMap = anjiePayService.accountResetPwd(resetParaMap);
            if(resetMap==null&&!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                return resetMap;
            }*/

            /**设置主卡成功， 更新token1和token2的主卡号*/
            updateTokenBizService.updateToken(token, dto.getAliasCode(), dto.getMemberCode(),custname,null);

            //检查是否有预留的分享红包，如果有则领取，无就不操作
//            redPacketShareBizService.obligateService(loginName,custName,paraMap.get("aliasCode"),loginName);
        }
        return resultMap;
    }
    @Override
    public boolean checkMainAliasCodeOpen(String mainAliaCode) throws Exception {
        boolean exist = false;//默认这个卡没有开通过
        /*AccountSetMainAliasCodeDTO dto = new AccountSetMainAliasCodeDTO();
        dto.setAliasCode(mainAliaCode);
        AccountSetMainAliasCode mainAliasCode = this.doQuery(dto);
        if(mainAliasCode!=null)exist = true;*/
        /*Map<String, String> paraMap = new HashMap<String,String>();
        paraMap.put("aliasCode", mainAliaCode);*/

        if(!exist) {
            Map<String, Object> result = accountQueryAliasCodeService.accountQueryAliasCode(mainAliaCode);
            logger.info("payment||返回的参数为：" + result);
            if (result != null) {
                if (PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals((String) result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
                    if (PayPlatformEnum.MAIN_ALIAS_CUSTCLASS_ACCOUNT_EXIST.getCode().equals(result.get("custClass")))
                        exist = true;
                }else if(PayPlatformEnum.RESPONSE_ACCOUNT_REPEAT.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                    exist = true;
                }
            }
        }
        return exist;
    }
}
