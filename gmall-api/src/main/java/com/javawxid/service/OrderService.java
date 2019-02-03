package com.javawxid.service;

import com.javawxid.bean.OrderInfo;

public interface OrderService {

    void genTradeCode(String tradeCode, String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    void saveOrder(OrderInfo orderInfo);

    OrderInfo getOrderByOutTradeNo(String outTradeNo);
}
