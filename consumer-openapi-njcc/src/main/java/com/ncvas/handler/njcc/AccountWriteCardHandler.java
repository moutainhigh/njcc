package com.ncvas.handler.njcc;

import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeResultVO;
import com.ncvas.entity.ResponeVO;
import com.ncvas.handler.AbstractApiHandler;
import com.ncvas.handler.ApiHandlerCallback;
import com.ncvas.njcc.service.NjccOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Created by xiecaixia on 2017-4-25.
 * 写卡渠道
 */
@Service
public class AccountWriteCardHandler extends AbstractApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccountWriteCardHandler.class);

    @Autowired
    private NjccOrderService njccOrderService;

    @Override
    public ResponeVO doHandler(Object params) {
        final AccountWriteCardParams accountWriteCardParams = (AccountWriteCardParams) params;
        return super.buildCallback(new ApiHandlerCallback() {
            @Override
            public ResponeVO callback() throws Exception {
                Map<String, Object> result = njccOrderService.saveWriteCardAndUpdateStatus(accountWriteCardParams.getOrderId(),
                        accountWriteCardParams.getToken(), accountWriteCardParams.getCardNo(), accountWriteCardParams.getStatus());
                logger.info("njcc||返回的参数为：" + result);
                return new ResponeResultVO(result);
            }
        });
    }

    @Override
    public void checkRequestParams(Object params) throws ApiMessageException {
        AccountWriteCardParams rparams = (AccountWriteCardParams) params;
        logger.info("njcc||写卡渠道传递reqParam："+this.getClass().getSimpleName()+"======>>"+rparams.toString());
    }

    @Override
    public Class<?> getRequestParams() {
        return AccountWriteCardParams.class;
    }

    public final static class AccountWriteCardParams {
        private String orderId;  /**订单号*/
        private String cardNo;   /**卡号*/
        private String token;
        private String status;   /**状态*/

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "AccountWriteCardParams{" +
                    "orderId='" + orderId + '\'' +
                    ", cardNo='" + cardNo + '\'' +
                    ", token='" + token + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}
