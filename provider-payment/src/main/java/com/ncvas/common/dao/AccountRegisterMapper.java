package com.ncvas.common.dao;


import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountRegister;
import com.ncvas.payment.entity.AccountRegisterDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountRegisterMapper extends BaseMapper<AccountRegister> {

    int reportsRegisterPeople(@Param("dto") AccountRegisterDTO accountRegisterDTO);

    public List<AccountRegisterDTO> reportsPeopleAndType(@Param("dto") AccountRegisterDTO accountRegisterDTO);

    List<AccountRegister> queryReqBizType();

}
