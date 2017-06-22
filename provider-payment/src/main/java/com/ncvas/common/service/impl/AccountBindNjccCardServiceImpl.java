package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountBindNjccCardMapper;
import com.ncvas.common.service.biz.UpdateTokenBizService;
import com.ncvas.payment.entity.*;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountBindNjccCardService;
import com.ncvas.payment.service.AccountCertificationService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.util.AmountUtils;
import com.ncvas.util.EncryptUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountBindNjccCardService")
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class AccountBindNjccCardServiceImpl extends AbstractBaseService<AccountBindNjccCard> implements AccountBindNjccCardService {
    @Autowired
    private AccountBindNjccCardMapper accountBindNjccCardMapper;
    @Autowired
    private AccountCertificationService accountCertificationService;

    @Override
    protected BaseMapper<AccountBindNjccCard> getBaseMapper() {
        return accountBindNjccCardMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountRegisterDTO.class;
    }

    private static final Logger logger = LoggerFactory.getLogger(AccountBindNjccCardServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;
    @Value("#{configProperties['encryptKey']}")
    private String encryptKey;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UpdateTokenBizService updateTokenBizService;

    @Override
    public Map<String, Object> redisSaveCardInfo(Map<String, Object> paraMap) throws Exception {
        Map resMap = new HashMap();
        AccountLoginDTO accountLoginDTO = (AccountLoginDTO) paraMap.get("accountLoginDTO");
        String payPwd = (String) paraMap.get("payPwd");
        paraMap.put("payPwd", EncryptUtils.aesEncrypt(payPwd, encryptKey));
        redisTemplate.opsForValue().set(accountLoginDTO.getMemberCode() + RedisEnum.NJCC_BIND_CARD_INFO.getCode(), paraMap);
        resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), PayPlatformEnum.RESPONSE_SUCCESS.getCode());
        return resMap;
    }

    @Override
    public Map<String, Object> accountBindNjccCard(AccountLoginDTO accountLoginDTO, String aliasCode, String memberCode, String memberName, String socialCode, String registered, String cardCategory,String reqBizType) throws Exception {

        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("aliasCode", aliasCode);
        paraMap.put("memberCode", memberCode);
        paraMap.put("memberName", memberName);
        paraMap.put("socialCode", socialCode);
        paraMap.put("registered", registered);
        paraMap.put("cardCategory", cardCategory);
        paraMap.put("accountLoginDTO", accountLoginDTO);
        Map<String, Object> resultMap = anjiePayService.accountBindNjccCard(paraMap);
        if (PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            AccountBindNjccCardDTO dto = new AccountBindNjccCardDTO();
            dto.setAliasCode(AmountUtils.fixNumberRemovePre0(aliasCode));
            dto.setMemberCode(memberCode);
            dto.setReqBizType(reqBizType);
            AccountBindNjccCard abnc = this.doQuery(dto);
            boolean exist = false;
            if (abnc == null) {
                abnc = new AccountBindNjccCard();
            } else {
                exist = true;
            }
            abnc.setAliasCode(AmountUtils.fixNumberRemovePre0(aliasCode));
            abnc.setMemberCode(memberCode);
            abnc.setMemberName(memberName);
            abnc.setSocialCode(socialCode);
            abnc.setRegistered(registered);
            abnc.setBindingStatus(PayPlatformEnum.ACCOUNT_BIND_NJCC_CARD_BINDINGSTATUS.getCode());
            abnc.setLoginName(accountLoginDTO.getLoginName());
            abnc.setCardCategory((String) paraMap.get("cardCategory"));
            abnc.setReqBizType(reqBizType);
            if (exist) {
                this.doUpdate(abnc);
            } else {
                this.doCreate(abnc);
            }

            AccountCertificationDTO acto = new AccountCertificationDTO();
            acto.setIdNo(socialCode);
            AccountCertification accountCertification = accountCertificationService.doQuery(acto);
            if (accountCertification == null) {
                accountCertification = new AccountCertification();
                accountCertification.setLoginName(accountLoginDTO.getLoginName());
                accountCertification.setIdName(memberName);
                accountCertification.setIdType(PayPlatformEnum.ACCOUNT_QUICKPAYMENT_BANK_CARD_IDTYPE.getCode());
                accountCertification.setIdNo(socialCode);
                accountCertificationService.doCreate(accountCertification);
            }

            //把实名信息同步到token
            updateToken(abnc);

        }
        return resultMap;
    }

    private void updateToken(AccountBindNjccCard abnc) {
        String Temp = (String) redisTemplate.opsForValue().get(abnc.getMemberCode() + RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
        Map<String, Object> json = JSONObject.fromObject(Temp);
        String token = (String) json.get("token");
        updateTokenBizService.updateToken(token, null, abnc.getMemberCode(), abnc.getMemberName(), abnc.getSocialCode());
    }

    @Override
    public Map<String, Object> accountUnBindNjccCard(String aliasCode, String memberCode) throws Exception {
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("aliasCode", aliasCode);
        paraMap.put("memberCode", memberCode);
        Map<String, Object> unbindNjccCardMap = anjiePayService.accountUnbindNjccCard(paraMap);
        if (unbindNjccCardMap != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(unbindNjccCardMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            AccountBindNjccCardDTO dto = new AccountBindNjccCardDTO();
            dto.setAliasCode(AmountUtils.fixNumberRemovePre0(aliasCode));
            dto.setMemberCode(memberCode);
            AccountBindNjccCard accountBindNjccCard = this.doQuery(dto);

            if (accountBindNjccCard != null) {
                accountBindNjccCard.setBindingStatus(PayPlatformEnum.ACCOUNT_BIND_NJCC_CARD_UNBINDINGSTATUS.getCode());
                int updateInt = this.doUpdate(accountBindNjccCard);
                if (updateInt == 1) {
                    logger.info("update智汇卡绑定表信息====>>success");
                } else {
                    logger.info("update智汇卡绑定表信息====>>error");
                }
            }
        }
        return unbindNjccCardMap;
    }
}
