package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountLoginMapper;
import com.ncvas.payment.entity.AccountLogin;
import com.ncvas.payment.entity.AccountLoginDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountModifyMemberInfoService;
import com.ncvas.payment.service.AnjiePayService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by caiqi on 2016/12/20.
 */
@Service("accountModifyMemberInfoService")
public class AccountModifyMemberInfoServiceImpl extends AbstractBaseService<AccountLogin> implements AccountModifyMemberInfoService {

    private static final Logger logger =LoggerFactory.getLogger(AccountCertificationServiceImpl.class);
    @Autowired
    private AccountLoginMapper accountLoginMapper;
    @Autowired
    private AnjiePayService anjiePayService;

    @Override
    protected BaseMapper<AccountLogin> getBaseMapper() {
        return accountLoginMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountLoginDTO.class;
    }

    @Override
    public Map<String, Object> modifyMemberInfo(Map<String, String> paraMap) throws Exception {
        Map<String, Object> resultMap = anjiePayService.accountModifyMembetInfo(paraMap);
        if (resultMap.get("result").equals(true) && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {

            AccountLoginDTO dto = new AccountLoginDTO();
            dto.setLoginName(paraMap.get("loginName"));
            dto.setMemberCode(paraMap.get("memberCode"));
            /*dto.setCustName(paraMap.get("name"));*/
            dto.setEmail(paraMap.get("email"));
           /* dto.setMobile(paraMap.get("mobile"));*/
            dto.setAddress(paraMap.get("address"));
            /*dto.setNickname(paraMap.get("nickname"));*/
            dto.setSex(paraMap.get("sex"));

            List<AccountLogin> accountLogin = accountLoginMapper.getAccountLogin(dto);
            if(accountLogin.size()==0){
                this.doCreate(dto);
                logger.info("njcc||返回参数保存数据完成!");
            }else{
                dto.setUpdatedate(new Date());
                dto.setId(accountLogin.get(0).getId());
                this.doUpdate(dto);
                logger.info("njcc||返回参数修改数据完成!");
            }
        }
        return resultMap;
    }
}
