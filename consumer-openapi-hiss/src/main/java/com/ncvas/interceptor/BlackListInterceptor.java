package com.ncvas.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.ncvas.base.enums.RedisEnum;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.util.DESUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BlackListInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(BlackListInterceptor.class);

	@Autowired
	private RedisTemplate redisTemplate;

	@Value("#{configProperties['login.serCode']}")
	private String loginSerCode;
	@Value("#{configProperties['requestKey']}")
	private String requestKey;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try{
			String serCode = DESUtil.decode(request.getParameter("serCode"),requestKey);
			if(loginSerCode.equals(serCode)){
				String dataMsg = DESUtil.decode(request.getParameter("dataMsg"),requestKey);
				String logincode =(String) JSONObject.parseObject(dataMsg).get("logincode");
				if(!StringUtil.isEmpty(logincode)){
					return DetermineBlackList(logincode,null,null,response);
				}
			}else {
				String token = request.getParameter("token");
				String  Temp= (String)redisTemplate.opsForValue().get(token + RedisEnum.ACCOUNT_LOGIN_SERVICE.getCode());
				if(StringUtil.isEmpty(Temp)){
					return true;
				}else {
					JSONObject json = JSONObject.parseObject(Temp);
					String loginid = (String) json.get("nickname");
					String mobile = (String)json.get("loginName");
					String aliascode = (String)json.get("aliascode");
					if(!StringUtil.isEmpty(loginid)||
							!StringUtil.isEmpty(mobile)||
							!StringUtil.isEmpty(aliascode)){
						return DetermineBlackList(loginid,mobile,aliascode,response);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.warn("BlackListInterceptor#error", e);
			responseWriter(response);
			return false;
		}
		return true;
	}

	public Boolean DetermineBlackList(String str,String mobile,String aliascode ,HttpServletResponse response) throws IOException{
		String phone = "";
		String ascode = "";
		if(!StringUtil.isEmpty(str))
		{
			String val = (String) redisTemplate.opsForHash().get("BlackListInterceptor",str);
			if(!StringUtil.isEmpty(mobile)&&!StringUtil.isEmpty(aliascode)){
				phone = (String) redisTemplate.opsForHash().get("BlackListInterceptor",mobile);
				ascode = (String) redisTemplate.opsForHash().get("BlackListInterceptor",aliascode);
				if(str.equals(val)||
						mobile.equals(phone)||
						aliascode.equals(ascode)){
					responseWriter(response);
					return false;
				}
			}
			if(str.equals(val)){
				responseWriter(response);
				return false;
			}
		}
		return true;
	}

	private void responseWriter(HttpServletResponse response)throws IOException{
		response.setHeader("sign", DigestUtils.md5Hex("{\"responseCode\":1002,\"responseDesc\":\"账号已被冻结，请联系管理员！\"}"));
		response.getWriter().write(DESUtil.encode("{\"responseCode\":1002,\"responseDesc\":\"账号已被冻结，请联系管理员！\"}",requestKey));
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}
}
