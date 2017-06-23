package com.ncvas.handler.fin;

import com.ncvas.common.utils.StringUtil;
import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeResultVO;
import com.ncvas.entity.ResponeVO;
import com.ncvas.handler.AbstractApiHandler;
import com.ncvas.handler.ApiHandlerCallback;
import com.ncvas.njcc.service.NjccJqyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年5月27日
 * 加密内容接口
 */
@Service
public class AesEncryptHandler extends AbstractApiHandler {
	
	private static final Logger logger =Logger.getLogger(AesEncryptHandler.class);
	
	@Autowired
	private NjccJqyService jqyService;

	@Override
	public ResponeVO doHandler(Object params) {
		final AesEncryptParams rparams = (AesEncryptParams) params;
		return super.buildCallback(new ApiHandlerCallback() {
			
			@Override
			public ResponeVO callback() throws Exception {
				String str = jqyService.aesEncrypt(rparams.getAesStr());
				if(!StringUtil.isEmpty(str)){
					Map<String, Object> result = new HashMap<String, Object>();
					logger.info("fin||伽乾益加密后内容"+str);
					result.put("aesEncrypt",str);
					return new ResponeResultVO(result);
				}
				return RESP_SERVICE_BUSY_ERROR;
			}
		});
	}

	@Override
	public void checkRequestParams(Object params) throws ApiMessageException {
		AesEncryptParams rparams = (AesEncryptParams) params;
		logger.info("fin||加密内容接口传递的参数为："+rparams.toString());
		// 校验需要加密的参数
		if(isBlank(rparams.getAesStr())){
			throw new ApiMessageException(RESP_PARAMS_ERROR);
		}
	}

	@Override
	public Class<?> getRequestParams() {
		return AesEncryptParams.class;
	}
	
	public static class AesEncryptParams{
		private String aesStr;

		public String getAesStr() {
			return aesStr;
		}

		public void setAesStr(String aesStr) {
			this.aesStr = aesStr;
		}
	}
}
