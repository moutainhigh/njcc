package com.ncvas.common.service.impl;

import com.ncvas.common.utils.StringUtil;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AccountCheckNjccCardListService;
import com.ncvas.payment.service.AnjiePayService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc_xin.
 * @date 2016年11月24日
 */
@Service("accountCheckNjccCardListService")
public class AccountCheckNjccCardListServiceImpl implements AccountCheckNjccCardListService {
	private static final Logger logger =LoggerFactory.getLogger(AccountCheckNjccCardListServiceImpl.class);
	@Autowired
	private AnjiePayService anjiePayService;

	@Override
	public Map<String, Object> accountCheckNjccCardList(String memberCode) throws Exception {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("memberCode", memberCode);
		Map<String,Object> resultMap = anjiePayService.accountCheckNjccCardList(paraMap);
		if(resultMap!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(resultMap.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
			Map<String,Object> rows = new HashMap<>();
			List<Map<String,Object>> resultlist = new ArrayList<>();
			if(resultMap.get("zhCardList")!=null){
				JSONArray zhCardList = JSONArray.fromObject(resultMap.get("zhCardList"));
				for(int i = 0;i<zhCardList.size();i++){
					JSONObject json = (JSONObject)zhCardList.get(i);
					Map<String,Object> temMap = new HashMap<>();
					temMap.put("memberCode",String.valueOf(json.get("memberCode")));
					/*temMap.put("payPwd",String.valueOf(json.get("payPwd")));*/
					temMap.put("cardType",String.valueOf(json.get("cardType")));
					temMap.put("photo",String.valueOf(json.get("photo")));
					temMap.put("aliasCode",String.valueOf(json.get("aliasCode")));
					temMap.put("psgnCode",String.valueOf(json.get("psgnCode")));
					temMap.put("cardClass",String.valueOf(json.get("cardClass")));
					temMap.put("custClass",String.valueOf(json.get("custClass")));
					temMap.put("cardStatus",String.valueOf(json.get("cardStatus")));
					temMap.put("usrField",String.valueOf(json.get("usrField")));
					temMap.put("custStatus",String.valueOf(json.get("custStatus")));
					temMap.put("lossStauts",String.valueOf(json.get("lossStauts")));
					temMap.put("cardBalance",String.valueOf(json.get("cardBalance")));
					temMap.put("accBalance",String.valueOf(json.get("accBalance")));
					temMap.put("setPwd",String.valueOf(json.get("setPwd")));
					temMap.put("socialCode",String.valueOf(json.get("socialCode")));
					if (!StringUtil.isEmpty(String.valueOf(json.get("cardCategory")))) {
//						01：市民卡A卡 02：市民卡B卡 03：金陵通卡、紫金卡 04：市民卡C卡 05：旅游年卡 06：助老卡
						String cardcategory = String.valueOf(json.get("cardCategory"));
//						重写类型，适配app
						if("03".equals(cardcategory)){
							if ("0000".equals(String.valueOf(json.get("custClass")))) {
								cardcategory = "07";//金陵通记名卡
							} else {
								cardcategory="08";//紫金卡
							}
						}else if ("00".equals(cardcategory)){
							/**00是支付那边返回回来的， 给app的文档写了09*/
							cardcategory="09";//虚拟卡
						}
						temMap.put("cardcategory",cardcategory);
					}
					temMap.put("setMain",String.valueOf(json.get("setMain")));
					temMap.put("isNotCard",String.valueOf(json.get("isNotCard")));
					temMap.put("registered",String.valueOf(json.get("registered")));
					/*temMap.put("bindDate",String.valueOf(json.get("bindDate")));*/
					temMap.put("name",String.valueOf(json.get("name")));
					/*temMap.put("id",String.valueOf(json.get("id")));*/
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
