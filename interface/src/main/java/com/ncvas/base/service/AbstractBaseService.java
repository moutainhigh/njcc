package com.ncvas.base.service;

import com.ncvas.base.entity.Page;
import com.ncvas.base.entity.ValueObject;

import java.util.List;

public abstract class AbstractBaseService<E extends ValueObject> implements BaseService<E> {
	
	@Override
	public E doCreate(E entity) {
		getBaseMapper().add(entity);
		return entity;
	}

	@Override
	public int doUpdate(E entity) {
		return getBaseMapper().update(entity);
	}

	@Override
	public int doRemove(String id) {
		return getBaseMapper().delete(id);
	}

	@Override
	public int doRemoves(String[] ids) {
		return getBaseMapper().deletes(ids);
	}

	@Override
	public List<E> doQuery(E entityDto, int beginNum, int endNum) {
		return getBaseMapper().query(entityDto, beginNum, endNum);
	}

	@Override
	public E doView(String id) {
		return getBaseMapper().get(id);
	}
	
	@Override
	public Page<E> getPageModel(E entityDto, int beginNum, int endNum) {
		Page<E> page = new Page<E>();
		page.setTotal(getBaseMapper().count(entityDto));
		page.setRows(getBaseMapper().query(entityDto, beginNum, endNum));
		return page;
	}
	
	@Override
	public E doQuery(E entityDto) {
		List<E> list = getBaseMapper().query(entityDto, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	protected abstract BaseMapper<E> getBaseMapper();
	
	protected abstract Class<? extends ValueObject> getEntityDTOClass();

}
