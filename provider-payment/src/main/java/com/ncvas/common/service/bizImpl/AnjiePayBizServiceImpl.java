package com.ncvas.common.service.bizImpl;

import com.ncvas.base.entity.PaySystemMemo;
import com.ncvas.base.entity.PaySystemMemoDto;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.base.utils.JsonUtils;
import com.ncvas.common.entity.PayOrder;
import com.ncvas.common.entity.PayOrderDTO;
import com.ncvas.common.entity.PayWaterMemo;
import com.ncvas.common.entity.PayWaterMemoDto;
import com.ncvas.common.enums.SnEnum;
import com.ncvas.common.mq.queue.send.QueueSender;
import com.ncvas.common.service.*;
import com.ncvas.common.service.biz.UpdateTokenBizService;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.entity.*;
import com.ncvas.payment.enums.PayEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.enums.WriteTypeEnum;
import com.ncvas.payment.service.*;
import com.ncvas.payment.service.biz.AnjiePayBizService;
import com.ncvas.util.AmountUtils;
import com.pay.util.JSonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("anjiePayBizService")
//@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public final class AnjiePayBizServiceImpl implements AnjiePayBizService {
	static final Logger logger = LoggerFactory.getLogger(AnjiePayBizServiceImpl.class);

	@Autowired
	private AnjiePayService anjiePayService;
	@Autowired
	private PaySystemMemoService paySystemMemoService;
	@Autowired
	private PayWaterMemoService payWaterMemoService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private PayService payService;
	@Autowired
	private UpdateTokenBizService updateTokenBizService;
	@Autowired
	private PayPlatformConfig payPlatformConfig;
	@Autowired
	private AccountBindingQuickpaymentBankCardService accountBindingQuickpaymentBankCardService;
	@Autowired
	private AccountCertificationService accountCertificationService;
	@Autowired
	private SnService snService;
	@Autowired
	private AccountQueryMainAliasCodeService accountQueryMainAliasCodeService;
	@Autowired
	private AccountRegisterNjccVirtualService accountRegisterNjccVirtualService;
	@Autowired
	private AccountVerdictPayPasswordService accountVerdictPayPasswordService;
	@Autowired
	private AccountUnBinDingQuickPayMentBankCardService accountUnBinDingQuickPayMentBankCardService;
	@Autowired
	private AccountQuickPayCancelService accountQuickPayCancelService;
	@Autowired
	private AccountQuickPayOrderService accountQuickPayOrderService;
	@Autowired
	private AccountQueryAliasCodeService accountQueryAliasCodeService;
	@Autowired
	private QueueSender queueSender;
	@Autowired
	private SysDictionaryDataService sysDictionaryDataService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private PayOrderDepositService payOrderDepositService;
	@Autowired
	private PayOrderWaterService payOrderWaterService;
	@Autowired
	private PayOrderBindBankCardService payOrderBindBankCardService;
	@Autowired
	private AccountSetMainAliasCodeService accountSetMainAliasCodeService;
	@Autowired
	private PayOrderWithdrawService payOrderWithdrawService;
	@Autowired
	private AccountWriteCardService accountWriteCardService;

	private String code,desc,success,error,payProcess,payOrderGen,mainAliasPwdCheck,pwdNotMatch,accbalanceError,
			bindBankPay,njccApp,payFailure,paySuccess,bankCardBind,bankCardUnbind,bindBankCardRegisterVirtralOpen,deposit,withdraw,depositBindCard,payAndBindCard,bankCardBindRefund;
	private void init(){
		code = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE");
		desc = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC");
		success = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_SUCCESS");
		error = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_ERROR");
		mainAliasPwdCheck = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","BIND_BANK_CARD_MAIN_ALIAS_PWD_CHECK");
		pwdNotMatch = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_ERROR_PWD_NOT_MATCH");
		accbalanceError = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","ERROR_ACCBALANCE_CODE");
		bindBankCardRegisterVirtralOpen = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","BIND_BANK_CARD_REGISTER_VIRTRAL_OPEN");

		bindBankPay = sysDictionaryDataService.getSysDictDataValue("PayTypeEnum","PAY_TYPE_BIND_BANK_CARD_PAY");
		deposit = sysDictionaryDataService.getSysDictDataValue("PayTypeEnum","PAY_TYPE_DEPOSIT");
		withdraw = sysDictionaryDataService.getSysDictDataValue("PayTypeEnum","WITHDRAW");

		njccApp = sysDictionaryDataService.getSysDictDataValue("ReqBizProjEnum","REQ_BIZ_PROJ_NJCC");

		payFailure = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE");
		paySuccess = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_SUCCESS");
		payProcess = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_PROCESS");
		payOrderGen = sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_ORDER_GEN");

		bankCardBind = sysDictionaryDataService.getSysDictDataValue("BankCardEnum","BANK_CARD_BIND");
		bankCardUnbind = sysDictionaryDataService.getSysDictDataValue("BankCardEnum","BANK_CARD_UNBIND");
		depositBindCard = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","QUICK_PAY_MODE_DEPOSITBINDCARD");
		payAndBindCard = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","QUICK_PAY_MODE_PAYANDBIND");
		bankCardBindRefund = sysDictionaryDataService.getSysDictDataValue("RefundTypeEnum","BANK_CARD_BIND_REFUND");
	}

	public Map<String, Object> bindBankCard(String buyerMarked,String userName,String cardNo,String certNo
			,String mobile,String checkCode,String orderId,String memberCode,String checkMainAliasPwd
			,String registerVirtral,String password,String reqSource) throws Exception{
		Boolean flag = redisTemplate.opsForHash().hasKey(memberCode + RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(), cardNo);
		//如果本身存在的卡号，则提示用户该银行卡已绑定
		if(flag){
			Map<String, Object> flagMap= new HashMap<>();
			flagMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			flagMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),PayPlatformEnum.BIND_BANK_CARD_FLAG.getDescription());
			return flagMap;
		}

		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
		//		判断当前主卡有没有智汇账户 有的话校验支付密码 默认校验支付密码
		if(StringUtils.isEmpty(checkMainAliasPwd) || PayPlatformEnum.BIND_BANK_CARD_MAIN_ALIAS_PWD_CHECK.getCode().equals(checkMainAliasPwd)) {
			Map valiateRes = valiatPayPwdeMainAliasAllowEmpty(memberCode, password);
			if (valiateRes == null || !PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(valiateRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
				return valiateRes;
			}
		}
		PaySystemMemoDto reqDto = new PaySystemMemoDto();
		reqDto.setPaySysNo(orderId);
		PaySystemMemo psm = paySystemMemoService.doQuery(reqDto);
		if(psm == null){
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"订单号："+orderId+"查不到信息");
			return resMap;
		}
		psm.setLoginid(buyerMarked);
		String amount = String.valueOf(Double.valueOf(payPlatformConfig.getWebgateBindCardAmount())/100);
		psm.setAmount(amount);
		psm.setActualamount(amount);
		psm.setRemark("绑定银行卡，订单处理中，后面直接调用支付绑卡");
		psm.setStatus(new Integer(PayEnum.PAY_PROCESS.getCode()));
		psm.setReqSource(reqSource);
		psm.setPaytype(PayEnum.PAY_TYPE_BIND_BANK_CARD_PAY.getCode());
		psm.setBankcardno(cardNo);
		try {
			paySystemMemoService.update(psm);
		}catch (Exception e){
			e.printStackTrace();
		}

		//		绑定银行卡 支付1分钱
		Map bindByPayRes = payService.quickPay(orderId,buyerMarked,userName,cardNo,payPlatformConfig.getWebgateBindCardAmount()
				,certNo,mobile,checkCode,PayPlatformEnum.QUICK_PAY_MODE_PAYANDBIND.getCode(),password,null,null,null
				,null,null);
		String remark,status;
		if(bindByPayRes==null || !PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(bindByPayRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),bindByPayRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));

			remark = "绑定银行卡失败"+bindByPayRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode());
			status = PayEnum.PAY_FAILURE.getCode();
			synchPaySystemMemo(orderId,remark,null,null,status,memberCode);
			return resMap;
		}
