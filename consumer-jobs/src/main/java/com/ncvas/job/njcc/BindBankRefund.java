package com.ncvas.job.njcc;

import com.ncvas.job.WeconexTaskJob;
import com.ncvas.payment.service.biz.AnjiePayBizService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class BindBankRefund implements WeconexTaskJob {
    private static Logger log = Logger.getLogger(BindBankRefund.class);
    @Autowired
    private AnjiePayBizService anjiePayBizService;

    public void execute() {
        log.info("收到Quartz的处理绑定银行卡支付一分钱的退款");
        System.out.println("===================================BindBankRefundStart===================================");
        try {
            anjiePayBizService.bindBankCardRefund("1");
            System.out.println("==================================BindBankRefundEnd====================================");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("处理绑定银行卡支付一分钱的退款失败。");
        }
    }
}
