package com.ncvas.controller;

import com.alibaba.fastjson.JSON;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.base.enums.VersionEnum;
import com.ncvas.controller.base.AbstractController;
import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.*;
import com.ncvas.handler.ApiHandler;
import com.ncvas.handler.TokenCheckable;
import com.ncvas.mq.queue.send.QueueSender;
import com.ncvas.payment.entity.AccountLoginDTO;
import com.ncvas.payment.enums.ReqBizTypeEnum;
import com.ncvas.util.DESUtil;
import com.ncvas.util.DateUtil;
import com.ncvas.util.EncryptUtils;
import com.ncvas.util.StringUtil;
import com.ncvas.utils.NcvasApplicationContextUtils;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/api")
public class ApiController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

	@Autowired
	private RedisTemplate redisTemplate;
	@Resource
	QueueSender queueSender;

	@Value("#{configProperties['encryptKey']}")
	private String encryptKey;

	@Value("#{configProperties['requestKey']}")
	private String requestKey;

	@Value("#{configProperties['backupSerCode']}")
	private String backupSerCode;

	@RequestMapping(value = "ncvas.html", method = RequestMethod.POST)
	public void service(RequestVO requestVO, HttpServletRequest request, HttpServletResponse response) {
		ResponeVO responeVO = null;
		// 禁用缓存
		response.setHeader("Cache-Control", "no-cache"); // or no-store
		response.setHeader("Pragrma", "no-cache");
		response.setDateHeader("Expires", 0);
		String erminalCode =null;
		try {
			String appVersion = request.getHeader("appVersion");
			logger.info("用户版本号为:"+appVersion);

			//判断版本号是否为空，空的话直接提示用户更新最新APP
			if (appVersion == null || appVersion.trim().length() == 0) {
				throw new ApiMessageException(new ResponeVO(6, "请更新最新版本APP!") );
			}

			try {
				requestVO.setSerCode(DESUtil.decode(requestVO.getSerCode(),requestKey));
				requestVO.setDataMsg(DESUtil.decode(requestVO.getDataMsg(),requestKey));
				erminalCode = DESUtil.decode(requestVO.getErminalCode(),requestKey);
				requestVO.setErminalCode(erminalCode);
			}catch (Exception e){
				logger.info("解密请求数据错误:"+e);
				throw new ApiMessageException(ApiHandler.RESP_SYSTEM_ERROR);
			}

			String serCode =  requestVO.getSerCode();
			String dataMsg =  requestVO.getDataMsg();
			logger.info("请求业务编码SerCode==============>>>"+serCode);
			logger.info("请求业务内容DataMsg==============>>>"+dataMsg);
			String[] serCodes = backupSerCode.split(";");
			Set<String> set = new HashSet<String>(Arrays.asList(serCodes));
			if(!set.contains(serCode)) {
				//如果有新版本直接返回给APP
				Map<String, Object> versionAppMap = verifyAppVersion(appVersion, erminalCode);
				if(versionAppMap !=null){
					throw new ApiMessageException(new ResponeResultVO(12,"请更新最新版本！",versionAppMap));
				}
			}

			if(!requestVO.verifySignature()) {
				// 签名信息错误
				throw new ApiMessageException(ApiHandler.RESP_SIGN_ERROR);
			}
			logger.info("业务编码:"+requestVO.getSerCode());
			ApiHandler handler = NcvasApplicationContextUtils.getApiHandlerClassMaps().get(requestVO.getSerCode());
			if (handler != null) {
				if (handler instanceof RequestVOAware) {
					((RequestVOAware) handler).setRequestVO(requestVO);
				}
				if(handler instanceof HttpServletRequestVO){
					((HttpServletRequestVO) handler).setHttpServletRequest(request);
				}
				if (handler instanceof TokenCheckable) {
					//校验版本
					if(requestVO.verifyVersion()) {
						if("1".equals(JSON.parseObject(requestVO.getErminalCode()).get("os"))){//安卓
							throw new ApiMessageException(new ResponeVO(6, "请前往各大应用市场更新版本!") );
						}
					}
					// 实现该接口，必须校验token
					if (requestVO.getToken() == null || requestVO.getToken().trim().length() == 0) {
						throw new ApiMessageException(ApiHandler.RESP_LOGOUT_ERROR);
					}
					String  Temp= (String)redisTemplate.opsForValue().get(requestVO.getToken() + RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
					if (Temp == null) {
						throw new ApiMessageException(ApiHandler.RESP_LOGOUT_ERROR);
					}
					Map<String,Object> json = JSONObject.fromObject(Temp);
					String token =(String)json.get("token");
					if (token == null) {
						throw new ApiMessageException(ApiHandler.RESP_LOGOUT_ERROR);
					}
					AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
					accountLoginDTO.setToken(token);
					accountLoginDTO.setMemberCode((String)json.get("memberCode"));
					accountLoginDTO.setLoginName((String)json.get("loginName"));
					accountLoginDTO.setBalance(new BigDecimal((Integer) json.get("balance")));
					accountLoginDTO.setLevelCode((String)json.get("levelCode"));
					accountLoginDTO.setMobile((String)json.get("mobile"));
					accountLoginDTO.setEmail((String)json.get("email"));
					accountLoginDTO.setCustName((String)json.get("custName"));
					accountLoginDTO.setMemberName((String)json.get("memberName"));
					accountLoginDTO.setIdNo((String)json.get("idNo"));
					accountLoginDTO.setAliascode((String)json.get("aliascode"));
					accountLoginDTO.setNickname((String)json.get("nickname"));
					/**（PS: 这里是否需要cookie, 先写上)*/
					accountLoginDTO.setCookie(EncryptUtils.aesDecrypt((String) json.get("cookie"),encryptKey));
					accountLoginDTO.setId((String)json.get("id"));
					((TokenCheckable) handler).setMember(accountLoginDTO);
					logger.info("token:"+token);

					Map<String,Object> param = new HashMap<>();
					param.put("loginid",accountLoginDTO.getMemberCode());
					param.put("dateStr", DateUtil.praseDate(new Date(),null));
					param.put("reqBizProj", ReqBizTypeEnum.REQ_BIZ_TYPE_HISS.getCode());//记录来源分类，此处是代表医疗APP总入口
					queueSender.sendMap("memberActiveDestination", param);
				}
				Object paramsObject = null;
				Class<?> paramsClass = ((ApiHandler) handler).getRequestParams();
				if (paramsClass != null) {
					if (requestVO.getDataMsg() == null) {
						throw new ApiMessageException(ApiHandler.RESP_PARAMS_ERROR);
					}
					JSONObject jsonObject = JSONObject.fromObject(requestVO.getDataMsg());
					try {
						paramsObject = JSONObject.toBean(jsonObject, paramsClass);
					} catch (Exception e) {
						logger.warn("ApiController#service " + e.getMessage());
						throw new ApiMessageException(ApiHandler.RESP_PARAMS_ERROR);
					}
				} else if (!StringUtils.isBlank(requestVO.getDataMsg())) {
					paramsObject = JSONObject.fromObject(requestVO.getDataMsg());
				}
				((ApiHandler) handler).checkRequestParams(paramsObject);
				responeVO = ((ApiHandler) handler).doHandler(paramsObject);
			} else {
				responeVO = ApiHandler.RESP_UNKNOWN_SERVICE_CODE_ERROR;
			}
		} catch (ApiMessageException e) {
			logger.warn("ApiController#service " + e.getResponeVO());
			responeVO = e.getResponeVO();
		} catch (JSONException e) {
			logger.warn("ApiController#service " + e.getMessage());
			responeVO = ApiHandler.RESP_JSON_FORMAT_ERROR;
		} catch (Throwable e) {
			logger.warn("ApiController#service", e);
			responeVO = ApiHandler.RESP_SERVICE_BUSY_ERROR;
		}
		if (responeVO == null) {
			responeVO = ApiHandler.RESP_UNKNOWN_ERROR;
		}
		this.responseMessageToClient(responeVO, response);
	}

	/**
	 * 请求JSON回应
	 * @param responeVO
	 * @param response
	 */
	private void responseMessageToClient(ResponeVO responeVO, HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json; charset=utf-8"); 
		try {
			JsonConfig config = new JsonConfig();
//			response.getWriter().write(JSONObject.fromObject(responeVO, config).toString());	//不加密返回
			response.setHeader("sign", DigestUtils.md5Hex(JSONObject.fromObject(responeVO, config).toString()));
			response.getWriter().write(DESUtil.encode(JSONObject.fromObject(responeVO, config).toString(),requestKey));
		} catch (Exception e) {
			logger.warn("ApiController#service", e);
			try {
//			response.getWriter().write("{\"responseCode\":3,\"responseDesc\":\"服务器繁忙\"}"); 	//不加密返回
				response.setHeader("sign", DigestUtils.md5Hex("{\"responseCode\":3,\"responseDesc\":\"服务器繁忙\"}"));
				response.getWriter().write(DESUtil.encode("{\"responseCode\":3,\"responseDesc\":\"服务器繁忙\"}",requestKey));
			} catch (IOException e1) {
				logger.warn("ApiController#service", e);
			}
		}
	}

	private Map<String,Object> verifyAppVersion(String appVersion,String erminalCode) {
		JsonConfig config = new JsonConfig();
		JSONObject json = JSONObject.fromObject(erminalCode, config);
		String os = String.valueOf(json.get("os"));
		String versionKey = (String)redisTemplate.opsForHash().get(RedisEnum.VERSION_KEY.getCode(),os+"_"+VersionEnum.VERSION_TYPE_HISS.getCode());
		Integer version = Integer.valueOf(appVersion.replaceAll("\\.", "").replaceAll(" ",""));
		if(!StringUtil.isEmpty(versionKey)){
			JSONObject versionControl = JSONObject.fromObject(versionKey);
			return versionMap(version,versionControl);
		}
		return  null;
	}

	private Map<String,Object> versionMap(Integer version,JSONObject versionControl){
		Integer versionName = Integer.valueOf(String.valueOf(versionControl.get("versionName")).replaceAll("\\.", "").replaceAll(" ",""));//最新版本
		if(versionName>version){//当前最新版本比app传过来的要高
			Map<String,Object> resultMap=new HashMap<String, Object>();
			if(versionControl!=null){
				resultMap.put("versionName",String.valueOf(versionControl.get("versionName")));
				resultMap.put("versionCode",Integer.valueOf(String.valueOf(versionControl.get("versionCode"))));
				resultMap.put("versionClient",String.valueOf(versionControl.get("versionClient")));
				resultMap.put("versiondes",String.valueOf(versionControl.get("versiondes")));
				resultMap.put("downloadUrl",String.valueOf(versionControl.get("downloadUrl")));
				resultMap.put("versionType",String.valueOf(versionControl.get("versionType")));
				resultMap.put("ismust", String.valueOf(versionControl.get("ismust")));
				return resultMap;
			}
		}
		return null;
	}
	
}
