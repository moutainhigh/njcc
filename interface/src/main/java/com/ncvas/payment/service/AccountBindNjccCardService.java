package com.ncvas.payment.service;

import com.ncvas.payment.entity.AccountLoginDTO;

import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountBindNjccCardService {
//    存入缓存
    public Map<String, Object> redisSaveCardInfo(Map<String, Object> paraMap) throws Exception;
    public Map<String,Object> accountBindNjccCard(AccountLoginDTO accountLoginDTO, String aliasCode, String memberCode, String memberName, String socialCode, String registered, String cardCategory,String reqBizType) throws Exception;
    public Map<String,Object> accountUnBindNjccCard(String aliasCode, String memberCode) throws Exception;
}