//		订单改为成功
		remark = "绑定银行卡，支付成功了";
		status = PayEnum.PAY_SUCCESS.getCode();
		synchPaySystemMemo(orderId,remark,null,null,status,memberCode);
//		同步绑定银行卡信息
		synchAccountBindingQuickpaymentBankCard(cardNo,certNo,buyerMarked,mobile);
//		同步实名信息
		synchAccountCertification(certNo,buyerMarked,userName,memberCode);
//		开通虚拟账户 默认开通
		if(StringUtils.isEmpty(registerVirtral) || PayPlatformEnum.BIND_BANK_CARD_REGISTER_VIRTRAL_OPEN.getCode().equals(registerVirtral)) {
			accountRegisterVirtral(memberCode, cardNo, userName, certNo, password,buyerMarked);
		}
		return resMap;
	}

	public Map<String, Object> bindBankCard2(String buyerMarked,String userName,String cardNo,String certNo
			,String mobile,String checkCode,String orderId,String memberCode,String checkMainAliasPwd
			,String registerVirtral,String password,String reqsource) throws Exception{
		init();
		Boolean flag = redisTemplate.opsForHash().hasKey(memberCode + RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(), cardNo);
		//如果本身存在的卡号，则提示用户该银行卡已绑定
		if(flag){
			Map<String, Object> flagMap= new HashMap<>();
			flagMap.put(code,error);
			flagMap.put(desc,PayPlatformEnum.BIND_BANK_CARD_FLAG.getDescription());
			return flagMap;
		}
		Map resMap = new HashMap();
		resMap.put(code,success);

		//		判断当前主卡有没有智汇账户 有的话校验支付密码 默认校验支付密码
		if(StringUtils.isEmpty(checkMainAliasPwd) || mainAliasPwdCheck.equals(checkMainAliasPwd)) {
			Map valiateRes = valiatPayPwdeMainAliasAllowEmpty(memberCode, password);
			if (valiateRes == null || !success.equals(valiateRes.get(code))) {
				return valiateRes;
			}
		}

		PayOrderDTO payOrderDTO = new PayOrderDTO();
		payOrderDTO.setOrderId(orderId);
		PayOrder payOrder = payOrderService.doQuery(payOrderDTO);
		if(payOrder == null){
			resMap.put(code,error);
			resMap.put(desc,"订单号："+orderId+"查不到信息");
			return resMap;
		}
		String amount = String.valueOf(Double.valueOf(payPlatformConfig.getWebgateBindCardAmount())/100);
		payOrder.setAmount(amount);
		payOrder.setPayType(bindBankPay);
		payOrder.setReqBizProj(njccApp);
		payOrder.setStatus(payProcess);
		payOrderService.doUpdate(payOrder);
		//		绑定银行卡 支付1分钱
		Map bindByPayRes = payService.quickPay2(orderId,buyerMarked,userName,cardNo,AmountUtils.changeY2F(amount)
				,certNo,mobile,checkCode,payAndBindCard,password,null,null,null
				,null,null);
		if(bindByPayRes==null || !success.equals(bindByPayRes.get(code))) {
			if(pwdNotMatch.equals(bindByPayRes.get(code))){
				resMap.put(code,bindByPayRes.get(code));
			}else{
				resMap.put(code, error);
			}
			resMap.put(desc,bindByPayRes.get(desc));
			createBindBankCard(orderId,amount,reqsource,payFailure,"",cardNo,memberCode,payOrder.getReqBizProj(),"绑定银行卡失败"+bindByPayRes.get(desc),certNo);
			return resMap;
		}
//		创建绑定银行卡订单
		createBindBankCard(orderId,amount,reqsource,paySuccess,"",cardNo,memberCode,payOrder.getReqBizProj(),"绑定银行卡，支付成功了",certNo);
//		同步实名信息
		synchAccountCertification(certNo,buyerMarked,userName,memberCode);
//		开通虚拟账户 默认开通
		if(StringUtils.isEmpty(registerVirtral) || bindBankCardRegisterVirtralOpen.equals(registerVirtral)) {
			accountRegisterVirtral(memberCode, cardNo, userName, certNo, password,buyerMarked);
		}

//		MQ调用退款
		sendMqRefund(payOrder.getOrderId(),payOrder.getAmount(),payOrder.getReqBizProj(),reqsource,memberCode,bankCardBindRefund);
		return resMap;
	}
	private void createBindBankCard(String orderid,String amount,String reqsource,String status,String paybank,String bankcardno
			,String memberCode,String reqBizProj,String remark,String certNo) throws Exception{
		PayOrderBindBankCard bindBankCard = new PayOrderBindBankCard();
		bindBankCard.setOrderid(orderid);
		bindBankCard.setAmount(amount);
		bindBankCard.setReqsource(reqsource);
		bindBankCard.setStatus(status);
		bindBankCard.setPaybank(paybank);
		bindBankCard.setBankcardno(bankcardno);
		bindBankCard.setMembercode(memberCode);
		bindBankCard.setReqBizProj(reqBizProj);
		bindBankCard.setRemark(remark);
		bindBankCard.setBindType(bankCardBind);
		bindBankCard.setCertNo(certNo);
		payOrderBindBankCardService.doCreate(bindBankCard);

		PayOrder payOrder = new PayOrder();
		payOrder.setStatus(status);
		payOrderService.updateByPayOrderId(payOrder);
	}

	private void synchAccountBindingQuickpaymentBankCard(String cardNo,String certNo,String buyerMarked,String mobile) throws Exception{
		AccountQuickpaymentBankCardDTO aqpdto = new AccountQuickpaymentBankCardDTO();
		aqpdto.setIdNo(certNo);
		aqpdto.setBankAcct(cardNo);
		AccountQuickpaymentBankCard accountQuickpaymentBankCard =accountBindingQuickpaymentBankCardService.doQuery(aqpdto);
		boolean aqpExist = true;
		if(accountQuickpaymentBankCard == null){
			aqpExist = false;
			accountQuickpaymentBankCard = new AccountQuickpaymentBankCard();
		}
		accountQuickpaymentBankCard.setLoginName(buyerMarked);
		accountQuickpaymentBankCard.setBankAcct(cardNo);
		accountQuickpaymentBankCard.setIdType(PayPlatformEnum.ACCOUNT_QUICKPAYMENT_BANK_CARD_IDTYPE.getCode());
		accountQuickpaymentBankCard.setIdNo(certNo);
		accountQuickpaymentBankCard.setMobile(mobile);
		accountQuickpaymentBankCard.setBindType(PayPlatformEnum.ACCOUNT_QUICKPAYMENT_BANK_CARD_BINDINGSTATUS.getCode());
		if(aqpExist){
			accountBindingQuickpaymentBankCardService.doUpdate(accountQuickpaymentBankCard);
		}else{
			accountBindingQuickpaymentBankCardService.doCreate(accountQuickpaymentBankCard);
		}
	}
	private void synchPayOrderDeposit(String orderId,String amount, String aliasCodeType,String status, String aliascode, String paybank,
							   String bankcardno, String memberCode,String reqBizProj, String remark,String certNo,String reqSource) throws Exception{
		/*String accBalance = "";
		if(StringUtils.isNotBlank(memberCode)){
			Map mainAlias = valiateMainAlias(memberCode);
			if(success.equals(mainAlias.get(code))) {
				accBalance = (String)mainAlias.get("accBalance");
			}
		}*/
		//验证医疗一账通账户获取医疗账户余额
		/*Map<String, String> tempParams2 = new HashMap<String,String>();
		tempParams2.put("aliasCode",aliascode);
		Map result = anjiePayService2.hisVerify(tempParams2);
		String hisbalance = null;
		if (result != null && success.equals(result.get(code))) {
			if (result.get("rescontent") != null) {
				JSONObject json = JSONObject.fromObject(result.get("rescontent"));
				hisbalance = String.valueOf(json.get("ACCBALANCE"));
				hisbalance = StringUtil.isEmpty(hisbalance) ? "0" : hisbalance;
			}
		}*/
		PayOrderDeposit payOrderDeposit = new PayOrderDeposit();
		payOrderDeposit.setOrderid(orderId);
		payOrderDeposit.setAmount(amount);
		payOrderDeposit.setReqsource(reqSource);
		payOrderDeposit.setStatus(status);
		payOrderDeposit.setAliascode(aliascode);
		payOrderDeposit.setPaybank(paybank);
		payOrderDeposit.setBankcardno(bankcardno);
		payOrderDeposit.setMembercode(memberCode);
		//余额查询改为通过线程查询
		//payOrderDeposit.setAliascodeBalanceNj(accBalance);
		//payOrderDeposit.setAliascodeBalanceHis(hisbalance);
		payOrderDeposit.setReqBizProj(reqBizProj);
		payOrderDeposit.setAliascodeType(aliasCodeType);
		payOrderDeposit.setRemark(remark);
		payOrderDeposit.setCertNo(certNo);

		PayOrderDepositDTO payOrderDepositDTO = new PayOrderDepositDTO();
		payOrderDepositDTO.setOrderid(orderId);
		List<PayOrderDeposit> payOrderDepositList = payOrderDepositService.doQuery(payOrderDepositDTO,Integer.MIN_VALUE,Integer.MAX_VALUE);
		if (payOrderDepositList.size() == 0) {
			payOrderDeposit = payOrderDepositService.doCreate(payOrderDeposit);
		} else {
			payOrderDeposit.setId(payOrderDepositList.get(0).getId());
			payOrderDepositService.doUpdate(payOrderDeposit);
		}
	}
	private void synchPaySystemMemo(String orderId,String remark,String refundOrderID,String refundType,String status,String memberCode) throws Exception{
		String accBalance = "";
		if(StringUtils.isNotBlank(memberCode)){
			Map mainAlias = valiateMainAlias(memberCode);
			if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(mainAlias.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
				accBalance = (String)mainAlias.get("accBalance");
			}
		}
		PaySystemMemoDto reqDto = new PaySystemMemoDto();
		reqDto.setPaySysNo(orderId);
		PaySystemMemo psm = paySystemMemoService.selectLastPaySystemMemo(reqDto);
		psm.setRemark(remark);
		psm.setStatus(new Integer(status));
		psm.setRefundOrder(refundOrderID);
		psm.setRefundType(refundType);
		if(StringUtils.isNotBlank(accBalance))psm.setAliascodeBalance(accBalance);
		paySystemMemoService.update(psm);
	}
	private void synchAccountCertification(String certNo,String buyerMarked,String userName,String memberCode) throws Exception{
		AccountCertificationDTO acto = new AccountCertificationDTO();
		acto.setIdNo(certNo);
		AccountCertification accountCertification =accountCertificationService.doQuery(acto);
		if(accountCertification == null){
			accountCertification = new AccountCertification();
			accountCertification.setLoginName(buyerMarked);
			accountCertification.setIdName(userName);
			accountCertification.setIdType(PayPlatformEnum.ACCOUNT_QUICKPAYMENT_BANK_CARD_IDTYPE.getCode());
			accountCertification.setIdNo(certNo);
			accountCertificationService.doCreate(accountCertification);
		}
		String accountLoginDTOStr = (String)redisTemplate.opsForValue().get(memberCode+RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
		AccountLoginDTO accountLoginDTO = JsonUtils.toObject(accountLoginDTOStr,AccountLoginDTO.class);
		updateTokenBizService.updateToken(accountLoginDTO.getToken(), null, memberCode, userName, certNo);
	}
	//	校验支付密码
	private Map valiatePayPwd(String aliasCode,String password,String memberCode) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());

		Map<String, Object> valiatePwdResMap = accountVerdictPayPasswordService.accountVerdictPayPassword(aliasCode,password,PayPlatformEnum.QUICK_PAY_ALIAS_CODE_TYPE_NJCC.getCode(),memberCode);
		if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(valiatePwdResMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			String isVerify = (String)valiatePwdResMap.get("isVerify");
			if(PayPlatformEnum.NO_VERIFY_PWD.getCode().equals(isVerify)){
				resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.PAY_RESPONSE_ERROR_PAY_CODE.getCode());
				resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"支付密码不正确");
				return resMap;
			}
		}else{
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),valiatePwdResMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
			return resMap;
		}
		return resMap;
	}
	private Map valiateMainAlias(String memberCode) throws Exception{

//		判断一下有没有主卡
		Map<String,Object> mainAlias = accountQueryMainAliasCodeService.accountQueryMainAliasCode(memberCode);
		return mainAlias;
	}
	private Map valiatPayPwdeMainAliasAllowEmpty(String memberCode,String password) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
