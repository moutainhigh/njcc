package com.ncvas.handler;

import com.ncvas.entity.RequestVO;
import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.ResponeVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractApiHandler implements ApiHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractApiHandler.class);
	
	/**
	 * 图片上传请求路径
	 */
	@Value("#{configProperties['requestPath']}")
	public String  imageRequestPathStr;
	
	public boolean isBlank(String str) {
		return StringUtils.isBlank(str);
	}
	/**
	 * 创建回调
	 * @param callback
	 * @return
	 */
	public ResponeVO buildCallback(ApiHandlerCallback callback) {
		ResponeVO respvo = null;
		try {
			respvo = callback.callback();
			if (respvo == null) respvo = RESP_UNKNOWN_ERROR;
		} catch (ApiMessageException e) {
			logger.warn("AbstractApiHandler#buildCallback" + e.getResponeVO());
			respvo = e.getResponeVO();
		} catch (Exception e) {
			logger.warn("AbstractApiHandler#buildCallback", e);
			respvo = RESP_SERVICE_BUSY_ERROR;
			e.printStackTrace();
		}
		return respvo;
	}

	public JSONObject erminalCodeToJsonObject(RequestVO requestVO) {
		if(isBlank(requestVO.getErminalCode())) {
			return null;
		}
		return JSONObject.fromObject(requestVO.getErminalCode());
	}

	public String resetImageReqestUrl(String url) {
		if (isBlank(url)) return url;
		if (url.startsWith("http") || url.startsWith("HTTP") || url.toLowerCase().startsWith("http")) {
			return url;
		}
		return imageRequestPathStr + url;
	}
	
	/**
	 * 根据原图URL成小图URL(-source.xxx修改成-medium.jpg)
	 * @param sourceUrlStr
	 * @return
	 */
	public String toMinPicUrlStr(String sourceUrlStr) {
		String minPicUrl = sourceUrlStr;
		if (minPicUrl.indexOf("source") > 0) {
			minPicUrl = minPicUrl.substring(0, minPicUrl.indexOf("source")) + "medium.jpg";
		}
		return minPicUrl;
	}
}
