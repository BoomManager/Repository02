package com.javawxid.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.javawxid.annotations.LoginRequired;
import com.javawxid.bean.OrderInfo;
import com.javawxid.conf.AlipayConfig;
import com.javawxid.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {// /alipay/submit

    @Autowired
    AlipayClient alipayClient;

    @Reference
    OrderService orderService;

    @RequestMapping("/alipay/callback/return")
    public String callBackReturn(){

        return "finish";
    }


    @LoginRequired(isNeedLogin = true)
    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String goToPay(HttpServletRequest request, String outTradeNo ,BigDecimal totalAmount, ModelMap map){

        OrderInfo orderInfo = orderService.getOrderByOutTradeNo(outTradeNo);
        String skuName = orderInfo.getOrderDetailList().get(0).getSkuName();

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        Map<String,String> requestMap = new HashMap<>();

        requestMap.put("out_trade_no",outTradeNo);
        requestMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        requestMap.put("total_amount","0.01");
        requestMap.put("subject",skuName);

        alipayRequest.setBizContent(JSON.toJSONString(requestMap));//填充业务参数
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(form);

        return form;
    }

    @LoginRequired(isNeedLogin = true)
    @RequestMapping("paymentIndex")
    public String paymentIndex(HttpServletRequest request, String outTradeNo , BigDecimal totalAmount, ModelMap map){
        String userId = (String)request.getAttribute("userId");

        map.put("outTradeNo",outTradeNo);
        map.put("totalAmount",totalAmount);

        return "paymentindex";
    }
}
