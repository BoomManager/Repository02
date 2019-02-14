package com.javawxid.service;


import com.javawxid.bean.PaymentInfo;

public interface PaymentService {
    void save(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendPaymentSuccess(String outTradeNo, String paymentStatus,String trackingNo);

    void sendDelayPaymentCheck(String outTradeNo, int i);

    PaymentInfo checkPaymentResult(String out_trade_no);

    boolean checkPaymentStatus(String out_trade_no);
}
