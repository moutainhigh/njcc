package com.ncvas.handler.njcc;

import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeResultVO;
import com.ncvas.entity.ResponeVO;
import com.ncvas.handler.AbstractApiHandler;
import com.ncvas.handler.ApiHandlerCallback;
import com.ncvas.handler.TokenCheckable;
import com.ncvas.njcc.service.NjccMemberService;
import com.ncvas.payment.entity.AccountLoginDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by caiqi on 2016/11/29.
 * 是否已设置支付密码
 */
@Service
public class AccountCheckPayPwdHandler extends AbstractApiHandler implements TokenCheckable {

    @Autowired
    private NjccMemberService njccMemberService;
    private AccountLoginDTO accountLoginDTO;
    private static final Logger logger = Logger.getLogger(AccountCheckPayPwdHandler.class);
    @Override
    public ResponeVO doHandler(Object params) {
        final AccountCheckPayPwdParams accountCheckPayPwdParams = (AccountCheckPayPwdParams) params;
        return super.buildCallback(new ApiHandlerCallback() {
            @Override
            public ResponeVO callback() throws Exception {
                Map<String,Object> result = njccMemberService.accountCheckPayPwd(accountCheckPayPwdParams.getMemberCode(),accountCheckPayPwdParams.getAliasCode());
                if(result!=null){
                    if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals((String)result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                        logger.info("njcc||返回的参数为：" + result);
                        return new ResponeResultVO(result);
                    }else if(!StringUtil.isEmpty((String)result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                        logger.info("njcc||code为:" + result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()) + ", desc为:" + PayPlatformEnum.RESPONSE_KEY_DESC.getCode());
                        return new ResponeVO(Integer.parseInt(PayPlatformEnum.PAY_RESPONSE_ERROR_CODE.getCode()), (String)result.get(PayPlatformEnum.RESPONSE_KEY_DESC.getCode()));
                    }
                }
                return RESP_SERVICE_BUSY_ERROR;
            }
        });
    }

    @Override
    public void checkRequestParams(Object params) throws ApiMessageException {

        AccountCheckPayPwdParams rparams = (AccountCheckPayPwdParams) params;
        logger.info("njcc||是否已设置支付密码reqParam："+this.getClass().getSimpleName()+"======>>"+rparams.toString());
        if (isBlank(rparams.getMemberCode()) || isBlank(rparams.getAliasCode())) {
            throw new ApiMessageException(RESP_PARAMS_ERROR);
        }
    }

    @Override
    public Class<?> getRequestParams() {
        return AccountCheckPayPwdParams.class;
    }

    @Override
    public void setMember(AccountLoginDTO user) {
        this.accountLoginDTO = user;
    }

    public final static class AccountCheckPayPwdParams {
        private String memberCode;
        private String aliasCode;

        public String getMemberCode() {
            return memberCode;
        }

        public void setMemberCode(String memberCode) {
            this.memberCode = memberCode;
        }

        public String getAliasCode() {
            return aliasCode;
        }

        public void setAliasCode(String aliasCode) {
            this.aliasCode = aliasCode;
        }

        @Override
        public String toString() {
            return "AccountCheckPayPwdParams{" +
                    "memberCode='" + memberCode + '\'' +
                    ", aliasCode='" + aliasCode + '\'' +
                    '}';
        }
    }
}
