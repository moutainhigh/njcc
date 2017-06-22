package com.ncvas.common.service.impl;

import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountTransactionService;
import com.ncvas.payment.service.AnjiePayService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月21日
 */
@Service("accountTransactionService")
@Transactional(rollbackFor={RuntimeException.class, Exception.class})
public class AccountTransactionServiceImpl implements AccountTransactionService {
	private static final Logger logger =LoggerFactory.getLogger(AccountTransactionServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;
	@Override
	public Map<String, Object> accountTransaction(Map<String, String> paraMap) throws Exception{
		Map<String,Object> resultMap = anjiePayService.accountTransaction(paraMap);

		if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			Map resMap = new HashMap();
			JSONObject rescontent = JSONObject.fromObject(resultMap.get("rescontent"));
			resMap.put("transtype",String.valueOf(rescontent.get("TRANSTYPE")));
			resMap.put("aliascode",String.valueOf(rescontent.get("ALIASCODE")));
			resMap.put("totalcount",String.valueOf(rescontent.get("TOTALCOUNT")));
			resMap.put("pageindex",String.valueOf(rescontent.get("PAGEINDEX")));
			resMap.put("pagesize",String.valueOf(rescontent.get("PAGESIZE")));
			List<Map<String,Object>> resultlist = new ArrayList<>();
			if(resultMap.get("rescontentlist")!=null){
				JSONArray rescontentlist = JSONArray.fromObject(resultMap.get("rescontentlist"));
				for(int i = 0;i<rescontentlist.size();i++){
					JSONObject json = (JSONObject)rescontentlist.get(i);
					Map<String,Object> temMap = new HashMap<>();
					temMap.put("transtime",String.valueOf(json.get("TRANSTIME")));
					temMap.put("transtype",String.valueOf(json.get("TRANSTYPE")));
					temMap.put("transaddr",String.valueOf(json.get("TRANSADDR")));
					temMap.put("transamt",String.valueOf(json.get("TRANSAMT")));
					resultlist.add(temMap);
				}
			}
			resMap.put("rows",resultlist);
			resMap.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
			return resMap;
		}
		return resultMap;
	}
}
