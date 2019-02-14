package com.javawxid.paymentListener;

import com.javawxid.bean.PaymentInfo;
import com.javawxid.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import java.util.Date;

@Component
public class DelayCheckListener {

    @Autowired
    PaymentService paymentService;


    @JmsListener(containerFactory = "jmsQueueListener", destination = "PAYMENT_CHECK_QUEUE")
    public void consumeCheckResult(MapMessage mapMessage) {
        try {
            String out_trade_no = mapMessage.getString("out_trade_no");
            int count = mapMessage.getInt("count");
            // 如果没有支付成功，再次发送延迟检查队列
            if (count > 0) {
                // 进行支付状态检查
                System.out.println("正在进行第" + (6 - count) + "支付结果次检查");
                PaymentInfo paymentInfo = paymentService.checkPaymentResult(out_trade_no);
                if (paymentInfo.getPaymentStatus()!=null&&(paymentInfo.getPaymentStatus().equals("TRADE_SUCCESS") || paymentInfo.getPaymentStatus().equals("TRADE_FINISHED"))) {
                    // 交易成功或者失败，记录交易状态
                    System.out.println("检查交易结果成功，记录交易状态。。。");// 修改支付系统的状态信息
                    // 修改支付信息
                    boolean b = paymentService.checkPaymentStatus(out_trade_no);
                    if(!b){
                        PaymentInfo paymentInfoUpdate = new PaymentInfo();
                        paymentInfoUpdate.setPaymentStatus("已支付");
                        paymentInfoUpdate.setCallbackContent(paymentInfo.getCallbackContent());
                        paymentInfoUpdate.setOutTradeNo(out_trade_no);
                        paymentInfoUpdate.setAlipayTradeNo(paymentInfo.getAlipayTradeNo());
                        paymentInfoUpdate.setCallbackTime(new Date());
                        paymentService.updatePayment(paymentInfoUpdate);
                        // 发送系统消息，出发并发商品支付业务消息队列
                        paymentService.sendPaymentSuccess(paymentInfo.getOutTradeNo(),paymentInfo.getPaymentStatus(),paymentInfo.getAlipayTradeNo());
                    }
                } else {
                    // 再次进行延迟检查
                    System.out.println("正在进行第" + (6 - count) + "支付结果次检查，检查用户尚未付款成功，继续巡检");
                    paymentService.sendDelayPaymentCheck(out_trade_no, count - 1);
                }
            } else {
                System.out.println("支付结果次检查次数耗尽，支付未果。。。");
            }
        } catch (Exception e) {

        }
    }

}
