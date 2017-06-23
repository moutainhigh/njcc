package com.ncvas.base.service;


import com.ncvas.base.entity.Page;
import com.ncvas.base.entity.ValueObject;

import java.util.List;

public interface BaseService<E extends ValueObject> {

	/**
	 * 创建记录
	 * @param entity
	 * @return
	 */
	public E doCreate(E entity);
	/**
	 * 更新记录
	 * @param entity
	 */
	public int doUpdate(E entity);
	/**
	 * 根据ID查询记录
	 * @param id
	 * @return
	 */
	public E doView(String id);
	/**
	 * 根据ID删除记录
	 * @param id
	 */
	public int doRemove(String id);
	/**
	 * 根据ID数组删除记录
	 * @param ids
	 */
	public int doRemoves(String[] ids);
	/**
	 * 查询记录列表
	 * @param entityDto
	 * @param beginNum
	 * @param endNum
	 * @return
	 */
	public List<E> doQuery(E entityDto, int beginNum, int endNum);
	/**
	 * 查询记录页
	 * @param entityDto
	 * @param beginNum
	 * @param endNum
	 * @return
	 */
	public Page<E> getPageModel(E entityDto, int beginNum, int endNum);
	/**
	 * 查询记录
	 * @param entityDto
	 * @return
	 */
	public E doQuery(E entityDto);
	
}