//		判断一下有没有主卡
		Map<String,Object> mainAlias = valiateMainAlias(memberCode);
//如果找不到主卡 就直接过掉 不去验证密码
		if(!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(mainAlias.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))
				&& PayPlatformEnum.RESPONSE_ERROR_ZHCARD_NOT_SET_MAIN_ERROR.getCode().equals(mainAlias.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			return resMap;
		}else if(!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(mainAlias.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			return mainAlias;
		}else{
			String custClass = (String)mainAlias.get("custClass");
			String aliasCode = (String)mainAlias.get("aliasCode");
//			0000：未开通智汇账户0002：已开通智汇账户 如果已经开通了 去校验支付密码
			if(PayPlatformEnum.MAIN_ALIAS_CUSTCLASS_ACCOUNT_EXIST.getCode().equals(custClass)){
				Map<String, Object> valiateResMap = valiatePayPwd(aliasCode,password,memberCode);
				if(PayPlatformEnum.PAY_RESPONSE_ERROR_PAY_CODE.getCode().equals(valiateResMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
					valiateResMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR_PWD_NOT_MATCH.getCode());
				}
				return valiateResMap;
			}
		}
		return resMap;
	}
	private Map valiatPayPwdeMainAliasNotAllowEmpty(String memberCode,String password) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
