package com.ncvas.common.dao;

import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountLogin;
import com.ncvas.payment.entity.AccountLoginDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by caiqi on 2016/11/21.
 */
public interface AccountLoginMapper extends BaseMapper<AccountLogin> {

    public List<AccountLogin> getAccountLogin(@Param("dto") AccountLoginDTO accountLoginDTO);
}
