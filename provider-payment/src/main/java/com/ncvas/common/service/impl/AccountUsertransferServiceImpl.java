package com.ncvas.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.ncvas.base.entity.PaySystemMemo;
import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AccountUsertransferMapper;
import com.ncvas.common.entity.OpenapiLog;
import com.ncvas.common.entity.PayOrder;
import com.ncvas.common.enums.SnEnum;
import com.ncvas.common.service.*;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.entity.AccountUsertransfer;
import com.ncvas.payment.entity.AccountUsertransferDTO;
import com.ncvas.payment.entity.PayOrderTransferIn;
import com.ncvas.payment.entity.PayOrderTransferOut;
import com.ncvas.payment.enums.PayEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.*;
import com.ncvas.util.AmountUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年5月16日
 */
@Service("accountUsertransferService")
public class AccountUsertransferServiceImpl extends AbstractBaseService<AccountUsertransfer> implements AccountUsertransferService {

    @Autowired
    private AccountUsertransferMapper mapper;
    @Autowired
    private OpenapiLogService openapiLogService;
    @Autowired
    private AnjiePayService anjiePayService;
    @Autowired
    private PaySystemMemoService paySystemMemoService;
    @Autowired
    private AccountQueryMainAliasCodeService accountQueryMainAliasCodeService;
    @Autowired
    private PayInfoCreateService payInfoCreateService;
    @Autowired
    private SnService snService;
    @Autowired
    private SysDictionaryDataService sysDictionaryDataService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PayOrderTransferInService payOrderTransferInService;
    @Autowired
    private PayOrderTransferOutService payOrderTransferOutService;
    @Autowired
    private PayOrderWaterService payOrderWaterService;

    private String RESPONSE_SUCCESS ;
    private String RESPONSE_KEY_CODE ;
    private String PAY_SUCCESS ;
    private String PAY_FAILURE ;
    private String PAY_TYPE_ACCOUNT_TO_ACCOUNT ;
    private String RESPONSE_USERTRANSFER_ERROR_PWD_NOT_MATCH ;
    private String PAY_ORDER_CLOSE ;
    private String PAY_ORDER_GEN ;
    private String REQ_BIZ_PROJ_NJCC ;
    private String TRANSFER_NJCC ;
    private String TRANSFER_HISS ;
    private String ALIAS_CODE_NJCC ;
    private String ALIAS_CODE_HISS ;
    private String NJCC_TO_NJCC ;
    private String NJCC_TO_HISS ;
    private String HISS_TO_HISS ;
    private String HISS_TO_NJCC ;
    private String RESPONSE_KEY_DESC ;
    private String RESPONSE_ERROR_PWD_NOT_MATCH ;

    private static final Logger logger = LoggerFactory.getLogger(AccountUsertransferServiceImpl.class);

    @Override
    protected BaseMapper<AccountUsertransfer> getBaseMapper() {
        return mapper;
    }

    @Override
    protected Class<? extends ValueObject> getEntityDTOClass() {
        return AccountUsertransferDTO.class;
    }