//		判断一下有没有主卡
		Map<String,Object> mainAlias = valiateMainAlias(memberCode);
//如果找不到主卡 就直接过掉 不去验证密码
		if(!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(mainAlias.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			return mainAlias;
		}else{
			String custClass = (String)mainAlias.get("custClass");
			String aliasCode = (String)mainAlias.get("aliasCode");
//			0000：未开通智汇账户0002：已开通智汇账户 如果已经开通了 去校验支付密码
			if(PayPlatformEnum.MAIN_ALIAS_CUSTCLASS_ACCOUNT_EXIST.getCode().equals(custClass)){
				Map<String, Object> valiateResMap = valiatePayPwd(aliasCode,password,memberCode);
				if(PayPlatformEnum.PAY_RESPONSE_ERROR_PAY_CODE.getCode().equals(valiateResMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
					valiateResMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR_PWD_NOT_MATCH.getCode());
				}
				return valiateResMap;
			}
		}
		return resMap;
	}
	private void accountRegisterVirtral(String memberCode,String bankAcctId,String memberName,String socialCode,String payPwd,String loginName) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
		Map<String, Object> paraMap = new HashMap<String,Object>();
		paraMap.put("memberCode",memberCode);
		paraMap.put("bankAcctId",bankAcctId);
		paraMap.put("memberName",memberName);
		paraMap.put("socialCode",socialCode);
		paraMap.put("payPwd",payPwd);
//		调用开通虚拟账户
		Map<String,Object> registerVirtualRes = accountRegisterNjccVirtualService.accountRegisterNjccVirtual(memberCode, bankAcctId
				, memberName, socialCode, payPwd,loginName);
//如果找不到主卡 就直接过掉 不去验证密码
		if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(registerVirtualRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode())))
			logger.info(memberCode+"开通虚拟账户成功");
		else{
			logger.info(memberCode+"开通虚拟账户失败："+registerVirtualRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
		}
	}
	//	绑银行卡退款
	public void bindBankCardRefund(String reqSource) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
//先去查一下 属于绑定银行卡流程的 做绑定银行卡支付的 成功的 这些订单处理返回流程
		PaySystemMemoDto reqDto = new PaySystemMemoDto();
		reqDto.setReqSource(reqSource);
		reqDto.setPaytype(PayEnum.PAY_TYPE_BIND_BANK_CARD_PAY.getCode());
		reqDto.setStatus(new Integer(PayEnum.PAY_SUCCESS.getCode()));
		List<String> notInRefundTypeList = new ArrayList<>();
		notInRefundTypeList.add(PayEnum.PAY_REFUND_PROCESS.getCode());
		notInRefundTypeList.add(PayEnum.PAY_REFUND_SUCCESS.getCode());
		notInRefundTypeList.add(PayEnum.PAY_REFUND_FAILED.getCode());
		reqDto.setNotInRefundType(notInRefundTypeList);
//每次处理100笔
		List<PaySystemMemo> psmlist = paySystemMemoService.doQuery(reqDto,0,100);
//		先标识这部分正在处理
		for(PaySystemMemo processPsm:psmlist){
			processPsm.setRefundType(PayEnum.PAY_REFUND_PROCESS.getCode());
			paySystemMemoService.update(processPsm);
		}
		for(PaySystemMemo processPsm:psmlist){
//			获取退款订单流水信息
			PayWaterMemoDto pwmDto = new PayWaterMemoDto();
			pwmDto.setStatus(new Integer(PayEnum.PAY_SUCCESS.getCode()));
			pwmDto.setPaysysno(processPsm.getPaySysNo());
			PayWaterMemo pwm = payWaterMemoService.doQuery(pwmDto);
//			获取原本的请求，开始原路返回
			HashMap<String,String> reqSourceMap = (HashMap<String,String>)JSonUtil.json2Object(pwm.getReqmsg(),HashMap.class);
			String paySysCode = snService.getSnCode(SnEnum.SEQ_NJCC_PAY_SYSTEM_CODE);
//			String mobile = reqSourceMap.get("mobile");
			String refundOrderID = paySysCode; //退款生成id
			String refundAmount = reqSourceMap.get("orderAmount");
//			成产退款订单

			PaySystemMemo psm = new PaySystemMemo();
			psm.setLoginid(processPsm.getLoginid());
			psm.setAmount(processPsm.getAmount());
			psm.setActualamount(processPsm.getActualamount());
			psm.setPaySysNo(paySysCode);
			psm.setRemark(processPsm.getPaySysNo());
			psm.setStatus(new Integer(PayEnum.PAY_PROCESS.getCode()));
			psm.setReqSource(reqSource);
			psm.setPaytype(PayEnum.PAY_TYPE_BIND_BANK_CARD_REFUND.getCode());
			psm = paySystemMemoService.create(psm);
			//		绑定银行卡 退还1分钱
			Map paraMap = new HashMap();
			paraMap.put("refundOrderID",refundOrderID);
			paraMap.put("orderID",reqSourceMap.get("orderID"));//需要退款的原订单号
			paraMap.put("refundAmount",refundAmount);
			Map refundRes = payService.refund(paraMap);
			String remark=null,refundType=null,status=null;
			if(refundRes!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(refundRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
				remark = "退款成功，退款订单号："+reqSourceMap.get("orderID")+",处理退款的订单是:"+paySysCode;
				logger.info(remark);
				refundType = PayEnum.PAY_REFUND_SUCCESS.getCode();
				status = PayEnum.PAY_SUCCESS.getCode();
			}else{
				remark="退款失败，退款订单号："+reqSourceMap.get("orderID")+",处理退款的订单是:"+paySysCode+"失败原因："+refundRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode());
				logger.info(remark);
				refundType = PayEnum.PAY_REFUND_FAILED.getCode();
				status = PayEnum.PAY_FAILURE.getCode();
			}
			remark = "绑定银行卡，退款成功了,退款订单号："+reqSourceMap.get("orderID")+",处理退款的订单是:"+paySysCode;
			synchPaySystemMemo(reqSourceMap.get("orderID"),remark,refundOrderID,refundType,status,null);
		}
	}

	public Map<String, Object> accountDeposit(String buyerMarked,String userName,String cardNo,String certNo,String mobile,String orderId,String amount
			,String memberCode,String aliasCode,String aliasCodeType,String password, String reqSource) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
		PaySystemMemoDto reqDto = new PaySystemMemoDto();
		reqDto.setPaySysNo(orderId);
		PaySystemMemo psm = paySystemMemoService.selectLastPaySystemMemo(reqDto);
		if(psm == null){
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"订单号："+orderId+"查不到信息");
			return resMap;
		}else if (Float.parseFloat(psm.getAmount()) != Float.parseFloat(amount)){
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"订单号："+orderId+"金额和支付不一致");
			logger.info("订单号："+orderId+"金额:"+psm.getAmount()+"请求金额:"+amount);
			return resMap;
		}
		psm.setLoginid(buyerMarked);
		psm.setAmount(amount);
		psm.setActualamount(amount);
		psm.setRemark("充值");
		psm.setStatus(new Integer(PayEnum.PAY_PROCESS.getCode()));
		psm.setReqSource(reqSource);
		psm.setPaytype(PayEnum.PAY_TYPE_DEPOSIT.getCode());
		psm.setAliascodeType(aliasCodeType);
		psm.setBankcardno(cardNo);
		paySystemMemoService.update(psm);
		//充值
		Map depositRes = payService.quickPay(null,buyerMarked,userName,cardNo,String.valueOf((int)(Double.valueOf(amount)*100))
				,certNo,mobile,null,PayPlatformEnum.QUICK_PAY_MODE_DEPOSIT.getCode(),password,null,null,null
				,aliasCode,aliasCodeType);
		String remark,status;
		if(depositRes==null || !PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(depositRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			if(PayPlatformEnum.RESPONSE_ERROR_PWD_NOT_MATCH.getCode().equals(depositRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
				resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),depositRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()));
				resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), PayPlatformEnum.RESPONSE_ERROR_PWD_NOT_MATCH.getDescription());
			}else{
				resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), PayPlatformEnum.RESPONSE_ERROR.getCode());
				resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(), depositRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
			}
			remark = buyerMarked+"充值失败"+depositRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode());
			status = PayEnum.PAY_FAILURE.getCode();
			synchPaySystemMemo(orderId,remark,null,null,status,memberCode);
			return resMap;
		}
