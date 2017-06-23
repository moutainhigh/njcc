package com.ncvas.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.ncvas.base.exception.AnjiePayException;
import com.ncvas.common.mq.queue.send.QueueSender;
import com.ncvas.payment.entity.PayPlatformConfig;
import com.ncvas.payment.enums.CharsetTypeEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.enums.PaySerCode;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.payment.service.HessianInvokeService;
import com.ncvas.payment.service.TradeDataSingnatureService;
import com.ncvas.payment.utils.HttpTools;
import com.ncvas.util.IpUtil;
import com.pay.inf.enums.SystemCodeEnum;
import com.pay.inf.params.HessianInvokeParam;
import com.pay.inf.service.SysTraceNoService;
import com.pay.inf.utils.HessianInvokeHelper;
import com.pay.util.JSonUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
@Service("anjiePayService")
public final class AnjiePayServiceImpl implements AnjiePayService {
	private static final Logger logger = LoggerFactory.getLogger(AnjiePayServiceImpl.class);
	@Autowired
	private HessianInvokeService payCoreServer;
	@Autowired
	private TradeDataSingnatureService tradeDataSingnatureService;
	@Autowired
	private PayPlatformConfig payPlatformConfig;
	@Resource
	QueueSender queueSender;

	private Map payReq(PaySerCode sc,Map reqMap) throws Exception{
		String dataMsg = JSonUtil.toJSonString(reqMap);
		logger.info("pay bussi code: "+sc.getCode()+"---"+sc.getDesc()+"--reqParam:"+ dataMsg);
//		加签
		String signMsg = "";
		signMsg = tradeDataSingnatureService.genSignByRSA(dataMsg, CharsetTypeEnum.UTF8);
		HessianInvokeParam param = HessianInvokeHelper.processRequest(dataMsg);
		String sysTraceNo = SysTraceNoService
				.generateSysTraceNo(SystemCodeEnum.VAS.getCode());
		String result = payCoreServer.invokeForRSA(sc.getCode(),
				sysTraceNo, SystemCodeEnum.ACCOUNT.getCode(),
				SystemCodeEnum.ACCOUNT.getCode(),
				SystemCodeEnum.ACCOUNT.getVersion(), param.getDataLength(),
				param.getMsgCompress(), param.getDataMsg(),signMsg);

		param.parse(result);
		HessianInvokeHelper.processResponse(param);
		result = param.getDataMsg();
		logger.info("pay bussi resp code: "+sc.getCode()+"---"+sc.getDesc()+"--reqParam:"+JSonUtil.toJSonString(reqMap)+"--respParam:"+result);
		Map<String, JSONObject> res = JSON.parseObject(result, new HashMap<String, JSONObject>().getClass());
		//记录异常日志
		Map<String,String> resMap = JSON.parseObject(result, new HashMap<String, String>().getClass());
		if (!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
			//获取Linux服务器的ip地址
			//String linuxUrl = getLinuxLocalIp();
			String linuxUrl = "";
			try {
//				linuxUrl = InetAddress.getLocalHost().getHostAddress();
				linuxUrl = IpUtil.getLocalIP();
				logger.info("获取服务器ip："+linuxUrl);
			}catch (Exception e){
				logger.info("获取服务器ip："+e.getMessage());
			}
			recordException(sc.getCode(),JSonUtil.toJSonString(reqMap),result,String.valueOf(resMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode())),"",linuxUrl);
		}
		if(res.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode())==null&&res.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode())==null
				||"".equals(res.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))||"".equals(res.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()))){
			Map<String,Object> errorRes = new HashMap<>();
			errorRes.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),"3");
			errorRes.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),PayPlatformEnum.RESP_SERVICE_BUSY_ERROR.getDescription());
			return errorRes;
		}
		return res;
	}
	//	post 去支付网关
	private Map paySend(String requrl,Map reqMap)throws AnjiePayException {
		InputStream inputStream = null;
		try {
			String dataMsg = beanAppendToStr(reqMap);
			//验签
			String signMsg = "";
			signMsg = tradeDataSingnatureService.genSignByRSA(dataMsg, CharsetTypeEnum.UTF8);
			//字符拼装
			String str = dataMsg + "&signMsg=" + signMsg;
			logger.info("pay bussi url: \""+requrl+"\"--reqParam:"+ dataMsg);
//			String result = new HttpTools().doPostQueryCmd(requrl, str);
			String result = new HttpTools().doPostQueryCmd(requrl, str,Integer.parseInt(payPlatformConfig.getConnectTimeout()),Integer.parseInt(payPlatformConfig.getReadTimeout()));
//			result = "{'"+PayPlatformEnum.RESPONSE_KEY_CODE.getCode()+"':'"+PayPlatformEnum.RESPONSE_SUCCESS.getCode()+"'}";
			Map<String, JSONObject> res = JSON.parseObject(result, new HashMap<String, JSONObject>().getClass());
			logger.info("pay bussi resp url: \""+requrl+"\"--reqParam:"+dataMsg+"--respParam:"+result);
			//记录异常日志
			Map<String,String> resMap = JSON.parseObject(result, new HashMap<String, String>().getClass());
			if (!PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
				//获取Linux服务器的ip地址
				//String linuxUrl = getLinuxLocalIp();
				String linuxUrl = "";
				try {
//				linuxUrl = InetAddress.getLocalHost().getHostAddress();
					linuxUrl = IpUtil.getLocalIP();
					logger.info("获取服务器ip："+linuxUrl);
				}catch (Exception e){
					logger.info("获取服务器ip："+e.getMessage());
				}
				recordException("",JSonUtil.toJSonString(reqMap),result,String.valueOf(resMap.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode())),"",linuxUrl);
			}
			if(res.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode())==null&&res.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode())==null
					||"".equals(res.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))||"".equals(res.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()))){
				Map<String,Object> errorRes = new HashMap<>();
				errorRes.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),"3");
				errorRes.put(PayPlatformEnum.RESPONSE_KEY_DESC.getCode(),PayPlatformEnum.RESP_SERVICE_BUSY_ERROR.getDescription());
				return errorRes;
			}
			return res;
		} catch (AnjiePayException e) {
			throw e;
		}  catch (IOException e) {
			throw AnjiePayException.EX_NET_ERROR;
		} catch (Exception e) {
			throw AnjiePayException.EX_NET_ERROR;
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (Exception e2) {
			}
		}
	}

	//记录异常日志
	private void recordException(String sercode,String reqParam,String respParam,
								 String description,String reqUrl,String linuxUrl) throws Exception{

		Map<String, String> map = new HashMap<>();
		map.put("sercode",sercode);
		map.put("reqParam",reqParam);
		map.put("respParam",respParam);
		map.put("description",description);
		map.put("reqUrl",reqUrl);
		map.put("linuxUrl",linuxUrl);
		queueSender.sendMap("payExceptionDestination",map);
	}

	private static String beanAppendToStr(Map reqMap) throws Exception {
		StringBuffer buffer = new StringBuffer();
		try {
			Iterator ittmp=reqMap.entrySet().iterator();
			while (ittmp.hasNext()){
				Map.Entry e=(Map.Entry)ittmp.next();
				String key = (String)e.getKey();
				String val = (String)e.getValue();
				buffer.append("&").append(key).append("=").append(val);
			}
		} catch (Exception e) {
			throw e;
		}
		return buffer.length() > 0 ? buffer.substring(1) : null;
	}
	public Map<String,Object> accountRegister(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_REGISTER,reqMap);
		return resMap;
	}
	@Override
	public Map<String,Object> accountLogin(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_LOGIN,reqMap);
		return resMap;
	}
	public Map<String,Object> accountFindPassword(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_FIND_PASSWORD,reqMap);
		return resMap;
	}
	public Map<String,Object> accountSetPayPassword(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_SET_PAY_PASSWORD,reqMap);
		return resMap;
	}
	public Map<String,Object> accountModifyPassword(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_MODIFY_PASSWORD,reqMap);
		return resMap;
	}
	public Map<String,Object> accountCertification(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_CERTIFICATION,reqMap);
		return resMap;
	}
	public Map<String,Object> accountBindingQuickpaymentBankCard(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_BINDING_QUICKPAYMENT_BANK_CARD,reqMap);
		return resMap;
	}
	public Map<String,Object> accountQueryQuickpaymentBankCard(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_QUERY_QUICKPAYMENT_BANK_CARD,reqMap);
		return resMap;
	}
	@Override
	public Map<String,Object> accountUnBinDingQuickPayMentBankCard(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_UNBINDING_QUICKPAYMENT_BANK_CARD,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountGetCheckCode(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_GET_CHECKCODE,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountMemberinfo(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_MEMBERINFO,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountCheckCertification(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_CHECK_CERTIFICATION,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountSetupSecurityQuestion(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_SETUP_SECURITY_QUESTION,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountResetPwd(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_RESET_LOGIN_PWD,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountWithdraw(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_WITHDRAW,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountSetMobilePayPassword(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_SET_MOBILE_PAY_PASSWORD,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountQueryAliasCode(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_QUERY_ALIASCODE,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountCheckNjccCardList(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_CHECK_NJCCCARD_LIST,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountSetMainAliasCode(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_SET_MAIN_ALIASCODE,reqMap);
		return resMap;
	}
	@Override
	public Map<String, Object> accountBindNjccCard(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_BIND_NJCC_CARD,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountUnbindNjccCard(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_UNBIND_NJCC_CARD,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountCheckPayPwd(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_CHECK_PAY_PWD,reqMap);
		return resMap;
	}
	@Override
	public Map<String, Object> quickPay(Map reqMap) throws Exception {
		Map<String,Object> resMap = paySend(payPlatformConfig.getWebgateQuickPayApi(),reqMap);
//		Map<String,Object> resMap = paySend("http://172.16.2.58:8080/webgate/quickPayApi.htm",reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountQuickPayMentBankList(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_QUICKPAYMENT_BANK_LIST,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountQueryMainAliasCode(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_QUERY_MAIN_ALIASCODE,reqMap);
		return resMap;
	}

	public Map<String, Object> refund(Map reqMap) throws Exception {
		Map<String,Object> resMap = paySend(payPlatformConfig.getWebgateRefund(),reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountQuickPayOreder(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_QUICK_PAY_ORDER,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountRegisterNjccVirtual(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_REGISTER_NJCC_VIRTUAL,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountVerdictPayPassword(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_VERDICT_PAY_PASSWORD,reqMap);
		return resMap;
	}
	@Override
	public Map<String, Object> accountQuickPayCancel(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_QUICK_PAY_CANCEL,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> prepaidOrderInquiry(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.PREPAID_ORDER_INQUIRY,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> chargeAccQuickPayOrder(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.CHARGE_ACC_QUICK_PAY_ORDER,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountUsertransfer(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_USERTRANSFER,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> accountModifyMembetInfo(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.MODIFY_MEMBER_INFO,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> queryBusSubwayTransRd(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.QUERY_BUS_SUBWAY_TRANS_RD,reqMap);
		return resMap;
	}
	@Override
	public Map<String, Object> accountTransaction(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.ACCOUNT_TRANSACTION,reqMap);
		return resMap;
	}
	@Override
	public Map<String, Object> hisVerify(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.HIS_VERIFY,reqMap);
		return resMap;
	}
	@Override
	public Map<String, Object> queryHisAccountInfo(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.QUERY_HIS_ACC_INFO,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> queryHisTransRd(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.QUERY_HIS_TRANS_RD,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> hisAccountOpen(Map reqMap) throws Exception {
		Map<String,Object> resMap = payReq(PaySerCode.HIS_ACCOUNT_OPEN,reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> withdraw(Map reqMap) throws Exception {
		Map<String,Object> resMap = paySend(payPlatformConfig.getWebgateWithdrawApi(),reqMap);
//		Map<String,Object> resMap = paySend("http://172.16.0.68:8090/webgate/autoWithdraw.htm",reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> queryForApplyStatus(Map reqMap) throws Exception {
		Map<String, Object> resMap = payReq(PaySerCode.QUERY_FOR_APPLY_STATUS, reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> applyForCard(Map reqMap) throws Exception {
		Map<String, Object> resMap = payReq(PaySerCode.APPLY_FOR_CARD, reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> queryForApplyCertification(Map reqMap) throws Exception {
		Map<String, Object> resMap = payReq(PaySerCode.QUERY_FOR_APPLY_CERTIFICATION, reqMap);
		return resMap;
	}

	@Override
	public Map<String, Object> queryForWebsite(Map reqMap) throws Exception {
		Map<String, Object> resMap = payReq(PaySerCode.QUERY_FOR_WEBSITE,reqMap);
		return resMap;
	}
}