    @Override
    public Map<String, Object> accountUsertransfer(String loginName, String type, String amount, String payeeAliasCode, String msg, String password,
                                                   String memberId, String loginId, String memberCode, String aliascode, String reqBizType,
                                                   String reqSource,String bizType) throws Exception {
        //生成订单号
        PaySystemMemo psm = payInfoCreateService.createPayNo(reqSource, reqBizType,
                PayEnum.PAY_TYPE_NJCC_ACCOUNT_USERTRANSFER.getCode(), loginId, amount, amount, aliascode, "生成智汇转值订单");

        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("orderID", psm.getPaySysNo());

        OpenapiLog openapiLog = new OpenapiLog();
        String amountFormat = AmountUtils.changeY2F(amount);//转换为分传给支付平台
        paraMap.put("loginName", loginName);
        paraMap.put("type", type);
        paraMap.put("amount", amountFormat);
        paraMap.put("payeeAliasCode", payeeAliasCode);
        paraMap.put("msg", msg);
        paraMap.put("password", password);

        openapiLog.setUserid(memberId);
        openapiLog.setAmount(amount);
        openapiLog.setInfodesc(JSON.toJSONString(paraMap));
        openapiLog.setLogtype("1");
        openapiLogService.doCreate(openapiLog);
        logger.info("hiss||智汇卡转值插入请求信息日志成功!");

        Map<String, Object> resultMap = anjiePayService.accountUsertransfer(paraMap);
        Map<String,Object> result = new HashMap<>();
        if (resultMap != null) {
            //由于成功失败都需要所以这里组装数据由外部初始化
            AccountUsertransferDTO acdto = new AccountUsertransferDTO();
            acdto.setOrderId(psm.getPaySysNo());
            acdto.setOrderamt(amount);
            acdto.setTranstype(type);
            acdto.setLoginName(loginId);
            acdto.setMsg(msg);
            acdto.setReqBizType(bizType == null ? "1":bizType);

            psm.setLastUpdateTime(new Date());//订单返回时间
            if (PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
                Map<String,Object> json = (Map<String,Object>)JSON.toJSON(resultMap.get("responseDetail"));
                acdto.setOrdertime((String) json.get("ORDERTIME"));
                // 返回的订单号
                acdto.setOrderno((String) json.get("ORDERID"));
                acdto.setOutacccode((String) json.get("OUTACCCODE"));
                acdto.setInacccode((String) json.get("INACCCODE"));
                acdto.setOutaccbalance((String) json.get("OUTACCBALANCE"));
                acdto.setInaccbalance((String) json.get("INACCBALANCE"));
                acdto.setTranstype((String) json.get("TRANSTYPE"));
                acdto.setStatus(Integer.parseInt(PayEnum.PAY_SUCCESS.getCode()));

                //判断如果有，就去除掉
                String tempOutaccbalance = "0";
                if(!StringUtil.isEmpty(acdto.getOutaccbalance())){
                    tempOutaccbalance = acdto.getOutaccbalance().indexOf(",")>0?acdto.getOutaccbalance().replace(",",""):acdto.getOutaccbalance();
                }

                String accbalance = AmountUtils.formatNumber(tempOutaccbalance,2);
                psm.setReceivedLoginName(paraMap.get("payeeAliasCode"));
                psm.setReceivedAliascode((String) json.get("INACCCODE"));
                psm.setStatus(Integer.parseInt(PayEnum.PAY_SUCCESS.getCode()));
                psm.setRemark("智汇转值成功");
                psm.setAliascodeBalance(accbalance);
                if (type.equals(PayEnum.USER_TRANSFER_HIS_TO_ALIASCODE.getCode())||type.equals(PayEnum.USER_TRANSFER_HIS_TO_HIS.getCode())) {
                    // 如果是医疗账户转值则查询智汇账户余额入库
                    Map<String, String> tempParams2 = new HashMap<String,String>();
                    tempParams2.put("memberCode",memberCode);
                    //tempParams2.put("memberCode", String.valueOf(JSONObject.fromObject(result.get("memberInfo")).get("memberCode")));
                    //tempParams2.put("aliasCode",(String) json.get("OUTACCCODE"));
                    Map<String,Object> balance = anjiePayService.accountQueryMainAliasCode(tempParams2);
                    accbalance = String.valueOf(JSONObject.fromObject(balance.get("zhCard")).get("accBalance"));
                    accbalance = StringUtil.isEmpty(accbalance) ? "0" : accbalance;
                    psm.setAliascodeBalance(accbalance);
                }

                result.put("orderid", acdto.getOrderId());
                result.put("ordertime", acdto.getOrdertime());
                result.put("orderno",acdto.getOrderno());
                result.put("orderamt",acdto.getOrderamt());
                result.put("outacccode",acdto.getOutacccode());
                result.put("inacccode",acdto.getInacccode());
                result.put("transtype",acdto.getTranstype());
                result.put("outaccbalance",accbalance);

                String tempInaccbalance = "0";
                if(!StringUtil.isEmpty(acdto.getOutaccbalance())){
                    tempInaccbalance = acdto.getOutaccbalance().indexOf(",")>0?acdto.getInaccbalance().replace(",",""):acdto.getInaccbalance();
                }
                result.put("inaccbalance",AmountUtils.formatNumber(tempInaccbalance,2));
                result.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
            }else{
                acdto.setStatus(Integer.parseInt(PayEnum.PAY_FAILURE.getCode()));
                acdto.setRemark((String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
                String accbalance = accountQueryMainAliasCodeService.getCardAccBalance(memberCode);
                if(!StringUtil.isEmpty(accbalance)&&!accbalance.equals(PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode())) acdto.setOutaccbalance(accbalance);
                psm.setStatus(Integer.parseInt(PayEnum.PAY_FAILURE.getCode()));
                psm.setAliascodeBalance(accbalance);
                psm.setRemark((String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
                if(PayPlatformEnum.RESPONSE_USERTRANSFER_ERROR_PWD_NOT_MATCH.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                    result.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR_PWD_NOT_MATCH.getCode());
                }else {
                    result.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),(String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()));
                }
                result.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),(String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
            }
            mapper.add(acdto);
        }
        paySystemMemoService.doUpdate(psm);

        OpenapiLog oalog = new OpenapiLog();
        oalog.setUserid(memberId);
        oalog.setAmount(amount);
        oalog.setInfodesc(JSON.toJSONString(resultMap));
        oalog.setLogtype("2");
        openapiLogService.doCreate(oalog);
        logger.info("hiss||智汇卡转值插入返回信息日志成功!");

        return result;
    }

    @Override
    public Map<String, Object> accountUsertransfer2(String loginName, String type, String amount, String payeeAliasCode, String msg, String password,
                                                    String memberId, String loginId, String memberCode, String aliascode, String reqBizType,
                                                    String reqSource,String bizType) throws Exception {
        long starTime=System.currentTimeMillis();

        //一次性获取各种所需redis枚举
        RESPONSE_SUCCESS = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_SUCCESS");
        RESPONSE_KEY_CODE = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE");
        PAY_SUCCESS = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_SUCCESS");
        PAY_FAILURE = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE");
        PAY_TYPE_ACCOUNT_TO_ACCOUNT = sysDictionaryDataService.getSysDictDataValue("PayTypeEnum","PAY_TYPE_ACCOUNT_TO_ACCOUNT");
        RESPONSE_USERTRANSFER_ERROR_PWD_NOT_MATCH = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_USERTRANSFER_ERROR_PWD_NOT_MATCH");
        PAY_ORDER_CLOSE = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_CLOSE");
        PAY_ORDER_GEN = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_GEN");
        REQ_BIZ_PROJ_NJCC = sysDictionaryDataService.getSysDictDataValue("ReqBizProjEnum","REQ_BIZ_PROJ_NJCC");
        TRANSFER_NJCC = sysDictionaryDataService.getSysDictDataValue("ReqSourceEnum","TRANSFER_NJCC");
        TRANSFER_HISS = sysDictionaryDataService.getSysDictDataValue("ReqSourceEnum","TRANSFER_HISS");
        ALIAS_CODE_NJCC = sysDictionaryDataService.getSysDictDataValue("AliasCodeTypeEnum","ALIAS_CODE_NJCC");
        ALIAS_CODE_HISS = sysDictionaryDataService.getSysDictDataValue("AliasCodeTypeEnum","ALIAS_CODE_HISS");
        NJCC_TO_NJCC = sysDictionaryDataService.getSysDictDataValue("TransTypeEnum","NJCC_TO_NJCC");
        NJCC_TO_HISS = sysDictionaryDataService.getSysDictDataValue("TransTypeEnum","NJCC_TO_HISS");
        HISS_TO_HISS = sysDictionaryDataService.getSysDictDataValue("TransTypeEnum","HISS_TO_HISS");
        HISS_TO_NJCC = sysDictionaryDataService.getSysDictDataValue("TransTypeEnum","HISS_TO_NJCC");
        RESPONSE_KEY_DESC = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC");
        RESPONSE_ERROR_PWD_NOT_MATCH = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_ERROR_PWD_NOT_MATCH");

        //生成订单号
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("loginName",loginName);
        paraMap.put("type",type);
        paraMap.put("amount",amount);
        paraMap.put("payeeAliasCode",payeeAliasCode);
        paraMap.put("msg",msg);
        paraMap.put("password",password);
        paraMap.put("reqBizType",reqBizType);
        paraMap.put("memberId",memberId);
        paraMap.put("loginId",loginId);
        paraMap.put("memberCode",memberCode);
        paraMap.put("aliascode",aliascode);
        PayOrder payOrder = createPayNo2(paraMap);
        paraMap.put("orderID", payOrder.getOrderId());

        String mqStatus = "";
        String mqRemark = "";

        String transAmount = amount;
        //String memberCode = paraMap.get("memberCode");
        //String aliascode = paraMap.get("aliascode");
        amount = AmountUtils.changeY2F(amount);//转换为分传给支付平台

        //整理传参数据
        paraMap.put("amount", amount);
        paraMap.remove("memberId");
        paraMap.remove("loginId");
        paraMap.remove("memberCode");
        paraMap.remove("aliascode");

        //转账处理
        Map<String, Object> resultMap = anjiePayService.accountUsertransfer(paraMap);
        Map<String, Object> result = new HashMap<>();
        if (resultMap != null) {
            //转账接收
            PayOrderTransferIn payOrderTransferIn = new PayOrderTransferIn();
            payOrderTransferIn.setOrderId(payOrder.getOrderId());
            payOrderTransferIn.setAmount(transAmount);
            payOrderTransferIn.setReqBizProj(reqBizType);
            payOrderTransferIn.setTransType(paraMap.get("type"));
            payOrderTransferIn.setTransMsg(paraMap.get("msg"));
            payOrderTransferIn.setReqSource(reqSource);
            //接收的memberCode需要从接口中查询
            if (paraMap.get("payeeAliasCode").length()==11){
                //手机号码则说明是账号
                Map<String,String> memberCodeAndAliasCodeMap = getMemberCodeAndAliasCodeByLoginName(paraMap.get("payeeAliasCode"));
                payOrderTransferIn.setMemberCodeIn(memberCodeAndAliasCodeMap.get("memberCode"));
                payOrderTransferIn.setAliasCodeIn(memberCodeAndAliasCodeMap.get("aliasCode"));
            }else {
                //否则说明是卡面号
                payOrderTransferIn.setMemberCodeIn(getMemberCodeByAliasCode(paraMap.get("payeeAliasCode")));
                payOrderTransferIn.setAliasCodeIn(paraMap.get("payeeAliasCode"));
            }
            //转账发起
            PayOrderTransferOut payOrderTransferOut = new PayOrderTransferOut();
            payOrderTransferOut.setOrderId(payOrder.getOrderId());
            payOrderTransferOut.setAmount(transAmount);
            payOrderTransferOut.setMemberCodeOut(memberCode);
            payOrderTransferOut.setReqBizProj(reqBizType);
            payOrderTransferOut.setTransType(paraMap.get("type"));
            payOrderTransferOut.setTransMsg(paraMap.get("msg"));
            payOrderTransferOut.setAliasCodeOut(aliascode);

            //给AliasCodeType和ReqSource赋值
            setAliasCodeTypeAndReqSource(paraMap.get("type"),payOrderTransferOut,payOrderTransferIn);

            if (RESPONSE_SUCCESS.equals(resultMap.get(RESPONSE_KEY_CODE))) {
                Map<String, Object> json = (Map<String, Object>) JSON.toJSON(resultMap.get("responseDetail"));
                String inAccbalanceOld = (String) json.get("INACCBALANCE");
                String outAccbalanceOld = (String) json.get("OUTACCBALANCE");

                payOrderTransferIn.setTransType((String) json.get("TRANSTYPE"));
                payOrderTransferIn.setAliasCodeIn((String) json.get("INACCCODE"));
                payOrderTransferIn.setStatus(PAY_SUCCESS);

                payOrderTransferOut.setTransType((String) json.get("TRANSTYPE"));
                payOrderTransferOut.setAliasCodeOut((String) json.get("OUTACCCODE"));
                payOrderTransferOut.setStatus(PAY_SUCCESS);

                mqStatus = PAY_SUCCESS;

                //判断如果有，就去除掉
                String tempOutaccbalance = "0";
                String tempInaccbalance = "0";
                if (!StringUtil.isEmpty(outAccbalanceOld)) {
                    tempOutaccbalance = outAccbalanceOld.indexOf(",") > 0 ? outAccbalanceOld.replace(",", "") : outAccbalanceOld;
                }
                if (!StringUtil.isEmpty(inAccbalanceOld)) {
                    tempInaccbalance = inAccbalanceOld.indexOf(",") > 0 ? inAccbalanceOld.replace(",", "") : inAccbalanceOld;
                }
                String outAccbalance = AmountUtils.formatNumber(tempOutaccbalance, 2);
                String inAccbalance = AmountUtils.formatNumber(tempInaccbalance, 2);

                payOrderTransferIn.setRemark("转值成功");
                payOrderTransferOut.setRemark("转值成功");
                mqRemark = "转值成功";

                //余额查询改为通过线程进行查询
                /*if (paraMap.get("type").equals(NJCC_TO_NJCC) ){
                    //智汇账户（发起）转智汇账户（接收）情况
                    payOrderTransferOut.setAliasCodeBalanceNjOut(outAccbalance);
                    payOrderTransferIn.setAliasCodeBalanceNjIn(inAccbalance);
                    //暂时不查询医疗余额
                    //payOrderTransferOut.setAliasCodeBalanceHisOut(getAliasCodeBalanceHis(payOrderTransferOut.getAliasCodeOut()));//查询发起账户对应的医疗账户余额
                    //payOrderTransferIn.setAliasCodeBalanceHisIn(getAliasCodeBalanceHis(payOrderTransferIn.getAliasCodeIn()));//查询接收账户对应的医疗账户余额
                }else if ( paraMap.get("type").equals(NJCC_TO_HISS)){
                    //智汇账户（发起）转医疗账户（接收）情况
                    payOrderTransferOut.setAliasCodeBalanceNjOut(outAccbalance);
                    //暂时不查询医疗余额
                    //payOrderTransferIn.setAliasCodeBalanceHisIn(inAccbalance);
                    //payOrderTransferOut.setAliasCodeBalanceHisOut(getAliasCodeBalanceHis(payOrderTransferOut.getAliasCodeOut()));//查询发起账户对应的医疗账户余额
                    payOrderTransferIn.setAliasCodeBalanceNjIn(getAliasCodeBalanceNjcc(payOrderTransferIn.getMemberCodeIn()));//查询接收账户对应的智汇账户余额
                }else if(paraMap.get("type").equals(HISS_TO_NJCC)){
                    //医疗账户（发起）转智汇账户（接收）情况
                    payOrderTransferOut.setAliasCodeBalanceHisOut(outAccbalance);
                    payOrderTransferIn.setAliasCodeBalanceNjIn(inAccbalance);
                    payOrderTransferOut.setAliasCodeBalanceNjOut(getAliasCodeBalanceNjcc(payOrderTransferOut.getMemberCodeOut()));//查询发起账户对应的智汇账户余额
                    //暂时不查询医疗余额
                    //payOrderTransferIn.setAliasCodeBalanceHisIn(getAliasCodeBalanceHis(payOrderTransferIn.getAliasCodeIn()));//查询接收账户对应的医疗账户余额
                }else if (paraMap.get("type").equals(HISS_TO_HISS)){
                    //医疗账户（发起）转医疗账户（接收）情况
                    payOrderTransferOut.setAliasCodeBalanceHisOut(outAccbalance);
                    payOrderTransferIn.setAliasCodeBalanceHisIn(inAccbalance);
                    payOrderTransferOut.setAliasCodeBalanceNjOut(getAliasCodeBalanceNjcc(payOrderTransferOut.getMemberCodeOut()));//查询发起账户对应的智汇账户余额
                    payOrderTransferIn.setAliasCodeBalanceNjIn(getAliasCodeBalanceNjcc(payOrderTransferIn.getMemberCodeIn()));//查询接收账户对应的智汇账户余额
                }*/

                result.put("orderid", payOrder.getOrderId());
                result.put("ordertime", (String) json.get("ORDERTIME"));
                result.put("orderno", (String) json.get("ORDERID"));
                result.put("orderamt", transAmount);
                result.put("outacccode", (String) json.get("OUTACCCODE"));
                result.put("inacccode", (String) json.get("INACCCODE"));
                result.put("transtype", (String) json.get("TRANSTYPE"));
                result.put("outaccbalance", outAccbalance);

                result.put("inaccbalance", AmountUtils.formatNumber(tempInaccbalance, 2));
                result.put(RESPONSE_KEY_CODE, RESPONSE_SUCCESS);

                //设置该订单状态为成功
                payOrder.setStatus(PAY_SUCCESS);
                payOrder.setPayType(PAY_TYPE_ACCOUNT_TO_ACCOUNT);
            } else {
                payOrderTransferOut.setStatus(PAY_FAILURE);
                payOrderTransferOut.setRemark((String) resultMap.get(RESPONSE_KEY_DESC));
                //payOrderTransferOut.setAliasCodeBalanceNjOut(getAliasCodeBalanceNjcc(payOrderTransferOut.getAliasCodeOut()));
                //payOrderTransferOut.setAliasCodeBalanceHisOut(getAliasCodeBalanceHis(payOrderTransferOut.getAliasCodeOut()));

                payOrderTransferIn.setStatus(PAY_FAILURE);
                payOrderTransferIn.setRemark((String) resultMap.get(RESPONSE_KEY_DESC));
                //payOrderTransferIn.setAliasCodeBalanceNjIn(getAliasCodeBalanceNjcc(payOrderTransferIn.getAliasCodeIn()));
                //payOrderTransferIn.setAliasCodeBalanceHisIn(getAliasCodeBalanceHis(payOrderTransferIn.getAliasCodeIn()));

                mqStatus = PAY_FAILURE;
                mqRemark = (String) resultMap.get(RESPONSE_KEY_DESC);

                if (RESPONSE_USERTRANSFER_ERROR_PWD_NOT_MATCH.equals(resultMap.get(RESPONSE_KEY_CODE))) {
                    result.put(RESPONSE_KEY_CODE, RESPONSE_ERROR_PWD_NOT_MATCH);
                } else {
                    result.put(RESPONSE_KEY_CODE, (String) resultMap.get(RESPONSE_KEY_CODE));
                }
                result.put(RESPONSE_KEY_DESC, (String) resultMap.get(RESPONSE_KEY_DESC));

                //设置该订单状态为失败
                payOrder.setStatus(PAY_FAILURE);
                payOrder.setPayType(PAY_TYPE_ACCOUNT_TO_ACCOUNT);
            }
            payOrderTransferInService.doCreate(payOrderTransferIn);
            payOrderTransferOutService.doCreate(payOrderTransferOut);
            payOrderWaterService.sendMsg(payOrder.getOrderId(),mqStatus,transAmount,mqRemark,JSON.toJSONString(paraMap),JSON.toJSONString(resultMap),null);
        }else {
            //支付没结果返回的情况，则设置该订单状态为关闭
            payOrder.setStatus(PAY_ORDER_CLOSE);
        }
        payOrderService.doUpdate(payOrder);
        long endTime=System.currentTimeMillis();
        logger.info("转账service方法accountUsertransfer2总时间开销:"+(endTime-starTime));
        return result;
    }

    private PayOrder createPayNo2(Map<String, String> paraMap) throws Exception {
        String paySysCode = snService.getSnCode(SnEnum.SEQ_NJCC_PAY_SYSTEM_CODE);
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderId(paySysCode);
        payOrder.setAmount(paraMap.get("amount"));
        payOrder.setStatus(PAY_ORDER_GEN);
        payOrder.setReqBizProj(StringUtil.isEmpty(paraMap.get("reqBizType"))?REQ_BIZ_PROJ_NJCC:paraMap.get("reqBizType"));
        //此处生成订单号时先不给类型赋值，方便在定时器处理类型为空的记录（代表没意义的记录），类型在业务处理成功时才赋值
        //payOrder.setPayType(PAY_TYPE_ACCOUNT_TO_ACCOUNT);
        payOrder = payOrderService.doCreate(payOrder);
        //订单插入redis
        redisService.insertOderInfo(payOrder);
        return payOrder;
    }

    //根据转值类型给AliasCodeType和ReqSource赋值
    private void setAliasCodeTypeAndReqSource(String type,PayOrderTransferOut payOrderTransferOut,PayOrderTransferIn payOrderTransferIn){
        if (null == type){
            return;
        }
        if (type.equals(NJCC_TO_NJCC) ){
            //智汇账户（发起）转智汇账户（接收）
            payOrderTransferOut.setReqSource(TRANSFER_NJCC);
            payOrderTransferIn.setReqSource(TRANSFER_NJCC);
            payOrderTransferOut.setAliasCodeType(ALIAS_CODE_NJCC);
            payOrderTransferIn.setAliasCodeType(ALIAS_CODE_NJCC);
        }else if (type.equals(NJCC_TO_HISS)){
            //智汇账户（发起）转医疗账户（接收）
            payOrderTransferOut.setReqSource(TRANSFER_NJCC);
            payOrderTransferIn.setReqSource(TRANSFER_NJCC);
            payOrderTransferOut.setAliasCodeType(ALIAS_CODE_NJCC);
            payOrderTransferIn.setAliasCodeType(ALIAS_CODE_HISS);
        }else if(type.equals(HISS_TO_NJCC)){
            //医疗账户（发起）转智汇账户（接收）
            payOrderTransferOut.setReqSource(TRANSFER_HISS);
            payOrderTransferIn.setReqSource(TRANSFER_HISS);
            payOrderTransferOut.setAliasCodeType(ALIAS_CODE_HISS);
            payOrderTransferIn.setAliasCodeType(ALIAS_CODE_NJCC);
        }else if (type.equals(HISS_TO_HISS)){
            //医疗账户（发起）转医疗账户（接收）
            payOrderTransferOut.setReqSource(TRANSFER_HISS);
            payOrderTransferIn.setReqSource(TRANSFER_HISS);
            payOrderTransferOut.setAliasCodeType(ALIAS_CODE_HISS);
            payOrderTransferIn.setAliasCodeType(ALIAS_CODE_HISS);
        }
    }

    //通过aliasCode查找memberCode
    private String getMemberCodeByAliasCode(String aliasCode) throws Exception {
        String memberCode = null;
        Map<String, String> tempParams = new HashMap<String, String>();
        tempParams.put("aliasCode", aliasCode);
        Map<String, Object> resultMap = anjiePayService.accountQueryAliasCode(tempParams);
        if (resultMap != null && RESPONSE_SUCCESS.equals(resultMap.get(RESPONSE_KEY_CODE))) {
            if (resultMap.get("zhCard") != null) {
                JSONObject json = JSONObject.fromObject(resultMap.get("zhCard"));
                memberCode = String.valueOf(json.get("memberCode"));
            }
        }
        return memberCode;
    }

    //通过loginName查找memberCode
    private Map<String,String> getMemberCodeAndAliasCodeByLoginName(String loginName) throws Exception {
        Map<String,String> result = new HashMap<String,String>();
        Map<String, String> tempParams = new HashMap<String, String>();
        tempParams.put("loginName", loginName);
        Map<String, Object> resultMap = anjiePayService.accountQueryMainAliasCode(tempParams);
        if (resultMap != null && RESPONSE_SUCCESS.equals(resultMap.get(RESPONSE_KEY_CODE))) {
            if (resultMap.get("zhCard") != null) {
                JSONObject json = JSONObject.fromObject(resultMap.get("zhCard"));
                result.put("memberCode",String.valueOf(json.get("memberCode")));
                result.put("aliasCode",String.valueOf(json.get("aliasCode")));
            }
        }
        return result;
    }

    /*private PaySystemMemo createPayNo(Map<String, String> paraMap){
        String paySysCode = snService.getSnCode(SnEnum.SEQ_NJCC_PAY_SYSTEM_CODE);
        PaySystemMemo psm = new PaySystemMemo();
        psm.setLoginid(paraMap.get("loginId"));
        psm.setAmount(paraMap.get("amount"));
        psm.setActualamount(paraMap.get("amount"));
        psm.setPaySysNo(paySysCode);
        psm.setRemark("生成智汇转值订单");
        psm.setReqSource(ReqEnum.SOURCE_NJCC_ACCOUNT_USERTRANSFER.getCode());
        psm.setStatus(new Integer(PayEnum.PAY_ORDER_GEN.getCode()));
        psm.setReqBizType(PayEnum.PAY_REQ_BIZ_TYPE_NJCC_APP.getCode());
        psm.setPaytype(PayEnum.PAY_TYPE_NJCC_ACCOUNT_USERTRANSFER.getCode());
        psm.setAliascode(paraMap.get("aliascode"));
        psm.setRequestTime(new Date());
        if (paraMap.get("type").equals("10")) {
            psm.setAliascodeType("2");
        }

        psm = paySystemMemoService.create(psm);
        return  psm;
    }*/

}
