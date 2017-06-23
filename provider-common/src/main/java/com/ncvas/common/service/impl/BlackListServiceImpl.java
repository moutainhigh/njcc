package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.BlackListMapper;
import com.ncvas.common.entity.BlackList;
import com.ncvas.common.entity.BlackListDTO;
import com.ncvas.common.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lc_xin.
 * @date 2016年9月28日
 */
@Service("blackListService")
public class BlackListServiceImpl extends AbstractBaseService<BlackList> implements BlackListService {
	
	@Autowired
	private BlackListMapper blackListMapper;
	
	@Override
	protected BaseMapper<BlackList> getBaseMapper() {
		return blackListMapper;
	}

	@Override
	protected Class<? extends ValueObject> getEntityDTOClass() {
		return BlackListDTO.class;
	}
}
