package com.ncvas.handler.njcc;

import com.ncvas.common.utils.StringUtil;
import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeResultListVO;
import com.ncvas.entity.ResponeVO;
import com.ncvas.handler.AbstractApiHandler;
import com.ncvas.handler.ApiHandlerCallback;
import com.ncvas.handler.TokenCheckable;
import com.ncvas.njcc.service.NjccOrderService;
import com.ncvas.payment.entity.AccountLoginDTO;
import com.ncvas.payment.enums.PayPlatformEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xiecai on 2016/6/12.
 * 交易记录查询接口
 */
@Service
public class AccountTransactionHandler extends AbstractApiHandler implements TokenCheckable {

    private static final Logger logger = LoggerFactory.getLogger(AccountTransactionHandler.class);
    @Autowired
    private NjccOrderService njccOrderService;
    private AccountLoginDTO accountLoginDTO;

    @Override
    public ResponeVO doHandler(Object params) {
        final AccountTransactionParams rparams = (AccountTransactionParams) params;
        return super.buildCallback(new ApiHandlerCallback() {
            @Override
            public ResponeVO callback() throws Exception {
                //前端传过来的参数
                Map<String,Object> result = njccOrderService.accountTransaction(rparams.getAliasCode(), rparams.getYear() == null ? "" : rparams.getYear(),
                        rparams.getPageindex(), rparams.getPagesize(), rparams.getTranstype(), rparams.getClassify());
                if(result!=null){
                    if(PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals((String)result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
                        logger.info("njcc||返回的参数为：" + result);
                        List<Map<String, String>> rows = (List<Map<String, String>>)result.get("rows");
                        if("1".equals(rparams.getPageindex())&&rows.size()==0){
                            return new ResponeVO(30004, "无更多记录");
                        }else if(rows.size()==0) {
                            return new ResponeVO(30004, "全部加载完成");
                        }
                        return new ResponeResultListVO(result);
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
        AccountTransactionParams rparams = (AccountTransactionParams) params;
        logger.info("njcc||交易记录查询传递的参数为：" + rparams.toString());
        if (isBlank(rparams.getAliasCode()) || isBlank(rparams.getYear()) || isBlank(rparams.getPageindex()) || isBlank(rparams.getPagesize())) {
            throw new ApiMessageException(RESP_PARAMS_ERROR);
        }
    }

    @Override
    public Class<AccountTransactionParams> getRequestParams() {
        return AccountTransactionParams.class;
    }

    public static class AccountTransactionParams {
        private String aliasCode;    //卡面号
        private String year;         //年月
        private String pageindex;    //查询页数
        private String pagesize;     //单页记录数
        private String transtype;    //交易类型
        private String classify;     //交易子类

        public String getAliasCode() {
            return aliasCode;
        }

        public void setAliasCode(String aliasCode) {
            this.aliasCode = aliasCode;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getPageindex() {
            return pageindex;
        }

        public void setPageindex(String pageindex) {
            this.pageindex = pageindex;
        }

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }

        public String getTranstype() {
            return transtype;
        }

        public void setTranstype(String transtype) {
            this.transtype = transtype;
        }

        public String getClassify() {
            return classify;
        }

        public void setClassify(String classify) {
            this.classify = classify;
        }
    }

    @Override
    public void setMember(AccountLoginDTO user) {
        this.accountLoginDTO=user;
    }
}
