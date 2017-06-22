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
import com.ncvas.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 预约订单取消
 * @author lc_xin.
 *
 */
@Service
public class AccountQuickPayCancelHandler extends AbstractApiHandler implements TokenCheckable {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountQuickPayCancelHandler.class);

	@Autowired
	private NjccOrderService njccOrderService;
	private AccountLoginDTO accountLoginDTO;
	@Override
	public ResponeVO doHandler(Object params) {
		final AccountQuickPayCancelParams accountQuickPayCancelParams = (AccountQuickPayCancelParams) params;
		return super.buildCallback(new ApiHandlerCallback() {
			@Override
			public ResponeVO callback() throws Exception {

				Map<String,Object> result = njccOrderService.accountQuickPayCancel(accountLoginDTO.getLoginName(), accountQuickPayCancelParams.getOrderNo());
				if(result!=null){
					if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals((String)result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
						logger.info("njcc||返回的参数为：" + result);
						return new ResponeResultVO(result);
					}else if(!StringUtil.isEmpty((String)result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
						logger.info("njcc||code为:" + result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()) + ", desc为:" + result.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
						return new ResponeVO(Integer.parseInt(PayPlatformEnum.PAY_RESPONSE_ERROR_CODE.getCode()), (String)result.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
					}
				}
				return RESP_SERVICE_BUSY_ERROR;
			}
		});
	}

	@Override
	public void checkRequestParams(Object params) throws ApiMessageException {
		AccountQuickPayCancelParams rparams = (AccountQuickPayCancelParams) params;
		logger.info("njcc||AccountQuickPayCancelHandler||撤销预约订单传递的参数为："+rparams.toString());
		if (isBlank(rparams.getOrderNo())) {
			throw new ApiMessageException(RESP_PARAMS_ERROR);
		}
	}

	@Override
	public Class<?> getRequestParams() {
		return AccountQuickPayCancelParams.class;
	}

	public final static class AccountQuickPayCancelParams {
		public String orderNo;

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		@Override
		public String toString() {
			return "AccountQuickPayCancelParams{" +
					"orderNo='" + orderNo + '\'' +
					'}';
		}
	}

	@Override
	public void setMember(AccountLoginDTO user) {
		this.accountLoginDTO = user;
	}
}
