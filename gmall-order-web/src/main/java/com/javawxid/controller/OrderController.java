package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.javawxid.annotations.LoginRequired;
import com.javawxid.bean.*;
import com.javawxid.bean.enums.PaymentWay;
import com.javawxid.service.CartService;
import com.javawxid.service.OrderService;
import com.javawxid.service.SkuService;
import com.javawxid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class OrderController {

    @Reference
    CartService cartService;

    @Reference
    UserService userService;

    @Reference
    OrderService orderService;

    @Reference
    SkuService skuService;

    @LoginRequired(isNeedLogin = true)
    @RequestMapping("submitOrder")
    public String submitOrder(String deliveryAddressId,String tradeCode,HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        String userId = (String)request.getAttribute("userId");

        boolean b = orderService.checkTradeCode(tradeCode,userId);

        if(b){

            UserAddress userAddress = userService.getAddressById(deliveryAddressId);
            List<CartInfo> cartInfos = cartService.cartListFromCache(userId);

            // 订单保存业务(订单数据的一致性校验，库存价格)
            // 对订单对象进行封装
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setProcessStatus("订单已提交");
            orderInfo.setOrderStatus("订单已提交");
            String outTradeNo = "atguigu"+userId;
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = sdf.format(date);
            outTradeNo = outTradeNo + format + System.currentTimeMillis();
            orderInfo.setOutTradeNo(outTradeNo);//22019125123213123123123123
            orderInfo.setUserId(userId);
            orderInfo.setPaymentWay(PaymentWay.ONLINE);
            BigDecimal mySum = getMySum(cartInfos);
            orderInfo.setTotalAmount(mySum);
            orderInfo.setOrderComment("尚硅谷商城");
            orderInfo.setDeliveryAddress(userAddress.getUserAddress());
            orderInfo.setCreateTime(new Date());
            orderInfo.setConsignee(userAddress.getConsignee());
            orderInfo.setConsigneeTel(userAddress.getPhoneNum());
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,1);
            orderInfo.setExpireTime(c.getTime());

            List<OrderDetail> orderDetails = new ArrayList<>();
            List<String> delCartIds = new ArrayList<>();
            // 对订单详情进行封装
            for (CartInfo cartInfo : cartInfos) {
                if(cartInfo.getIsChecked().equals("1")){
                    // 验价
                    SkuInfo sku = skuService.getSkuById(cartInfo.getSkuId());
                    int i = sku.getPrice().compareTo(cartInfo.getSkuPrice());
                    if(i==0){
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setSkuNum(cartInfo.getSkuNum());
                        orderDetail.setImgUrl(cartInfo.getImgUrl());
                        orderDetail.setOrderPrice(cartInfo.getCartPrice());
                        orderDetail.setSkuId(cartInfo.getSkuId());
                        orderDetail.setSkuName(cartInfo.getSkuName());
                        orderDetails.add(orderDetail);
                        delCartIds.add(cartInfo.getId());
                    }else{
                        return "tradeFail";
                    }
                }
            }

            orderInfo.setOrderDetailList(orderDetails);

            orderService.saveOrder(orderInfo);

            // 删除购物车中已经保存订单数据
            // cartService.delCartByIds(delCartIds);
            return "redirect:http://payment.gmall.com:8090/paymentIndex?outTradeNo="+outTradeNo+"&totalAmount="+mySum;// 重定向支付系统，由支付系统对接支付宝平台，完成支付业务
        }else{
            return "tradeFail";
        }

    }

    @LoginRequired(isNeedLogin = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map) {

        String userId = (String)request.getAttribute("userId");
        // 根据userid查询缓存中的购物车数据
        List<CartInfo> cartInfos = cartService.cartListFromCache(userId);

        // 将购物车数据转化为订单列表数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {

            if(cartInfo.getIsChecked().equals("1")){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setHasStock("1");
                orderDetailList.add(orderDetail);
            }

        }
        

        // 查询userid的收货人列表信息
        List<UserAddress> userAddressList = userService.getAddressListByUserId(userId);

        map.put("userAddressList",userAddressList);
        map.put("orderDetailList",orderDetailList);
        map.put("totalAmount",getMySum(cartInfos));

        // 生成交易码，写入缓存
        String tradeCode = UUID.randomUUID().toString();
        map.put("tradeCode",tradeCode);
        orderService.genTradeCode(tradeCode,userId);

        return "trade";
    }


    private BigDecimal getMySum(List<CartInfo> cartList) {
        BigDecimal b = new BigDecimal("0");
        for (CartInfo cartInfo : cartList) {
            String isChecked = cartInfo.getIsChecked();

            if (isChecked.equals("1")) {
                b = b.add(cartInfo.getCartPrice());
            }
        }
        return b;
    }
}
