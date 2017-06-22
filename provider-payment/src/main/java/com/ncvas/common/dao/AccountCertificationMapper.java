package com.ncvas.common.dao;

import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountCertification;
import com.ncvas.payment.entity.AccountCertificationDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
public interface AccountCertificationMapper extends BaseMapper<AccountCertification> {

    public List<AccountCertification> findByIdNo(@Param("dto") AccountCertificationDTO accountCertificationDTO);
    AccountCertification findByLoginName(String loginName);
}
