package com.ncvas.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountLoginMapper;
import com.ncvas.common.entity.Advertisement;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.entity.*;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.enums.ReqBizTypeEnum;
import com.ncvas.payment.service.*;
import com.ncvas.util.EncryptUtils;
import com.ncvas.util.Md5Helper;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by caiqi on 2016/11/21.
 */
@Service("accountLoginService")
public class AccountLoginServiceImpl extends AbstractBaseService<AccountLogin> implements AccountLoginService {

    private static final Logger logger = LoggerFactory.getLogger(AccountLoginServiceImpl.class);
    @Autowired
    private AccountLoginMapper accountLoginMapper;
    @Autowired
    private AnjiePayService anjiePayService;
    @Autowired
    private AccountLoginHistoryService accountLoginHistoryService;
    @Autowired
    private AccountQueryAliasCodeService accountQueryAliasCodeService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("#{configProperties['token.minutes']}")
    private String minutes;
    @Value("#{configProperties['encryptKey']}")
    private String encryptKey;

    @Override
    protected BaseMapper<AccountLogin> getBaseMapper() {
        return accountLoginMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountLoginDTO.class;
    }

    @Override
    public Map<String, Object> accountLogin(Map reqMap) throws Exception {
        String devid = (String) reqMap.get("devid");
        String reqBizType = (String) reqMap.get("reqBizType");
        reqMap.remove("devid");
        reqMap.remove("reqBizType");

        Map<String,Object> resultMap = anjiePayService.accountLogin(reqMap);
        Map<String, Object> result = new HashMap<String, Object>();
        //		自用，如果不是0000 就是有问题
        result.put("responseCode", resultMap.get("responseCode"));
        result.put("responseDesc", resultMap.get("responseDesc"));
        if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
            AccountLoginDTO dto = new AccountLoginDTO();
            Map<String,Object> resultMember = (Map<String, Object>) resultMap.get("memberInfo");
            String aliasCode = null;

            Map<String, Object> resultMapForMasterCard = (Map<String, Object>)resultMap.get("zhCard");
            if (resultMapForMasterCard != null) {
                dto.setAliascode(String.valueOf(resultMapForMasterCard.get("aliasCode")));
            }
            /*if(SetPwd.HAVE_BEEN_SETTED.getValue().equals(resultMap.get("setPwd"))){
                Map<String, Object> memberCodeMap = new HashMap<String, Object>();
                memberCodeMap.put("memberCode", resultMember.get("memberCode"));
                Map<String, Object> resultInfoMap = accountQueryMainAliasCodeService.accountQueryMainAliasCode(memberCodeMap);
                aliasCode = (String) resultInfoMap.get("aliasCode");
            }*/
            dto.setMemberCode(String.valueOf(resultMember.get("memberCode")));
            dto.setLoginName((String) resultMember.get("loginName"));
            dto.setMemberName((String) resultMember.get("memberName"));
            dto.setEmail((String) resultMember.get("email"));
            dto.setMobile((String) resultMember.get("mobile"));
            dto.setCustName((String) resultMember.get("name"));
            dto.setLevelCode(String.valueOf(resultMember.get("levelCode")));
            dto.setIdNo((String) resultMember.get("idNo"));
            dto.setNickname((String) resultMember.get("nickname"));
            dto.setBalance(new BigDecimal(String.valueOf(resultMember.get("balance"))));
            //dto.addDesc("M.CREATED");
            dto.setOrders(dto.getOrders());
            dto.setLogintime((String) resultMember.get("lastLoginTime"));

            //统计总登录次数
            AccountLoginHistoryDTO dto2 = new AccountLoginHistoryDTO();
            dto2.setMemberCode(dto.getMemberCode());
            List<AccountLoginHistory> accountLoginHistoryList = accountLoginHistoryService.doQuery(dto2,1,Integer.MAX_VALUE);
            dto.setLogincount(String.valueOf(accountLoginHistoryList.size()));
            dto.setReqBizType(reqBizType);
            //dto.setLogincount((String) resultMember.get("loginCount"));
            List<AccountLogin> accountLogin = accountLoginMapper.getAccountLogin(dto);
            AccountLogin createMember = new AccountLogin();

            //新建用户登录记录
            AccountLoginHistoryDTO accountLoginHistoryDTO = new AccountLoginHistoryDTO();
            accountLoginHistoryDTO.setMemberCode(String.valueOf(resultMember.get("memberCode")));
            accountLoginHistoryDTO.setMemberName((String) resultMember.get("memberName"));
            accountLoginHistoryDTO.setMobile((String) resultMember.get("mobile"));
            accountLoginHistoryDTO.setCustName((String) resultMember.get("name"));
            accountLoginHistoryDTO.setReqBizType(reqBizType);
            accountLoginHistoryService.doCreate(accountLoginHistoryDTO);

            if(accountLogin.size()==0){
                createMember = this.doCreate(dto);
                logger.info("njcc||返回参数保存数据完成!");
            }else{
                dto.setUpdatedate(new Date());
                dto.setId(accountLogin.get(0).getId());
                this.doUpdate(dto);
                logger.info("njcc||返回参数修改数据完成!");
            }

            /**为了app调用新接口， 仍能兼容旧接口，更新token1的数据（把主卡号更新到token1上） */
            Map<String, Object> paraMap = new HashMap<String,Object>();
            paraMap.put("memberCode",dto.getMemberCode());
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setLoginid(dto.getNickname());
            memberDTO.setMobile(dto.getMobile());
            memberDTO.setCustname(dto.getCustName());
            memberDTO.setAliascode(aliasCode);
            memberDTO.setCookie(dto.getCookie());
            memberDTO.setId(dto.getId());
            memberDTO.setSocialcode(dto.getIdNo());
            memberDTO.setMemberCode(String.valueOf(resultMember.get("memberCode")));

            /*Map<String, Object> resultMapForMasterCard = queryMainAliasCodeService.accountQueryMainAliasCode(paraMap);
            if(resultMapForMasterCard != null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals((String)resultMapForMasterCard.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                memberDTO.setAliascode(String.valueOf(resultMapForMasterCard.get("aliasCode")));
            }*/

            if (resultMapForMasterCard != null) {
                memberDTO.setAliascode(String.valueOf(resultMapForMasterCard.get("aliasCode")));
            }

            // 创建新的TOKEN
            String sourceToken = devid + System.currentTimeMillis() + (accountLogin.size()==0?createMember.getId():accountLogin.get(0).getId()) + new Random().nextInt(1000);
            dto.setToken(Md5Helper.md5Hex(sourceToken));
            memberDTO.setToken(Md5Helper.md5Hex(sourceToken));
            dto.setCookie(EncryptUtils.aesEncrypt((String) reqMap.get("loginPwd"),encryptKey));
            memberDTO.setCookie(EncryptUtils.aesEncrypt((String) reqMap.get("loginPwd"),encryptKey));
            try {
                String  Temp = (String)redisTemplate.opsForValue().get(dto.getMemberCode()+ RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
                if(!StringUtil.isEmpty(Temp)){
                    Map<String,Object> json =(Map<String,Object>) JSONObject.parse(Temp);
                    String token =(String)json.get("token");
                    redisTemplate.delete(token+RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
                }
                String  oldTemp = (String)redisTemplate.opsForValue().get(memberDTO.getToken()+"memberService");
                if(!StringUtil.isEmpty(oldTemp)){
                    Map<String,Object> json =(Map<String,Object>)JSONObject.parse(oldTemp);
                    String token =(String)json.get("token");
                    redisTemplate.delete(token+"memberService");
                }

                redisTemplate.opsForValue().set(dto.getMemberCode()+RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode(),JSONObject.toJSONString(dto),Integer.valueOf(minutes), TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(dto.getToken()+RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode(),JSONObject.toJSONString(dto),Integer.valueOf(minutes), TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(memberDTO.getLoginid()+"memberService",JSONObject.toJSONString(memberDTO),Integer.valueOf(minutes), TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(memberDTO.getToken()+"memberService",JSONObject.toJSONString(memberDTO),Integer.valueOf(minutes), TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.warn("MemberServiceImpl#redisTemplate#error", e);
            }

            result.put("memberCode",dto.getMemberCode());
            result.put("loginName", dto.getLoginName());
            result.put("memberName", dto.getMemberName());
            result.put("name", dto.getCustName());
            result.put("idNo", resultMember.get("idNo"));
            result.put("loginId", resultMember.get("nickname"));
            result.put("token", Md5Helper.md5Hex(sourceToken));
            result.put("certificationState",resultMap.get("certificationState"));

            // 智汇主卡信息
            Map<String, Object> mainCard = (Map<String, Object>) resultMap.get("zhCard");
            if (mainCard != null) {
                //Map<String, Object> zhCard = accountQueryAliasCodeService.accountQueryAliasCode((String) mainCard.get("aliasCode"));

                result.put("accBalance", mainCard.get("accBalance"));
                result.put("aliasCode", mainCard.get("aliasCode"));
                result.put("bindDate", mainCard.get("bindDate"));
                if (!StringUtil.isEmpty((String) mainCard.get("cardCategory"))) {
//						01：市民卡A卡 02：市民卡B卡 03：金陵通卡、紫金卡 04：市民卡C卡 05：旅游年卡 06：助老卡
                    String cardCategory = (String) mainCard.get("cardCategory");
//						重写类型，适配app
                    if("03".equals(cardCategory)){
                        if ("0000".equals(String.valueOf(mainCard.get("custClass")))) {
                            cardCategory = "07";//金陵通记名卡
                        } else {
                            cardCategory="08";//紫金卡
                        }
                    }else if ("00".equals(cardCategory)){
                        /**00是支付那边返回回来的， 给app的文档写了09*/
                        cardCategory="09";//虚拟卡
                    }
                    result.put("cardCategory",cardCategory);
                }
                //result.put("cardCategory", ((Map<String, Object>) resultMap.get("zhCard")).get("cardCategory"));
                result.put("cardClass", mainCard.get("cardClass"));
                result.put("cardStatus", mainCard.get("cardStatus"));
                result.put("cardType", mainCard.get("cardType"));
                result.put("custClass", mainCard.get("custClass"));
                result.put("photo", mainCard.get("photo"));
                result.put("psgnCode", mainCard.get("psgnCode"));
                result.put("registered", mainCard.get("registered"));
                result.put("setMain", mainCard.get("setMain"));
                result.put("setPwd", mainCard.get("setPwd"));
                result.put("zhAcctCode", mainCard.get("zhAcctCode"));
                result.put("zhMedicalCode", mainCard.get("zhMedicalCode"));
                result.put("lossStauts", mainCard.get("lossStatus"));
            } else {
                result.put("accBalance", null);
                result.put("aliasCode", null);
                result.put("bindDate", null);
                result.put("cardCategory",null);
                result.put("cardClass", null);
                result.put("cardStatus", null);
                result.put("cardType", null);
                result.put("custClass", null);
                result.put("photo", null);
                result.put("psgnCode", null);
                result.put("registered", null);
                result.put("setMain", null);
                result.put("setPwd", null);
                result.put("zhAcctCode", null);
                result.put("zhMedicalCode", null);
                result.put("lossStauts", null);
            }

            // 智汇卡或者银行卡列表
            Map<String, String> tempzhCard = new HashMap<String,String>();
            tempzhCard.put("memberCode",dto.getMemberCode());
            Map<String,Object> zhCardListMap = anjiePayService.accountCheckNjccCardList(tempzhCard);
            List<Map<String, Object>> resultlist = new ArrayList<>();
            if(zhCardListMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(zhCardListMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
                Map<String, Object> rows = new HashMap<>();
                if (zhCardListMap.get("zhCardList") != null) {
                    JSONArray zhCardList = JSONArray.fromObject(zhCardListMap.get("zhCardList"));
                    for (int i = 0; i < zhCardList.size(); i++) {
                        net.sf.json.JSONObject json = (net.sf.json.JSONObject) zhCardList.get(i);
                        Map<String, Object> temMap = new HashMap<>();
                        temMap.put("aliasCode", String.valueOf(json.get("aliasCode")));
                        temMap.put("type", 1);
                        if (!StringUtil.isEmpty(String.valueOf(json.get("cardCategory")))) {
                            String cardCategory = valOfCardCategory(json);
                            temMap.put("cardCategory",cardCategory);
                        }
                        //卡列表去掉c卡以及挂失卡
                        if (!"04".equals(String.valueOf(json.get("cardCategory"))) && !"1".equals(String.valueOf(json.get("lossStauts")))) {
                            resultlist.add(temMap);
                        }
                    }
                }
            }
            tempzhCard.put("loginName",dto.getLoginName());
            zhCardListMap = anjiePayService.accountQuickPayMentBankList(tempzhCard);
            if(zhCardListMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(zhCardListMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
                if (zhCardListMap.get("bankList") != null) {
                    JSONArray zhCardList = JSONArray.fromObject(zhCardListMap.get("bankList"));
                    for (int i = 0; i < zhCardList.size(); i++) {
                        net.sf.json.JSONObject json = (net.sf.json.JSONObject) zhCardList.get(i);
                        Map<String, Object> temMap = new HashMap<>();
                        temMap.put("aliasCode", String.valueOf(json.get("cardNo")));
                        temMap.put("type", 2);
                        resultlist.add(temMap);
                    }
                }
            }
            result.put("cardList",resultlist);
            logger.info("njcc||返回的参数为：" + JSON.toJSONString(result));
        }
        return result;
    }

    /*private List<Map<String, Object>> cardList(AccountLoginDTO dto) throws Exception {
        Map<String, String> tempzhCard = new HashMap<String,String>();
        tempzhCard.put("memberCode",dto.getMemberCode());
        Map<String,Object> zhCardListMap = anjiePayService.accountCheckNjccCardList(tempzhCard);
        List<Map<String, Object>> resultlist = new ArrayList<>();
        if(zhCardListMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(zhCardListMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            Map<String, Object> rows = new HashMap<>();
            if (zhCardListMap.get("zhCardList") != null) {
                JSONArray zhCardList = JSONArray.fromObject(zhCardListMap.get("zhCardList"));
                for (int i = 0; i < zhCardList.size(); i++) {
                    net.sf.json.JSONObject json = (net.sf.json.JSONObject) zhCardList.get(i);
                    Map<String, Object> temMap = new HashMap<>();
                    temMap.put("aliasCode", String.valueOf(json.get("aliasCode")));
                    temMap.put("type", 1);
                    if (!StringUtil.isEmpty(String.valueOf(json.get("cardCategory")))) {
                        String cardCategory = valOfCardCategory(json);
                        temMap.put("cardCategory",cardCategory);
                    }
                    resultlist.add(temMap);
                }
            }
        }
        tempzhCard.put("loginName",dto.getLoginName());
        zhCardListMap = anjiePayService.accountQuickPayMentBankList(tempzhCard);
        if(zhCardListMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(zhCardListMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            if (zhCardListMap.get("bankList") != null) {
                JSONArray zhCardList = JSONArray.fromObject(zhCardListMap.get("bankList"));
                for (int i = 0; i < zhCardList.size(); i++) {
                    net.sf.json.JSONObject json = (net.sf.json.JSONObject) zhCardList.get(i);
                    Map<String, Object> temMap = new HashMap<>();
                    temMap.put("aliasCode", String.valueOf(json.get("cardNo")));
                    temMap.put("type", 2);
                    resultlist.add(temMap);
                }
            }
        }
        return resultlist;
    }*/

    private String valOfCardCategory(net.sf.json.JSONObject json){
        //01：市民卡A卡 02：市民卡B卡 03：金陵通卡、紫金卡 04：市民卡C卡 05：旅游年卡 06：助老卡
        String  cardCategory = String.valueOf(json.get("cardCategory"));
        //重写类型，适配app
        if("03".equals(cardCategory)){
            if ("0000".equals(String.valueOf(json.get("custClass")))) {
                cardCategory = "07";//金陵通记名卡
            } else {
                cardCategory="08";//紫金卡
            }
        }else if ("00".equals(cardCategory)){
            /**00是支付那边返回回来的， 给app的文档写了09*/
            cardCategory="09";//虚拟卡
        }
        return cardCategory;
    }

}
