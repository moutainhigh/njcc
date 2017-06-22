package com.ncvas.common.dao;

import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountWriteCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xiecaixia on 2017-4-25.
 */
public interface AccountWriteCardMapper extends BaseMapper<AccountWriteCard> {
    AccountWriteCard getByOrderId(String orderId);

    /**
     * 获取所有待写卡的支付卡号记录
     * @param beginNum
     * @param endNum
     * @return
     */
    List<String> queryGroupByAliascode(@Param("beginNum") int beginNum, @Param("endNum") int endNum);

    /**
     * 获取所有待写卡的支付卡号的条数
     * @return
     */
    Integer countGroupByAliascode();
}
