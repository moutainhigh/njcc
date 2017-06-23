package com.ncvas.mq.queue.receive;

import com.ncvas.hiss.service.HissBankCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by Administrator on 2016-7-14.
 */
@Component
public class BankCallBackListener implements MessageListener {
    protected transient final Logger logger = LoggerFactory.getLogger(BankCallBackListener.class);
    @Autowired
    private HissBankCardService hissBankCardService;
    @Override
    public void onMessage(Message m) {
        try {
            logger.info("收到处理银行mq信息----------");
            MapMessage message = (MapMessage) m;
            String depositAmount=message.getString("depositAmount");
            String depositOrderNo=message.getString("depositOrderNo");
            String status=message.getString("status");
            logger.info("第三方银行回调收到消息：depositAmount:{},depositOrderNo:{},status:{}"
                    ,depositAmount,depositOrderNo,status);
            hissBankCardService.processPayPlatformCallBack(depositOrderNo,status,depositAmount);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}