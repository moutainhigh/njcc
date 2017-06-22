package com.ncvas.payment.service;

import com.ncvas.base.service.BaseService;
import com.ncvas.payment.entity.AccountWriteCard;

import java.util.List;

/**
 * Created by xiecaixia on 2017-4-25.
 */
public interface AccountWriteCardService extends BaseService<AccountWriteCard> {

    AccountWriteCard getByOrderId(String orderId);

    List<String> queryGroupByAliascode(int beginNum, int endNum);

    Integer countGroupByAliascode();
}
