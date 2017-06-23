package com.ncvas.payment.service;

import java.util.Map;

/**
 * 市民卡申领
 * Created by fancz on 2017/6/20.
 */
public interface ApplyForCardService {
    //市民卡申领
    Map<String,Object> applyForCard(Map<String, String> paraMap) throws Exception;
}
