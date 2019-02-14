package com.javawxid.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.javawxid.annotations.LoginRequired;
import com.javawxid.bean.OrderInfo;
import com.javawxid.bean.PaymentInfo;
import com.javawxid.bean.UserInfo;
import com.javawxid.conf.AlipayConfig;
import com.javawxid.service.OrderService;
import com.javawxid.service.PaymentService;
import com.javawxid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {// /alipay/submit

    @Autowired
    AlipayClient alipayClient;

    @Reference
    OrderService orderService;

    @Autowired
    PaymentService paymentService;
    @Reference
    UserService userService;
    @LoginRequired(isNeedLogin = true)
    @RequestMapping("paymentIndex")
    public String paymentIndex(HttpServletRequest request, String outTradeNo , BigDecimal totalAmount, ModelMap map){
        String userId = (String)request.getAttribute("userId");
        UserInfo userInfo = userService.getUserById(userId);
        String nickName = userInfo.getNickName();
        map.put("nickName",nickName);
        map.put("outTradeNo",outTradeNo);
        map.put("totalAmount",totalAmount);

        return "paymentindex";
    }

    @RequestMapping("/alipay/callback/return")
    public String callBackReturn(HttpServletRequest request,Map<String,String> paramsMap){// 页面同步反转的回调

        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        String sign = request.getParameter("sign");
        try {
            boolean b = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key,AlipayConfig.charset,AlipayConfig.sign_type);// 对支付宝回调签名的校验
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 修改支付信息
        // 幂等性检查
        boolean b = paymentService.checkPaymentStatus(out_trade_no);
        if(!b){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setCallbackContent(request.getQueryString());
            paymentInfo.setOutTradeNo(out_trade_no);
            paymentInfo.setAlipayTradeNo(trade_no);
            paymentInfo.setCallbackTime(new Date());
            paymentService.updatePayment(paymentInfo);

            // 发送系统消息，出发并发商品支付业务消息队列
            paymentService.sendPaymentSuccess(paymentInfo.getOutTradeNo(),paymentInfo.getPaymentStatus(),trade_no);
        }


        return "finish";
    }

    /**
     * 去支付，必须登录
     * @param request
     * @param outTradeNo
     * @param totalAmount
     * @param map
     * @return
     */
    @LoginRequired(isNeedLogin = true)
    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String goToPay(HttpServletRequest request, String outTradeNo ,BigDecimal totalAmount, ModelMap map){
        //根据订单号获取订单信息
        OrderInfo orderInfo = orderService.getOrderByOutTradeNo(outTradeNo);
        //获取商品名称
        String skuName = orderInfo.getOrderDetailList().get(0).getSkuName();
        //创建PC场景下单并支付请求对象
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        //设置同步返回地址，HTTP/HTTPS开头字符串
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        //支付宝服务器主动通知商户服务器里指定的页面http/https路径。
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        Map<String,String> requestMap = new HashMap<>();

        requestMap.put("out_trade_no",outTradeNo);
        requestMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        requestMap.put("total_amount","0.01");
        requestMap.put("subject",skuName);
        //填充业务参数
        alipayRequest.setBizContent(JSON.toJSONString(requestMap));
        String form="";
        try {
            //调用SDK生成表单
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(form);

        // 生成(保存)支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setSubject(skuName);
        paymentInfo.setCreateTime(new Date());
        paymentService.save(paymentInfo);

        // 发送检查支付结果的消息队列，
        paymentService.sendDelayPaymentCheck(outTradeNo,5);
        return form;
    }


}
