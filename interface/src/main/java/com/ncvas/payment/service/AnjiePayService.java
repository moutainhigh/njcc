package com.ncvas.payment.service;

import java.util.Map;

public interface AnjiePayService {
	/**
	 * 注册（“10601”）
	 */
	public Map<String,Object> accountRegister(Map reqMap) throws Exception;
	/**
	 * 登录（“10602”）
	 */
	public Map<String,Object> accountLogin(Map reqMap) throws Exception;
	/**
	 * 找回密码（“10603”）
	 */
	public Map<String,Object> accountFindPassword(Map reqMap) throws Exception;
	/**
	 * 设置支付密码（“10604”）
	 */
	public Map<String,Object> accountSetPayPassword(Map reqMap) throws Exception;
	/**
	 * 修改密码（“10605”）
	 */
	public Map<String,Object> accountModifyPassword(Map reqMap) throws Exception;
	/**
	 * 实名认证（“10607”）
	 */
	public Map<String,Object> accountCertification(Map reqMap) throws Exception;
	/**
	 * 绑定快捷银行卡（“10610”）
	 */
	public Map<String,Object> accountBindingQuickpaymentBankCard(Map reqMap) throws Exception;
	/**
	 * 查询快捷银行卡（“10611”）
	 */
	public Map<String,Object> accountQueryQuickpaymentBankCard(Map reqMap) throws Exception;
	/**
	 * 查询快捷银行卡列表（“10612”）
	 */
	public Map<String,Object> accountQuickPayMentBankList(Map reqMap) throws Exception;
	/**
	 * 解绑快捷银行卡（“10613”）
	 */
	public Map<String,Object> accountUnBinDingQuickPayMentBankCard(Map reqMap) throws Exception;
	/**
	 * 获取短信验证码（“10614”）
	 */
	public Map<String,Object> accountGetCheckCode(Map reqMap) throws Exception;
	/**
	 * 查询会员信息（“10615”）
	 */
	public Map<String,Object> accountMemberinfo(Map reqMap) throws Exception;
	/**
	 * 判断会员是否实名认证（“10616”）
	 */
	public Map<String,Object> accountCheckCertification(Map reqMap) throws Exception;
	/**
	 * 设置安全问题（“10617”）
	 */
	public Map<String,Object> accountSetupSecurityQuestion(Map reqMap) throws Exception;
	/**
	 * 重置密码（“10618”）
	 */
	public Map<String,Object> accountResetPwd(Map reqMap) throws Exception;
	/**
	 * 提现（“10620”）
	 */
	public Map<String,Object> accountWithdraw(Map reqMap) throws Exception;
	/**
	 * 修改/设置手机支付密码（“10628”）
	 */
	public Map<String,Object> accountSetMobilePayPassword(Map reqMap) throws Exception;
	/**
	 * 查询智汇卡（“10635”）
	 */
	public Map<String,Object> accountQueryAliasCode(Map reqMap) throws Exception;
	/**
	 * 查询智汇卡列表（“10636”）
	 */
	public Map<String,Object> accountCheckNjccCardList(Map reqMap) throws Exception;
	/**
	 * 设置智汇主卡（“10637”）
	 */
	public Map<String,Object> accountSetMainAliasCode(Map reqMap) throws Exception;
	/**
	 * 绑定智汇卡（“10633”）
	 */
	public Map<String, Object> accountBindNjccCard(Map reqMap) throws Exception;
	/**
	 * 解绑智汇卡（“10634”）
	 */
	public Map<String, Object> accountUnbindNjccCard(Map reqMap) throws Exception;
	/**
	 * 是否已设置支付密码（“10638”）
	 */
	public Map<String,Object> accountCheckPayPwd(Map reqMap) throws Exception;
	/**
	 * 查询用户主卡信息（“10639”）
	 */
	public Map<String,Object> accountQueryMainAliasCode(Map reqMap) throws Exception;
	/**
	 * 校验智汇账户支付密码（“10640”）
	 */
	public Map<String,Object> accountVerdictPayPassword(Map reqMap) throws Exception;
	/**
	 * 开通虚拟智汇账户（“10641”）
	 */
	public Map<String,Object> accountRegisterNjccVirtual(Map reqMap) throws Exception;
	/**
	 * 智汇卡转值（“10643”）
	 */
	public Map<String,Object> accountUsertransfer(Map reqMap) throws Exception;
	/**
	 * 快捷支付
	 */
	public Map<String, Object> quickPay(Map reqMap) throws Exception;
	/**
	 * 退款
	 */
	public Map<String, Object> refund(Map reqMap) throws Exception;
	/**
	 * 快充预约("10644")
	 */
	public Map<String, Object> accountQuickPayOreder(Map reqMap) throws Exception;
	/**
	 * 快充预约撤销
	 */
	public Map<String, Object> accountQuickPayCancel(Map reqMap) throws Exception;
	/**
	 * 被充值卡快充预约订单查询（“10647”）
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> prepaidOrderInquiry(Map reqMap) throws Exception;

	/**
	 *扣款账户快充预约订单查询
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> chargeAccQuickPayOrder(Map reqMap) throws Exception;
	/**
	 * 修改会员信息（“10650”）
	 */
	public Map<String, Object> accountModifyMembetInfo(Map reqMap) throws Exception;
	/**
	 *查询公交、地铁交易记录（“10646”）
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryBusSubwayTransRd(Map reqMap) throws Exception;

	/**
	 *查询交易记录
	 */
	public Map<String, Object> accountTransaction(Map reqMap) throws Exception;
	/**
	 * 验证医疗一账通账户10651
	 */
	public Map<String,Object> hisVerify(Map reqMap) throws Exception;
	/**
	 * 医疗一账通查询账户信息（“10653”）
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryHisAccountInfo(Map reqMap) throws Exception;

	/**
	 * 医疗一账通查询交易记录（“10652”）
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryHisTransRd(Map reqMap) throws Exception;

	/**
	 *开通医疗一账通账户（“10654”）
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> hisAccountOpen(Map reqMap) throws Exception;
	/**
	 * 新版走网关提现
	 */
	public Map<String, Object> withdraw(Map reqMap) throws Exception;

	/**
	 * 市民卡申领进度查询(10671)
	 * @param reqMap
	 * @return
	 * @throws Exception
     */
	Map<String, Object> queryForApplyStatus(Map reqMap) throws Exception;

	/**
	 * 市民卡申领(10670)
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> applyForCard(Map reqMap) throws Exception;

	/**
	 * 市民卡申领资格查询(10672)
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryForApplyCertification(Map reqMap) throws Exception;

	/**
	 * 网点查询(10673)
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryForWebsite(Map reqMap) throws Exception;
}
