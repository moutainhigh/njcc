package com.ncvas.payment.service.biz;

import java.util.Map;

/**
 * Created by xiecaixia on 2017-4-25.
 */
public interface AccountWriteCardBizService {
    /**
     * 新增写卡渠道同时更新预约订单表
     * @param orderId 订单号
     * @param token
     * @param cardNo  卡号
     * @param status 状态
     * @throws Exception
     */
    Map<String, String> saveWriteCardAndUpdateStatus(String orderId, String token, String cardNo, String status) throws Exception;
}