//		订单改为成功
		remark = buyerMarked+"充值成功";
		status = PayEnum.PAY_SUCCESS.getCode();
		synchPaySystemMemo(orderId,remark,null,null,status,memberCode);
//		获取余额
		String accBalance = accountQueryMainAliasCodeService.getCardAccBalance(memberCode);
		if(!PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode().equals(accBalance))resMap.put("accBalance",accBalance);
		return resMap;
	}

	public Map<String, Object> accountDeposit2(String buyerMarked,String userName,String cardNo,String certNo,String mobile,String orderId,String amount
			,String memberCode,String aliasCode,String aliasCodeType,String password, String reqSource,String reqBizProj) throws Exception{
		init();
		Map resMap = new HashMap();
		resMap.put(code,success);
//		PayOrderDTO payOrderDTO = new PayOrderDTO();
//		payOrderDTO.setOrderId(orderId);
//		PayOrder payOrder = payOrderService.doQuery(payOrderDTO);
		PayOrder payOrder = null;
		try {
			payOrder = redisService.getOderInfoNew(orderId);
			resMap.put("orderId",payOrder.getOrderId());
		}catch (Exception e) {
			resMap.put(code,error);
			resMap.put(desc,"订单已处理完成，订单号："+orderId);
			return resMap;
		}
		if(payOrder == null){
			resMap.put(code,error);
			resMap.put(desc,"订单号："+orderId+"查不到信息");
			return resMap;
		}else if (Float.parseFloat(payOrder.getAmount()) != Float.parseFloat(amount)){
			resMap.put(code,error);
			resMap.put(code,"订单号："+orderId+"金额和支付不一致");
			logger.info("订单号："+orderId+"金额:"+payOrder.getAmount()+"请求金额:"+amount);
			return resMap;
		}
		payOrder.setPayType(deposit);
		payOrder.setStatus(payProcess);
		payOrderService.doUpdate(payOrder);

		//充值
		Map depositRes = payService.quickPay2(orderId,buyerMarked,userName,cardNo,AmountUtils.changeY2F(amount)
				,certNo,mobile,null,depositBindCard,password,null,null,null
				,aliasCode,aliasCodeType);
		String remark,status;
		if(depositRes==null || !success.equals(depositRes.get(code))) {
			if(pwdNotMatch.equals(depositRes.get(code))){
				status = payOrderGen;
				payOrder.setStatus(status);
				//支付密码错误可以重新支付，需将订单放回redis
				redisService.insertOderInfo(payOrder);
				resMap.put(code,depositRes.get(code));
				resMap.put(desc, sysDictionaryDataService.getSysDictData("PayPlatformEnum","RESPONSE_ERROR_PWD_NOT_MATCH").getDictDataDesc());
			}else{
				status = payFailure;
				payOrder.setStatus(status);
				resMap.put(code, depositRes.get(code));
				resMap.put(desc, depositRes.get(desc));
			}
			remark = buyerMarked+"充值失败"+depositRes.get(desc);
			payOrderService.doUpdate(payOrder);
			synchPayOrderDeposit(orderId,amount,aliasCodeType,status,aliasCode,null,cardNo,memberCode,reqBizProj,remark,certNo,reqSource);
		} else {
			//		订单改为成功
			remark = buyerMarked+"充值成功";
			status = paySuccess;
			payOrder.setStatus(status);
			payOrderService.doUpdate(payOrder);
			synchPayOrderDeposit(orderId,amount,aliasCodeType,status,aliasCode,null,cardNo,memberCode,reqBizProj,remark,certNo,reqSource);
//		获取余额
			String accBalance = accountQueryMainAliasCodeService.getCardAccBalance(memberCode);
			if(!PayPlatformEnum.ERROR_ACCBALANCE_CODE.getCode().equals(accBalance))resMap.put("accBalance",accBalance);
		}
		//payOrderWaterService.sendMsg(payOrder.getOrderId(),payOrder.getStatus(),payOrder.getAmount(),remark,JSonUtil.toJSonString(payParam),JSonUtil.toJSonString(depositRes));
		return resMap;
	}

	public Map<String, Object> unbindBankCard(String loginName,String memberCode,String bankAcct,String password) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
		//		判断当前主卡有没有智汇账户 有的话校验支付密码
		Map valiateRes = valiatPayPwdeMainAliasNotAllowEmpty(memberCode,password);
		if(valiateRes==null || !PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(valiateRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			return valiateRes;
		}
		resMap = accountUnBinDingQuickPayMentBankCardService.accountUnBinDingQuickPayMentBankCard(loginName,bankAcct);
		if(resMap!=null&& PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			redisTemplate.opsForHash().delete(memberCode + RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(),bankAcct);
		}
		return resMap;
	}
	public Map<String, Object> unbindBankCard2(String loginName,String memberCode,String bankAcct,String password) throws Exception{
		init();
		Map resMap = new HashMap();
		resMap.put(code,success);
		//		判断当前主卡有没有智汇账户 有的话校验支付密码
		Map valiateRes = valiatPayPwdeMainAliasNotAllowEmpty(memberCode,password);
		if(valiateRes==null || !success.equals(valiateRes.get(code))) {
			return valiateRes;
		}
		Map<String, Object> unbindParaMap = new HashMap<String, Object>();
		unbindParaMap.put("loginName",loginName);
		unbindParaMap.put("bankAcct",bankAcct);
		resMap = payOrderBindBankCardService.bankCardUnbind(loginName, memberCode, bankAcct);
		if(resMap!=null&& success.equals(resMap.get(code))){
			redisTemplate.opsForHash().delete(memberCode + RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(),bankAcct);
		}
		return resMap;
	}
	public Map<String, Object> accountQuickPayCancel(String loginName,String orderNo) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
		//		判断当前主卡有没有智汇账户 有的话校验支付密码
		resMap = accountQuickPayCancelService.accountQuickPayCancel(orderNo,loginName);
		return resMap;
	}

	@Override
	public Map<String, Object> accountQuickPayOrder(Map<String, String> paraMap) throws Exception {

		// 首先查询被充值卡是否有快充预约
		Map<String, String> tempReq = new HashMap<>();
		tempReq.put("aliasCode",paraMap.get("payeeAliasCode"));
		Map<String,Object> tempMap = anjiePayService.prepaidOrderInquiry(tempReq);
		if (tempMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(tempMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			Map errMap = new HashMap();
			if(tempMap.get("responseDetail")!=null) {
				JSONArray row = JSONArray.fromObject(tempMap.get("responseDetail"));
				if (row != null && row.size() >= 0) {
					JSONObject json = (JSONObject)row.get(0);
					errMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(), PayPlatformEnum.RESPONSE_ERROR.getCode());
					errMap.put("orderseq",json.get("ORDERSEQ"));
					errMap.put("cdtalias",json.get("CDTALIAS"));
					errMap.put("payalias",json.get("PAYALIAS"));
					errMap.put("transamt",json.get("TRANSAMT"));
					errMap.put("transtime",json.get("TRANSTIME"));
					//errMap.put("accbalance",json.get("accbalance"));
					return errMap;
				}
			}
		}

		Map<String,Object> resultMap = anjiePayService.accountQuickPayOreder(paraMap);
			AccountQuickPayOrderDTO dto = new AccountQuickPayOrderDTO();
			dto.setLoginName(paraMap.get("loginName"));
			String orderID = paraMap.get("orderID");
			dto.setOrderID(orderID);
			dto.setPayeeAliasCode(paraMap.get("payeeAliasCode"));
			Map<String,Object> map = (Map<String, Object>) resultMap.get("responseDetail");
			dto.setAmount(String.valueOf(Double.parseDouble(paraMap.get("amount"))/100));

			//放置手环的信息和快充来源
			dto.setDeviceid(paraMap.get("deviceid"));
			dto.setDevicemac(paraMap.get("devicemac"));
			dto.setDevicename(paraMap.get("devicename"));
			dto.setDevicemodel(paraMap.get("devicemodel"));
			dto.setSourcetype(paraMap.get("sourcetype"));

		if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			dto.setStatus(PayPlatformEnum.QUICK_PAY_ORDER_SUCCESS.getCode());
			dto.setRemark(PayPlatformEnum.QUICK_PAY_ORDER_SUCCESS.getDescription());
			/**成功再保存数据， 避免空指针问题*/
			String mastaliascode = ((String) map.get("MSTALIASCODE")).trim();
			dto.setMstaliascode(mastaliascode);
			dto.setAccbalance(((String) map.get("ACCBALANCE")).trim());
			String aliascode = ((String) map.get("ALIASCODE")).trim();
			dto.setAliascode(aliascode);

			/**预约成功就把数据记录到写卡渠道表去*/
			AccountWriteCardDTO accountWriteCardDTO = new AccountWriteCardDTO();
			accountWriteCardDTO.setAliascode(mastaliascode);
			accountWriteCardDTO.setOrderId(orderID);
			accountWriteCardDTO.setCardNo(aliascode);
			accountWriteCardDTO.setStatus(WriteTypeEnum.WAITING_FOR_WRITE.getCode());
			accountWriteCardDTO.setRemark(WriteTypeEnum.WAITING_FOR_WRITE.getDescription());
			accountWriteCardDTO.setReqBizProj(paraMap.get("reqBizType") == null ? "1" : paraMap.get("reqBizType"));
			accountWriteCardService.doCreate(accountWriteCardDTO);
		}else {
			dto.setStatus(PayPlatformEnum.QUICK_PAY_ORDER_FAILURE.getCode());
			dto.setRemark((String) resultMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
		}
		accountQuickPayOrderService.doCreate(dto);
		return resultMap;
	}

	public Map<String, Object> payWebView(String buyerMarked,String mobile,String orderId,String amount,String orgCode,String memberCode
			,String aliasCode,String aliasCodeType, String reqSource) throws Exception{
		Map resMap = new HashMap();
		resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
		PaySystemMemoDto reqDto = new PaySystemMemoDto();
		reqDto.setPaySysNo(orderId);
		PaySystemMemo psm = paySystemMemoService.selectLastPaySystemMemo(reqDto);
		if(psm == null){
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"订单号："+orderId+"查不到信息");
			return resMap;
		}else if (Float.parseFloat(psm.getAmount()) != Float.parseFloat(amount)){
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),"订单号："+orderId+"金额和支付不一致");
			logger.info("订单号："+orderId+"金额:"+psm.getAmount()+"请求金额:"+amount);
			return resMap;
		}
		psm.setLoginid(buyerMarked);
		psm.setAmount(amount);
		psm.setActualamount(amount);
		psm.setRemark("网银充值");
		psm.setReqSource(reqSource);
		psm.setPaytype(PayEnum.PAY_TYPE_DEPOSIT.getCode());
		paySystemMemoService.update(psm);
