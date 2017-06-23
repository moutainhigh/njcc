package com.ncvas.payment.service.biz;



import java.util.Map;

public interface AnjiePayBizService {
	/**
	 * 绑定银行卡 支付一分钱
	 */
	public Map<String, Object> bindBankCard(String buyerMarked,String userName,String cardNo,String certNo
			,String mobile,String checkCode,String orderId,String memberCode,String checkMainAliasPwd
			,String registerVirtral,String password,String reqSource) throws Exception;
	/**
	 * 绑定银行卡 支付一分钱
	 */
	public Map<String, Object> bindBankCard2(String buyerMarked,String userName,String cardNo,String certNo
			,String mobile,String checkCode,String orderId,String memberCode,String checkMainAliasPwd
			,String registerVirtral,String password,String reqSource) throws Exception;
	/**
	 * 绑定银行卡 退款
	 */
	public void bindBankCardRefund(String reqSource) throws Exception;
	/**
	 * 解绑银行卡
	 */
	public Map<String, Object> unbindBankCard(String loginName,String memberCode,String bankAcct,String password) throws Exception;
	public Map<String, Object> unbindBankCard2(String loginName,String memberCode,String bankAcct,String password) throws Exception;
	/**
	 * 充值 智汇账户
	 */
	public Map<String, Object> accountDeposit(String buyerMarked,String userName,String cardNo,String certNo,String mobile,String orderId,String amount
			,String memberCode,String aliasCode,String aliasCodeType,String password, String reqSource) throws Exception;
	public Map<String, Object> accountDeposit2(String buyerMarked,String userName,String cardNo,String certNo,String mobile,String orderId,String amount
			,String memberCode,String aliasCode,String aliasCodeType,String password, String reqSource,String reqBizProj) throws Exception;

	/**
	 * 快充预约撤销
	 */
	public Map<String, Object> accountQuickPayCancel(String loginName,String orderNo) throws Exception;
	/**
	 * 快充预约
	 */
	public Map<String, Object> accountQuickPayOrder(Map<String, String> paraMap) throws Exception;
	/**
	 * 跳转网银
	 */
	public Map<String, Object> payWebView(String buyerMarked,String mobile,String orderId,String amount,String orgCode,String memberCode
			,String aliasCode,String aliasCodeType, String reqSource) throws Exception;
	/**
	 * 跳转网银(订单表重构的版本)
	 */
	public Map<String, Object> payWebView2(String buyerMarked,String mobile,String orderId,String amount,String orgCode,String memberCode
			,String aliasCode,String aliasCodeType, String reqSource) throws Exception;
	/*第三方网银回调*/
	public boolean processPayPlatformCallBack(String orderID,String stateCode,String amount);

	/*纯绑卡*/
	public Map<String, Object> bindBankCardPure(String buyerMarked, String userName, String cardNo, String certNo
			, String mobile, String checkCode, String orderId, String memberCode, String checkMainAliasPwd
			, String registerVirtral,String reqsource) throws Exception;
	/*实名绑定银行卡前判断一下此卡是否已经开通智汇账户*/
	public Map<String, Object> bindBankCardCheck(String buyerMarked, String userName, String cardNo, String certNo
			, String mobile, String checkCode, String orderId, String memberCode, String checkMainAliasPwd
			, String registerVirtral,String reqsource) throws Exception ;
	/**
	 * 提现
	 */
	public Map<String, Object> accountWithdraw(String userName,String cardNo,String orderId,String amount
			,String memberCode,String aliasCode,String password,String type,String reqSource,String reqBizProj,String payBank) throws Exception;

}
