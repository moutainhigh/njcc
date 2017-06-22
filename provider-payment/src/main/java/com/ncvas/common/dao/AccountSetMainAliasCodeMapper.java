package com.ncvas.common.dao;


import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountSetMainAliasCode;
import com.ncvas.payment.entity.AccountSetMainAliasCodeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by caiqi on 2016/11/25.
 */
public interface AccountSetMainAliasCodeMapper extends BaseMapper<AccountSetMainAliasCode> {

    public List<AccountSetMainAliasCode> findByMemberCode(@Param("dto") AccountSetMainAliasCodeDTO accountSetMainAliasCodeDTO);
}
