package com.ncvas.common.service.impl;

import com.ncvas.base.entity.PaySystemMemo;
import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountWithdrawMapper;
import com.ncvas.common.entity.PayOrder;
import com.ncvas.common.service.*;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.entity.AccountWithdraw;
import com.ncvas.payment.entity.AccountWithdrawDTO;
import com.ncvas.payment.entity.PayOrderWithdraw;
import com.ncvas.payment.entity.PayOrderWithdrawDTO;
import com.ncvas.payment.enums.PayEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountWithdrawService;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.payment.service.PayOrderWithdrawService;
import com.pay.util.JSonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caiqi on 2016/11/23.
 */
@Service("accountWithdrawService")
public class AccountWithdrawServiceImpl extends AbstractBaseService<AccountWithdraw> implements AccountWithdrawService {

    private static final Logger logger =LoggerFactory.getLogger(AccountWithdrawServiceImpl.class);

    @Autowired
    private AnjiePayService anjiePayService;

    @Autowired
    private AccountWithdrawMapper accountWithdrawMapper;

    @Autowired
    private PaySystemMemoService paySystemMemoService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SysDictionaryDataService sysDictionaryDataService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private PayOrderWithdrawService payOrderWithdrawService;
    @Autowired
    private PayOrderWaterService payOrderWaterService;