//		paraMap.put("noticeUrl", PayPlatformEnum..getCode());
		//充值
		Map webViewRes = payService.quickPay(orderId,buyerMarked,null,null,String.valueOf((int)(Double.valueOf(amount)*100))
				,null,mobile,null,PayPlatformEnum.QUICK_PAY_MODE_DEPOSIT.getCode(),null
				,PayPlatformEnum.QUICK_PAY_CARD_TYPE_WEB_VIEW.getCode(),null,orgCode
				,aliasCode,aliasCodeType);
//		不需要同步余额
		String remark,status;
		if(webViewRes==null || !PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(webViewRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),webViewRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
			remark = buyerMarked+"网银发起跳转失败"+webViewRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode());
			status = PayEnum.PAY_FAILURE.getCode();
			synchPaySystemMemo(orderId,remark,null,null,status,null);
			return resMap;
		}
//		订单改为成功
		remark = buyerMarked+"跳转页面成功";
		status = PayEnum.PAY_SUCCESS.getCode();
		synchPaySystemMemo(orderId,remark,null,null,status,null);
		resMap.put("webViewRes",webViewRes);
		return resMap;
	}

	public Map<String, Object> payWebView2(String buyerMarked,String mobile,String orderId,String amount,String orgCode,String memberCode
			,String aliasCode,String aliasCodeType, String reqSource) throws Exception{

		//注意：此方法是做医疗网银时中途暂停的了，所以是未完成的，后期需重新检查。made by fancz

		String responseKeyCode = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE");
		String responseKeyDesc = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC");
		String responseSuccess =  sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_SUCCESS");
		String responseError =  sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_ERROR");
		String paySuccess =  sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_SUCCESS");
		String payFailure =  sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_FAILURE");
		String quickPayModeDepositBindCard = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","QUICK_PAY_MODE_DEPOSITBINDCARD");
		String queckPayCardTypeWebView = sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","QUICK_PAY_CARD_TYPE_WEB_VIEW");

		Map resMap = new HashMap();
		resMap.put(responseKeyCode,responseSuccess);
		//充值
		Map webViewRes = payService.quickPay(orderId,buyerMarked,null,null,String.valueOf((int)(Double.valueOf(amount)*100))
				,null,mobile,null,PayPlatformEnum.QUICK_PAY_MODE_DEPOSIT.getCode(),null
				,PayPlatformEnum.QUICK_PAY_CARD_TYPE_WEB_VIEW.getCode(),null,orgCode
				,aliasCode,aliasCodeType);
//		不需要同步余额
		String remark,status;
		if(webViewRes==null || !PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(webViewRes.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_ERROR.getCode());
			resMap.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),webViewRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
			remark = buyerMarked+"网银发起跳转失败"+webViewRes.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode());
			status = PayEnum.PAY_FAILURE.getCode();
			synchPaySystemMemo(orderId,remark,null,null,status,null);
			return resMap;
		}
