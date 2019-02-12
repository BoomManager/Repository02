package com.javawxid.service;


import com.javawxid.bean.PaymentInfo;

public interface PaymentService {
    void save(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendPaymentSuccess(String outTradeNo, String paymentStatus, String trackingNo);
}
