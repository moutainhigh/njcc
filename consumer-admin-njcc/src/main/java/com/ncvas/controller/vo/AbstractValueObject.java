package com.ncvas.controller.vo;

import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.entity.ValueObjectDTO;

/**
 * 抽象VO
 * @author twy
 *
 * @param <E>
 */
public interface AbstractValueObject<E extends ValueObject> {

	public abstract ValueObjectDTO toEntityDto();
	
	public abstract E toEntity();
	
}
