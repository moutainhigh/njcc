package com.ncvas.controller.base;

import com.ncvas.entity.ResponeVO;

public class ApiMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3920996272933565062L;
	
	private ResponeVO responeVO;

	public ApiMessageException(ResponeVO responeVO) {
		this.responeVO = responeVO;
	}
	
	public ApiMessageException(ResponeVO responeVO, Throwable throwable) {
		super(throwable);
		this.responeVO = responeVO;
	}

	public ResponeVO getResponeVO() {
		return responeVO;
	}

}
