package com.ncvas.common.service.impl;

import com.ncvas.payment.entity.MasApplyForCard;
import com.ncvas.payment.enums.PayPlatformEnum;
import com.ncvas.payment.service.AnjiePayService;
import com.ncvas.payment.service.ApplyForCardService;
import com.ncvas.payment.service.MasApplyForCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by fancz on 2017/6/20.
 */
@Service("applyForCardService")
public class ApplyForCardServiceImpl implements ApplyForCardService {

    private static final Logger logger = LoggerFactory.getLogger(ApplyForCardServiceImpl.class);
    @Autowired
    private AnjiePayService anjiePayService;
    @Autowired
    private MasApplyForCardService masApplyForCardService;

    @Override
    public Map<String, Object> applyForCard(Map<String, String> paraMap) throws Exception {
        Map<String, Object> result = anjiePayService.applyForCard(paraMap);
        if(result!=null && PayPlatformEnum.RESPONSE_SUCCESS.getCode().equals(result.get(PayPlatformEnum.RESPONSE_KEY_CODE.getCode()))){
            //只有提交成功才进行存库
            MasApplyForCard masApplyForCard = new MasApplyForCard();
            masApplyForCard.setCustname((String) paraMap.get("custname"));
            masApplyForCard.setMobile((String) paraMap.get("mobile"));
            masApplyForCard.setCertifyid((String) paraMap.get("certifyid"));
            masApplyForCard.setRecmode((String) paraMap.get("recmode"));
            masApplyForCard.setAddress((String) paraMap.get("address"));
            masApplyForCard.setPhoto((String) paraMap.get("photo"));
            masApplyForCard.setExpress((String) paraMap.get("express"));
            masApplyForCard.setStatus((String) paraMap.get("status"));
            masApplyForCard.setStatusDesc((String) paraMap.get("statusDesc"));
            masApplyForCard.setMemberCode((String) paraMap.get("memberCode"));
            masApplyForCard.setStatus("1");// 1：审核申领信息 20：审核成功 21：审核失败 3：开卡中 4：领卡
            masApplyForCard.setStatusDesc("提交成功");
            masApplyForCard.setRecipients((String) paraMap.get("recipients"));
            masApplyForCard.setRecipientsMobile((String) paraMap.get("recipientsmobile"));
            masApplyForCard.setStacode((String) paraMap.get("stacode"));
            masApplyForCardService.doCreate(masApplyForCard);
            logger.info("mas====>>applyForCard返回参数保存数据完成!");
        }
        return result;
    }
}
