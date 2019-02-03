package com.javawxid.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.javawxid.bean.OrderDetail;
import com.javawxid.bean.OrderInfo;
import com.javawxid.mapper.OrderDetailMapper;
import com.javawxid.mapper.OrderInfoMapper;
import com.javawxid.service.OrderService;
import com.javawxid.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public void genTradeCode(String tradeCode, String userId) {
        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:"+userId+":tradeCode",60*30,tradeCode);

        jedis.close();
    }

    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {

        boolean b = false;

        Jedis jedis = redisUtil.getJedis();
        String tradeCodeFromCache = jedis.get("user:" + userId + ":tradeCode");

        if(tradeCode.equals(tradeCodeFromCache)){
            b = true;

            jedis.del("user:" + userId + ":tradeCode");
        }

        return b;
    }

    @Override
    public void saveOrder(OrderInfo orderInfo) {

        // 保存订单信息
        orderInfoMapper.insertSelective(orderInfo);

        String orderId = orderInfo.getId();

        // 保存订单详情
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insertSelective(orderDetail);
        }

    }

    @Override
    public OrderInfo getOrderByOutTradeNo(String outTradeNo) {

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo = orderInfoMapper.selectOne(orderInfo);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }
}
