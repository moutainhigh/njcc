package com.ncvas.common.service.impl;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.service.AbstractBaseService;
import com.ncvas.base.service.BaseMapper;
import com.ncvas.common.dao.AdvertisementMapper;
import com.ncvas.common.entity.Advertisement;
import com.ncvas.common.entity.AdvertisementDTO;
import com.ncvas.common.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lc_xin.
 * @date 2016年6月16日
 */
@Service("advertisementService")
public class AdvertisementServiceImpl extends AbstractBaseService<Advertisement> implements AdvertisementService {
	
	@Autowired
	private AdvertisementMapper advertisementMapper;
	
	@Override
	protected BaseMapper<Advertisement> getBaseMapper() {
		return advertisementMapper;
	}

	@Override
	protected Class<? extends ValueObject> getEntityDTOClass() {
		return AdvertisementDTO.class;
	}
}
