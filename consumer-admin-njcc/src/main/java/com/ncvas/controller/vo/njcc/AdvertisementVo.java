package com.ncvas.controller.vo.njcc;

import com.ncvas.base.entity.ValueObjectDTO;
import com.ncvas.common.entity.Advertisement;
import com.ncvas.common.entity.AdvertisementDTO;
import com.ncvas.controller.vo.AbstractValueObject;
import org.springframework.beans.BeanUtils;

/**
 * @author lc_xin.
 * @date 2016年6月16日
 */
public class AdvertisementVo extends AdvertisementDTO implements AbstractValueObject<Advertisement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8691242258716636064L;

	@Override
	public ValueObjectDTO toEntityDto() {
		AdvertisementDTO dto = new AdvertisementDTO();
		BeanUtils.copyProperties(this, dto);
		return dto;
	}

	@Override
	public Advertisement toEntity() {
		return this;
	}
}
