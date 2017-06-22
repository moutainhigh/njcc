package com.ncvas.common.service.bizImpl;

import com.alibaba.fastjson.JSONObject;
import com.ncvas.common.utils.DateUtil;
import com.ncvas.payment.entity.AccountQuickPayOrder;
import com.ncvas.payment.entity.AccountWriteCard;
import com.ncvas.payment.enums.ChannelTypeEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.enums.StatusEnum;
import com.ncvas.payment.enums.WriteTypeEnum;
import com.ncvas.payment.service.AccountQueryAliasCodeService;
import com.ncvas.payment.service.AccountQuickPayOrderService;
import com.ncvas.payment.service.AccountWriteCardService;
import com.ncvas.payment.service.biz.AccountWriteCardBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiecaixia on 2017-4-25.
 */
@Service("accountWriteCardBizService")
public class AccountWriteCardBizServiceImpl implements AccountWriteCardBizService {

    private static final Logger logger = LoggerFactory.getLogger(AccountWriteCardBizServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AccountWriteCardService accountWriteCardService;
    @Autowired
    private AccountQuickPayOrderService accountQuickPayOrderService;
    @Autowired
    private AccountQueryAliasCodeService accountQueryAliasCodeService;

    @Override
    public Map<String, String> saveWriteCardAndUpdateStatus(String orderId, String token, String cardNo, String status) throws Exception {
        /**修改预约订单状态的业务和记录写卡渠道的业务整合在一起， 不用app调两个接口, 状态成功就成功， 写卡失败，预约订单可重复写卡， 不作处理*/
        if (StatusEnum.SUCCESS.getCode().equals(status)) {
            this.updateQuickPayOrderStatus(orderId, WriteTypeEnum.QUICK_PAY_ORDER_WRITE_SUCCESS.getCode(), WriteTypeEnum.QUICK_PAY_ORDER_WRITE_SUCCESS.getDescription());
        }
        /**如果在没有登录的情况下写卡（匿名写卡）， 根据订单号去查询写卡渠道表，写卡成功失败都入库*/
        /**先出token中取出loginName和身份证， 用卡号去查卡信息， 比较身份证是否一致， 不一致则认为帮别人写卡*/
        AccountWriteCard accountWriteCard = accountWriteCardService.getByOrderId(orderId);
        String orderStatus = accountWriteCard.getStatus();
        if (accountWriteCard != null) {
            /**判断原来的数据是否处于待写卡或者写卡失败的状态了*/
            if (WriteTypeEnum.WAITING_FOR_WRITE.getCode().equals(orderStatus) || WriteTypeEnum.WRITE_CARD_FAILED.getCode().equals(orderStatus)) {
                AccountWriteCard writeCard = new AccountWriteCard();
                writeCard.setId(accountWriteCard.getId());
                if (StringUtils.isEmpty(token)) {
                    writeCard.setWriteType(WriteTypeEnum.ANONYMOUS_WRITE.getCode());
                    writeCard.setRemark(WriteTypeEnum.ANONYMOUS_WRITE.getDescription());
                } else {
                    String Temp = (String) redisTemplate.opsForValue().get(token + "memberService");
                    JSONObject json = JSONObject.parseObject(Temp);
                    String loginName = (String) json.get("mobile");
                    String socialcode = (String) json.get("socialcode");
                    writeCard.setLoginName(loginName);

                    Map<String, Object> resultMap = accountQueryAliasCodeService.accountQueryAliasCode(cardNo);
                    if (resultMap != null) {
                        if (PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
                            if (resultMap.get("socialCode").equals(socialcode)) {
                                writeCard.setWriteType(WriteTypeEnum.WRITE_FOR_MYSELF.getCode());
                            } else {
                                writeCard.setWriteType(WriteTypeEnum.WRITE_FOR_YOU.getCode());
                            }
                        } else {
                            //writeCard.setRemark((String)resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
                            logger.error("写卡渠道调用支付平台获取卡信息失败， 卡号为：" + cardNo + (String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
                        }
                    } else {
                        //writeCard.setRemark("获取卡信息失败");
                        logger.error("写卡渠道调用支付平台返回空，获取卡信息失败，卡号为：" + cardNo);
                    }

                }
                /**判断app返回写卡状态（app传 0为失败， 1为成功）， 成功设置状态 0写卡成功， 失败设置 6写卡失败*/
                if (StatusEnum.SUCCESS.getCode().equals(status)) {
                    writeCard.setStatus(WriteTypeEnum.WRITE_CARD_SUCCESS.getCode());
                    writeCard.setRemark(WriteTypeEnum.WRITE_CARD_SUCCESS.getDescription());
                } else {
                    writeCard.setStatus(WriteTypeEnum.WRITE_CARD_FAILED.getCode());
                    writeCard.setRemark(WriteTypeEnum.WRITE_CARD_FAILED.getDescription());
                }
                writeCard.setChannelType(ChannelTypeEnum.APP.getCode());
                writeCard.setWriteTime(DateUtil.praseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                accountWriteCardService.doUpdate(writeCard);
            }else {
                logger.info("该订单不是处于待写卡或者写卡失败的状态，订单号为："+orderId);
            }
        } else {
            logger.error("该预约订单号不存在， 订单号为：" + orderId);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), PayPlatformEnum.RESPONSE_SUCCESS.getCode());
        return resultMap;
    }

    private void updateQuickPayOrderStatus(String orderId, String status, String remark) {
        AccountQuickPayOrder quickPayOrder = accountQuickPayOrderService.getByOrderId(orderId);
        if (quickPayOrder != null) {
            accountQuickPayOrderService.updateStatus(orderId, status, remark);
        } else {
            logger.error("获取预约订单信息失败，该订单不存在, 订单号为： " + orderId);
        }
    }
}
