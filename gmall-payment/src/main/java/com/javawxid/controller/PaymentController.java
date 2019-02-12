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
import com.javawxid.conf.AlipayConfig;
import com.javawxid.service.OrderService;
import com.javawxid.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@Controller
public class PaymentController {// /alipay/submit

    @Autowired
    AlipayClient alipayClient;

    @Reference
    OrderService orderService;

    @Autowired
    PaymentService paymentService;

    @RequestMapping("/alipay/callback/return")
    public String callBackReturn(HttpServletRequest request,Map<String,String> paramsMap){// 页面同步反转的回调

        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");

        String sign = request.getParameter("sign");
        try {
            boolean b = AlipaySignature.rsaCheckV1(paramsMap,AlipayConfig.alipay_public_key,AlipayConfig.charset,AlipayConfig.sign_type);// 对支付宝回调签名的校验
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 修改支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setCallbackContent(request.getQueryString());
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setAlipayTradeNo(trade_no);
        paymentInfo.setCallbackTime(new Date());
        // 发送系统消息，出发并发商品支付业务服务O2O消息队列
        paymentService.sendPaymentSuccess(paymentInfo.getOutTradeNo(),paymentInfo.getPaymentStatus(),trade_no);

        paymentService.updatePayment(paymentInfo);

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

        // 生成(保存)支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setSubject(skuName);
        paymentInfo.setCreateTime(new Date());
        paymentService.save(paymentInfo);

        // 发送检查支付结果的消息队列
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
