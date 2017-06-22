package com.ncvas.handler.njcc;

import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeResultVO;
import com.ncvas.entity.ResponeVO;
import com.ncvas.handler.AbstractApiHandler;
import com.ncvas.handler.ApiHandlerCallback;
import com.ncvas.handler.TokenCheckable;
import com.ncvas.njcc.service.NjccOrderService;
import com.ncvas.payment.entity.AccountLoginDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.enums.ReqBizTypeEnum;
import com.ncvas.util.AmountUtils;
import com.ncvas.util.MessyCodeCheck;
import com.ncvas.util.StringUtil;
import ocx.AESWithJCE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 快充预约接口
 * @author caiqi
 * @date 2016年12月19日
 */
@Service
public class AccountQuickPayOrderHandler extends AbstractApiHandler implements TokenCheckable {

	@Autowired
	private NjccOrderService njccOrderService;

	@Value("#{configProperties['passwordKey']}")
	private String passwordKey;
	private AccountLoginDTO dto;
	private static final Logger logger = LoggerFactory.getLogger(AccountQuickPayOrderHandler.class);
	
	@Override
	public ResponeVO doHandler(Object params) {
		final QuickPayOrderParams rparams = (QuickPayOrderParams) params;
		return super.buildCallback(new ApiHandlerCallback() {
			@Override
			public ResponeVO callback() throws Exception {
				String password = AESWithJCE.getResult(dto.getToken(), rparams.getPassword());
				if (StringUtil.isEmpty(password) || MessyCodeCheck.isMessyCode(password)) {
					return RESP_SYSTEM_ERROR;
				}

				Map<String, Object> result = njccOrderService.quickPayOrder(rparams.getOrderID(), rparams.getLoginName(),
						AmountUtils.changeY2F(rparams.getAmount()), rparams.getPayeeAliasCode(), password, rparams.getDeviceid(),
						rparams.getDevicename(), rparams.getDevicemac(), rparams.getDevicemodel(), rparams.getSourcetype() == "" ?"1":rparams.getSourcetype(), ReqBizTypeEnum.REQ_BIZ_TYPE_NJCC.getCode());
				if (result != null) {
					if (PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
						logger.info("njcc||返回的参数为：" + result);
						return new ResponeResultVO(result);
					}if (PayPlatformEnum.RESPONSE_ERROR.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))) {
						logger.info("njcc||返回的参数为：" + result);
						Map<String, String> errMap =new HashMap<String, String>();
						errMap.put("orderseq",String.valueOf(result.get("orderseq")));
						errMap.put("cdtalias",String.valueOf(result.get("cdtalias")));
						errMap.put("payalias",String.valueOf(result.get("payalias")));
						errMap.put("transamt",String.valueOf(result.get("transamt")));
						errMap.put("transtime",String.valueOf(result.get("transtime")));
						errMap.put("accbalance",String.valueOf(result.get("accbalance")));
						ResponeVO errRes = new ResponeResultVO(errMap);
						errRes.setResponseCode(5);
						errRes.setResponseDesc("此卡之前有一笔预约处理,请先处理,订单号："+String.valueOf(result.get("orderseq")));
						return errRes;
					} else {
						logger.info("njcc||code为:" + result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()) + ", desc为:" + result.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
						return new ResponeVO(Integer.parseInt(PayPlatformEnum.PAY_RESPONSE_ERROR_CODE.getCode()), (String) result.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
					}
				}
				return RESP_SERVICE_BUSY_ERROR;
			}
		});
	}

	@Override
	public void checkRequestParams(Object params) throws ApiMessageException {
		QuickPayOrderParams rparams = (QuickPayOrderParams) params;
		logger.info("njcc||快充预约申请的参数为："+rparams.toString());
		if (isBlank(rparams.getOrderID())||isBlank(rparams.getLoginName())||isBlank(rparams.getAmount())||
				isBlank(rparams.getPassword())||isBlank(rparams.getPayeeAliasCode())) {
			throw new ApiMessageException(RESP_PARAMS_ERROR);
		}
	}

	@Override
	public Class<?> getRequestParams() {
		return QuickPayOrderParams.class;
	}

	@Override
	public void setMember(AccountLoginDTO user) {
		this.dto = user;
	}

	public static class QuickPayOrderParams{
		private String orderID;
		private String loginName;
		private String amount;
		private String password;
		private String payeeAliasCode;
		private String deviceid;		 //设备唯一标识
		private String devicename;    //设备名称
		private String devicemac;		//设备MAC地址
		private String devicemodel; //设备型号
		private String sourcetype; //来源 1为智汇APP 2为手环

		public String getOrderID() {
			return orderID;
		}

		public void setOrderID(String orderID) {
			this.orderID = orderID;
		}

		public String getLoginName() {
			return loginName;
		}

		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPayeeAliasCode() {
			return payeeAliasCode;
		}

		public void setPayeeAliasCode(String payeeAliasCode) {
			this.payeeAliasCode = payeeAliasCode;
		}

		public String getDeviceid() {
			return deviceid;
		}

		public void setDeviceid(String deviceid) {
			this.deviceid = deviceid;
		}

		public String getDevicename() {
			return devicename;
		}

		public void setDevicename(String devicename) {
			this.devicename = devicename;
		}

		public String getDevicemac() {
			return devicemac;
		}

		public void setDevicemac(String devicemac) {
			this.devicemac = devicemac;
		}

		public String getDevicemodel() {
			return devicemodel;
		}

		public void setDevicemodel(String devicemodel) {
			this.devicemodel = devicemodel;
		}

		public String getSourcetype() {
			return sourcetype;
		}

		public void setSourcetype(String sourcetype) {
			this.sourcetype = sourcetype;
		}

		@Override
		public String toString() {
			return "QuickPayOrderParams{" +
					"orderID='" + orderID + '\'' +
					", loginName='" + loginName + '\'' +
					", amount='" + amount + '\'' +
					", password='" + password + '\'' +
					", payeeAliasCode='" + payeeAliasCode + '\'' +
					", deviceid='" + deviceid + '\'' +
					", devicename='" + devicename + '\'' +
					", devicemac='" + devicemac + '\'' +
					", devicemodel='" + devicemodel + '\'' +
					", sourcetype='" + sourcetype + '\'' +
					'}';
		}
	}

	private String fixCard(String targetCard){
		//		如果不足16位，前面补0
		StringBuffer sb = new StringBuffer();
		int cardLength = 16;
		for(int i=0;i<cardLength-targetCard.length();i++){
			sb.append("0");
		}
		targetCard = sb.toString()+targetCard;
		return targetCard;
	}
}
