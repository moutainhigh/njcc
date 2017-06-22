package com.ncvas.common.dao;

import com.ncvas.base.service.BaseMapper;
import com.ncvas.payment.entity.AccountLoginHistory;
import com.ncvas.payment.entity.AccountLoginHistoryDTO;
import org.apache.ibatis.annotations.Param;

/**
 * @author lc_xin.
 * @date 2016年6月12日
 */
public interface AccountLoginHistroyMapper extends BaseMapper<AccountLoginHistory> {
    public int reportsPeople(@Param("dto") AccountLoginHistoryDTO accountLoginHistoryDTO);
}