//		订单改为成功
		remark = buyerMarked+"跳转页面成功";
		status = PayEnum.PAY_SUCCESS.getCode();
		synchPaySystemMemo(orderId,remark,null,null,status,null);
		resMap.put("webViewRes",webViewRes);
		return resMap;
	}

	/*支付平台银行网页支付结果处理*/
	public boolean processPayPlatformCallBack(String orderID,String stateCode,String amount) {
		try {
			PaySystemMemoDto psm = new PaySystemMemoDto();
			psm.setPaySysNo(orderID);
			PaySystemMemo psmvo = paySystemMemoService.selectLastPaySystemMemo(psm);
			if (Float.parseFloat(psmvo.getAmount())*100 != Float.parseFloat(amount)) {
				logger.warn("The order amount is not the same[pay=" + amount + ",order=" + psmvo.getAmount() + "]");
				return false;
			}
			psmvo.setStateCode(stateCode);
//			0---处理中 1---充值成功  2---充值失败 3 充值已充退
			if(stateCode.equals("1")){
				psmvo.setStatus(new Integer(PayEnum.PAY_SUCCESS.getCode()));
			}
			if(stateCode.equals("2")||stateCode.equals("3")){
				psmvo.setStatus(new Integer(PayEnum.PAY_FAILURE.getCode()));
			}
			if(stateCode.equals("0")){{
				psmvo.setRemark("处理中");
				logger.info(orderID+"订单处理中，这个情况我们不更新订单");
				return false;
			}}
			if(stateCode.equals("1"))psmvo.setRemark("充值成功");
			if(stateCode.equals("2"))psmvo.setRemark("充值失败");
			if(stateCode.equals("3"))psmvo.setRemark("充值已充退");
			Map<String, Object> res = accountQueryAliasCodeService.accountQueryAliasCode(psmvo.getAliascode());
			String accbalance = (String)res.get("accBalance");
			psmvo.setAliascodeBalance(StringUtil.isEmpty(accbalance)?"0":accbalance);
			paySystemMemoService.update(psmvo);
		} catch (Exception e) {
			logger.warn("第三方#callback", e);
		}
		return false;
	}

	public Map<String, Object> bindBankCardCheck(String buyerMarked, String userName, String cardNo, String certNo
			, String mobile, String checkCode, String orderId, String memberCode, String checkMainAliasPwd
			, String registerVirtral,String reqsource) throws Exception{
		init();
		Map resMap = new HashMap();
		resMap.put(code,success);
		Boolean flag = redisTemplate.opsForHash().hasKey(memberCode + RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(), cardNo);
		//如果本身存在的卡号，则提示用户该银行卡已绑定
		if(flag){
			Map<String, Object> flagMap= new HashMap<>();
			flagMap.put(code,error);
			flagMap.put(desc,PayPlatformEnum.BIND_BANK_CARD_FLAG.getDescription());
			return flagMap;
		}
		//银行卡实名校验这个银行卡有没有开通过智慧账户
		boolean check = accountSetMainAliasCodeService.checkMainAliasCodeOpen(cardNo);
		if(check){
			resMap.put(code,error);
			resMap.put(desc, "此银行卡已开通过智汇账户");
			logger.info("bankCardService|bindBankCardPure|checkcode error req：cardNo:{"+cardNo+"}此银行卡已开通过智汇账户");
			return resMap;
		}

		Map<String, String> payParam = new HashMap<String,String>();
		payParam.put("buyerMarked", buyerMarked);
		payParam.put("userName", userName);
		payParam.put("cardNo", cardNo);
		payParam.put("certNo", certNo);
		payParam.put("mobile", mobile);
		payParam.put("checkCode", checkCode);
		payParam.put("orderID", orderId);
		payParam.put("memberCode", memberCode);
		payParam.put("orderAmount",payPlatformConfig.getWebgateBindCardAmount());
		payParam.put("mode", PayPlatformEnum.QUICK_PAY_MODE_PAYANDBIND.getCode());

		PayOrderDTO payOrderDTO = new PayOrderDTO();
		payOrderDTO.setOrderId(orderId);
		PayOrder payOrder = payOrderService.doQuery(payOrderDTO);
		if(payOrder == null){
			resMap.put(code,error);
			resMap.put(desc,"订单号："+orderId+"查不到信息");
			return resMap;
		}
		String amount = String.valueOf(Double.valueOf(payPlatformConfig.getWebgateBindCardAmount())/100);
		payOrder.setAmount(amount);
		payOrder.setPayType(bindBankPay);
		payOrder.setReqBizProj(njccApp);
		payOrder.setStatus(payProcess);
		payOrderService.doUpdate(payOrder);

		//		绑定银行卡 支付1分钱
		Map bindByPayRes = payService.quickPay2(orderId,buyerMarked,userName,cardNo,AmountUtils.changeY2F(amount)
				,certNo,mobile,checkCode,payAndBindCard,null,null,null,null
				,null,null);
		if(bindByPayRes==null || !success.equals(bindByPayRes.get(code))) {
			if(pwdNotMatch.equals(bindByPayRes.get(code))){
				resMap.put(code,bindByPayRes.get(code));
			}else{
				resMap.put(code, error);
			}
			resMap.put(desc,bindByPayRes.get(desc));
			createBindBankCard(orderId,amount,reqsource,payFailure,"",cardNo,memberCode,payOrder.getReqBizProj(),"绑定银行卡失败"+bindByPayRes.get(desc),certNo);
			return resMap;
		}
//		创建绑定银行卡订单
		createBindBankCard(orderId,amount,reqsource,paySuccess,"",cardNo,memberCode,payOrder.getReqBizProj(),"绑定银行卡，支付成功了",certNo);
//		同步实名信息
		synchAccountCertification(certNo,buyerMarked,userName,memberCode);

//		MQ调用退款
		sendMqRefund(payOrder.getOrderId(),payOrder.getAmount(),payOrder.getReqBizProj(),reqsource,memberCode,bankCardBindRefund);
		return resMap;
	}
	public Map<String, Object> bindBankCardPure(String buyerMarked, String userName, String cardNo, String certNo
			, String mobile, String checkCode, String orderId, String memberCode, String checkMainAliasPwd
			, String registerVirtral,String reqsource) throws Exception {
		init();
		Boolean flag = redisTemplate.opsForHash().hasKey(memberCode + RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(), cardNo);
		//如果本身存在的卡号，则提示用户该银行卡已绑定
		if(flag){
			Map<String, Object> flagMap= new HashMap<>();
			flagMap.put(code,error);
			flagMap.put(desc,PayPlatformEnum.BIND_BANK_CARD_FLAG.getDescription());
			return flagMap;
		}

		Map resMap = new HashMap();
		resMap.put(code,success);

		PayOrderDTO payOrderDTO = new PayOrderDTO();
		payOrderDTO.setOrderId(orderId);
		PayOrder payOrder = payOrderService.doQuery(payOrderDTO);
		if(payOrder == null){
			resMap.put(code,error);
			resMap.put(desc,"订单号："+orderId+"查不到信息");
			return resMap;
		}
		String amount = String.valueOf(Double.valueOf(payPlatformConfig.getWebgateBindCardAmount())/100);
		payOrder.setAmount(amount);
		payOrder.setPayType(bindBankPay);
		payOrder.setReqBizProj(njccApp);
		payOrder.setStatus(payProcess);
		payOrderService.doUpdate(payOrder);

		//		绑定银行卡 支付1分钱
		Map bindByPayRes = payService.quickPay2(orderId,buyerMarked,userName,cardNo,AmountUtils.changeY2F(amount)
				,certNo,mobile,checkCode,payAndBindCard,null,null,null,null
				,null,null);
		if(bindByPayRes==null || !success.equals(bindByPayRes.get(code))) {
			if(pwdNotMatch.equals(bindByPayRes.get(code))){
				resMap.put(code,bindByPayRes.get(code));
			}else{
				resMap.put(code, error);
			}
			resMap.put(desc,bindByPayRes.get(desc));
			createBindBankCard(orderId,amount,reqsource,payFailure,"",cardNo,memberCode,payOrder.getReqBizProj(),"绑定银行卡失败"+bindByPayRes.get(desc),certNo);
			return resMap;
		}
//		创建绑定银行卡订单
		createBindBankCard(orderId,amount,reqsource,paySuccess,"",cardNo,memberCode,payOrder.getReqBizProj(),"绑定银行卡，支付成功了",certNo);

//		MQ调用退款
		sendMqRefund(payOrder.getOrderId(),payOrder.getAmount(),payOrder.getReqBizProj(),reqsource,memberCode,bankCardBindRefund);

		return resMap;
	}

	public Map<String, Object> accountWithdraw(String userName,String cardNo,String orderId,String amount
			,String memberCode,String aliasCode,String password,String type,String reqSource,String reqBizProj,String payBank) throws Exception{
		init();
		Map resMap = new HashMap();
		resMap.put(code,success);
		PayOrder payOrder = null;
		try {
			payOrder = redisService.getOderInfoNew(orderId);
			resMap.put("orderId",payOrder.getOrderId());
		}catch (Exception e) {
			resMap.put(code,error);
			resMap.put(desc,"订单已处理完成，订单号："+orderId);
			return resMap;
		}
		if (Float.parseFloat(payOrder.getAmount()) != Float.parseFloat(amount)) {
			resMap.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_CODE"), 2);
			resMap.put(sysDictionaryDataService.getSysDictDataValue("PayPlatformEnum","RESPONSE_KEY_DESC"), "订单金额与支付金额不一致，订单金额为：" + payOrder.getAmount() + "，支付金额" + amount);
			return resMap;
		}
		payOrder.setPayType(withdraw);
		payOrder.setStatus(payProcess);
		payOrderService.doUpdate(payOrder);

		Map<String, String> payParam = new HashMap<String,String>();
		payParam.put("userName", userName);
		payParam.put("bankCard", cardNo);
		payParam.put("type", type);
		payParam.put("orderID", orderId);
		payParam.put("amount", amount);
		payParam.put("payerMemberCode", memberCode);
		payParam.put("aliasCode", aliasCode);
		payParam.put("password", password);
		payParam.put("orderAmount",AmountUtils.changeY2F(amount));
		//提现
		Map withdrawRes = payService.withdraw(payParam);
		String remark,status;
		if(withdrawRes==null || !success.equals(withdrawRes.get(code))) {
			if(sysDictionaryDataService.getSysDictDataValue("PayEnum","PAY_PWD_ERROR").equals(withdrawRes.get(code))){
				status = payOrderGen;
				payOrder.setStatus(status);
				//支付密码错误可以重新支付，需将订单放回redis
				redisService.insertOderInfo(payOrder);
				resMap.put(code,withdrawRes.get(code));
				resMap.put(desc, sysDictionaryDataService.getSysDictData("PayPlatformEnum","RESPONSE_ERROR_PWD_NOT_MATCH").getDictDataDesc());
			}else{
				status = payFailure;
				payOrder.setStatus(status);
				resMap.put(code, withdrawRes.get(code));
				resMap.put(desc, withdrawRes.get(desc));
			}
			remark = "提现失败"+withdrawRes.get(desc);
			payOrderService.doUpdate(payOrder);
			synchPayOrderWithdraw(orderId,amount,type,status,aliasCode,payBank,cardNo,memberCode,reqBizProj,remark,reqSource);
		} else {
			//		订单改为成功
			remark = "请求成功";
			//将状态改为处理中，后续会一小时调一次支付的提现结果查询去更新订单状态
			status = payProcess;
			payOrder.setStatus(status);
			payOrderService.doUpdate(payOrder);
			synchPayOrderWithdraw(orderId,amount,type,status,aliasCode,payBank,cardNo,memberCode,reqBizProj,remark,reqSource);
			resMap.put(code, withdrawRes.get(code));
			resMap.put(desc, withdrawRes.get(desc));
		}
		return resMap;
	}

	private void synchPayOrderWithdraw(String orderId,String amount, String type,String status, String aliascode, String paybank,
									   String bankcardno, String memberCode,String reqBizProj, String remark,String reqSource) throws Exception{
		PayOrderWithdraw payOrderWithdraw = new PayOrderWithdraw();
		payOrderWithdraw.setOrderId(orderId);
		payOrderWithdraw.setAmount(amount);
		payOrderWithdraw.setReqSource(reqSource);
		payOrderWithdraw.setAliascodeType(type);
		payOrderWithdraw.setStatus(status);
		payOrderWithdraw.setAliascode(aliascode);
		payOrderWithdraw.setPayBank(paybank);
		payOrderWithdraw.setBankCardNo(bankcardno);
		payOrderWithdraw.setMemberCode(memberCode);
		payOrderWithdraw.setReqBizProj(reqBizProj);
		payOrderWithdraw.setRemark(remark);

		PayOrderWithdrawDTO payOrderWithdrawDTO = new PayOrderWithdrawDTO();
		payOrderWithdrawDTO.setOrderId(orderId);
		List<PayOrderWithdraw> payOrderWithdrawList = payOrderWithdrawService.doQuery(payOrderWithdrawDTO,Integer.MIN_VALUE,Integer.MAX_VALUE);
		if (payOrderWithdrawList.size() == 0) {
			payOrderWithdrawService.doCreate(payOrderWithdraw);
		} else {
			payOrderWithdraw.setId(payOrderWithdrawList.get(0).getId());
			payOrderWithdrawService.doUpdate(payOrderWithdraw);
		}
	}

	private void sendMqRefund(String orderID,String orderAmount,String reqBizProj,String reqSource,String memberCode,String refundType){
		Map<String,Object> param = new HashMap<>();
		param.put("orderID",orderID);
		param.put("orderAmount",orderAmount);
		param.put("reqBizProj",reqBizProj);
		param.put("reqSource",reqSource);
		param.put("memberCode",memberCode);
		param.put("refundType",refundType);
		queueSender.sendMap("payOrderRefundDestination", param);
	}
}
