package com.ncvas.common.service.impl;

import com.ncvas.base.enums.RedisEnum;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountQuickPayMentBankListService;
import com.ncvas.payment.service.AnjiePayService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lc_xin.
 * @date 2016年12月06日
 */
@Service("accountQuickPayMentBankListService")
public class AccountQuickPayMentBankListServiceImpl implements AccountQuickPayMentBankListService {
	private static final Logger logger =LoggerFactory.getLogger(AccountQuickPayMentBankListServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Value("#{configProperties['cardNo.minutes']}")
	private String minutes;

	@Override
	public Map<String, Object> accountQuickPayMentBankList(Map<String, Object> paraMap) throws Exception {
		String memberCode = (String) paraMap.get("memberCode");
		paraMap.remove("memberCode");
		Map<String,Object> resultMap = anjiePayService.accountQuickPayMentBankList(paraMap);
		redisTemplate.delete(memberCode+ RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode());
		if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			Map<String,Object> rows = new HashMap<>();
			List<Map<String,Object>> resultlist = new ArrayList<>();
			if(resultMap.get("bankList")!=null){
				JSONArray bankList = JSONArray.fromObject(resultMap.get("bankList"));
				String listData = resultMap.get("bankList").toString();
				redisTemplate.opsForValue().set(memberCode + RedisEnum.QUICK_PAY_BANK_INFO.getCode(),listData, 30, TimeUnit.MINUTES);
				for(int i = 0;i<bankList.size();i++){
					JSONObject json = (JSONObject)bankList.get(i);
					Map<String,Object> temMap = new HashMap<>();
					temMap.put("bankId",String.valueOf(json.get("bankId")));
					temMap.put("bankName",String.valueOf(json.get("bankName")));
					temMap.put("buyerMarked",String.valueOf(json.get("buyerMarked")));
					temMap.put("cantractNo",String.valueOf(json.get("cantractNo")));
					temMap.put("cardNo",String.valueOf(json.get("cardNo")));
					temMap.put("cardType",String.valueOf(json.get("cardType")));
					temMap.put("certNo",String.valueOf(json.get("certNo")));
					temMap.put("checkFlag",String.valueOf(json.get("checkFlag")));
//					temMap.put("createDate",String.valueOf(json.get("createDate")));
					temMap.put("defaultFlag",String.valueOf(json.get("defaultFlag")));
					temMap.put("lastSendDate",String.valueOf(json.get("lastSendDate")));
					temMap.put("merCode",String.valueOf(json.get("merCode")));
					temMap.put("mobile",String.valueOf(json.get("mobile")));
					temMap.put("sendCount",String.valueOf(json.get("sendCount")));
					temMap.put("status",String.valueOf(json.get("status")));
//					temMap.put("updateDate",String.valueOf(json.get("updateDate")));
					temMap.put("userName",String.valueOf(json.get("userName")));
					temMap.put("validDate",String.valueOf(json.get("validDate")));

					//把卡号缓存在redis，绑定重复卡号时提示用户
					redisTemplate.opsForHash().put(memberCode+RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(),String.valueOf(json.get("cardNo")),"");
					redisTemplate.expire(memberCode+RedisEnum.QUICK_PAY_BANK_CARD_NO.getCode(),Integer.parseInt(minutes), TimeUnit.MINUTES);
					resultlist.add(temMap);
				}
			}
			rows.put("rows",resultlist);
			rows.put(PayPlatformEnum.RESPONSE_KEY_CODE.getCode(),PayPlatformEnum.RESPONSE_SUCCESS.getCode());
			return rows;
		}
		return resultMap;
	}
}