    @Override
    public Map<String, Object> accountWithdraw(String memberCode, Map<String, String> paraMap, String reqSource) throws Exception {

        Map res = new HashMap();
        PaySystemMemo paySystemMemo = null;
        try {
            paySystemMemo = redisService.getOderInfo(paraMap.get("orderID"));
        } catch (Exception e) {
            res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), 1005);
            res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), "该订单已处理完成！订单号为：" + paraMap.get("orderID"));
            return res;
        }
        if (paySystemMemo == null) {
            res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), 1005);
            res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), "查无此订单信息：" + paraMap.get("orderID"));
            return res;
        }
        if (paySystemMemo == null) {
            res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), "2");
            res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), "查无此订单信息");
            return res;
        }
        if (!PayEnum.PAY_ORDER_GEN.getCode().equals(String.valueOf(paySystemMemo.getStatus()))) {
            logger.info("当前订单记录：" + JSonUtil.toJSonString(paySystemMemo));
            if (PayEnum.PAY_PROCESS.getCode().equals(String.valueOf(paySystemMemo.getStatus()))) {
                res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), 2);
                res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), "当前订单处理中");
                return res;
            }
            if (PayEnum.PAY_SUCCESS.getCode().equals(String.valueOf(paySystemMemo.getStatus()))
                    || PayEnum.PAY_FAILURE.getCode().equals(String.valueOf(paySystemMemo.getStatus()))
                    || PayEnum.PAY_ORDER_CLOSE.getCode().equals(String.valueOf(paySystemMemo.getStatus()))) {
                res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), 2);
                res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), "当前订单已经处理完成");
                return res;
            }
        }
        if (Float.parseFloat(paySystemMemo.getAmount()) * 100 != Float.parseFloat(paraMap.get("amount"))) {
            res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), 2);
            res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), "订单金额与支付金额不一致，订单金额为：" + paySystemMemo.getAmount() + "，支付金额" + Float.parseFloat(paraMap.get("price"))/100);
            return res;
        }
        paySystemMemo.setStatus(new Integer(PayEnum.PAY_PROCESS.getCode()));
        paySystemMemo.setAliascode(paraMap.get("aliasCode"));
        //paySystemMemo.setPaybank(bankCardId);
        paySystemMemo.setReqSource(reqSource);

        //保存银行卡相关信息
        String jsonString = (String) redisTemplate.opsForValue().get(memberCode + RedisEnum.QUICK_PAY_BANK_INFO.getCode());
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (paraMap.get("bankAcct").equals(jsonObject.get("cardNo"))) {
                paySystemMemo.setPaybank((String) jsonObject.get("bankId"));
                paySystemMemo.setBankName((String) jsonObject.get("bankName"));
                paySystemMemo.setBankcardno((String) jsonObject.get("cardNo"));
                break;
            }
        }
        paySystemMemo.setLastUpdateTime(new Date());
        paySystemMemoService.update(paySystemMemo);

        Map<String, Object> resultMap = anjiePayService.accountWithdraw(paraMap);
        if (!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode())) && !resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()).equals(PayEnum.PAY_PWD_ERROR.getCode())) {
            paySystemMemo.setStatus(new Integer(PayEnum.PAY_FAILURE.getCode()));
            paySystemMemo.setRemark((String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
            paySystemMemo.setLastUpdateTime(new Date());
            paySystemMemoService.update(paySystemMemo);
            return resultMap;
        }

        paySystemMemo.setAliascode(paraMap.get("aliasCode"));
        paySystemMemo.setPaybank("SHDF");
        paySystemMemo.setReqSource(reqSource);

        /*// 根据loginName查询会员信息获得memberCode
        Map<String, String> tempParams = new HashMap<String,String>();
        tempParams.put("loginName", paraMap.get("loginName"));
        Map<String,Object> result = anjiePayService.accountMemberinfo(tempParams);*/

        // 查询智汇卡信息获得账户余额
        Map<String, String> tempParams2 = new HashMap<String,String>();
        tempParams2.put("memberCode",memberCode);
        //tempParams2.put("memberCode", String.valueOf(JSONObject.fromObject(result.get("memberInfo")).get("memberCode")));
        tempParams2.put("aliasCode",paraMap.get("aliasCode"));
        Map<String,Object> result = anjiePayService.accountQueryAliasCode(tempParams2);
        String accbalance = String.valueOf(JSONObject.fromObject(result.get("zhCard")).get("accBalance"));
        accbalance = StringUtil.isEmpty(accbalance) ? "0" : accbalance;
        paySystemMemo.setAliascodeBalance(accbalance);
        paySystemMemo.setAliascodeType(paraMap.get("type"));
        if (!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
            if (resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()).equals(PayEnum.PAY_PWD_ERROR.getCode())) {
                paySystemMemo.setStatus(new Integer(PayEnum.PAY_ORDER_GEN.getCode()));
                redisService.insertOderInfo(paySystemMemo);
            } else {
                paySystemMemo.setStatus(new Integer(PayEnum.PAY_FAILURE.getCode()));
            }
            paySystemMemo.setRemark((String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
            paySystemMemo.setLastUpdateTime(new Date());
            paySystemMemoService.update(paySystemMemo);
            res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), "1006");
            res.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
            return res;
        } else {
            paySystemMemo.setStatus(new Integer(PayEnum.PAY_SUCCESS.getCode()));
            paySystemMemo.setRemark((String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
            paySystemMemo.setLastUpdateTime(new Date());
            paySystemMemoService.update(paySystemMemo);
            res.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), PayPlatformEnum.RESPONSE_SUCCESS.getCode());
            return res;
        }

    }

    @Override
    public Map<String, Object> accountWithdraw2(String memberCode, Map<String, String> paraMap) throws Exception {
        Map res = new HashMap();

        PayOrder payOrder = null;
        try {
            payOrder = redisService.getOderInfoNew(paraMap.get("orderID"));
            res.put("orderId", payOrder.getOrderId());
        }catch (Exception e) {
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), 1005);
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), "该订单已处理完成！订单号为：" + paraMap.get("orderID"));
            return res;
        }

        //PaySystemMemo paySystemMemo = paySystemMemoService.selectLastPaySystemMemo(reqDto);
        if (payOrder == null) {
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), 1005);
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), "查无此订单信息：" + paraMap.get("orderID"));
            return res;
        }
        if (!sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_GEN").equals(payOrder.getStatus())) {
            logger.info("当前订单记录：" + JSonUtil.toJSonString(payOrder));
            if (sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_PROCESS").equals(payOrder.getStatus())) {
                res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), 2);
                res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), "当前订单处理中");
                return res;
            }
            if (sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_SUCCESS").equals(String.valueOf(payOrder.getStatus()))
                    || sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE").equals(String.valueOf(payOrder.getStatus()))
                    || sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_CLOSE").equals(String.valueOf(payOrder.getStatus()))) {
                res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), 2);
                res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), "当前订单已经处理完成");
                return res;
            }
        }
        if (Float.parseFloat(payOrder.getAmount()) * 100 != Float.parseFloat(paraMap.get("amount"))) {
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), 2);
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), "订单金额与支付金额不一致，订单金额为：" + payOrder.getAmount() + "，支付金额" + Float.parseFloat(paraMap.get("amount"))/100);
            return res;
        }
        payOrder.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_PROCESS"));
        payOrder.setPayType(sysDictionaryDataService.getSysDictDataValue("PayTypeEnum","WITHDRAW"));
        payOrderService.doUpdate(payOrder);

        PayOrderWithdrawDTO payOrderWithdrawDTO = new PayOrderWithdrawDTO();
        payOrderWithdrawDTO.setOrderId(payOrder.getOrderId());
        List<PayOrderWithdraw> payOrderWithdrawList = payOrderWithdrawService.doQuery(payOrderWithdrawDTO,Integer.MIN_VALUE,Integer.MAX_VALUE);
        PayOrderWithdraw  payOrderWithdraw = new PayOrderWithdraw();
        payOrderWithdraw.setOrderId(payOrder.getOrderId());
        payOrderWithdraw.setAmount(payOrder.getAmount());
        payOrderWithdraw.setReqBizProj(payOrder.getReqBizProj());
        payOrderWithdraw.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_GEN"));
        if (payOrderWithdrawList.size() == 0) {
            payOrderWithdraw = payOrderWithdrawService.doCreate(payOrderWithdraw);
        } else {
            payOrderWithdraw.setId(payOrderWithdrawList.get(0).getId());
            payOrderWithdrawService.doUpdate(payOrderWithdraw);
        }
        if (paraMap.get("type") != null) {
            if (paraMap.get("type").equals("1")) {
                payOrderWithdraw.setReqSource(sysDictionaryDataService.getSysDictDataValue("ReqSourceEnum","WITHDRAW_NJCC"));
            }else {
                payOrderWithdraw.setReqSource(sysDictionaryDataService.getSysDictDataValue("ReqSourceEnum","WITHDRAW_HISS"));
            }
        }
        payOrderWithdraw.setAliascode(paraMap.get("aliasCode"));

        //保存银行卡相关信息
        String jsonString = (String) redisTemplate.opsForValue().get(memberCode + RedisEnum.QUICK_PAY_BANK_INFO.getCode());
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        for (int i = 0; i < jsonArray.size(); i++) {
            net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (paraMap.get("bankAcct").equals(jsonObject.get("cardNo"))) {
                payOrderWithdraw.setPayBank((String) jsonObject.get("bankId"));
                payOrderWithdraw.setBankCardNo((String) jsonObject.get("cardNo"));
                break;
            }
        }
        payOrderWithdraw.setMemberCode(memberCode);
        payOrderWithdraw.setAliascodeType(paraMap.get("type"));
        payOrderWithdrawService.doUpdate(payOrderWithdraw);

        Map<String, Object> resultMap = anjiePayService.accountWithdraw(paraMap);
        if (!sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_SUCCESS").equals(resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"))) &&
                !sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_PWD_ERROR").equals(resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE")))) {
            //更新订单表状态
            payOrder.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE"));
            payOrderService.doUpdate(payOrder);
            //更新提现表状态
            payOrderWithdraw.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE"));
            payOrderWithdraw.setRemark((String) resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC")));
            payOrderWithdrawService.doUpdate(payOrderWithdraw);
            return resultMap;
        }

        // 查询智汇卡信息获得智汇账户余额

        /*Map<String, String> tempParams2 = new HashMap<String,String>();
        tempParams2.put("aliasCode",paraMap.get("aliasCode"));
        Map<String,Object> result = anjiePayService2.accountQueryAliasCode(tempParams2);
        String accbalance = String.valueOf(JSONObject.fromObject(result.get("zhCard")).get("accBalance"));
        accbalance = StringUtil.isEmpty(accbalance) ? "0" : accbalance;
        payOrderWithdraw.setAliascodeBalanceNj(accbalance);*/

        //验证医疗一账通账户获取医疗账户余额
        /*result = anjiePayService2.hisVerify(tempParams2);
        if (result != null && sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_SUCCESS").equals(result.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE")))) {
            if (result.get("rescontent") != null) {
                JSONObject json = JSONObject.fromObject(result.get("rescontent"));
                String hisbalance = String.valueOf(json.get("ACCBALANCE"));
                hisbalance = StringUtil.isEmpty(hisbalance) ? "0" : hisbalance;
                payOrderWithdraw.setAliascodeBalanceHis(hisbalance);
            }
        }*/
        if (!sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_SUCCESS").equals(resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE")))) {
            if (sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_PWD_ERROR").equals(resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE")))) {
                //支付密码错误，可以重新支付，将状态设置为订单生成
                payOrder.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_GEN"));
                payOrderWithdraw.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_GEN"));
                redisService.insertOderInfo(payOrder);
            } else {
                //其它错误，订单状态改为失败
                payOrder.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE"));
                payOrderWithdraw.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE"));
            }
            payOrderWithdraw.setRemark((String) resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC")));
            payOrderService.doUpdate(payOrder);
            payOrderWithdrawService.doUpdate(payOrderWithdraw);
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE")));
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC")));
        } else {
            payOrder.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_SUCCESS"));
            payOrderWithdraw.setStatus(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_SUCCESS"));
            payOrderWithdraw.setRemark((String) resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC")));
            payOrderService.doUpdate(payOrder);
            payOrderWithdrawService.doUpdate(payOrderWithdraw);
            res.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), resultMap.get(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE")));
        }
        payOrderWaterService.sendMsg(payOrder.getOrderId(),payOrder.getStatus(),payOrder.getAmount(),payOrderWithdraw.getRemark(),JSonUtil.toJSonString(paraMap),JSonUtil.toJSonString(resultMap),null);
        //res.put("orderId", payOrder.getOrderId());
        return res;
    }

    @Override
    protected BaseMapper<AccountWithdraw> getBaseMapper() {
        return accountWithdrawMapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountWithdrawDTO.class;
    }
}
